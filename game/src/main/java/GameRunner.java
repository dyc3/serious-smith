package main.java;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.extra.entity.components.HealthComponent;
import com.almasb.fxgl.entity.components.IDComponent;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.entity.view.EntityView;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.scene.Viewport;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.ProgressBar;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.Map;

public class GameRunner extends GameApplication
{
	/** Boss starting health. **/
	private static final int BOSS_HEALTH = 1000000;
	/** Boss health bar offset on the x axis. **/
	private static final int BOSS_HEALTH_BAR_OFFSET_X = -10;
	/** Boss health bar offset on the y axis. **/
	private static final int BOSS_HEALTH_BAR_OFFSET_Y = -20;

	/** Chance to receive some experience when the player hits the boss. **/
	private static final double XP_ON_HIT_CHANCE = 0.4;

    private Entity player;
    private Entity boss;

    /** Program entry **/
    public static void main(String[] args)
    {
        launch(args);
    }

	/** Initialize the game window and load settings. **/
	@Override
    protected void initSettings(GameSettings settings)
    {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Serious Smith");
        settings.setVersion("0.1");
    }

    /** Initialize the game. Sets up background and builds player and boss entities. Binds camera to player. **/
    @Override
    protected void initGame()
    {
        // set up factories
		ProjectileFactory projectileFactory = new ProjectileFactory();
		getGameWorld().addEntityFactory(projectileFactory);

		// set up background
        Rectangle bg0 = new Rectangle(-getWidth() * 500, -getHeight() * 500,
                getWidth() * 1000, getHeight() * 1000);
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

        Rectangle rectPlayer = new Rectangle(0, 0, 25, 25);
        rectPlayer.setFill(Color.BLUE);
        player = Entities.builder()
                .type(EntType.PLAYER)
                .at(0, 300)
                .viewFromNodeWithBBox(rectPlayer)
                .with(new HealthComponent(100))
				.with(new IDComponent("player", 0))
				.with(new PlayerComponent(getInput(), projectileFactory))
                .with(new CollidableComponent(true))
                .buildAndAttach(getGameWorld());

        Rectangle rectBoss = new Rectangle(0, 0, 100, 100);
        rectBoss.setFill(Color.RED);
        boss = Entities.builder()
                .type(EntType.BOSS)
                .at(0, 0)
                .viewFromNodeWithBBox(rectBoss)
                .with(new HealthComponent(BOSS_HEALTH))
                .with(new BossComponent(projectileFactory))
                .with(new IDComponent("boss", 0))
                .with(new CollidableComponent(true))
                .buildAndAttach(getGameWorld());

        Viewport viewport = getGameScene().getViewport();
        viewport.bindToEntity(player,
                       (getWidth() / 2) - (player.getWidth() / 2),
                       (getHeight() / 2) - (player.getHeight() / 2));
    }

    @Override
    protected void initInput()
    {

    }

    /** Initializes UI elements, including health bars hovering over entities in the world. **/
    @Override
    protected void initUI()
    {
        ProgressBar pbarPlayerHealth = new ProgressBar();
        pbarPlayerHealth.setTranslateX(50);
        pbarPlayerHealth.setTranslateY(100);
        pbarPlayerHealth.makeHPBar();
        pbarPlayerHealth.currentValueProperty().bind(player.getComponent(HealthComponent.class).valueProperty());

		ProgressBar pbarPlayerXP = new ProgressBar();
		pbarPlayerXP.setTranslateX(50);
		pbarPlayerXP.setTranslateY(120);
		pbarPlayerXP.setFill(Color.color(0.2, 0.7, 1));
		pbarPlayerXP.setMinValue(0);
		pbarPlayerXP.setMaxValue(PlayerComponent.XP_PER_LEVEL);
		pbarPlayerXP.currentValueProperty().bind(player.getComponent(PlayerComponent.class).getXpProperty());

		getGameScene().addUINode(pbarPlayerHealth);
		getGameScene().addUINode(pbarPlayerXP);

        ProgressBar pbarBossHealth = new ProgressBar();
        pbarBossHealth.setWidth(120);
        pbarBossHealth.makeHPBar();
		pbarBossHealth.setMaxValue(BOSS_HEALTH);
		pbarBossHealth.setLabelVisible(true);
        pbarBossHealth.currentValueProperty().bind(boss.getComponent(HealthComponent.class).valueProperty());

        Entities.builder()
                .viewFromNode(pbarBossHealth)
                .with(new IrremovableComponent())
                .with(new ParentFollowerComponent(boss, BOSS_HEALTH_BAR_OFFSET_X, BOSS_HEALTH_BAR_OFFSET_Y))
                .buildAndAttach(getGameWorld());
    }

    @Override
    protected void initGameVars(Map<String, Object> vars)
    {

    }

    /** Draws grid lines so the player can know that they are moving.
     * @param bg EntityView to add the lines to
     * @param background Location and dimensions of the background to cover. **/
    private void drawBgGrid(EntityView bg, Rectangle background)
    {
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

    /** Initialize physics settings.
     * Specifies what can collide with what, and what happens when those collisions occur. **/
    @Override
    protected void initPhysics()
    {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntType.BOSS, EntType.PROJECTILE)
		{
            @Override
            protected void onCollisionBegin(Entity boss, Entity projectile)
            {
                HealthComponent health = boss.getComponent(HealthComponent.class);
                BaseProjectileComponent proj = projectile.getComponent(PlayerProjectileComponent.class);
                health.setValue(health.getValue() - proj.calcDamage());
                projectile.removeFromWorld();

                if (FXGLMath.randomBoolean(XP_ON_HIT_CHANCE))
				{
					player.getComponent(PlayerComponent.class).addXP(1);
				}
            }
        });

		getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntType.PLAYER, EntType.BOSS_PROJECTILE)
		{
			@Override
			protected void onCollisionBegin(Entity player, Entity projectile)
			{
				HealthComponent health = player.getComponent(HealthComponent.class);
				BaseProjectileComponent proj = projectile.getComponent(BaseProjectileComponent.class);
				health.setValue(health.getValue() - proj.calcDamage());
				projectile.removeFromWorld();
			}
		});

		getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntType.PLAYER, EntType.BOSS)
		{
			@Override
			protected void onCollisionBegin(Entity player, Entity boss)
			{
				BossComponent b = boss.getComponent(BossComponent.class);
				if (b.getCurrentAttack() == BossAttack.RAM)
				{
					b.endAttack();
					HealthComponent health = player.getComponent(HealthComponent.class);
					health.setValue(health.getValue() - b.getRamAttackDamage());
				}
			}
		});

		getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntType.PLAYER, EntType.XP_ORB)
		{
			/** Handle collisions between players and experience orbs. **/
			@Override
			protected void onCollisionBegin(Entity entPlayer, Entity orb)
			{
				PlayerComponent player = entPlayer.getComponent(PlayerComponent.class);
				player.addXP((int)(Math.random() * 4 + 1));
				orb.removeFromWorld();
			}
		});
    }
}
