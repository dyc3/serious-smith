package main.java;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.extra.entity.components.HealthComponent;
import com.almasb.fxgl.input.Input;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;

/** Controls the player. Should not be extended. **/
public final class PlayerComponent extends Component
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

	/** XP required to level up. **/
	public static final int XP_PER_LEVEL = 100;
    /** Damage dealt per projectile at level 1. **/
    private static final int INIT_DAMAGE = 4;
    /** Maximum amount damage can increase by on level up. **/
    private static final int MAX_DAMAGE_CHANGE_PER_LEVEL = 8;
	/** Multiply fireInterval by this value on level up. **/
	private static final double FIRE_INTERVAL_MULTIPLIER = 0.95;
    /** Initial maximum health. **/
    private static final int INIT_MAX_HEALTH = 100;
	/** Minimum amount damage can increase by on level up. **/
	private static final int MIN_HEALTH_CHANGE_PER_LEVEL = 5;
	/** Maximum amount damage can increase by on level up. **/
	private static final int MAX_HEALTH_CHANGE_PER_LEVEL = 10;
	/** Initial critical hit chance of the player's projectiles. **/
	private static final double INIT_CRIT_CHANCE = 0.1;
	/** Initial critical hit multiplier of the player's projectiles. **/
	private static final double INIT_CRIT_MULTIPLIER = 2;

	/** Movement speed. **/
    private double speed;
	/** Time (in seconds) remaining to fire a projectile. **/
	private double timeToFire = 0;
	/** Time (in seconds) between firing projectiles. **/
    private double fireInterval;
	/** Damage that the player's projectiles will deal. **/
	private int damage = INIT_DAMAGE;
	/** Critical hit chance of the player's projectiles. **/
	private double critChance = INIT_CRIT_CHANCE;
	/** Critical hit multiplier of the player's projectiles. **/
	private double critMultiplier = INIT_CRIT_MULTIPLIER;
	/** Maximum health. **/
	private IntegerProperty maxHealth = new SimpleIntegerProperty(INIT_MAX_HEALTH);
	/** Tracks the player's experience. **/
	private IntegerProperty xp = new SimpleIntegerProperty();
	/** Tracks the player's level. **/
	private IntegerProperty level = new SimpleIntegerProperty(1);

	/** Indicates if the player is currently dashing. **/
    private boolean dashing = false;
    /** When the player starts dashing, this is set, and the player begins a fixed path to this point. **/
    private Point2D dashTarget;
    /** Time (in seconds) remaining to wait before next dash. **/
    private double dashCooldown = 0;

    /** Keep track of the boss entity. **/
	private Entity boss;
	/** Use to read keyboard input. **/
	private Input input;
	/** Use this to spawn projectiles. **/
	private ProjectileFactory projFactory;

	/** Creates a PlayerComponent. Do not create multiple players.
	 * @param input Use to read keyboard input.
	 * @param factory Use this to spawn projectiles. **/
    public PlayerComponent(Input input, ProjectileFactory factory)
    {
        speed = DEFAULT_MOVE_SPEED;
        fireInterval = DEFAULT_FIRE_INTERVAL;
        this.input = input;
		this.projFactory = factory;
    }

	/**
	 * Gets the direction the player is moving in based on input.
	 * @return The direction the player should move in.
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
            data.put("damage", getDamage());
			data.put("critChance", getCritChance());
			data.put("critMultiplier", getCritMultiplier());
            getEntity().getWorld().addEntity(projFactory.spawnPlayerProjectile(data));
        }
    }

    /** Gets the player's experience.
	 * @return 0 <= experience < XP_PER_LEVEL **/
    public int getXP()
	{
		return xp.getValue();
	}

	/** Gets the player's experience as an integer property.
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

	/** Gets the player's level as an integer property.
	 * @return player level property. **/
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

	/** Gets the chance for projectile critical damage.
	 * @return double between 0 and 1 **/
	public double getCritChance()
	{
		return critChance;
	}

	/** Gets the multiplier for projectile critical damage.
	 * @return double > 1 **/
	public double getCritMultiplier()
	{
		return critMultiplier;
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

	/** Gets the player's maximum health as an integer property.
	 * @return player max health property. **/
	public IntegerProperty getMaxHealthProperty()
	{
		return maxHealth;
	}

	/** Gets if the player is dashing.
	 * @return true if the player is dashing, false if not. **/
	public boolean getIsDashing()
	{
		return dashing;
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
		damage += FXGLMath.random(1, MAX_DAMAGE_CHANGE_PER_LEVEL);
		fireInterval *= FIRE_INTERVAL_MULTIPLIER;
		int healthIncrease = FXGLMath.random(MIN_HEALTH_CHANGE_PER_LEVEL, MAX_HEALTH_CHANGE_PER_LEVEL);
		maxHealth.setValue(maxHealth.getValue() + healthIncrease);
		critChance += 0.1 / level.getValue();
		critMultiplier += 0.1;
		System.out.println("level up: crit chance: " + critChance + " multiplier: x" + critMultiplier);

		// heal the player on level up
		HealthComponent health = entity.getComponent(HealthComponent.class);
		health.setValue(maxHealth.getValue());
	}

	/** Shortcut to deals damage to the player.
	 * @param amount The amount of damage to deal. **/
	public void dealDamage(int amount)
	{
		if (getIsDashing())
			return;

		HealthComponent health = entity.getComponent(HealthComponent.class);
		health.setValue(health.getValue() - amount);
	}
}
