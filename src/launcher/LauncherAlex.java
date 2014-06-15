package launcher;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import core.GameProperties;
import core.Project;
import core.GameProperties.GameState;

public class LauncherAlex {
	
	public static void main(String[] args) {

		GameProperties.setGameState(GameState.MENU);
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = Project.TITLE + " v" + Project.VERSION;
		cfg.vSyncEnabled = true; //saves some cpu -> save in option menu
		cfg.useGL20 = true;
//		cfg.fullscreen = true;
		
		int mode = 0;
		GameProperties.displayMode = LwjglApplicationConfiguration.getDisplayModes()[mode];
		System.out.println("Mode"+mode+": "+GameProperties.displayMode.toString());
		
		new LwjglApplication(new Project(), cfg);
	}							//start view project Z11 = first img set
}
