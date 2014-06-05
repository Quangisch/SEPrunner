package launcher;

import menu.Project;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import core.ingame.GameProperties;
import core.ingame.GameProperties.GameState;

public class LauncherAlex {
	
	public static void main(String[] args) {

		GameProperties.setGameState(GameState.MENU);
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = Project.TITLE + " v" + Project.VERSION;
		cfg.vSyncEnabled = true; //saves some cpu -> save in option menu
		cfg.useGL20 = true;
		cfg.width = GameProperties.width;
		cfg.height = GameProperties.height;
		
		new LwjglApplication(new Project(), cfg);
	}							//start view project Z11 = first img set
}
