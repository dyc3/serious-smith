package main.java;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.input.Input;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;

public class PlayerComponent extends Component
{
    /** Movement speed. **/
    private static final double DEFAULT_MOVE_SPEED = 200;
    /** Time between auto fire in seconds. **/
    private static final double DEFAULT_FIRE_INTERVAL = 0.25;
    /** Distance to travel while dashing. **/
    private static final double DASH_DISTANCE = 200;
    /** Speed while dashing. **/
    private static final double DASH_SPEED = 1200;
    /** Minimum time between dashes in seconds. **/
    private static final double DASH_COOLDOWN = 1;
    /** Multiply the fire rate by this number while dashing. **/
    private static final double DASH_FIRE_INTERVAL_MULTIPLIER = 0.1;
    /** Damage dealt per projectile at level 1. **/
    private static final int INIT_DAMAGE = 4;
    /** Maximum amount damage can increase by on level up. **/
    private static final int MAX_DAMAGE_CHANGE_PER_LEVEL = 8;
	/** Multiply fireInterval by this value on level up. **/
	private static final double FIRE_INTERVAL_MULTIPLIER = 0.95;
    /** XP required to level up. **/
    public static final int XP_PER_LEVEL = 100;

    private double speed;
    private Input input;
	private ProjectileFactory projFactory;
	private double timeToFire = 0;
	/** Time (in seconds) between firing projectiles. **/
    private double fireInterval;

	private int damage = INIT_DAMAGE;
	/** Tracks the player's experience. **/
	private IntegerProperty xp = new SimpleIntegerProperty();
	/** Tracks the player's level. **/
	private IntegerProperty level = new SimpleIntegerProperty(1);

    private Entity boss;

    private boolean dashing = false;
    private Point2D dashTarget;
    private double dashCooldown = 0;

    public PlayerComponent(Input input, ProjectileFactory factory)
    {
        speed = DEFAULT_MOVE_SPEED;
        fireInterval = DEFAULT_FIRE_INTERVAL;
        this.input = input;
		this.projFactory = factory;
    }


	/**
	 * Gets the direction the player is moving in based on input.
	 * @return
	 */
    private Point2D getMoveDirection()
    {
        int x = 0, y = 0;
        if (input.isHeld(KeyCode.W))
        {
            y = -1;
        }
        else if (input.isHeld(KeyCode.S))
        {
            y = 1;
        }

        if (input.isHeld(KeyCode.A))
        {
            x = -1;
        }
        else if (input.isHeld(KeyCode.D))
        {
            x = 1;
        }
        return new Point2D(x, y);
    }

    /** Update every tick.
     * @param tpf Time per frame. **/
    @Override
    public void onUpdate(double tpf)
    {
        if (boss == null)
        {
            boss = getEntity().getWorld().getEntityByID("boss", 0).get();
        }

        if (input.isHeld(KeyCode.SHIFT) && !dashing && dashCooldown <= 0
				&& !getMoveDirection().equals(new Point2D(0, 0)))
        {
            dashing = true;
            Point2D move = getMoveDirection().multiply(DASH_DISTANCE);
            double dashX = entity.getPosition().getX() + move.getX();
            double dashY = entity.getPosition().getY() + move.getY();
            dashTarget = new Point2D(dashX, dashY);

			// reset time to fire so that the increased fire rate when dashing is consistent
            timeToFire = 0;
        }

        if (dashing)
        {
            if (entity.getPosition().distance(dashTarget) <= DASH_SPEED * tpf)
            {
                dashing = false;
                dashCooldown = DASH_COOLDOWN;
            }
            else
            {
                entity.translateTowards(dashTarget, DASH_SPEED * tpf);
            }
        }
        else
        {
            double moveDistance = speed * tpf;
            Point2D move = getMoveDirection().multiply(moveDistance);
            entity.translate(move);
            if (dashCooldown > 0)
            {
                dashCooldown -= tpf;
            }
        }

        // projectile firing
        timeToFire -= tpf;
        if (input.isHeld(KeyCode.SPACE) && timeToFire <= 0)
        {
            timeToFire = fireInterval;
            if (dashing)
			{
				timeToFire *= DASH_FIRE_INTERVAL_MULTIPLIER;
			}
            SpawnData data = new SpawnData(getEntity().getCenter());
            data.put("target", boss);
            getEntity().getWorld().addEntity(projFactory.spawnPlayerProjectile(data));
        }

        // Spawn some xp orbs sometimes, in random positions near the player
        if (FXGLMath.randomBoolean(0.1))
		{
			XPFactory.spawnRandomXpOrb(getEntity().getWorld(), getEntity().getCenter());
		}
    }

    /** Gets the player's experience.
	 * @return 0 <= experience < XP_PER_LEVEL **/
    public int getXP()
	{
		return xp.getValue();
	}

	/** Gets the player's experience as a integer property.
	 * @return player experience property. **/
	public IntegerProperty getXpProperty()
	{
		return xp;
	}

	/** Gets the player's level.
	 * @return integer > 0 **/
	public int getLevel()
	{
		return level.getValue();
	}

	public IntegerProperty getLevelProperty()
	{
		return level;
	}

	/** Gets the damage each player's projectiles deals.
	 * @return integer >= INIT_DAMAGE **/
	public int getDamage()
	{
		return damage;
	}

	/** Calculates the required experience to level up.
	 * @return integer > 0 **/
	public int getXpToNextLevel()
	{
		return XP_PER_LEVEL * level.getValue();
	}

	/** Gets the required experience to level up as an IntegerBinding.
	 * @return IntegerBinding equivalent to the result of getXpToNextLevel() **/
	public IntegerBinding getXpToNextLevelBinding()
	{
		return getLevelProperty().multiply(XP_PER_LEVEL);
	}

	/** Increase the player's experience and levels up when threashold is reached.
	 * @param exp The amount of experience to add. **/
	public void addXP(int exp)
	{
		// NOTE: add(), subtract(), etc. methods on SimpleIntegerProperty aren't used to modify the value.
		// Instead, they are used to create NEW bindings.
		// See https://docs.oracle.com/javafx/2/api/javafx/beans/binding/NumberExpressionBase.html
		this.xp.set(this.xp.getValue() + exp);

		while (this.xp.getValue() >= getXpToNextLevel())
		{
			this.xp.set(this.xp.getValue() - getXpToNextLevel());
			levelUp();
		}
	}

	/** Increase the player's level by one and buff stats. **/
	public void levelUp()
	{
		level.setValue(level.getValue() + 1);
		damage += Math.ceil(Math.random() * MAX_DAMAGE_CHANGE_PER_LEVEL);
		fireInterval *= FIRE_INTERVAL_MULTIPLIER;
	}
}
