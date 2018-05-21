package main.java;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

/** Makes experience orbs slowly gravitate towards the player. **/
public class XpOrbComponent extends Component
{
	/** Speed of experience orb movement. **/
	private static final double XP_ORB_SPEED = 5000;

	private Entity player = null;

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
}
