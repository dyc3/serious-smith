package main.java;

import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class XPFactory implements EntityFactory
{
	private int XP_ORB_SIZE = 3;

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
}
