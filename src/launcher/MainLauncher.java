package launcher;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import core.GameProperties;
import core.GameProperties.GameScreen;
import core.Project;

public class MainLauncher {
	
	public static void main(String[] args) {


		GameProperties.initPrefDisplayMode();
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "SEPrunner";
		cfg.useGL20 = true;
		cfg.resizable = false;
		cfg.width = GameProperties.SCALE_WIDTH;
		cfg.height = GameProperties.SCALE_HEIGHT;
		
		cfg.fullscreen = false;
		
		if(cfg.fullscreen) {
			cfg.width = GameProperties.prefDisplayMode.width;
			cfg.height = GameProperties.prefDisplayMode.height;
		}
		
		new LwjglApplication(new Project(GameScreen.MENU_OPTION), cfg);

	}
	
}
