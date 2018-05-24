package main.java;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

public class ParentFollowerComponent extends Component
{
    private Entity following;
    private double offsetX;
    private double offsetY;

    public ParentFollowerComponent(Entity toFollow, double offsetX, double offsetY)
    {
        following = toFollow;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    /** Update every tick.
     * @param tpf Time per frame. **/
    @Override
    public void onUpdate(double tpf)
    {
        entity.setX(following.getX() + offsetX);
        entity.setY(following.getY() + offsetY);
    }

    /** Set the follower offset.
	 * @param x Offset on the X axis.
	 * @param y Offset on the Y axis. **/
    public void setOffset(double x, double y)
    {
        offsetX = x;
        offsetY = y;
    }
}
