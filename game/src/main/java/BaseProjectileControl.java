package main.java;

import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;

public class BaseProjectileControl extends com.almasb.fxgl.entity.control.ProjectileControl
{
    /** Default value of critChance. **/
    private final static double DEFAULT_CRIT_CHANCE = 0.1;

    private int baseDamage = 4;
    /** Probability that this projectile is does critical damage. **/
    private double critChance;

    public BaseProjectileControl(Point2D direction, int speed)
    {
        super(direction, speed);
        this.critChance = DEFAULT_CRIT_CHANCE;
    }

    public BaseProjectileControl(Point2D direction, int speed, double critChance)
    {
        super(direction, speed);
        this.critChance = critChance;
    }

    @Override
    public void onUpdate(Entity entity, double tpf)
    {
        super.onUpdate(entity, tpf);

        this.setDirection(calcVector());
    }

    /** Calculate the direction the projectile will go in every tick. **/
    public Point2D calcVector()
    {
        return this.getDirection();
    }

    /** Sets the base damage that this projectile does on collision.
     * @param damage An integer. **/
    public void setBaseDamage(int damage)
    {
        baseDamage = damage;
    }

    /** Gets the base damage that this projectile does on collision. **/
    public int getBaseDamage()
    {
        return baseDamage;
    }

    /** Sets the chance that this projectile does critical damage on collision.
     * @param chance A double from 0 to 1, representing a probability. **/
    public void setCritChance(double chance)
    {
        critChance = chance;
    }

    /** Gets the chance that this projectile does critical damage on collision. **/
    public double getCritChance()
    {
        return critChance;
    }

    /** Calculate the damage this projectile will do on collision. **/
    public int calcDamage()
    {
        int mul = Math.random() <= getCritChance() ? 2 : 1;
        return baseDamage * mul;
    }
}
