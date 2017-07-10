package goo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Square {

	public Sprite sprite;
	public PolygonShape shape;
	public BodyDef bodyDef;
	public FixtureDef fixtureDef;

	private final Boot game = (Boot) Gdx.app.getApplicationListener();
	private final Assets assets = game.getAssets();

	public Square(float x, float y, float width, float height, int color) {

		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(x, y);
		bodyDef.gravityScale = .1f;

		shape = new PolygonShape();
		shape.setAsBox(width, height);

		fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.friction = 2f;
		fixtureDef.restitution = 0f;
		fixtureDef.density = .01f;

		switch(color) {
		case 0:
			sprite = new Sprite(assets.manager.get(Assets.black));
			break;
		case 1:
			sprite = new Sprite(assets.manager.get(Assets.green));
			break;
		case 2:
			sprite = new Sprite(assets.manager.get(Assets.red));
			break;
		case 3:
			sprite = new Sprite(assets.manager.get(Assets.pink));
			break;
		case 4:
			sprite = new Sprite(assets.manager.get(Assets.purple));
			break;
		}
		if (!(color < 0)) {
			sprite.setAlpha(1f);
			sprite.setSize(width * 2.02f, height * 2.02f);
			sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
		}
	}

	public void update(float delta) {

	}

	public void draw(SpriteBatch batch) {
		update(Gdx.graphics.getDeltaTime());
		sprite.draw(batch);
	}
}
