package goo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Splash implements Screen {
	private static final float TIMESTEP = 1 / 60f;
	private static final int VELOCITYITERATIONS = 1, POSITIONITERATIONS = 6;

	private World world;
	private Box2DDebugRenderer debugRenderer;
	private SpriteBatch batch;
	private OrthographicCamera camera;

	private Stage hud;
	private Table table0;
	private Table table1;

	private Body ground;
	private Array<Body> bodies = new Array<Body>();

	private BitmapFont font;

	@Override
	public void show() {
		table0 = new Table();
		table0.setFillParent(true);

		table1 = new Table();
		table1.setFillParent(true);

		world = new World(new Vector2(0f, -25.81f), true);
		debugRenderer = new Box2DDebugRenderer();
		batch  = new SpriteBatch();
		camera = new OrthographicCamera();

		hud = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new OrthographicCamera()), batch);

		hud.addActor(table0);
		hud.addActor(table1);

		// free type font
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/galax___.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();

		parameter.size = 120;
		parameter.color = Color.WHITE;
		font = generator.generateFont(parameter);

		// hud buttons
		TextButtonStyle style = new TextButtonStyle();
		style.font = font;

		TextButton title = new TextButton("IT'S LOADING", style);
		title.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});
		title.pad(15);

		parameter.size = 40;
		font = generator.generateFont(parameter);
		style.font = font;

		generator.dispose();

		// table
		table0.add(title);

		table1.center();

		// DEBUG
		//table0.debug();
		//table1.debug();

		final Square i = new Square(0f, -50f, 10, 1, -1);
		i.bodyDef.type = BodyType.StaticBody;
		i.fixtureDef.restitution = 0f;
		ground = world.createBody(i.bodyDef);
		ground.createFixture(i.fixtureDef);
		ground.setUserData(i.sprite);
		i.shape.dispose();

		final Square drop = new Square(5f, -4, 5, 5, -1);
		drop.bodyDef.angularVelocity = 2;
		drop.bodyDef.gravityScale = 10;
		drop.fixtureDef.restitution = 1;
		ground = world.createBody(drop.bodyDef);
		ground.createFixture(drop.fixtureDef);
		ground.setUserData(drop.sprite);
		Sprite sprite0 = new Sprite(new Texture(Gdx.files.internal("colors/green.png")));
		sprite0.setSize(5f * 2.02f, 5f * 2.02f);
		sprite0.setOrigin(sprite0.getWidth() / 2, sprite0.getHeight() / 2);
		ground.setUserData(sprite0);
		drop.shape.dispose();

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f/255f, 0f/255f, 0f/255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glCullFace(GL20.GL_BACK);

		world.step(TIMESTEP, VELOCITYITERATIONS, POSITIONITERATIONS);

		camera.position.set(-10f, -50f, 0f);
		camera.update();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		world.getBodies(bodies);
		for (Body body : bodies)
			if (body.getUserData() instanceof Sprite) {
				Sprite sprite = (Sprite) body.getUserData();
				sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
				sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
				sprite.draw(batch);
			}
		batch.end();
		batch.setProjectionMatrix(hud.getCamera().combined);
		hud.act(delta);
		hud.draw();

		// Debug sprite lines
		//		debugRenderer.render(world, camera.combined);

		// FPS
		// System.out.println(Gdx.graphics.getFramesPerSecond());

		// PC / Andriod controls
		//		Gdx.input.setCatchBackKey(true);
		//		if (Gdx.input.isKeyPressed(Input.Keys.BACK) || Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
		//		}
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = width / 15f; // 10 pc 15 andriod
		camera.viewportHeight = height / 15f;
		hud.getViewport().update(width, height);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

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
	}
}