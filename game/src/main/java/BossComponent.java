package main.java;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
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
	/** Spread of burst attack projectiles. **/
	private static final int BURST_ATTACK_PROJECTILE_SPREAD = 20;

	/** The probability of doing a big attack. **/
	private static final double BIG_ATTACK_CHANCE = 0.1;

    /** The minimum amount of time between attacks in seconds. Actual attack intervals may vary
     * depending on previous attack performed. **/
    private double baseAttackInterval;
    /** Time (in seconds) until the next attack. **/
    private double timeUntilAttack;

    /** Indicates the currently executing attack. Equals `null` when not attacking. **/
    private BossAttack currentAttack = null;
    /** Time (in seconds) since the start of the current attack.
	 * Each attack must determine when it ends on it's own. **/
    private double attackTime = 0;

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
        if (currentAttack != null)
        {
        	doAttack(tpf);
			attackTime += tpf;
		}
        else
        {
            timeUntilAttack -= tpf;
            attackTime = 0;

            if (timeUntilAttack <= 0)
			{
				timeUntilAttack = 0;
				currentAttack = nextAttack();

				System.out.println("Start attack: " + currentAttack);
			}
        }
    }

    /** Determine the next attack.
	 * @return The next attack to perform. **/
    private BossAttack nextAttack()
	{
		if (Math.random() < BIG_ATTACK_CHANCE)
		{
			return BossAttack.BURST;
		}
		else
		{
			return BossAttack.STAR;
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

    /** Finds the method that the current attack uses, and execute it with parameters.
	 * @param tpf Time per frame. **/
    public void doAttack(double tpf)
	{
		final List<Method> methods = new ArrayList<Method>();
		Class<?> klass = this.getClass();
		while (klass != Object.class) // need to iterated thought hierarchy in order to retrieve methods from above the current instance
		{
			// iterate though the list of methods declared in the class represented by klass variable, and add those annotated with the specified annotation
			final List<Method> allMethods = new ArrayList<Method>(Arrays.asList(klass.getMethods()));
			for (final Method method : allMethods)
			{
				HandlesAttack handles = method.getAnnotation(HandlesAttack.class);
				if (handles != null && handles.attack() == currentAttack)
				{
					methods.add(method);
				}
			}
			// move to the upper class in the hierarchy in search for more methods
			klass = klass.getSuperclass();
		}

		System.out.println("found " + methods.size() + " methods");

		// execute all handler methods
		for (Method m : methods)
		{
			try
			{
				m.invoke(this, tpf);
			}
			catch (IllegalAccessException | InvocationTargetException e)
			{
				e.printStackTrace();
			}
		}
	}

	/** Ends the current attack. Each attack calls this method when they are done. **/
	private void endAttack()
	{
		timeUntilAttack = baseAttackInterval;
		currentAttack = null;
	}

    /** Fires 8 dumb projectiles around the boss.
	 * @param tpf Time per frame. **/
    @HandlesAttack(attack = BossAttack.STAR)
    public void attackStar(double tpf)
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

		endAttack();
	}

	/** Fires a shotgun blast of dumb projectiles in the direction of the player.
	 * @param tpf Time per frame. **/
	@HandlesAttack(attack = BossAttack.BURST)
	public void attackBurst(double tpf)
	{
		Entity target = getPlayer();
		for (int i = 0; i < BURST_ATTACK_PROJECTILE_COUNT; i++)
		{
			Point2D targetPos = Utils.randomizePoint(target.getCenter(), BURST_ATTACK_PROJECTILE_SPREAD);
			Point2D dir = targetPos.subtract(entity.getCenter());

			Point2D spawnAt = Utils.randomizePoint(getEntity().getCenter(), BURST_ATTACK_PROJECTILE_SPREAD);
			SpawnData data = new SpawnData(spawnAt);
			data.put("direction", dir);
			data.put("size", BURST_ATTACK_PROJECTILE_SIZE);
			data.put("speed", BURST_ATTACK_PROJECTILE_SPEED);
			getEntity().getWorld().addEntity(projFactory.spawnDumbProjectile(data));
		}

		endAttack();
	}
}
