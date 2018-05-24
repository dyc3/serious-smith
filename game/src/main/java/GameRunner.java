package main.java;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.extra.entity.components.HealthComponent;
import com.almasb.fxgl.entity.components.IDComponent;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.entity.view.EntityView;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.scene.Viewport;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.ProgressBar;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import java.util.Map;

/** Main entry class for the application. **/
public class GameRunner extends GameApplication
{
	/** Width of the application window. **/
	private static final int WINDOW_WIDTH = 1280;
	/** Height of the application window. **/
	private static final int WINDOW_HEIGHT = 720;
	/** Title of the application window. **/
	private static final String WINDOW_TITLE = "Serious Smith";
	/** Version of the program. **/
	private static final String VERSION = "0.2";

	/** X offset for fixed HUD elements. **/
	private static final int UI_HUD_OFFSET_X = 50;
	/** Y offset for fixed HUD elements. **/
	private static final int UI_HUD_OFFSET_Y = 100;
	/** Y spacing for fixed HUD elements. **/
	private static final int UI_HUD_SPACING_Y = 20;

	/** Boss health bar offset on the x axis. **/
	private static final int UI_HUD_BOSS_HEALTH_BAR_WIDTH = 140;
	/** Boss health bar offset on the y axis. **/
	private static final int UI_HUD_BOSS_HEALTH_BAR_OFFSET_Y = -20;
	/** Color of the background. **/
	private static final Color COLOR_BG = Color.color(0.2, 0.2, 0.2);
	/** Color of the background grid lines. **/
	private static final Color COLOR_BG_GRID = Color.color(0.3, 0.3, 0.3);
	/** Color of the experience bar. **/
	private static final Color COLOR_XP_BAR = Color.color(0.2, 0.7, 1);

	/** Boss starting health. **/
	private static final int BOSS_HEALTH = 1000000;
	/** Width and height of the boss. **/
	private static final int BOSS_SIZE = 100;
	/** Width and height of the player. **/
	private static final int PLAYER_SIZE = 25;
	/** Chance to spawn an experience orb when the boss is damaged. **/
	private static final double XP_ORB_SPAWN_ON_DAMAGE_CHANCE = 0.5;
	/** Chance to receive some experience when the player hits the boss. **/
	private static final double XP_ON_HIT_CHANCE = 0.4;

	/** Quick reference to the player. **/
    private Entity player;
	/** Quick reference to the boss. **/
	private Entity boss;
	/** Quick reference to the camera holder. **/
	private Entity camHolder;

    /** Program entry.
	 * @param args command line arguments. **/
    public static void main(String[] args)
    {
        launch(args);
    }

	/** Initialize the game window and load settings. **/
	@Override
    protected void initSettings(GameSettings settings)
    {
        settings.setWidth(WINDOW_WIDTH);
        settings.setHeight(WINDOW_HEIGHT);
        settings.setTitle(WINDOW_TITLE);
        settings.setVersion(VERSION);
    }

    /** Initialize the game. Sets up background and builds player and boss entities. Binds camera to player. **/
    @Override
    protected void initGame()
    {
        // set up factories
		ProjectileFactory projectileFactory = new ProjectileFactory();
		getGameWorld().addEntityFactory(projectileFactory);

		// set up background
		int bgWidth = getWidth() * 1000;
		int bgHeight = getHeight() * 1000;
        Rectangle bg0 = new Rectangle(-bgWidth / 2, -bgHeight / 2, bgWidth, bgHeight);
        bg0.setFill(COLOR_BG);
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

        Rectangle rectPlayer = new Rectangle(0, 0, PLAYER_SIZE, PLAYER_SIZE);
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

        Rectangle rectBoss = new Rectangle(0, 0, BOSS_SIZE, BOSS_SIZE);
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

		camHolder = Entities.builder()
				.with(new IDComponent("camera holder", 0))
				.with(new ParentFollowerComponent(player, 0, 0))
				.with(new CameraShakerComponent())
				.buildAndAttach(getGameWorld());

        Viewport viewport = getGameScene().getViewport();
        viewport.bindToEntity(camHolder,
                       (getWidth() / 2) - (player.getWidth() / 2),
                       (getHeight() / 2) - (player.getHeight() / 2));
    }

