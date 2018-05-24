package main.java;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import javafx.geometry.Point2D;
import javafx.scene.effect.Glow;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
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
	/** Number of star attack projectiles. **/
	private static final int STAR_ATTACK_PROJECTILE_COUNT = 8;
	/** Time (in seconds) between firing stars. **/
	private static final double STAR_ATTACK_INTERVAL = 0.5;
	/** Maximum number of stars to fire when doing star attack. **/
	private static final int STAR_ATTACK_MAX = 10;
	/** Number of projectiles to fire in burst attack. **/
	private static final int BURST_ATTACK_PROJECTILE_COUNT = 20;
	/** Speed of burst attack projectiles. **/
	private static final int BURST_ATTACK_PROJECTILE_SPEED = 400;
	/** Size of burst attack projectiles. **/
	private static final int BURST_ATTACK_PROJECTILE_SIZE = 7;
	/** Spread of burst attack projectiles. **/
	private static final int BURST_ATTACK_PROJECTILE_SPREAD = 20;
	/** Speed of ram attack. **/
	private static final int RAM_ATTACK_SPEED = 600;
	/** Maximum duration (in seconds) of ram attack. **/
	private static final int RAM_ATTACK_DURATION = 2;
	/** Damage of ram attack. **/
	public static final int RAM_ATTACK_DAMAGE = 40;
  /** Camera shake factor on collision with player. **/
	public static final float RAM_ATTACK_CAMERA_SHAKE = 5;
	/** Number of beams in the laser attack. **/
	private static final int LASER_ATTACK_NUM_BEAMS = 2;
	/** Maximum duration (in seconds) of laser attack. **/
	private static final int LASER_ATTACK_DURATION = 12;
	/** Amount of damage to deal to the player each frame the collide with laser. **/
	public static final int LASER_ATTACK_DAMAGE = 1;
	/** Laser beam length. **/
	private static final int LASER_ATTACK_BEAM_LENGTH = 8000;
	/** Laser beam color. **/
	private static final Color LASER_ATTACK_BEAM_COLOR = Color.color(0.9, 0.1, 0.1);
	/** Glow strength of the lasers. Set to 0 to disable glow. **/
	private static final double LASER_ATTACK_BEAM_GLOW = 0.8;

	/** The probability of doing a big attack. **/
	private static final double BIG_ATTACK_CHANCE = 0.5;

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
		if (FXGLMath.randomBoolean(BIG_ATTACK_CHANCE))
		{
			return FXGLMath.random(new BossAttack[] {
					BossAttack.RAM,
					BossAttack.BURST,
					BossAttack.LASER}).get();
		}
		else
		{
			return BossAttack.LASER;
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

	/** Gets the current attack.
	 * @return The current attack **/
	public BossAttack getCurrentAttack()
	{
		return currentAttack;
	}

	/** Gets Ram attack damages with any damage modifiers applied.
	 * @return Ram attack damage **/
	public int getRamAttackDamage()
	{
		return RAM_ATTACK_DAMAGE;
	}

	/** Finds the method that the current attack uses, and execute it with parameters.
	 * @param tpf Time per frame. **/
    public void doAttack(double tpf)
	{
		final List<Method> methods = new ArrayList<Method>();
		Class<?> klass = this.getClass();
		// iterate though hierarchy in order to retrieve methods from above the current instance
		while (klass != Object.class)
		{
			// iterate though the list of methods declared in the class represented by klass variable,
			// and add those annotated with the specified annotation
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
	public void endAttack()
	{
		timeUntilAttack = baseAttackInterval;
		currentAttack = null;
		if (_lasers != null)
		{
			for (Entity e : _lasers)
			{
				e.removeFromWorld();
			}
			_lasers = null;
		}
	}

	/** Used to keep track of how many stars have fired during the current star attack. **/
	private int _starsFired = 0;

	/** Fires 8 dumb projectiles around the boss at intervals.
	 * @param tpf Time per frame. **/
    @HandlesAttack(attack = BossAttack.STAR)
    public void attackStar(double tpf)
	{
		int targetFired = (int)(attackTime / STAR_ATTACK_INTERVAL);

		while (_starsFired < targetFired)
		{
			for (Point2D dir : Utils.pointsOnCircle(1, STAR_ATTACK_PROJECTILE_COUNT))
			{
				SpawnData data = new SpawnData(getEntity().getCenter());
				data.put("direction", dir);
				data.put("size", STAR_ATTACK_PROJECTILE_SIZE);
				data.put("speed", STAR_ATTACK_PROJECTILE_SPEED);
				getEntity().getWorld().addEntity(projFactory.spawnDumbProjectile(data));
			}

			_starsFired++;
		}

		if (targetFired >= STAR_ATTACK_MAX)
		{
			endAttack();
			_starsFired = 0;
		}
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

	/** The direction the boss will ram towards. **/
	private Point2D _ramDirection = null;

	/** Attempts to ram the player.
	 * @param tpf Time per frame. **/
	@HandlesAttack(attack = BossAttack.RAM)
	public void attackRam(double tpf)
	{
		if (_ramDirection == null)
		{
			_ramDirection = getPlayer().getCenter().subtract(entity.getCenter());
		}
		getEntity().translateTowards(entity.getPosition().add(_ramDirection), RAM_ATTACK_SPEED * tpf);

		if (attackTime >= RAM_ATTACK_DURATION)
		{
			endAttack();
			_ramDirection = null;
		}
	}

	/** Keep track of lasers used in the laser attack. **/
	private Entity[] _lasers = null;
	/** 4 lasers matching the width of the boss, fired in each cardinal direction.
	 * @param tpf Time per frame. **/
	@HandlesAttack(attack = BossAttack.LASER)
	public void attackLaser(double tpf)
	{
		if (_lasers == null)
		{
			_lasers = new Entity[LASER_ATTACK_NUM_BEAMS];
			for (int i = 0; i < LASER_ATTACK_NUM_BEAMS; i++)
			{
				double width = entity.getWidth();
				Rectangle beam = new Rectangle(0, 0, width, LASER_ATTACK_BEAM_LENGTH);
				LinearGradient gradient = new LinearGradient(0, 0,
						1, 0,
						true,
						CycleMethod.NO_CYCLE,
						new Stop(0, LASER_ATTACK_BEAM_COLOR),
						new Stop(0.4, Color.WHITE),
						new Stop(0.6, Color.WHITE),
						new Stop(1, LASER_ATTACK_BEAM_COLOR));
				beam.setFill(gradient);
				if (LASER_ATTACK_BEAM_GLOW > 0)
				{
					beam.setEffect(new Glow(LASER_ATTACK_BEAM_GLOW));
				}

				_lasers[i] = Entities.builder()
						.type(EntType.BOSS_LASER)
						.viewFromNodeWithBBox(beam)
						.renderLayer(new RenderLayer()
						{
							@Override
							public String name()
							{
								return "lasers";
							}

							@Override
							public int index()
							{
								return entity.getRenderLayer().index() - 1;
							}
						})
						.with(new CollidableComponent(true))
						.buildAndAttach(entity.getWorld());
			}
		}

		for (int i = 0; i < _lasers.length; i++)
		{
			double angle = (360.0 / (_lasers.length * 2)) * i + (Math.max(0, attackTime - 2) * 10) % 360;
			Point2D targetPos = entity.getCenter();
			targetPos = targetPos.subtract(entity.getWidth() / 2, LASER_ATTACK_BEAM_LENGTH / 2);
			_lasers[i].setPosition(targetPos);
			_lasers[i].setRotation(angle);
		}

		if (attackTime > LASER_ATTACK_DURATION)
		{
			endAttack();
		}
	}
}
