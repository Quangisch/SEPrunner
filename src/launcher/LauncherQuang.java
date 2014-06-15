package launcher;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import core.GameProperties;
import core.Project;
import core.GameProperties.GameState;

public class LauncherQuang {
	
	public static void main(String[] args) {

		GameProperties.setGameState(GameState.MENU);
		GameProperties.initFromFile();
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "LibGDXTest";
		cfg.useGL20 = true;
		cfg.resizable = false;
//		cfg.fullscreen = true;
		
		int mode = 0;
		GameProperties.displayMode = LwjglApplicationConfiguration.getDisplayModes()[mode];
		System.out.println("Mode"+mode+": "+GameProperties.displayMode.toString());
		
		new LwjglApplication(new Project(), cfg);
		
//		Highscore.getInstance().loadHighscoreList();
//		List<Score> l = Highscore.getInstance().getHighscoreList(2);
//		for(int i = 0; i < l.size(); i++)
//			System.out.println("rank:"+i+" name:"+l.get(i).PLAYER_NAME+" time:"+l.get(i).TIME_STRING);
	}
	
}
