package launcher;

import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import core.GameProperties;
import core.GameProperties.GameState;
import core.Project;

public class LauncherQuang {
	
	public static void main(String[] args) {

		GameProperties.setGameState(GameState.MENU);
//		GameProperties.initFromFile();
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "SEPrunner";
		cfg.useGL20 = true;
		cfg.resizable = false;
		cfg.width = GameProperties.SCALE_WIDTH;
		cfg.height = GameProperties.SCALE_HEIGHT;
		
		
		System.out.println("Avaiable DisplayModes: ");
		int j = 0;
		for(DisplayMode m : LwjglApplicationConfiguration.getDisplayModes())
			System.out.println("Mode "+j+++": "+m.toString());
		
//		TODO select mode depending on screen ratio
		int mode = 0;
		
		GameProperties.displayMode = LwjglApplicationConfiguration.getDisplayModes()[mode];
		System.out.println("\n>>Selected Mode "+mode+": "+GameProperties.displayMode.toString());
		
		new LwjglApplication(new Project(), cfg);
		
//		Highscore.getInstance().loadHighscoreList();
//		List<Score> l = Highscore.getInstance().getHighscoreList(2);
//		for(int i = 0; i < l.size(); i++)
//			System.out.println("rank:"+i+" name:"+l.get(i).PLAYER_NAME+" time:"+l.get(i).TIME_STRING);
	}
	
}
