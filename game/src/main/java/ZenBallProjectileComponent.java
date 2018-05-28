package main.java;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.entity.view.EntityView;
import javafx.geometry.Point2D;
import javafx.scene.effect.Glow;
import javafx.scene.shape.Circle;

public class ZenBallProjectileComponent extends BaseProjectileComponent
{
	private static final boolean USE_LERP_MODE = true;

	private Entity boss;
	private Entity player;
	private boolean passiveMode = true;
	private Point2D target;

	public ZenBallProjectileComponent(int damage)
	{
		super(new Point2D(0, 0), 0);
		setBaseDamage(damage);
	}

	@Override
	public void onUpdate(double tpf)
	{
		if (boss == null)
		{
			boss = entity.getWorld().getEntityByID("boss", 0).get();
		}
		if (player == null)
		{
			player = entity.getWorld().getEntityByID("player", 0).get();
		}

		if (target == null)
		{
			target = boss.getCenter();
		}

		if (passiveMode)
		{
			if (USE_LERP_MODE)
			{
				entity.setPosition(Utils.lerpPoint2D(entity.getPosition(), target, tpf * 10));
			}
			else
			{
				Point2D targetDir = target.subtract(entity.getCenter());
				Point2D dir = Utils.lerpPoint2D(getDirection(), targetDir, tpf);
				setDirection(dir);

				double dist = target.distance(entity.getCenter());
				double distDir = getDirection().distance(targetDir);
				double speed = dist * distDir * tpf * 6;
				setSpeed(speed);
			}
		}

		super.onUpdate(tpf);
	}

	public boolean isPassiveMode()
	{
		return passiveMode;
	}

	public void setPassiveMode(boolean value)
	{
		passiveMode = value;

		if (!passiveMode)
		{
			ViewComponent viewComponent = entity.getViewComponent();
			EntityView view = viewComponent.getView();
			view.setEffect(new Glow(BossComponent.ZEN_ATTACK_GLOW * 2));
			Circle c = (Circle)view.getNodes().get(0);
			c.setFill(BossComponent.ZEN_ATTACK_COLOR_ACTIVE);

			Point2D targetDir = player.getPosition().subtract(entity.getPosition());
			setSpeed(1500); // setSpeed must come before setDirection
			setDirection(targetDir);
		}
	}

	public void setTarget(Point2D pos)
	{
		target = pos;
	}
}
