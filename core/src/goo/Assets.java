package goo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Assets {

	public AssetManager manager = new AssetManager();

	public static final AssetDescriptor<Texture> black =  new AssetDescriptor<Texture>("colors/black.png", Texture.class);
	public static final AssetDescriptor<Texture> blue =  new AssetDescriptor<Texture>("colors/blue.png", Texture.class);
	public static final AssetDescriptor<Texture> red =  new AssetDescriptor<Texture>("colors/red.png", Texture.class);
	public static final AssetDescriptor<Texture> green =  new AssetDescriptor<Texture>("colors/green.png", Texture.class);
	public static final AssetDescriptor<Texture> purple =  new AssetDescriptor<Texture>("colors/purple.png", Texture.class);
	public static final AssetDescriptor<Texture> pink =  new AssetDescriptor<Texture>("colors/pink.png", Texture.class);

	public static final AssetDescriptor<Texture> c_blue =  new AssetDescriptor<Texture>("colors/c_blue.png", Texture.class);
	public static final AssetDescriptor<Texture> c_green =  new AssetDescriptor<Texture>("colors/c_green.png", Texture.class);
	public static final AssetDescriptor<Texture> c_red =  new AssetDescriptor<Texture>("colors/c_red.png", Texture.class);
	public static final AssetDescriptor<Texture> c_exp =  new AssetDescriptor<Texture>("colors/c_exp.png", Texture.class);

	
	public static final AssetDescriptor<TextureAtlas> uiAtlas = new AssetDescriptor<TextureAtlas>("ui/atlas.pack", TextureAtlas.class);
	public static final AssetDescriptor<Skin> uiSkin = new AssetDescriptor<Skin>("ui/menuSkin.json", Skin.class, new SkinLoader.SkinParameter("ui/atlas.pack"));

	public void load() {
		FileHandleResolver resolver = new InternalFileHandleResolver();
		manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

		FreeTypeFontLoaderParameter galax_250 = new FreeTypeFontLoaderParameter();
		galax_250.fontFileName = "font/galax___.ttf";
		galax_250.fontParameters.size = scale_fonts(250);
		galax_250.fontParameters.borderWidth = 7f;
		galax_250.fontParameters.borderColor = Color.BLACK;
		manager.load("galax_250.ttf", BitmapFont.class, galax_250);

		FreeTypeFontLoaderParameter galax_120 = new FreeTypeFontLoaderParameter();
		galax_120.fontFileName = "font/galax___.ttf";
		galax_120.fontParameters.size = scale_fonts(120);
		galax_120.fontParameters.borderWidth = 3f;
		galax_120.fontParameters.borderColor = Color.BLACK;
		manager.load("galax_120.ttf", BitmapFont.class, galax_120);
		
		FreeTypeFontLoaderParameter galax_120_thick = new FreeTypeFontLoaderParameter();
		galax_120_thick.fontFileName = "font/galax___.ttf";
		galax_120_thick.fontParameters.size = scale_fonts(120);
		galax_120_thick.fontParameters.borderWidth = 7f;
		galax_120_thick.fontParameters.borderColor = Color.BLACK;
		manager.load("galax_120_thick.ttf", BitmapFont.class, galax_120_thick);

		FreeTypeFontLoaderParameter galax_80 = new FreeTypeFontLoaderParameter();
		galax_80.fontFileName = "font/galax___.ttf";
		galax_80.fontParameters.size = scale_fonts(80);
		galax_80.fontParameters.borderWidth = 3f;
		galax_80.fontParameters.borderColor = Color.BLACK;
		manager.load("galax_80.ttf", BitmapFont.class, galax_80);
		
		FreeTypeFontLoaderParameter galax_80_scarlet = new FreeTypeFontLoaderParameter();
		galax_80_scarlet.fontFileName = "font/galax___.ttf";
		galax_80_scarlet.fontParameters.size = scale_fonts(80);
		galax_80_scarlet.fontParameters.borderWidth = 3f;
		galax_80_scarlet.fontParameters.borderColor = Color.BLACK;
		galax_80_scarlet.fontParameters.color = Color.SCARLET;
		manager.load("galax_80_scarlet.ttf", BitmapFont.class, galax_80_scarlet);

		FreeTypeFontLoaderParameter galax_48 = new FreeTypeFontLoaderParameter();
		galax_48.fontFileName = "font/galax___.ttf";
		galax_48.fontParameters.size = scale_fonts(48);
		galax_48.fontParameters.borderWidth = 3f;
		galax_48.fontParameters.borderColor = Color.BLACK;
		manager.load("galax_48.ttf", BitmapFont.class, galax_48);
		
		manager.load(c_blue);
		manager.load(c_exp);
		manager.load(c_green);
		manager.load(c_red);
		manager.load(black);
		manager.load(blue);
		manager.load(red);
		manager.load(green);
		manager.load(purple);
		manager.load(pink);

		manager.load(uiAtlas);
		manager.load(uiSkin);
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
