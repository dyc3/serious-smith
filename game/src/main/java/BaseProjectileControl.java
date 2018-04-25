package main.java;

import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;

/** Base projectile class, from which all other projectiles should be extended from. **/
public class BaseProjectileControl extends com.almasb.fxgl.entity.control.ProjectileControl
{
    /** Default value of baseDamage. **/
    private static final int DEFAULT_BASE_DAMAGE = 4;
	/** Default value of critChance. **/
	private static final double DEFAULT_CRIT_CHANCE = 0.1;

	/** The base damage the projectile will deal on collision. **/
    private int baseDamage;
    /** Probability that this projectile does critical damage. **/
    private double critChance;

	/** Creates a new instance of BaseProjectileControl.
	 * @param direction The direction to move the projectile.
	 * @param speed How many units to move the projectile every second. **/
	public BaseProjectileControl(Point2D direction, int speed)
    {
        super(direction, speed);
        this.baseDamage = DEFAULT_BASE_DAMAGE;
        this.critChance = DEFAULT_CRIT_CHANCE;
    }

	/** Creates a new instance of BaseProjectileControl.
	 * @param direction The direction to move the projectile.
	 * @param speed How many units to move the projectile every second.
	 * @param critChance A probability of getting a critical hit. **/
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

    /** Calculate the direction the projectile will go in every tick.
	 * @return The direction the projectile will go in relative to itself. **/
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

    /** Gets the chance that this projectile does critical damage on collision.
	 * @return Probability of critical hit. **/
    public double getCritChance()
    {
        return critChance;
    }

    /** Calculate the damage this projectile will do on collision.
	 * @return Amount of damage to deal. **/
    public int calcDamage()
    {
        int mul = Math.random() <= getCritChance() ? 2 : 1;
        return baseDamage * mul;
    }
}
