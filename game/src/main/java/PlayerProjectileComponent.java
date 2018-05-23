package main.java;

import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;

/** A projectile control that homes in on it's target. **/
public class PlayerProjectileComponent extends BaseProjectileComponent
{
    /** Speed of the player's projectiles. **/
    private static final int PLAYER_PROJECTILE_SPEED = 1000;


    /** The entity that the projectile will move toward. **/
    private Entity target;
    /** The player's attributes. **/
    private PlayerComponent player = null;

    /** A projectile that homes in on it's target.
     * @param target The entity to target. **/
    public PlayerProjectileComponent(Entity target, int damage, double critChance, double critMultiplier)
    {
        super(new Point2D(0, 0),
				PLAYER_PROJECTILE_SPEED,
				damage,
				critChance,
				critMultiplier);
        this.target = target;
    }

    /** Update the projectile every tick.
	 * @param tpf Time per frame. **/
    @Override
    public void onUpdate(double tpf)
    {
        super.onUpdate(tpf);

        if (player == null)
		{
			player = entity.getWorld().getEntityByID("player", 0).get().getComponent(PlayerComponent.class);
			setBaseDamage(player.getDamage());
		}

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
