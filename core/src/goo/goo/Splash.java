package goo.goo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
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
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ShortArray;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Splash implements Screen {
	private static final float TIMESTEP = 1 / 60f;
	private static final int VELOCITYITERATIONS = 8, POSITIONITERATIONS = 3;

	private World world;
	private Box2DDebugRenderer debugRenderer;
	private PolygonSpriteBatch batch;
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
		batch  = new PolygonSpriteBatch();
		camera = new OrthographicCamera();

		hud = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new OrthographicCamera()), batch);

		hud.addActor(table0);
		hud.addActor(table1);

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/galax___.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();

		parameter.size = 120;
		parameter.color = Color.WHITE;
		font = generator.generateFont(parameter);

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

		table0.add(title);
		table1.center();

		PolygonSprite polygon_sprite;
		PolygonShape polygon_shape = new PolygonShape();
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		FixtureDef fixtureDef = new FixtureDef();

		//plat
		polygon_shape.setAsBox(10, 1);
		bodyDef.position.set(0, -50);
		fixtureDef.restitution = 0f;
		fixtureDef.shape = polygon_shape;
		ground = world.createBody(bodyDef);
		ground.createFixture(fixtureDef);

		//drop
		polygon_shape.setAsBox(5, 5);
		bodyDef.position.set(5, -4);
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.angularVelocity = 2f;
		bodyDef.gravityScale = 10f;

		fixtureDef.friction = 2f;
		fixtureDef.restitution = 1f;
		fixtureDef.density = .01f;
		fixtureDef.shape = polygon_shape;

		FloatArray f = new FloatArray();
		Vector2 tmp = new Vector2();
		for (int i = 0; i < polygon_shape.getVertexCount(); i++) {
			polygon_shape.getVertex(i, tmp);
			f.add(tmp.x);
			f.add(tmp.y);
		}

		Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		pixmap.setColor(new Color(0/255f, 255/255f, 85/255f, 1f));
		pixmap.fill();
		Texture texture = new Texture(pixmap);

		TextureRegion texture_region = new TextureRegion(texture);
		EarClippingTriangulator ear = new EarClippingTriangulator();
		ShortArray triangles = ear.computeTriangles(f);

		PolygonRegion poly_region = new PolygonRegion(texture_region, f.toArray(), triangles.toArray());
		polygon_sprite = new PolygonSprite(poly_region);
		polygon_sprite.setOrigin(
				polygon_sprite.getWidth() / 30, 
				polygon_sprite.getHeight() / 30);

		pixmap.dispose();

		ground = world.createBody(bodyDef);
		ground.createFixture(fixtureDef);
		ground.setUserData(polygon_sprite);

		polygon_shape.dispose();
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
		for (Body body : bodies) {
			if (body.getUserData() instanceof PolygonSprite) {
				PolygonSprite polygon_sprite = (PolygonSprite) body.getUserData();
				polygon_sprite.setPosition(
						body.getPosition().x - polygon_sprite.getWidth() / 15, 
						body.getPosition().y - polygon_sprite.getHeight() / 15);
				polygon_sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
				polygon_sprite.draw(batch);
			}
		}
		batch.end();
		batch.setProjectionMatrix(hud.getCamera().combined);
		hud.act(delta);
		hud.draw();
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = width / 15f;
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