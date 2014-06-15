package core;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;

public class ResourceManager extends AssetManager {
	
	private static ResourceManager manager;
	private ScheduledExecutorService exec;
	private List<Music> currentMusic;
	private List<Music> currentSounds;
	
	private ResourceManager() {
		currentMusic = new LinkedList<Music>();
		currentSounds = new LinkedList<Music>();
	}
	
	public void startMusic() {
		switch(GameProperties.getGameState()) {
		case INGAME:
			currentMusic.add(Gdx.audio.newMusic(Gdx.files.internal(FilePath.music_soundScape)));
			break;
		case INGAME_LOSE:
			currentMusic.add(Gdx.audio.newMusic(Gdx.files.internal(FilePath.music_lose)));
			break;
		case INGAME_PAUSE:
			break;
		case INGAME_WIN:
			currentMusic.add(Gdx.audio.newMusic(Gdx.files.internal(FilePath.music_win)));
			break;
		case MENU:
			currentMusic.add(Gdx.audio.newMusic(Gdx.files.internal(FilePath.music_menu)));
			break;
		default:
			break;
		}
		
		int currentMusicIndex = currentMusic.size()-1;
		currentMusic.get(currentMusicIndex).setVolume(GameProperties.musicVolume);
		currentMusic.get(currentMusicIndex).play();
		fadeOutPrevious(currentMusicIndex);
	}
	
	private void fadeOutPrevious(int index) {
		if(index <= 0)
			return;
		exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate(new FadeOut(index), 20, 100, TimeUnit.MILLISECONDS);
	}
	
	public void adjustMusicVolume() {
		if(!currentMusic.isEmpty())
			currentMusic.get(currentMusic.size()-1).setVolume(GameProperties.musicVolume);
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
		for(Music m : currentMusic)
			m.dispose();
		for(Music m : currentSounds)
			m.dispose();
	}
	
	private class FadeOut implements Runnable {
		private int index, removed;
		private FadeOut(int index) {
			this.index = index;
		}
		public void run() {
			
//			FADEOUT
			for(int i = 0; i < index-removed; i++) {
				Music m = currentMusic.get(i);
				m.setVolume(Math.max(m.getVolume()-0.05f, 0));
				if(m.getVolume() <= 0) {
					currentMusic.get(i).stop();
					removed++;
				}
			}
			
//			CLEANUP
			if(removed == index) {
				for(int i = 0; i < removed; i++) {
					currentMusic.get(0).dispose();
					currentMusic.remove(0);
				}
				exec.shutdown();
			}
		}
	}

}
