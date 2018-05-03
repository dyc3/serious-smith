package main.java;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

import java.util.List;

/** Controls the boss. **/
public class BossComponent extends Component
{
	/** The default interval (in seconds) between attacks. **/
	private static final double DEFAULT_BASE_ATTACK_INTERVAL = 2;

	/** Speed of star attack projectiles. **/
	private static final int STAR_ATTACK_PROJECTILE_SPEED = 200;
	/** Size of star attack projectiles. **/
	private static final int STAR_ATTACK_PROJECTILE_SIZE = 5;
	/** Number of projectiles to fire in burst attack. **/
	private static final int BURST_ATTACK_PROJECTILE_COUNT = 20;
	/** Speed of burst attack projectiles. **/
	private static final int BURST_ATTACK_PROJECTILE_SPEED = 400;
	/** Size of burst attack projectiles. **/
	private static final int BURST_ATTACK_PROJECTILE_SIZE = 7;

	/** The probability of doing a big attack. **/
	private static final double BIG_ATTACK_CHANCE = 0.1;

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

    /** Gets the closest player.
	 * @return Entity of closest player. **/
    public Entity getPlayer()
	{
		List<Entity> players = entity.getWorld().getEntitiesByType(EntType.PLAYER);
		if (players.size() == 0)
		{
			return null;
		}
		else if (players.size() == 1)
		{
			return players.get(0);
		}

		Entity closest = players.get(0);
		double closestDist = entity.getCenter().distance(closest.getCenter());
		for (int i = 1; i < players.size(); i++)
		{
			Entity ent = players.get(i);
			double dist = entity.getCenter().distance(ent.getCenter());
			if (dist < closestDist)
			{
				closest = ent;
				closestDist = dist;
			}
		}
		return closest;
	}

    /** Determines which attack to perform, then executes it. **/
    public void doAttack()
	{
		double pDoBigAttack = Math.random(); // probability
		if (pDoBigAttack < BIG_ATTACK_CHANCE)
		{
			attackBurst();
		}
		else
		{
			attackStar();
		}
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

	/** Fires a shotgun blast of dumb projectiles in the direction of the player. **/
	public void attackBurst()
	{
		Entity target = getPlayer();
		for (int i = 0; i < BURST_ATTACK_PROJECTILE_COUNT; i++)
		{
			Point2D dir = target.getCenter().subtract(entity.getCenter());

			SpawnData data = new SpawnData(getEntity().getCenter());
			data.put("direction", dir);
			data.put("size", BURST_ATTACK_PROJECTILE_SIZE);
			data.put("speed", BURST_ATTACK_PROJECTILE_SPEED);
			getEntity().getWorld().addEntity(projFactory.spawnDumbProjectile(data));
		}
	}
}
