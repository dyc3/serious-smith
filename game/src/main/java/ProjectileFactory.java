package main.java;

import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import javafx.scene.effect.BlendMode;
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
		ParticleEmitter playerProjectileEmitter = ParticleEmitters.newFireEmitter();
		playerProjectileEmitter.setStartColor(Color.LIGHTYELLOW);
		playerProjectileEmitter.setEndColor(Color.GREENYELLOW);
		playerProjectileEmitter.setBlendMode(BlendMode.SRC_ATOP);
		playerProjectileEmitter.setSize(2, 4);
		playerProjectileEmitter.setEmissionRate(.5);

		return Entities.builder()
				    .with(new ParticleComponent(playerProjectileEmitter))
					.from(data)
					.type(EntType.PROJECTILE)
					.viewFromNodeWithBBox(new Circle(0, 0, PLAYER_PROJECTILE_SIZE, Color.ORANGE))
					.viewFromTexture("projectile1.png")
					.with(new PlayerProjectileComponent(
							data.get("target"),
							data.get("damage"),
							data.get("critChance"),
							data.get("critMultiplier")))
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
