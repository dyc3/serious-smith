package main.java;

import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;

public class PlayerProjectileControl extends BaseProjectileControl
{
    private Entity target;

    public PlayerProjectileControl(Entity target)
    {
        super(new Point2D(0, 0), 1000);
        this.target = target;
    }

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
