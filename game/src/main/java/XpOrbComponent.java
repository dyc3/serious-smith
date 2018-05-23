package main.java;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

/** Makes experience orbs slowly gravitate towards the player. **/
public class XpOrbComponent extends Component
{
	/** Speed of experience orb movement. **/
	private static final double XP_ORB_SPEED = 5000;
	/** Maximum amount of experience that an orb can hold. */
	private static final int MAX_EXPERIENCE_PER_ORB = 20;
	/** Initial speed of ejection. **/
	private static final double XP_ORB_EJECTION_SPEED = 7 ;
	/** Ejection speed is decreased every frame by an amount multiplied by this factor. **/
	private static final double XP_ORB_EJECTION_DECAY = 10;

	/** Amount of experience that this orb contains. **/
	private int experience = 0;

	/** The player that the orb moves toward. **/
	private Entity player = null;

	/** When an orb spawns it gets "ejected" from it's spawn position. This is the speed the orb will travel. **/
	private double ejectionSpeed;
	/** When an orb spawns it gets "ejected" from it's spawn position. This is the direction the orb will travel. **/
	private Point2D ejectionDirection;


	/** Creates a new experience orb and calculates it's worth. **/
	public XpOrbComponent()
	{
		experience = FXGLMath.random(1, MAX_EXPERIENCE_PER_ORB);
		ejectionSpeed = XP_ORB_EJECTION_SPEED;
		ejectionDirection = Utils.randomizePoint(new Point2D(0, 0), 2);
	}

	/** Update every tick.
	 * @param tpf Time per frame. **/
	@Override
	public void onUpdate(double tpf)
	{
		if (player == null)
		{
			player = getEntity().getWorld().getEntityByID("player", 0).get();
		}

		if (ejectionSpeed > 0)
		{
			entity.translate(ejectionDirection.multiply(ejectionSpeed));
			ejectionSpeed -= tpf * XP_ORB_EJECTION_DECAY;
		}
		else
		{
			ejectionSpeed = 0;
		}

		double dist = entity.getCenter().distance(player.getCenter());
		getEntity().translateTowards(player.getCenter(), XP_ORB_SPEED / (dist / 2.0) * tpf);
	}

	/** Gets how much experience this orb is worth.
	 * @return integer > 0 **/
	public int getExperience()
	{
		return experience;
	}
}
