package main.java;

import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;

public class BossControl extends Control
{
    private double time;

    /** The minimum amount of time between attacks in seconds. Actual attack intervals may vary
     * depending on previous attack performed. **/
    private double baseAttackInterval;
    private double timeUntilAttack;

    public BossControl()
    {
        this.baseAttackInterval = 2;
        this.timeUntilAttack = baseAttackInterval;
    }

    public BossControl(double baseAttackInterval)
    {
        this.baseAttackInterval = baseAttackInterval;
        this.timeUntilAttack = baseAttackInterval;
    }

    @Override
    public void onUpdate(Entity entity, double tpf)
    {
        time += tpf;
        entity.translateX(Math.sin(time));

        if (timeUntilAttack <= 0)
        {
            // TODO: do attack
            timeUntilAttack = baseAttackInterval;
        }
        else
        {
            timeUntilAttack -= tpf;
        }
    }
}
