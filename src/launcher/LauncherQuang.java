package launcher;

import menu.Project;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import core.ingame.GameProperties;
import core.ingame.GameProperties.GameState;

public class LauncherQuang {
	
	public static void main(String[] args) {
		


		GameProperties.setGameState(GameState.INGAME);
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "LibGDXTest";
		cfg.useGL20 = true;
		cfg.width = GameProperties.width /2;
		cfg.height = GameProperties.height /2;
//		cfg.fullscreen = true;

		new LwjglApplication(new Project(), cfg);
		
		}
}
