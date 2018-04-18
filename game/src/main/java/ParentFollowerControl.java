package main.java;

import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;

public class ParentFollowerControl extends Control {
    private Entity following;
    private double offsetX;
    private double offsetY;

    public ParentFollowerControl(Entity toFollow, double offsetX, double offsetY)
    {
        following = toFollow;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    @Override
    public void onUpdate(Entity entity, double tpf)
    {
        entity.setX(following.getX() + offsetX);
        entity.setY(following.getY() + offsetY);
    }
}
