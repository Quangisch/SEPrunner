package launcher;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import core.GameProperties;
import core.Project;
import core.GameProperties.GameState;


public class MainLauncher {
	
	public static void main(String[] args) {

		GameProperties.initFromFile();
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "LibGDXTest";
		cfg.useGL20 = true;
		cfg.resizable = false;
		cfg.width = GameProperties.SCALE_WIDTH;
		cfg.height = GameProperties.SCALE_HEIGHT;
		
		int mode = 0;
		GameProperties.displayMode = LwjglApplicationConfiguration.getDisplayModes()[mode];
		System.out.println("Mode"+mode+": "+GameProperties.displayMode.toString());
		
		new LwjglApplication(new Project(GameState.MENU), cfg);
	}
}
