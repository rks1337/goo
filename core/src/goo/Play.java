package goo;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;	
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import bitfire.postprocessing.PostProcessor;
import bitfire.postprocessing.effects.Bloom;
import bitfire.utils.ShaderLoader;

public class Play implements Screen {
	private static final float TIMESTEP = 1 / 30f;
	private static final int VELOCITYITERATIONS = 1, POSITIONITERATIONS = 6;

	private Box2DDebugRenderer debugRenderer;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private World world;

	private Body ground;
	private Stage hud;
	//	private float xy;
	private Particle particle;
	private boolean spawn;
	private int particles;

	private Skin skin;
	private Table table0;
	private Table table1;
	private BitmapFont font;
	private TextButton fps_button;
	private TextButton particle_button;
	private Array<Body> bodies = new Array<Body>();

	private final Boot game = (Boot) Gdx.app.getApplicationListener();
	private final Assets assets = game.getAssets();

	private PostProcessor post_processor;

	@Override
	public void show() {
		world = new World(new Vector2(0f, -25.81f), true);
		debugRenderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera();
		batch  = new SpriteBatch();

		skin = assets.manager.get(Assets.uiSkin);
		table0 = new Table(skin);
		table1 = new Table(skin);
		table0.setFillParent(true);
		table1.setFillParent(true);
		hud = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new OrthographicCamera()), batch);
		hud.addActor(table0);
		hud.addActor(table1);

		TextButtonStyle style = new TextButtonStyle();
		font = assets.manager.get("galax_48.ttf", BitmapFont.class);
		style.font = font;

		// counters
		fps_button = new TextButton("", style);
		fps_button.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});
		fps_button.pad(15);

		particle_button = new TextButton("", style);
		particle_button.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});
		particle_button.pad(15);

		table0.top().left().padLeft(70);
		table0.add(particle_button);
		table1.top().right().padRight(70);
		table1.add(fps_button);

		// world
		final Square top_wall = new Square(0, 60, 80, 5.5f, 4);
		top_wall.bodyDef.type = BodyType.StaticBody;
		ground = world.createBody(top_wall.bodyDef);
		ground.createFixture(top_wall.fixtureDef);
		ground.setUserData(top_wall.sprite);
		top_wall.shape.dispose();

		final Square bottom_wall = new Square(0, -20, 80, 5.5f, 4);
		bottom_wall.bodyDef.type = BodyType.StaticBody;
		ground = world.createBody(bottom_wall.bodyDef);
		ground.createFixture(bottom_wall.fixtureDef);
		ground.setUserData(bottom_wall.sprite);
		bottom_wall.shape.dispose();

		final Square left_wall = new Square(-60, 10, 5.5f, 50f, 4);
		left_wall.bodyDef.type = BodyType.StaticBody;
		ground = world.createBody(left_wall.bodyDef);
		ground.createFixture(left_wall.fixtureDef);
		ground.setUserData(left_wall.sprite);
		left_wall.shape.dispose();

		final Square right_wall = new Square(60, 10, 5.5f, 50f, 4);
		right_wall.bodyDef.type = BodyType.StaticBody;
		ground = world.createBody(right_wall.bodyDef);
		ground.createFixture(right_wall.fixtureDef);
		ground.setUserData(right_wall.sprite);
		right_wall.shape.dispose();

		final Square float_plat = new Square(-10, 30, 10f, 10f, 4);
		float_plat.bodyDef.type = BodyType.DynamicBody;
		float_plat.bodyDef.gravityScale = .25f;
		float_plat.bodyDef.angularVelocity = 1f;
		ground = world.createBody(float_plat.bodyDef);
		ground.createFixture(float_plat.fixtureDef);
		ground.setUserData(float_plat.sprite);
		float_plat.shape.dispose();

		InputProcessor input_0 = new GestureDetector(new Gesture() {
		}) {
			@Override
			public boolean touchDown(float x, float y, int pointer, int button) {
				spawn = true;
				return super.touchDown(x, y, pointer, button);
			}

			@Override
			public boolean touchUp(float x, float y, int pointer, int button) {
				spawn = false;
				return super.touchUp(x, y, pointer, button);
			}
		};

		InputProcessor input_1 = hud;
		InputMultiplexer input_multiplexer = new InputMultiplexer();
		input_multiplexer.addProcessor(input_0);
		input_multiplexer.addProcessor(input_1);
		Gdx.input.setInputProcessor(input_multiplexer);

		// Shaders
		ShaderLoader.BasePath = "shaders/";
		post_processor = new PostProcessor(false, false, true);

		Bloom bloom = new Bloom(1000, 1000);
		bloom.setBaseIntesity(0);
		bloom.setBaseSaturation(0);
		bloom.setBloomSaturation(1);
		bloom.setBloomIntesity(5);
		bloom.setBlurAmount(0);
		bloom.setThreshold(.97f);

		post_processor.addEffect(bloom);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		world.step(TIMESTEP, VELOCITYITERATIONS, POSITIONITERATIONS);
		camera.position.set(0, 20, 0);
		camera.update();

		post_processor.capture();

		batch.begin();
		batch.setProjectionMatrix(camera.combined);
		world.getBodies(bodies);
		for (Body body : bodies) {
			Sprite sprite = (Sprite) body.getUserData();
			if (body.getUserData() instanceof Sprite) {
				if (body.getGravityScale() == 3f) {
					sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
					sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
					sprite.draw(batch);
				}
			}
			//			if (body.getLinearVelocity().y > -.3f && body.getLinearVelocity().y < .3f) {
			//				body.setType(BodyType.KinematicBody);
			//			}
		}
		batch.end();
		post_processor.render();

		batch.begin();
		world.getBodies(bodies);
		for (Body body : bodies) {
			Sprite sprite = (Sprite) body.getUserData();
			if (body.getUserData() instanceof Sprite) {
				if (!(body.getGravityScale() == 3f)) {
					sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
					sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
					sprite.draw(batch);
				}
			}
		}
		batch.end();

		if (spawn) {
			particles++;
			Vector3 mouse = new Vector3();
			camera.unproject(mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0));
			particle = new Particle(mouse.x, mouse.y);
			ground = world.createBody(particle.bodyDef);
			ground.createFixture(particle.fixtureDef);
			ground.setUserData(particle.sprite);
			particle.shape.dispose();
		}

		fps_button.setText("fps: " + Integer.toString(Gdx.graphics.getFramesPerSecond()));
		particle_button.setText("particles: " + Integer.toString(particles));

		batch.setProjectionMatrix(hud.getCamera().combined);
		hud.act(delta);
		hud.draw();

		// FPS / Debug
		//System.out.println("fps: "+ Gdx.graphics.getFramesPerSecond() + " particles: " + particles);

		// Controls
		Gdx.input.setCatchBackKey(true);
		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			debugRenderer.render(world, camera.combined);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.BACK) || Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			((Game) Gdx.app.getApplicationListener()).setScreen(new Play());
		}
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = 1200 / 10;
		camera.viewportHeight = 800 / 10;
		hud.getViewport().update(width, height);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {
		post_processor.rebind();
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		batch.dispose();
		hud.dispose();
		world.dispose();
		debugRenderer.dispose();
		post_processor.dispose();
	}
}
