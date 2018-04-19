package game;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.HealthComponent;
import com.almasb.fxgl.entity.view.EntityView;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.entity.component.IrremovableComponent;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.scene.Viewport;
import com.almasb.fxgl.ui.ProgressBar;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import main.java.PlayerControl;
import main.java.BossControl;
import main.java.ParentFollowerControl;

import java.util.Map;

public class GameRunner extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(600);
        settings.setHeight(600);
        settings.setTitle("Basic Game App");
        settings.setVersion("0.1");
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Entity player;
    private Entity boss;

    @Override
    protected void initGame() {

        Rectangle bg0 = new Rectangle(-getWidth() * 500, -getHeight() * 500, getWidth() * 1000, getHeight() * 1000);
        bg0.setFill(Color.color(0.2, 0.2, 0.2, 1));
        bg0.setBlendMode(BlendMode.DARKEN);

        EntityView bg = new EntityView();
        bg.addNode(bg0);

        drawBgGrid(bg, bg0);

        // we add IrremovableComponent because regardless of the level
        // the background and screen bounds stay in the game world
        Entities.builder()
                .viewFromNode(bg)
                .with(new IrremovableComponent())
                .buildAndAttach(getGameWorld());

        player = Entities.builder()
                .at(-25/2, 300)
                .viewFromNode(new Rectangle(25, 25, Color.BLUE))
                .with(new HealthComponent(100))
                .with(new PlayerControl(getInput()))
                .buildAndAttach(getGameWorld());

        boss = Entities.builder()
                .at(-50, -50)
                .viewFromNode(new Rectangle(100, 100, Color.RED))
                .with(new HealthComponent(100))
                .with(new BossControl())
                .buildAndAttach(getGameWorld());

        Viewport viewport = getGameScene().getViewport();
        viewport.bindToEntity(player, (getWidth()/2)-(player.getWidth()/2), (getHeight()/2)-(player.getHeight()/2));
    }

    @Override
    protected void initInput() {

    }

    @Override
    protected void initUI() {
        ProgressBar pbarPlayerHealth = new ProgressBar();
        pbarPlayerHealth.setTranslateX(50);
        pbarPlayerHealth.setTranslateY(100);
        pbarPlayerHealth.makeHPBar();
        pbarPlayerHealth.currentValueProperty().bind(player.getComponent(HealthComponent.class).valueProperty());

        getGameScene().addUINode(pbarPlayerHealth);

        ProgressBar pbarBossHealth = new ProgressBar();
        pbarBossHealth.setWidth(120);
        pbarBossHealth.makeHPBar();
        pbarBossHealth.currentValueProperty().bind(boss.getComponent(HealthComponent.class).valueProperty());

        Entities.builder()
                .viewFromNode(pbarBossHealth)
                .with(new IrremovableComponent())
                .with(new ParentFollowerControl(boss, -10, -20))
                .buildAndAttach(getGameWorld());
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {

    }

    private void drawBgGrid(EntityView bg, Rectangle background) {
        int gridSize = 50;

        for (double x = background.getX(); x < background.getX() + background.getWidth(); x += gridSize)
        {
            Line line = new Line(x, background.getY(), x, background.getY() + background.getHeight());
            line.setStroke(Color.color(0.3, 0.3, 0.3, 1));
            line.setStrokeWidth(1);
            bg.addNode(line);
        }

        for (double y = background.getY(); y < background.getY() + background.getHeight(); y += gridSize)
        {
            Line line = new Line(background.getX(), y, background.getX() + background.getWidth(), y);
            line.setStroke(Color.color(0.3, 0.3, 0.3, 1));
            line.setStrokeWidth(1);
            bg.addNode(line);
        }
    }
}
