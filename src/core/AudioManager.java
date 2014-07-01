package core;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import misc.Debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Music.OnCompletionListener;
import com.badlogic.gdx.utils.Disposable;

import core.GameProperties.GameScreen;
import core.GameProperties.GameState;

public class AudioManager implements Runnable, OnCompletionListener, Disposable {
	
	private static AudioManager manager;
	private ScheduledExecutorService exec;
	private List<MusicID> currentMusic;
	private List<MusicID> fadeOuts;
	private List<MusicID> currentSounds;
	
	private GameState currentMusicState;
	private GameScreen currentMusicScreen;
	
	private AudioManager() {
		fadeOuts = new LinkedList<MusicID>();
		currentSounds = new LinkedList<MusicID>();
		currentMusic = new LinkedList<MusicID>();
		
		exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate(this, 20, 200, TimeUnit.MILLISECONDS);
	}
	
	public void run() {
		try {
			manageMusic();
			fadeOut();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void manageMusic() {
		if(currentMusic.size() <= 0 || currentMusicScreen == null)
			return;
		Debug.println("playing: "+currentMusic.get(0).toString(), Debug.Mode.CONSOLE);
		for(MusicID m : currentMusic) {
			if(currentMusicScreen.equals(GameScreen.LEVEL3) && !m.isLooping() && m.getPosition() >= 96) {
				Debug.println("triggerNew: "+currentMusic.get(0).toString(), Debug.Mode.CONSOLE);
				startAudio(currentMusic, m.FILEPATH, false, false);
				currentMusic.remove(m);
				break;
			}
		}
	}
	
	private void fadeOut() {
		if(fadeOuts.size() <= 0)
			return;
		
		for(MusicID m : fadeOuts) {
			m.setVolume(Math.max(m.getVolume()-0.05f, 0));
			Debug.println("fadout: "+m.toString(), Debug.Mode.CONSOLE);
			if(m.getVolume() <= 0) {
				m.stop();
				m.dispose();
				fadeOuts.remove(m);
				break;
			}
		}
	}
	
	private void startAudio(List<MusicID> list, String filePath, boolean fadeoutPrevious) {
		startAudio(list, filePath, fadeoutPrevious, true);
	}
	
	private void startAudio(List<MusicID> list, String filePath, boolean fadeoutPrevious, boolean loop) {
		if(fadeoutPrevious) {
			for(MusicID m : list)
				fadeOuts.add(m);
			list.clear();
		}
		
		list.add(0,new MusicID(filePath));
		
		list.get(0).setVolume(list.equals(currentMusic) ? GameProperties.musicVolume : GameProperties.soundVolume);
		list.get(0).setLooping(fadeoutPrevious && loop);
		list.get(0).play();
	}
	
	private float prevVolume;
	private void startMusic(GameState state) {
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
	
	private void startMusic(GameScreen screen) {
		if(currentMusicScreen == null
				|| screen.INDEX >= 0
				|| (!screen.equals(currentMusicScreen) && screen.INDEX > 0)
				|| (screen.INDEX * currentMusicScreen.INDEX <= 0)) {

			startAudio(currentSounds, FilePath.sound_atmo, true);
			switch(screen) {
			case LEVEL1:
			case LEVEL2:
				startAudio(currentMusic, FilePath.music_soundScape, true);
				break;
			case LEVEL3:
				startAudio(currentMusic, FilePath.music_nAction, true, false);
				break;
			case MENU_BACKGROUND:
			case MENU_HIGHSCORE:
			case MENU_LEVELSELECT:
			case MENU_MAIN:
			case MENU_OPTION:
			case MENU_PROFILE:
			case MENU_SPLASH:
				startAudio(currentMusic, FilePath.music_menu, true);
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
	
	public static AudioManager resetInstance() {
		if(manager != null)
			manager.dispose();
		manager = null;
		return getInstance();
	}
	
	public static AudioManager getInstance() {
		if(manager == null)
			manager = new AudioManager();
		return manager;
	}
	
	public void dispose() {
		for(Music m : fadeOuts)
			m.dispose();
		for(Music m : currentSounds)
			m.dispose();
		for(Music m : currentMusic)
			m.dispose();
	}
	
	@Override
	public void onCompletion(Music music) {
		if(!music.isLooping()) {
			music.stop();
			music.dispose();
		}
	}
	
	private static class MusicID implements Music {
		final private String FILEPATH;
		final private Music MUSIC;
		final private int ID;
		private static int idCount = 0;
		
		private MusicID(String filePath) {
			this.FILEPATH = filePath;
			MUSIC = Gdx.audio.newMusic(Gdx.files.internal(filePath));
			ID = idCount++;
			MUSIC.setOnCompletionListener(getInstance());
			
		}
		@Override
		public void play() 	{ MUSIC.play(); }
		public void pause() { MUSIC.pause();}
		public void stop() 	{ MUSIC.stop();	}
		public float getPosition() 	{ return MUSIC.getPosition(); 	}
		public void dispose() 		{ MUSIC.dispose();				}
		public boolean isPlaying() 			{ return MUSIC.isPlaying();	}
		public boolean isLooping() 			{ return MUSIC.isLooping(); }
		public void setVolume(float volume) { MUSIC.setVolume(volume);	}
		public float getVolume() 			{ return MUSIC.getVolume();	}
		
		public void setPan(float pan, float volume) {
			MUSIC.setPan(pan, volume);
		}
		
		public void setLooping(boolean isLooping) {
			MUSIC.setLooping(isLooping);
		}
		
		public void setOnCompletionListener(OnCompletionListener listener) {
			MUSIC.setOnCompletionListener(listener);
		}
		
		public boolean equals(Object object) {
			if(object == null || !(object instanceof MusicID))
				return false;
			MusicID m = (MusicID) object;
			return m.FILEPATH.equals(FILEPATH) && MUSIC.getPosition() == m.MUSIC.getPosition();
		}
		
		public String toString() {
			return String.format("(%d) %s is%splaying @Position: %f @Volume: %f %s", 
					ID, FILEPATH, (isPlaying() ? " " : " NOT "), getPosition(), getVolume(), (isLooping() ? "is looping" : "")); 
		}
		
	}

	public static class AudioStarter implements Runnable {

		private GameScreen screen;
		private GameState state;
		
		public AudioStarter(GameScreen screen) {
			this.screen = screen;
		}
		
		public AudioStarter(GameState state) {
			this.state = state;
		}
		
		@Override
		public void run() {
			if(screen != null)
				getInstance().startMusic(screen);
			else if(state != null)
				getInstance().startMusic(state);
		}
		
	}
	
}
