package main.java;

import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

/** Controls the boss. **/
public class BossComponent extends Component
{
	/** The default interval (in seconds) between attacks. **/
	private static final double DEFAULT_BASE_ATTACK_INTERVAL = 2;
	/** Speed of star attack projectiles. **/
	private static final int STAR_ATTACK_PROJECTILE_SPEED = 200;
	/** Size of star attack projectiles. **/
	private static final int STAR_ATTACK_PROJECTILE_SIZE = 5;

    /** The minimum amount of time between attacks in seconds. Actual attack intervals may vary
     * depending on previous attack performed. **/
    private double baseAttackInterval;
    private double timeUntilAttack;

    private ProjectileFactory projFactory;

    public BossComponent(ProjectileFactory factory)
    {
    	this.projFactory = factory;
        this.baseAttackInterval = DEFAULT_BASE_ATTACK_INTERVAL;
        this.timeUntilAttack = baseAttackInterval;
    }

    public BossComponent(ProjectileFactory factory, double baseAttackInterval)
    {
		this.projFactory = factory;
        this.baseAttackInterval = baseAttackInterval;
        this.timeUntilAttack = baseAttackInterval;
    }

	/** Update every tick.
	 * @param tpf Time per frame. **/
    @Override
    public void onUpdate(double tpf)
    {
        if (timeUntilAttack <= 0)
        {
            doAttack();
            timeUntilAttack = baseAttackInterval;
        }
        else
        {
            timeUntilAttack -= tpf;
        }
    }

    /** Determines which attack to perform, then executes it. **/
    public void doAttack()
	{
		attackStar();
	}

    /** Fires 8 dumb projectiles around the boss. **/
    public void attackStar()
	{
		for (int y = -1; y <= 1; y++)
		{
			for (int x = -1; x <= 1; x++)
			{
				if (x == 0 && y == 0)
				{
					continue;
				}

				SpawnData data = new SpawnData(getEntity().getCenter());
				data.put("direction", new Point2D(x, y));
				data.put("size", STAR_ATTACK_PROJECTILE_SIZE);
				data.put("speed", STAR_ATTACK_PROJECTILE_SPEED);
				getEntity().getWorld().addEntity(projFactory.spawnDumbProjectile(data));
			}
		}
	}
}
