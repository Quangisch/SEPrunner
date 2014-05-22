package core.ingame;

import gameObject.DrawableAnimated;
import gameObject.DrawableStatic;

import java.util.List;

import com.badlogic.gdx.physics.box2d.World;

public interface Manageable {
	
	public boolean addDrawableStatic(DrawableStatic drawable);
	public boolean removeDrawableStatic(DrawableStatic drawable);
	public void clearDrawableStatic();
	public List<DrawableStatic> getDrawableStatic();
	
	public boolean addDrawableAnimated(DrawableAnimated drawable);
	public boolean removeDrawableAnimated(DrawableAnimated drawable);
	public void clearDrawableAnimated();
	public List<DrawableAnimated> getDrawableAnimated();
	
	public boolean addWorld(World world);
	public boolean removeWorld(World world);
	public void clearWorlds();
	public List<World> getWorlds();
	
}
