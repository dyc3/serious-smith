package main.java;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/** Used to spawn experience orbs. **/
public class XPFactory implements EntityFactory
{
	/** The size of the experience orbs. **/
	private static final int XP_ORB_SIZE = 3;

	/** Spawns an experience orb.
	 * @param data Attributes to spawn the projectile with, eg. position.
	 * @return XP orb entity. **/
	@Spawns("xp_orb")
	public Entity spawnXpOrb(SpawnData data)
	{
		return Entities.builder()
				.from(data)
				.type(EntType.XP_ORB)
				.viewFromNodeWithBBox(new Circle(0, 0, XP_ORB_SIZE, Color.GREEN))
				.with(new CollidableComponent(true))
				.build();
	}

	/** Spawns an experience orb at a random position. **/
	public static void spawnRandomXpOrb(GameWorld world, Point2D playerPos)
	{
		Point2D spawn = FXGLMath.randomPoint(new Rectangle2D(
				playerPos.getX() - 500,
				playerPos.getY() - 500,
				1000,1000));
		SpawnData data = new SpawnData(spawn);
		Entity orb = new XPFactory().spawnXpOrb(data);
		world.addEntity(orb);
	}
}
