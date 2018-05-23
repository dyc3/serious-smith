package main.java;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

/** Makes experience orbs slowly gravitate towards the player. **/
public class XpOrbComponent extends Component
{
	/** Speed of experience orb movement. **/
	private static final double XP_ORB_SPEED = 5000;
	/** Maximum amount of experience that an orb can hold. */
	private static final int MAX_EXPERIENCE_PER_ORB = 20;

	/** Amount of experience that this orb contains. **/
	private int experience = 0;

	/** The player that the orb moves toward. **/
	private Entity player = null;

	/** Creates a new experience orb and calculates it's worth. **/
	public XpOrbComponent()
	{
		experience = FXGLMath.random(1, MAX_EXPERIENCE_PER_ORB);
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
