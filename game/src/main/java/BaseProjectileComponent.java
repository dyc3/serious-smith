package main.java;

import com.almasb.fxgl.extra.entity.components.ProjectileComponent;
import javafx.geometry.Point2D;

/** Base projectile class, from which all other projectiles should be extended from. **/
public class BaseProjectileComponent extends ProjectileComponent
{
    /** Default value of baseDamage. **/
    private static final int DEFAULT_BASE_DAMAGE = 4;
	/** Default value of critChance. **/
	private static final double DEFAULT_CRIT_CHANCE = 0.1;
	/** Maximum lifetime of projectiles (in seconds). **/
	private static final double MAX_LIFETIME = 60;

	/** The base damage the projectile will deal on collision. **/
    private int baseDamage;
    /** Probability that this projectile does critical damage. **/
    private double critChance;
    /** Time (in seconds) that this projectile has existed. **/
    private double lifetime = 0;

	/** Creates a new instance of BaseProjectileComponent.
	 * @param direction The direction to move the projectile.
	 * @param speed How many units to move the projectile every second. **/
	public BaseProjectileComponent(Point2D direction, int speed)
    {
        super(direction, speed);
        this.baseDamage = DEFAULT_BASE_DAMAGE;
        this.critChance = DEFAULT_CRIT_CHANCE;
    }

	/** Creates a new instance of BaseProjectileComponent.
	 * @param direction The direction to move the projectile.
	 * @param speed How many units to move the projectile every second.
	 * @param critChance A probability of getting a critical hit. **/
    public BaseProjectileComponent(Point2D direction, int speed, double critChance)
    {
        super(direction, speed);
        this.critChance = critChance;
    }

	/** Update the projectile every tick.
	 * @param tpf Time per frame. **/
    @Override
    public void onUpdate(double tpf)
    {
        super.onUpdate(tpf);

        this.setDirection(calcVector());

        lifetime += tpf;
        if (lifetime > MAX_LIFETIME)
		{
			entity.removeFromWorld();
		}
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

    /** Gets the base damage that this projectile does on collision.
	 * @return Base damage. **/
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
