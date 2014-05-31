package launcher;

import menu.Project;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import core.ingame.GameProperties;

public class LauncherAlex {
	
	public static void main(String[] args) {

		GameProperties.switchMode(true, false);
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = Project.TITLE + " v" + Project.VERSION; //changed by menu
		cfg.vSyncEnabled = true; //changed by menu, saves some cpu
		cfg.useGL20 = true;	//changed by menu
		cfg.width = GameProperties.width;
		cfg.height = GameProperties.height;
//		cfg.fullscreen = true;
		
		
		//new LwjglApplication(GameRender.getInstance(), cfg);        taken out by menu
		new LwjglApplication(new Project(), cfg);
	}							//start view project Z11 = first img set
}
