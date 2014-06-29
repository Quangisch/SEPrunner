package core;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;

import core.GameProperties.GameScreen;
import core.GameProperties.GameState;

public class ResourceManager extends AssetManager {
	
	private static ResourceManager manager;
	private ScheduledExecutorService exec;
	private List<Music> currentMusic;
	private List<Music> fadeOuts;
	private List<Music> currentSounds;
	
	private GameState currentMusicState;
	private GameScreen currentMusicScreen;
	
	private ResourceManager() {
		fadeOuts = new LinkedList<Music>();
		currentSounds = new LinkedList<Music>();
		currentMusic = new LinkedList<Music>();
		
		exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate(new FadeOut(), 20, 200, TimeUnit.MILLISECONDS);
	}
	
	private void startAudio(List<Music> list, String filePath, boolean fadeoutPrevious) {
		if(fadeoutPrevious) {
			for(Music m : list)
				fadeOuts.add(m);
			list.clear();
		}
		
		list.add(0,Gdx.audio.newMusic(Gdx.files.local(filePath)));
		
		list.get(0).setVolume(list.equals(currentMusic) ? GameProperties.musicVolume : GameProperties.soundVolume);
		list.get(0).setLooping(true);
		list.get(0).play();
	}
	
	private float prevVolume;
	public void startMusic(GameState state) {
		if(!state.equals(currentMusicState))
			switch(state) {
			case LOSE:
				startAudio(currentMusic, FilePath.music_lose, true);
				break;
			case NORMAL:
				if(prevVolume != 0 && currentMusic.size() > 0) {
					currentMusic.get(0).setVolume(prevVolume);
					currentSounds.get(0).setVolume(GameProperties.soundVolume);
					prevVolume = 0;
				}
				break;
			case PAUSE:
				prevVolume = currentMusic.get(0).getVolume();
				currentMusic.get(0).setVolume(prevVolume * 0.3f);
				currentSounds.get(0).setVolume(GameProperties.soundVolume * 0.3f);
				break;
			case WIN:
				startAudio(currentMusic, FilePath.music_win, true);
				break;
			default:
				break;
			}
		currentMusicState = state;
	}
	
	public void startMusic(GameScreen screen) {
		if(currentMusicScreen == null
				|| screen.INDEX >= 0
				|| (!screen.equals(currentMusicScreen) && screen.INDEX > 0)
				|| (screen.INDEX * currentMusicScreen.INDEX <= 0)) {

			switch(screen) {
			case LEVEL1:
			case LEVEL2:
			case LEVEL3:
				startAudio(currentMusic, FilePath.music_soundScape, true);
				startAudio(currentSounds, FilePath.sound_atmo, true);
				break;
			case MENU_BACKGROUND:
			case MENU_HIGHSCORE:
			case MENU_LEVELSELECT:
			case MENU_MAIN:
			case MENU_OPTION:
			case MENU_PROFILE:
			case MENU_SPLASH:
				startAudio(currentMusic, FilePath.music_menu, true);
				startAudio(currentSounds, FilePath.sound_atmo, true);
				break;
			default:
				break;
			
			}
		}
		currentMusicScreen = screen;
	}
	
	public void adjustMusicVolume() {
		if(currentMusic != null)
			currentMusic.get(0).setVolume(GameProperties.musicVolume);
	}
	
	public void adjustSoundVolume() {
		for(Music s : currentSounds)
			s.setVolume(GameProperties.soundVolume);
	}
	
	public static ResourceManager resetInstance() {
		if(manager != null)
			manager.dispose();
		manager = null;
		return getInstance();
	}
	
	public static ResourceManager getInstance() {
		if(manager == null)
			manager = new ResourceManager();
		return manager;
	}
	
	public void dispose() {
		super.dispose();
		for(Music m : fadeOuts)
			m.dispose();
		for(Music m : currentSounds)
			m.dispose();
		for(Music m : currentMusic)
			m.dispose();
	}
	
	private class FadeOut implements Runnable {
		private FadeOut() {
			
		}
		
		public void run() {
			for(Music m : fadeOuts) {
				m.setVolume(Math.max(m.getVolume()-0.05f, 0));
				
				if(m.getVolume() <= 0) {
					m.stop();
					m.dispose();
					fadeOuts.remove(m);
					break;
				}
			}
		}
		
	}
	

}