    @Override
    protected void initInput()
    {
		// NOTE: We handle all player controls in PlayerComponent.
    }

    /** Initializes UI elements, including health bars hovering over entities in the world. **/
    @Override
    protected void initUI()
    {
    	int hudElementCount = 0;
    	PlayerComponent p = player.getComponent(PlayerComponent.class);

        ProgressBar pbarPlayerHealth = new ProgressBar();
        pbarPlayerHealth.setTranslateX(UI_HUD_OFFSET_X);
        pbarPlayerHealth.setTranslateY(UI_HUD_OFFSET_Y + (UI_HUD_SPACING_Y * hudElementCount++));
        pbarPlayerHealth.makeHPBar();
        pbarPlayerHealth.currentValueProperty().bind(player.getComponent(HealthComponent.class).valueProperty());
		pbarPlayerHealth.maxValueProperty().bind(p.getMaxHealthProperty());

		ProgressBar pbarPlayerXP = new ProgressBar();
		pbarPlayerXP.setTranslateX(UI_HUD_OFFSET_X);
		pbarPlayerXP.setTranslateY(UI_HUD_OFFSET_Y + (UI_HUD_SPACING_Y * hudElementCount++));
		pbarPlayerXP.setFill(COLOR_XP_BAR);
		pbarPlayerXP.setMinValue(0);
		pbarPlayerXP.currentValueProperty().bind(p.getXpProperty());
		pbarPlayerXP.maxValueProperty().bind(p.getXpToNextLevelBinding());

		getGameScene().addUINode(pbarPlayerHealth);
		getGameScene().addUINode(pbarPlayerXP);

        ProgressBar pbarBossHealth = new ProgressBar();
        pbarBossHealth.setWidth(UI_HUD_BOSS_HEALTH_BAR_WIDTH);
        pbarBossHealth.makeHPBar();
		pbarBossHealth.setMaxValue(BOSS_HEALTH);
		pbarBossHealth.setLabelVisible(true);
        pbarBossHealth.currentValueProperty().bind(boss.getComponent(HealthComponent.class).valueProperty());

        Entities.builder()
                .viewFromNode(pbarBossHealth)
                .with(new IrremovableComponent())
                .with(new ParentFollowerComponent(boss,
						(BOSS_SIZE - UI_HUD_BOSS_HEALTH_BAR_WIDTH) / 2,
						UI_HUD_BOSS_HEALTH_BAR_OFFSET_Y))
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
            line.setStroke(COLOR_BG_GRID);
            line.setStrokeWidth(1);
            bg.addNode(line);
        }

        for (double y = background.getY(); y < background.getY() + background.getHeight(); y += gridSize)
        {
            Line line = new Line(background.getX(), y, background.getX() + background.getWidth(), y);
            line.setStroke(COLOR_BG_GRID);
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

				// Spawn some xp orbs sometimes, ejecting from the boss
				if (FXGLMath.randomBoolean(XP_ORB_SPAWN_ON_DAMAGE_CHANCE))
				{
					Point2D spawn = boss.getCenter();
					SpawnData data = new SpawnData(spawn);
					Entity orb = new XPFactory().spawnXpOrb(data);
					boss.getWorld().addEntity(orb);
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

				CameraShakerComponent shaker = camHolder.getComponent(CameraShakerComponent.class);
				if (shaker.getShake() < 6)
				{
					shaker.addShake(2);
				}
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

					camHolder.getComponent(CameraShakerComponent.class).setShake(BossComponent.RAM_ATTACK_CAMERA_SHAKE);
				}
			}
		});

		getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntType.PLAYER, EntType.XP_ORB)
		{
			/** Handle collisions between players and experience orbs. **/
			@Override
			protected void onCollisionBegin(Entity entPlayer, Entity entOrb)
			{
				PlayerComponent player = entPlayer.getComponent(PlayerComponent.class);
				XpOrbComponent orb = entOrb.getComponent(XpOrbComponent.class);
				player.addXP(orb.getExperience());
				entOrb.removeFromWorld();
			}
		});
    }
}
