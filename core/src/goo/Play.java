package goo;

import java.util.Random;

import bitfire.postprocessing.PostProcessor;
import bitfire.postprocessing.effects.Bloom;
import bitfire.utils.ShaderLoader;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Play implements Screen {
	private static final float TIMESTEP = 1 / 60f;
	private static final int VELOCITYITERATIONS = 8, POSITIONITERATIONS = 3;

	private Box2DDebugRenderer debugRenderer;
	private OrthographicCamera camera;
	private PolygonSpriteBatch batch;
	private World world;

	private Body ground;
	private Stage hud;
	private boolean spawn;
	private boolean display_counters;
	private int particles;

	private Table table0;
	private Table table1;
	private BitmapFont font;
	private TextButton fps_button;
	private TextButton particle_button;
	private Array<Body> bodies = new Array<Body>();

	private PostProcessor post_processor;
	private int color = 1;

	@Override
	public void show() {
		world = new World(new Vector2(0f, -9.81f), true);
		debugRenderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera();
		batch  = new PolygonSpriteBatch();

		table0 = new Table();
		table1 = new Table();
		table0.setFillParent(true);
		table1.setFillParent(true);
		hud = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new OrthographicCamera()), batch);
		hud.addActor(table0);
		hud.addActor(table1);

		TextButtonStyle style = new TextButtonStyle();
		font = Assets.manager.get("galax_48.ttf", BitmapFont.class);
		style.font = font;

		// randomize color
		Random rnd = new Random();
		color = rnd.nextInt(3) + 1;

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

		table0.top().left().padLeft(75).padTop(30);
		table0.add(particle_button);
		table1.top().right().padRight(75).padTop(30);;
		table1.add(fps_button);

		world();

		InputProcessor input_0 = new GestureDetector(new Gesture() {
		}) {
			@Override
			public boolean touchDown(float x, float y, int pointer, int button) {
				spawn = true;
				Audio.goo_sound.play();
				return super.touchDown(x, y, pointer, button);
			}

			@Override
			public boolean touchUp(float x, float y, int pointer, int button) {
				spawn = false;
				Audio.goo_sound.pause();
				return super.touchUp(x, y, pointer, button);
			}

			@Override
			public boolean keyDown(int keycode) {
				switch(keycode) {
				case Keys.NUM_1:
					color = 1;
					break;
				case Keys.NUM_2:
					color = 2;
					break;
				case Keys.NUM_3:
					color = 3;
					break;
				}
				return super.keyDown(keycode);
			}
		};

		Gdx.input.setInputProcessor(input_0);

		//shader
		ShaderLoader.BasePath = "shaders/";
		post_processor = new PostProcessor(false, false, true);

		Bloom bloom = new Bloom(1000, 1000);
		bloom.setBaseIntesity(0);
		bloom.setBaseSaturation(0);
		bloom.setBloomSaturation(1);
		bloom.setBloomIntesity(10);
		bloom.setBlurAmount(0);
		bloom.setThreshold(.97f);

		post_processor.addEffect(bloom);
	}

	private void world() {
		// world
		PolygonShape polygon_shape = new PolygonShape();
		BodyDef bodyDef = new BodyDef();

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.friction = 2f;
		fixtureDef.restitution = 0.5f;
		fixtureDef.density = .01f;

		//top wall
		polygon_shape.setAsBox(40, 2);
		bodyDef.position.set(0, 32);
		fixtureDef.shape = polygon_shape;
		ground = world.createBody(bodyDef);
		ground.createFixture(fixtureDef);

		//bottom wall
		polygon_shape.setAsBox(40, 2);
		bodyDef.position.set(0, 8);
		fixtureDef.shape = polygon_shape;
		ground = world.createBody(bodyDef);
		ground.createFixture(fixtureDef);

		//left wall
		polygon_shape.setAsBox(2, 24);
		bodyDef.position.set(-19, 20);
		fixtureDef.shape = polygon_shape;
		ground = world.createBody(bodyDef);
		ground.createFixture(fixtureDef);

		//right wall
		polygon_shape.setAsBox(2, 24);
		bodyDef.position.set(19, 20);
		fixtureDef.shape = polygon_shape;
		ground = world.createBody(bodyDef);
		ground.createFixture(fixtureDef);

		//floating shape
		polygon_shape.setAsBox(3, 3);
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.gravityScale = 1;
		bodyDef.position.set(-10, 20);
		fixtureDef.shape = polygon_shape;
		ground = world.createBody(bodyDef);
		ground.createFixture(fixtureDef);

		polygon_shape.dispose();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		world.step(TIMESTEP, VELOCITYITERATIONS, POSITIONITERATIONS);

		camera.position.set(0, 20, 0);
		camera.update();

		batch.setProjectionMatrix(camera.combined);
		world.getBodies(bodies);

		Audio.goo_sound.setLooping(true);

		post_processor.capture();
		batch.begin();
		for (Body body : bodies) {
			if (body.getUserData() instanceof Sprite) {
				Sprite sprite = (Sprite) body.getUserData();
				if (color == 1) {
					sprite = new Sprite(Assets.manager.get(Assets.c_green));
				}
				if (color == 2) {
					sprite = new Sprite(Assets.manager.get(Assets.c_red));
				}
				if (color == 3) {
					sprite = new Sprite(Assets.manager.get(Assets.c_blue));
				}
				sprite.setAlpha(1);
				sprite.setSize(2.75f, 2.75f);
				sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
				sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
				sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
				sprite.draw(batch);
			}
		}
		batch.end();
		post_processor.render();

		if (spawn) {
			particles++;
			Vector3 mouse = new Vector3();
			camera.unproject(mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0));

			Sprite sprite = new Sprite();
			CircleShape shape = new CircleShape();
			shape.setRadius(.25f);

			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyType.DynamicBody;
			bodyDef.position.set(mouse.x, mouse.y);
			bodyDef.gravityScale = 6;

			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = shape;
			fixtureDef.friction = 0f;
			fixtureDef.restitution = 0f;
			fixtureDef.density = 1f;

			ground = world.createBody(bodyDef);
			ground.createFixture(fixtureDef);
			ground.setUserData(sprite);
			shape.dispose();
		}

		if (display_counters) {
			table0.setVisible(true);
			table1.setVisible(true);
		} else {
			table0.setVisible(false);
			table1.setVisible(false);
		}

		fps_button.setText("fps: " + Integer.toString(Gdx.graphics.getFramesPerSecond()));
		particle_button.setText("particles: " + Integer.toString(particles));



		batch.setProjectionMatrix(hud.getCamera().combined);
		hud.act(delta);
		hud.draw();

		// Controls
		Gdx.input.setCatchBackKey(true);
		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			debugRenderer.render(world, camera.combined);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.BACK) || Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			Audio.goo_sound.pause();
			((Game) Gdx.app.getApplicationListener()).setScreen(new Play());
		}
	}

	@Override
	public void resize(int width, int height) {
		if (width > 715) {
			display_counters = true;
		} else {
			display_counters = false;
		}
		camera.viewportWidth = 1200 / 30;
		camera.viewportHeight = 800 / 30;
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
