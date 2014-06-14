package launcher;

import menu.Project;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import core.ingame.GameProperties;
import core.ingame.GameProperties.GameState;

public class LauncherQuang {
	
	public static void main(String[] args) {

		GameProperties.setGameState(GameState.MENU);
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "LibGDXTest";
		cfg.useGL20 = true;
		cfg.resizable = false;
//		cfg.fullscreen = true;
		
		int mode = 0;
		GameProperties.displayMode = LwjglApplicationConfiguration.getDisplayModes()[mode];
		System.out.println("Mode"+mode+": "+GameProperties.displayMode.toString());
		
		new LwjglApplication(new Project(), cfg);

		
	}
	
}
