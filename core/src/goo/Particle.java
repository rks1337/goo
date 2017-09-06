package goo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Particle {

	public Sprite sprite;
	public CircleShape shape;
	public BodyDef bodyDef;
	public FixtureDef fixtureDef;

	private final Boot game = (Boot) Gdx.app.getApplicationListener();
	private final Assets assets = game.getAssets();

	public Particle(float x, float y) {

		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(x, y);
		bodyDef.gravityScale = 3f;

		shape = new CircleShape();
		shape.setRadius(2f);

		fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.friction = 0f;
		fixtureDef.restitution = 0f;
		fixtureDef.density = 0f;
		sprite = new Sprite(assets.manager.get(Assets.c_green));

		sprite.setAlpha(1f);
		sprite.setSize(10, 10);
		sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
	}

	public void update(float delta) {

	}

	public void draw(SpriteBatch batch) {
		update(Gdx.graphics.getDeltaTime());
		sprite.draw(batch);
	}
}
