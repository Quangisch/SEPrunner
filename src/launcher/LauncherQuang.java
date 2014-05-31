package launcher;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import core.ingame.GameProperties;
import core.ingame.GameRender;

public class LauncherQuang {
	
	public static void main(String[] args) {

		GameProperties.switchMode(false, true);
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "LibGDXTest";
		cfg.useGL20 = true;
		cfg.width = GameProperties.width;
		cfg.height = GameProperties.height;
//		cfg.fullscreen = true;
		
		
		new LwjglApplication(GameRender.getInstance(), cfg);
		}
}
