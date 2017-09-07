package goo;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class Boot extends Game {

	public static Assets assets;

	@Override
	public void create() {
		setScreen(new Splash());
		Timer.schedule(new Task(){
			@Override
			public void run() {
				assets = new Assets();
				assets.load();
				Assets.manager.finishLoading();
				Audio.create();
				setScreen(new Play());
			}
		}, 2f);
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void pause(){
		super.pause();
	}

	@Override
	public void resume(){
		super.resume();
	}

	public Assets getAssets() {
		return assets;
	}
}
