package launcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.HighscoreServer;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import core.GameProperties;
import core.GameProperties.GameScreen;
import core.Project;

public class MainLauncher {
	
	public static void main(String[] args) {
		new MainLauncher(args);
	}
	
	private LwjglApplicationConfiguration cfg;
	private BufferedReader consoleInput;
	private GameScreen startScreen;
	
	private MainLauncher(String[] args) {
		
		cfg = new LwjglApplicationConfiguration();
		cfg.title = "SEPrunner";
		cfg.resizable = false;
		cfg.width = GameProperties.SCALE_WIDTH;
		cfg.height = GameProperties.SCALE_HEIGHT;
		
		
		cfg.useGL20 = true;
		cfg.fullscreen = false;
		
		GameProperties.deleteUserFiles = false;
		GameProperties.offline = false;
		GameProperties.debug = false;
		GameProperties.fixedWorldStep = false;
		GameProperties.lowQuality = false;
		startScreen = GameScreen.MENU_HIGHSCORE;
		
		
		new HighscoreServer().updateLocalHighscoreFile();
		GameProperties.initPrefDisplayMode();
		checkArguments(args);

		if(cfg.fullscreen) {
			cfg.width = GameProperties.prefDisplayMode.width;
			cfg.height = GameProperties.prefDisplayMode.height;
		}
		
		new LwjglApplication(new Project(startScreen), cfg);
	}
	
	
	
	private void checkArguments(String[] args) {
		if(args == null)
			return;
		
		boolean invalidArgument = false;
		for(int i = 0; i < args.length; i++) {
			
			if(args[i].compareTo("-f") == 0) {
				cfg.fullscreen = true;
			
			} else if(args[i].compareTo("-gl1x") == 0) {
				cfg.useGL20 = false;
			
			} else if(args[i].compareTo("-r") == 0) {
				System.out.println("Reset Profiles and local Highscore? [yes/no]");
				String input = "";
				
				while(!handleInput(input))
					input = parseInput();
				
			} else if(args[i].compareTo("-fixed") == 0) {
					GameProperties.fixedWorldStep = true;	
				
			} else if(args[i].compareTo("-d") == 0) {
				GameProperties.debug = true;
				
			} else if(args[i].compareTo("-l") == 0) {
				GameProperties.lowQuality = true;	
			
			} else if(args[i].compareTo("-o") == 0) {
				GameProperties.offline = true;	
			
			} else if(args[i].compareTo("-s") == 0) {
				if(++i > args.length)
					invalidArgument = true;
				
				if(args[i].compareTo("menu") == 0)
					startScreen = GameScreen.MENU_MAIN;
				else if(args[i].compareTo("level1") == 0)
					startScreen = GameScreen.LEVEL1;
				else if(args[i].compareTo("level2") == 0)
					startScreen = GameScreen.LEVEL2;
				else if(args[i].compareTo("level3") == 0)
					startScreen = GameScreen.LEVEL3;
				else if(args[i].compareTo("main") == 0)
					startScreen = GameScreen.MENU_MAIN;
				else if(args[i].compareTo("levelSelect") == 0)
					startScreen = GameScreen.MENU_LEVELSELECT;
				else if(args[i].compareTo("profile") == 0)
					startScreen = GameScreen.MENU_PROFILE;
				else if(args[i].compareTo("option") == 0)
					startScreen = GameScreen.MENU_OPTION;
				else if(args[i].compareTo("highscore") == 0)
					startScreen = GameScreen.MENU_HIGHSCORE;
				else
					invalidArgument = true;
				
			} else if(args[i].compareTo("--help") == 0) {
				System.out.println("-s startScreen\n\tlevel1\n\tlevel2\n\tlevel3\n\tmain\n\tlevelSelect\n\tprofile\n\toption\n\thighscore");
				System.out.println("-r resetProfile");
				System.out.println("-f fullscreen");
				System.out.println("-gl1x useGL1.x");
				System.out.println("-d debug");
				System.out.println("-o offline");
				System.out.println("-fixed worldStep fixed at 1/60");
				System.out.println("-l lowQuality");
				System.exit(0);
			} else
				invalidArgument = true;
			
			if(invalidArgument) {
				System.err.println("Invalid Arguments");
				System.exit(-1);
			}
		
		} //for
	}
	
	private String parseInput() {
		consoleInput = new BufferedReader(new InputStreamReader(System.in));
		try {
			return consoleInput.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private boolean handleInput(String input) {
		if(input.compareTo("yes") == 0) { 
			GameProperties.resetUserData();
			System.out.println("\nProfiles and local Highscore deleted");
			return true;
		} else if(input.compareTo("no") == 0) {
			System.out.println("\nAbort Deletion");
			return true;
		} else 
			return false;
	}
	
}
