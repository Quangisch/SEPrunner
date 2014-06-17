package launcher;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import core.GameProperties;
import core.Project;
import core.GameProperties.GameState;

public class LauncherAlex {
	
	public static void main(String[] args) {

		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = Project.TITLE + " v" + Project.VERSION;
		cfg.vSyncEnabled = true; //saves some cpu -> save in option menu
		cfg.useGL20 = true;
		cfg.width = GameProperties.SCALE_WIDTH;
		cfg.height = GameProperties.SCALE_HEIGHT;
//		cfg.fullscreen = true;
		
		int mode = 0;
		GameProperties.displayMode = LwjglApplicationConfiguration.getDisplayModes()[mode];
		System.out.println("DisplayMode "+mode+": "+GameProperties.displayMode.toString());
		
		new LwjglApplication(new Project(GameState.MENU), cfg);
	}							//start view project Z11 = first img set
}
