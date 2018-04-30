package main.java;

import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.input.Input;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class PlayerComponent extends Component
{
    /** Movement speed. **/
    private final static double DEFAULT_MOVE_SPEED = 200;
    /** Time between auto fire in seconds. **/
    private final static double DEFAULT_FIRE_INTERVAL = 0.25;
    /** Distance to travel while dashing. **/
    private final static double DASH_DISTANCE = 200;
    /** Speed while dashing. **/
    private final static double DASH_SPEED = 1200;
    /** Minimum time between dashes in seconds. **/
    private final static double DASH_COOLDOWN = 1;

    private double speed;
    private Input input;
	private ProjectileFactory projFactory;
	private double timeToFire = 0;
    private double fireInterval; // seconds between firing projectiles

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

        if (input.isHeld(KeyCode.SHIFT) && !dashing && dashCooldown <= 0)
        {
            dashing = true;
            Point2D move = getMoveDirection().multiply(DASH_DISTANCE);
            double dashX = entity.getPosition().getX() + move.getX();
            double dashY = entity.getPosition().getY() + move.getY();
            dashTarget = new Point2D(dashX, dashY);
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
//            Entities.builder()
//                    .type(EntType.PROJECTILE)
//                    .at(entity.getPosition())
//                    .viewFromNodeWithBBox(new Circle(0, 0, 5, Color.ORANGE))
//                    .with(new PlayerProjectileComponent(boss))
//                    .with(new CollidableComponent(true))
//                    .buildAndAttach(entity.getWorld());
            SpawnData data = new SpawnData(getEntity().getCenter());
//            data.put("direction", new Point2D(x, y));
//            data.put("size", STAR_ATTACK_PROJECTILE_SIZE);
            data.put("target", boss);
//             getEntity().getWorld().spawn("proj_player", data);
            getEntity().getWorld().addEntity(projFactory.spawnPlayerProjectile(data));
        }
    }
}
