package main.java;

import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.component.CollidableComponent;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/** Should be used to spawn projectiles. **/
public class ProjectileFactory
{
	/** Size of the player's projectiles. **/
	private static final int PLAYER_PROJECTILE_SIZE = 5;

	/** Spawns a projectile fired by the player.
	 * Specify target using `data.put("target", Entity);`
	 * @param data Attributes to spawn the projectile with, eg. position.
	 * @return Projectile entity. **/
	@Spawns("player projectile")
	public Entity spawnPlayerProjectile(SpawnData data)
	{
		return Entities.builder()
					.from(data)
					.type(EntType.PROJECTILE)
					.viewFromNodeWithBBox(new Circle(0, 0, PLAYER_PROJECTILE_SIZE, Color.ORANGE))
					.with(new PlayerProjectileControl(data.get("target")))
					.with(new CollidableComponent(true))
					.build();
	}

	/** Spawns a dumb projectile fired by the boss.
	 * Specify size using `data.put("size", int);`
	 * Specify direction using `data.put("direction", Point2D);`
	 * Specify speed using `data.put("speed", int);`
	 * @param data Attributes to spawn the projectile with, eg. position.
	 * @return Projectile entity. **/
	@Spawns("dumb projectile")
	public Entity spawnDumbProjectile(SpawnData data)
	{
		return Entities.builder()
				.from(data)
				.type(EntType.BOSS_PROJECTILE)
				.viewFromNodeWithBBox(new Circle(0, 0, data.get("size"), Color.ORANGE))
				.with(new BaseProjectileControl(data.get("direction"), data.get("speed")))
				.with(new CollidableComponent(true))
				.build();
	}
}
