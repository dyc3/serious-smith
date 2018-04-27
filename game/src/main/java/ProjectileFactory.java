package main.java;

import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.CollidableComponent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/** Should be used to spawn projectiles. **/
public class ProjectileFactory implements EntityFactory
{
	/** Size of the player's projectiles. **/
	private static final int PLAYER_PROJECTILE_SIZE = 5;

	/** Spawns a projectile fired by the player.
	 * Specify target using `data.put("target", Entity);`
	 * @param data Attributes to spawn the projectile with, eg. position.
	 * @return Projectile entity. **/
	@Spawns("proj_player")
	public Entity spawnPlayerProjectile(SpawnData data)
	{
		return Entities.builder()
					.from(data)
					.type(EntType.PROJECTILE)
					.viewFromNodeWithBBox(new Circle(0, 0, PLAYER_PROJECTILE_SIZE, Color.ORANGE))
					.with(new PlayerProjectileComponent(data.get("target")))
					.with(new CollidableComponent(true))
					.build();
	}

	/** Spawns a dumb projectile fired by the boss.
	 * Specify size using `data.put("size", int);`
	 * Specify direction using `data.put("direction", Point2D);`
	 * Specify speed using `data.put("speed", int);`
	 * @param data Attributes to spawn the projectile with, eg. position.
	 * @return Projectile entity. **/
	@Spawns("proj_dumb")
	public Entity spawnDumbProjectile(SpawnData data)
	{
		int size = data.get("size");
		return Entities.builder()
				.from(data)
				.type(EntType.BOSS_PROJECTILE)
				.viewFromNodeWithBBox(new Circle(0, 0, size, Color.ORANGE))
				.with(new BaseProjectileComponent(data.get("direction"), data.get("speed")))
				.with(new CollidableComponent(true))
				.build();
	}
}
