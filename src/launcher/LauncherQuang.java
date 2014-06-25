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

//		cfg.width = 10;
//		cfg.height = 10;
		new LwjglApplication(new Project(GameState.INGAME), cfg);

//		PlayerProfile.deletePlayerProfile(0);
		
//		System.out.println((new PlayerProfile(1)).toString());
//		(new PlayerProfile(1)).deleteProfile();
		
//		Highscore.getInstance().loadHighscoreList();
//		List<Score> l = Highscore.getInstance().getHighscoreList(2);
//		for(int i = 0; i < l.size(); i++)
//			System.out.println("rank:"+i+" name:"+l.get(i).PLAYER_NAME+" time:"+l.get(i).TIME_STRING);
	}
	
}
