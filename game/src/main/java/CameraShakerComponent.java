package main.java;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.component.Component;

public class CameraShakerComponent extends Component
{
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

	public void setShake(float amount)
	{
		shakeAmount = amount;
	}

	public void addShake(float amount)
	{
		shakeAmount += amount;
	}
}
