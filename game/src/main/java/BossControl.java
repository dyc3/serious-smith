package main.java;

import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;

public class BossControl extends Control {
    private double time;

    @Override
    public void onUpdate(Entity entity, double tpf) {
        time += tpf;
        entity.translateX(Math.sin(time));
    }
}
