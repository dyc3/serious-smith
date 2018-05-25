package main.java;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.event.EventTrigger;
import com.almasb.fxgl.extra.entity.components.HealthComponent;
import com.almasb.fxgl.entity.components.IDComponent;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.entity.view.EntityView;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.scene.Viewport;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.ProgressBar;
import com.gluonhq.charm.down.plugins.audio.Audio;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

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
	private static final Color COLOR_UI_TEXT = Color.WHITE;

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

	private ProgressBar pbarPlayerHealth;
	private ProgressBar pbarPlayerXP;
	private ProgressBar pbarBossHealth;
	/** The entity that holds pbarBossHealth over the boss. **/
	private Entity entpbarBossHealth;
	private Text textGameOver;
	private Text textStartOver;
	private Sound sndXpPickup = getAssetLoader().loadSound("xp_pickup.wav");

	private UserAction actionRestart = new UserAction("restart game") {
		@Override
		protected void onActionBegin()
		{
			System.out.println("RESTART");
			textGameOver.setVisible(false);
			textStartOver.setVisible(false);

			setupGame(true);
			getInput().rebind(getInput().getActionByName("restart game"), MouseButton.NONE);
		}
	};

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

    /** Set up the game objects for a new game.
	 * @param restart Set to false if first time set up. Set to true to reset the game (after game end). **/
    protected void setupGame(boolean restart)
	{
		if (restart)
		{
			if (player != null)
			{
				player.removeFromWorld();
			}
			if (boss != null)
			{
				boss.removeFromWorld();
			}
			camHolder.removeFromWorld();

			for (Entity entity : getGameWorld().getEntitiesByType(EntType.PROJECTILE))
			{
				entity.removeFromWorld();
			}
			for (Entity entity : getGameWorld().getEntitiesByType(EntType.BOSS_PROJECTILE))
			{
				entity.removeFromWorld();
			}
			for (Entity entity : getGameWorld().getEntitiesByType(EntType.XP_ORB))
			{
				entity.removeFromWorld();
			}
			for (Entity entity : getGameWorld().getEntitiesByType(EntType.BOSS_LASER))
			{
				entity.removeFromWorld();
			}
		}

		ProjectileFactory projectileFactory = new ProjectileFactory();

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

		if (restart)
		{
			PlayerComponent player = this.player.getComponent(PlayerComponent.class);
			pbarPlayerHealth.currentValueProperty()
					.bind(this.player.getComponent(HealthComponent.class).valueProperty());
			pbarPlayerHealth.maxValueProperty().bind(player.getMaxHealthProperty());
			pbarPlayerXP.currentValueProperty().bind(player.getXpProperty());
			pbarPlayerXP.maxValueProperty().bind(player.getXpToNextLevelBinding());
			pbarBossHealth.currentValueProperty().bind(boss.getComponent(HealthComponent.class).valueProperty());
			entpbarBossHealth.getComponent(ParentFollowerComponent.class).setTarget(boss);
		}

		// set up win/lose events
		getEventBus().addEventTrigger(new EventTrigger<>(
				() -> boss.getComponent(HealthComponent.class).getValue() <= 0,
				() -> new GameEndEvent(GameEndEvent.WIN, true)
		));

		getEventBus().addEventTrigger(new EventTrigger<>(
				() -> player.getComponent(HealthComponent.class).getValue() <= 0,
				() -> new GameEndEvent(GameEndEvent.LOSE, false)
		));

		getEventBus().addEventHandler(GameEndEvent.WIN, event -> onWin());
		getEventBus().addEventHandler(GameEndEvent.LOSE, event -> onLose());
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
				.renderLayer(RenderLayer.BACKGROUND)
                .with(new IrremovableComponent())
                .buildAndAttach(getGameWorld());

		setupGame(false);
    }

    @Override
    protected void initInput()
    {
		// NOTE: We handle all player controls in PlayerComponent.
		getInput().addAction(actionRestart, MouseButton.NONE);
    }

    /** Initializes UI elements, including health bars hovering over entities in the world. **/
    @Override
    protected void initUI()
    {
    	int hudElementCount = 0;
    	PlayerComponent player = this.player.getComponent(PlayerComponent.class);

        pbarPlayerHealth = new ProgressBar();
        pbarPlayerHealth.setTranslateX(UI_HUD_OFFSET_X);
        pbarPlayerHealth.setTranslateY(UI_HUD_OFFSET_Y + (UI_HUD_SPACING_Y * hudElementCount++));
        pbarPlayerHealth.makeHPBar();
        pbarPlayerHealth.currentValueProperty().bind(this.player.getComponent(HealthComponent.class).valueProperty());
		pbarPlayerHealth.maxValueProperty().bind(player.getMaxHealthProperty());

		pbarPlayerXP = new ProgressBar();
		pbarPlayerXP.setTranslateX(UI_HUD_OFFSET_X);
		pbarPlayerXP.setTranslateY(UI_HUD_OFFSET_Y + (UI_HUD_SPACING_Y * hudElementCount++));
		pbarPlayerXP.setFill(COLOR_XP_BAR);
		pbarPlayerXP.setMinValue(0);
		pbarPlayerXP.currentValueProperty().bind(player.getXpProperty());
		pbarPlayerXP.maxValueProperty().bind(player.getXpToNextLevelBinding());

		getGameScene().addUINode(pbarPlayerHealth);
		getGameScene().addUINode(pbarPlayerXP);

        pbarBossHealth = new ProgressBar();
        pbarBossHealth.setWidth(UI_HUD_BOSS_HEALTH_BAR_WIDTH);
        pbarBossHealth.makeHPBar();
		pbarBossHealth.setMaxValue(BOSS_HEALTH);
		pbarBossHealth.setLabelVisible(true);
        pbarBossHealth.currentValueProperty().bind(boss.getComponent(HealthComponent.class).valueProperty());

		entpbarBossHealth = Entities.builder()
                .viewFromNode(pbarBossHealth)
				.with(new IrremovableComponent())
                .with(new ParentFollowerComponent(boss,
						(BOSS_SIZE - UI_HUD_BOSS_HEALTH_BAR_WIDTH) / 2,
						UI_HUD_BOSS_HEALTH_BAR_OFFSET_Y))
                .buildAndAttach(getGameWorld());

		textGameOver = getUIFactory().newText("Game Over", COLOR_UI_TEXT, 26);
		textGameOver.setX(getWidth() / 2 - 100);
		textGameOver.setY(getHeight() / 2 - 100);
		textGameOver.setVisible(false);
		getGameScene().addUINode(textGameOver);

		textStartOver = getUIFactory().newText("Click to try again.", COLOR_UI_TEXT, 18);
		textStartOver.setX(getWidth() / 2 - 100);
		textStartOver.setY(getHeight() / 2 + 100);
		textStartOver.setVisible(false);
		getGameScene().addUINode(textStartOver);

		// Play the background music.
		// Normally, we should just run `getAudioPlayer().loopBGM("intense.mp3")`, but
		// that doesn't work, because FXGL is stupid. So, as a workaround, we load the
		// music as a `Sound`, then we get the `Audio`, set it to loop, and feed it
		// into a `Music` object, and then play it.
		Sound bgmSound = getAssetLoader().loadSound("intense.wav");
		Audio bgmAudio = bgmSound.getAudio$fxgl_base();
		bgmAudio.setLooping(true);
		Music bgm = new Music(bgmAudio);
		bgm.setCycleCount(Integer.MAX_VALUE);
		getAudioPlayer().playMusic(bgm);
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
			protected void onCollisionBegin(Entity entPlayer, Entity projectile)
			{
				PlayerComponent player = entPlayer.getComponent(PlayerComponent.class);
				BaseProjectileComponent proj = projectile.getComponent(BaseProjectileComponent.class);
				player.dealDamage(proj.calcDamage());
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
			protected void onCollisionBegin(Entity entPlayer, Entity entBoss)
			{
				BossComponent boss = entBoss.getComponent(BossComponent.class);
				PlayerComponent player = entPlayer.getComponent(PlayerComponent.class);
				if (boss.getCurrentAttack() == BossAttack.RAM)
				{
					boss.endAttack();
					player.dealDamage(boss.getRamAttackDamage());

					camHolder.getComponent(CameraShakerComponent.class)
							.setShake(BossComponent.RAM_ATTACK_CAMERA_SHAKE);
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

				getAudioPlayer().stopSound(sndXpPickup);
				getAudioPlayer().playSound(sndXpPickup);
			}
		});

		getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntType.PLAYER, EntType.BOSS_LASER)
		{
			/** Handle collisions between players and lasers. **/
			@Override
			protected void onCollision(Entity entPlayer, Entity entLaser)
			{
				PlayerComponent player = entPlayer.getComponent(PlayerComponent.class);
				player.dealDamage(BossComponent.LASER_ATTACK_DAMAGE);

				camHolder.getComponent(CameraShakerComponent.class)
						.setShake(5);
			}
		});
    }

    /** Called when the game is won. **/
	protected void onWin()
	{
		System.out.println("Game won");
		boss.removeFromWorld();

		textGameOver.setText("You Win!");
		textGameOver.setVisible(true);
		textStartOver.setVisible(true);

		getInput().rebind(actionRestart, MouseButton.PRIMARY);
	}

	/** Called when the game is lost. **/
	protected void onLose()
	{
		System.out.println("Game lost");
		player.removeFromWorld();

		textGameOver.setText("Game Over");
		textGameOver.setVisible(true);
		textStartOver.setVisible(true);

		getInput().rebind(actionRestart, MouseButton.PRIMARY);
	}
}
