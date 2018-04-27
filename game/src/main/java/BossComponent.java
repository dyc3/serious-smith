package main.java;

import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/** Controls the boss. **/
public class BossComponent extends Component
{
	/** Speed of star attack projectiles. **/
	private static final int STAR_ATTACK_PROJECTILE_SPEED = 200;
	/** Size of star attack projectiles. **/
	private static final int STAR_ATTACK_PROJECTILE_SIZE = 5;

    private double time;

    /** The minimum amount of time between attacks in seconds. Actual attack intervals may vary
     * depending on previous attack performed. **/
    private double baseAttackInterval;
    private double timeUntilAttack;

    private ProjectileFactory projFactory;

    public BossComponent(ProjectileFactory factory)
    {
    	this.projFactory = factory;
        this.baseAttackInterval = 2;
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
        time += tpf;
        entity.translateX(Math.sin(time));

        if (timeUntilAttack <= 0)
        {
            // TODO: do attack
			attackStar();
            timeUntilAttack = baseAttackInterval;
        }
        else
        {
            timeUntilAttack -= tpf;
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

//				Entities.builder()
//						.type(EntType.BOSS_PROJECTILE)
//						.at(getEntity().getCenter())
//						.viewFromNodeWithBBox(new Circle(0, 0,
//								STAR_ATTACK_PROJECTILE_SIZE, Color.ORANGE))
//						.with(new BaseProjectileComponent(new Point2D(x, y),
//								STAR_ATTACK_PROJECTILE_SPEED))
//						.with(new CollidableComponent(true))
//						.buildAndAttach(getEntity().getWorld());
				SpawnData data = new SpawnData(getEntity().getCenter());
				data.put("direction", new Point2D(x, y));
				data.put("size", STAR_ATTACK_PROJECTILE_SIZE);
				data.put("speed", STAR_ATTACK_PROJECTILE_SPEED);
//				getEntity().getWorld().spawn("proj_dumb", data);
				getEntity().getWorld().addEntity(projFactory.spawnDumbProjectile(data));
			}
		}
	}
}
