package launcher;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import core.GameProperties;
import core.GameProperties.GameState;
import core.Project;

public class LauncherQuang {
	
	public static void main(String[] args) {

		GameProperties.initFromFile();
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "SEPrunner";
		cfg.useGL20 = true;
		cfg.resizable = false;
		cfg.width = GameProperties.SCALE_WIDTH;
		cfg.height = GameProperties.SCALE_HEIGHT;

		new LwjglApplication(new Project(GameState.INGAME), cfg);

		
	}
	
}
