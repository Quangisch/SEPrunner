package launcher;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import core.GameProperties;
import core.GameProperties.GameScreen;
import core.Project;


public class MainLauncher {
	
	public static void main(String[] args) {

		GameProperties.initFromFile();
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "LibGDXTest";
		cfg.useGL20 = true;
		cfg.resizable = false;
		cfg.width = GameProperties.SCALE_WIDTH;
		cfg.height = GameProperties.SCALE_HEIGHT;
		
		new LwjglApplication(new Project(GameScreen.MENU_PROFILE), cfg);
	}
}
