package goo.goo.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.addIcon("ui/goo_logo_32.png", Files.FileType.Internal);
		config.title = "goo";
		config.vSyncEnabled = true;
		config.width = 1280;
		config.height = 720;
		new LwjglApplication(new goo.goo.Boot(), config);
	}
}