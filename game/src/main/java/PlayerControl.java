package main.java;

import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;

public class PlayerControl extends Control
{
    private double speed;
    private Input input;

    public PlayerControl(Input input)
    {
        speed = 200;
        this.input = input;
    }

    @Override
    public void onUpdate(Entity entity, double tpf)
    {
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
    }
}
