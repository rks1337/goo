package goo.goo;

import com.badlogic.gdx.audio.Music;

public class Audio {

	public static Music goo_sound;

	public static void create() {
		goo_sound = Assets.manager.get(Assets.goo_sound);
	}

	public static void dispose() {
		goo_sound.dispose();
	}
}
