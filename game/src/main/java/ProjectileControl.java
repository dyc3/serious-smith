package main.java;

import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;

public class ProjectileControl extends com.almasb.fxgl.entity.control.ProjectileControl
{
    /** Default value of critChance. **/
    private final static double DEFAULT_CRIT_CHANCE = 0.1;

    private int baseDamage = 2;
    /** Probability that this projectile is does critical damage. **/
    private double critChance;
    private Entity target;

    public ProjectileControl(Entity target)
    {
        super(new Point2D(0, 0), 100);
        this.target = target;
        this.critChance = DEFAULT_CRIT_CHANCE;
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

    public void setBaseDamage(int damage)
    {
        baseDamage = damage;
    }

    public int getBaseDamage()
    {
        return baseDamage;
    }

    public double getCritChance()
    {
        return critChance;
    }

    public int calcDamage()
    {
        int mul = Math.random() <= critChance ? 2 : 1;
        return baseDamage * mul;
    }
}
