package core.ingame;

import gameObject.DrawableAnimated;
import gameObject.DrawableStatic;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.World;

public class GameManager implements Manageable {

	private static GameManager manager;
	
	private List<DrawableStatic> drawableStatic = new ArrayList<DrawableStatic>();
	private List<DrawableAnimated> drawableAnimated = new ArrayList<DrawableAnimated>();
	private List<World> worlds = new ArrayList<World>();
	
	private GameManager() {
		
	}
	
	public static GameManager getInstance() {
		if(manager == null)
			manager = new GameManager();
		return manager;
	}
	
	public synchronized boolean addDrawableStatic(DrawableStatic drawable) {
		return drawableStatic.add(drawable);
	}
	
	public synchronized boolean removeDrawableStatic(DrawableStatic drawable) {
		return drawableStatic.remove(drawable);
	}
	
	public void clearDrawableStatic() {
		drawableStatic.clear();
	}
	
	public List<DrawableStatic> getDrawableStatic() {
		return drawableStatic;
	}
	
	public synchronized boolean addDrawableAnimated(DrawableAnimated drawable) {
		return drawableAnimated.add(drawable);
	}
	
	public synchronized boolean removeDrawableAnimated(DrawableAnimated drawable) {
		return drawableAnimated.remove(drawable);
	}
	
	public void clearDrawableAnimated() {
		drawableAnimated.clear();
	}
	
	public List<DrawableAnimated> getDrawableAnimated() {
		return drawableAnimated;
	}
	
	public synchronized boolean addWorld(World world) {
		return worlds.add(world);
	}
	
	public synchronized boolean removeWorld(World world) {
		return worlds.remove(world);
	}
	
	public void clearWorlds() {
		worlds.clear();
	}
	
	public List<World> getWorlds() {
		return worlds;
	}
	
	public void toogleFullscreen() {
		if(Gdx.graphics.isFullscreen())
			Gdx.graphics.setDisplayMode(GameProperties.width, GameProperties.height, false);
		else
			setFullscreen();
	}
	
	public void setFullscreen() {
		if(Gdx.graphics.isFullscreen())
			return;
		Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width, Gdx.graphics.getDesktopDisplayMode().height, true);
	}

}
