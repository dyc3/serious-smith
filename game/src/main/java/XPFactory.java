package main.java;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/** Used to spawn experience orbs. **/
public class XPFactory implements EntityFactory
{
	/** The size of the experience orbs. **/
	private static final int XP_ORB_SIZE = 3;
	/** How big of a square area around the player to spawn experience orbs in. **/
	private static final int XP_SPAWN_AREA_SIZE = 1000;

	/** Spawns an experience orb.
	 * @param data Attributes to spawn the projectile with, eg. position.
	 * @return XP orb entity. **/
	@Spawns("xp_orb")
	public Entity spawnXpOrb(SpawnData data)
	{
		return Entities.builder()
				.from(data)
				.type(EntType.XP_ORB)
				.viewFromNodeWithBBox(new Circle(0, 0, XP_ORB_SIZE, Color.LIMEGREEN))
				.with(new CollidableComponent(true))
				.with(new XpOrbComponent())
				.build();



	}

	/** Spawns an experience orb at a random position.
	 * @param world World to spawn the orb in.
	 * @param playerPos Position of the player. **/
	public static void spawnRandomXpOrb(GameWorld world, Point2D playerPos)
	{
		Point2D spawn = FXGLMath.randomPoint(new Rectangle2D(
				playerPos.getX() - (XP_SPAWN_AREA_SIZE / 2),
				playerPos.getY() - (XP_SPAWN_AREA_SIZE / 2),
				XP_SPAWN_AREA_SIZE, XP_SPAWN_AREA_SIZE));
		SpawnData data = new SpawnData(spawn);
		Entity orb = new XPFactory().spawnXpOrb(data);

		/*ParticleEmitter emitter = ParticleEmitters.newFireEmitter();
		emitter.setStartColor(Color.LIGHTYELLOW);
		emitter.setEndColor(Color.RED);
		emitter.setBlendMode(BlendMode.SRC_OVER);
		emitter.setSize(5, 10);
		emitter.setEmissionRate(1);

		orb.addComponent(new ParticleComponent(emitter));*/

		world.addEntity(orb);


	}
}
