package core;

import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;

public class ResourceManager extends AssetManager {
	
	private static ResourceManager manager;
	private ScheduledExecutorService exec;
	private Music currentMusic;
	private List<Music> fadeOuts;
	private List<Music> currentSounds;
	
	private ResourceManager() {
		fadeOuts = new CopyOnWriteArrayList<Music>();
		currentSounds = new LinkedList<Music>();
		
		exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate(new FadeOut(), 20, 100, TimeUnit.MILLISECONDS);
	}
	
	public void startMusic() {
		
		if(currentMusic != null)
			fadeOuts.add(currentMusic);
		if(GameProperties.isInMenu()) {
			currentMusic = (Gdx.audio.newMusic(Gdx.files.local(FilePath.music_menu)));
//			Debug.println("startMenuMusic");
		} else {
			if(GameProperties.isCurrentGameState(GameProperties.GameState.NORMAL)) {
				currentMusic = (Gdx.audio.newMusic(Gdx.files.internal(FilePath.music_soundScape)));
//				Debug.println("ingameMusic");
			} else if(GameProperties.isCurrentGameState(GameProperties.GameState.WIN)) {
				currentMusic = (Gdx.audio.newMusic(Gdx.files.internal(FilePath.music_win)));
//				Debug.println("winMusic");
			} else if(GameProperties.isCurrentGameState(GameProperties.GameState.LOSE)) {
				currentMusic = (Gdx.audio.newMusic(Gdx.files.internal(FilePath.music_lose)));
//				Debug.println("loseMusic");
			}
		}
		

		
		
		currentMusic.setVolume(GameProperties.musicVolume);
		currentMusic.setLooping(true);
		currentMusic.play();

//		inMenu = GameProperties.isInMenu();
//		System.out.println(currentState + " "+ inMenu);
	}
	
	public void adjustMusicVolume() {
		if(currentMusic != null)
			currentMusic.setVolume(GameProperties.musicVolume);
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
		if(currentMusic != null)
			currentMusic.dispose();
	}
	
	private class FadeOut implements Runnable {
		private FadeOut() {
			
		}
		
		public void run() {
			try{
				for(Music m : fadeOuts) {
					m.setVolume(Math.max(m.getVolume()-0.05f, 0));
					if(m.getVolume() <= 0) {
						fadeOuts.remove(m);
						m.dispose();
					}
				}
			} catch(ConcurrentModificationException e) {
				
			}
			
		}
	}

}
