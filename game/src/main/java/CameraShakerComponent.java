package main.java;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.component.Component;

/** Used for camera shake. Requires ParentFollowerComponent to be present on the entity. **/
public class CameraShakerComponent extends Component
{
	/** Shake factor. **/
	private double shakeAmount = 0;

	/** Update every tick.
	 * @param tpf Time per frame. **/
	@Override
	public void onUpdate(double tpf)
	{
		ParentFollowerComponent follow = entity.getComponent(ParentFollowerComponent.class);
		follow.setOffset(FXGLMath.random(-1, 1) * shakeAmount, FXGLMath.random(-1, 1) * shakeAmount);
		if (shakeAmount > 0)
		{
			shakeAmount -= tpf * 10;
		}
		else
		{
			shakeAmount = 0;
		}
	}

	/** Sets the shake factor.
	 * @param amount Shake factor **/
	public void setShake(float amount)
	{
		shakeAmount = amount;
	}

	/** Adds shake on top of the current shake factor.
	 * @param amount Shake factor **/
	public void addShake(float amount)
	{
		shakeAmount += amount;
	}

	/** Get the shake factor.
	 * @return double >= 0 **/
	public double getShake()
	{
		return shakeAmount;
	}
}
