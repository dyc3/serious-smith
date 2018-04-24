package main.java;

import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;

/** A projectile control that homes in on it's target. **/
public class PlayerProjectileControl extends BaseProjectileControl
{
    private Entity target;

    /** A projectile control that homes in on it's target.
     * @param target The entity to target. **/
    public PlayerProjectileControl(Entity target)
    {
        super(new Point2D(0, 0), 1000);
        this.target = target;
    }

    /** A projectile control that homes in on it's target.
     * @param target The entity to target.
     * @param critChance Probability that this projectile does critical damage. **/
    public PlayerProjectileControl(Entity target, double critChance)
    {
        super(new Point2D(0, 0), 1000, critChance);
        this.target = target;
    }

    @Override
    public void onUpdate(Entity entity, double tpf)
    {
        super.onUpdate(entity, tpf);

        this.setDirection(calcVector());
        this.setSpeed(1000);
    }

    @Override
    public Point2D calcVector()
    {
        Point2D targetPos = target.getCenter();
        return targetPos.subtract(getEntity().getPosition());
    }
}
