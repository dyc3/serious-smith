package main.java;

import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.component.HealthComponent;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import main.java.ProjectileControl;

public class PlayerControl extends Control
{
    private double speed;
    private Input input;
    private double timeToFire = 0;
    private double fireInterval; // seconds between firing projectiles

    private Entity boss;

    public PlayerControl(Input input)
    {
        speed = 200;
        this.input = input;
        fireInterval = 0.25;
    }

    @Override
    public void onUpdate(Entity entity, double tpf)
    {
        if (boss == null)
        {
            boss = getEntity().getWorld().getEntityByID("boss", 0).get();
        }

        if (input.isHeld(KeyCode.W))
        {
            entity.translateY(-speed * tpf);
        }
        else if (input.isHeld(KeyCode.S))
        {
            entity.translateY(speed * tpf);
        }

        if (input.isHeld(KeyCode.A))
        {
            entity.translateX(-speed * tpf);
        }
        else if (input.isHeld(KeyCode.D))
        {
            entity.translateX(speed * tpf);
        }

        // projectile firing
        timeToFire -= tpf;
        if (input.isHeld(KeyCode.SPACE) && timeToFire <= 0)
        {
            timeToFire = fireInterval;
            Entities.builder()
                    .type(EntType.PROJECTILE)
                    .at(entity.getPosition())
                    .viewFromNodeWithBBox(new Circle(-5, -5, 5, Color.ORANGE))
                    .with(new ProjectileControl(boss))
                    .with(new CollidableComponent(true))
                    .buildAndAttach(entity.getWorld());
        }
    }
}
