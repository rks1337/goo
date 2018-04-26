package goo.goo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;

public class Assets {

	public static AssetManager manager = new AssetManager();

	public static final AssetDescriptor<Texture> c_blue =  new AssetDescriptor<Texture>("colors/c_blue.png", Texture.class);
	public static final AssetDescriptor<Texture> c_green =  new AssetDescriptor<Texture>("colors/c_green.png", Texture.class);
	public static final AssetDescriptor<Texture> c_red =  new AssetDescriptor<Texture>("colors/c_red.png", Texture.class);

	public static final AssetDescriptor<Music> goo_sound =  new AssetDescriptor<Music>("sounds/goo.ogg", Music.class);

	public void load() {
		FileHandleResolver resolver = new InternalFileHandleResolver();
		manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

		FreeTypeFontLoaderParameter galax_48 = new FreeTypeFontLoaderParameter();
		galax_48.fontFileName = "font/galax___.ttf";
		galax_48.fontParameters.size = scale_fonts(48);
		galax_48.fontParameters.borderWidth = 3f;
		galax_48.fontParameters.borderColor = Color.BLACK;
		manager.load("galax_48.ttf", BitmapFont.class, galax_48);

		manager.load(c_blue);
		manager.load(c_green);
		manager.load(c_red);

		manager.load(goo_sound);
	}

	public int scale_fonts(int size) {
		// divide the new screen width by the old width
		int font = Gdx.graphics.getWidth() * size / 1920;
		return font;
	}

	public void dispose() {
		manager.dispose();
	}
}
