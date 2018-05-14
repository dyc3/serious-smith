package main.java;

import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;

/** A projectile control that homes in on it's target. **/
public class PlayerProjectileComponent extends BaseProjectileComponent
{
    /** Speed of the player's projectiles. **/
    private static final int PLAYER_PROJECTILE_SPEED = 1000;
	/** Damage of the player's projectiles. **/
	private static final int PLAYER_PROJECTILE_DAMAGE = 4;
	/** Critical hit chance of the player's projectiles. **/
	private static final double PLAYER_PROJECTILE_CRIT_CHANCE = 0.1;
	/** Critical hit multiplier of the player's projectiles. **/
	private static final double PLAYER_PROJECTILE_CRIT_MULTIPLIER = 2;

    /** The entity that the projectile will move toward. **/
    private Entity target;

    /** A projectile that homes in on it's target.
     * @param target The entity to target. **/
    public PlayerProjectileComponent(Entity target)
    {
        super(new Point2D(0, 0),
				PLAYER_PROJECTILE_SPEED,
				PLAYER_PROJECTILE_DAMAGE,
				PLAYER_PROJECTILE_CRIT_CHANCE,
				PLAYER_PROJECTILE_CRIT_MULTIPLIER);
        this.target = target;
    }

    /** Update the projectile every tick.
	 * @param tpf Time per frame. **/
    @Override
    public void onUpdate(double tpf)
    {
        super.onUpdate(tpf);

        this.setDirection(calcVector());
        this.setSpeed(PLAYER_PROJECTILE_SPEED);
    }

	/** Calculate the direction the projectile will go in every tick.
	 * @return The direction the projectile will go in relative to itself. **/
    @Override
    public Point2D calcVector()
    {
        Point2D targetPos = target.getCenter();
        return targetPos.subtract(getEntity().getPosition());
    }
}
