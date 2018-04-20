package main.java;

import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;

public class ProjectileControl extends com.almasb.fxgl.entity.control.ProjectileControl
{
    private Entity target;

    public ProjectileControl(Entity target)
    {
        super(new Point2D(0,0), 100);
        this.target = target;
    }

    @Override
    public void onUpdate(Entity entity, double tpf)
    {
        super.onUpdate(entity, tpf);

        this.setDirection(calcVector());
        this.setSpeed(1000);
    }

    public Point2D calcVector()
    {
        Point2D targetPos = target.getCenter();
        return targetPos.subtract(getEntity().getPosition());
    }
}
