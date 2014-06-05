package core.ingame;

import com.badlogic.gdx.math.Vector2;

import gameObject.ICollisionable;

public interface MoveableCamera {

	/**
	 * Set moveable target to follow.
	 * @param moveable
	 */
	public void setToFollowMoveable(ICollisionable moveable);
	
	/**
	 * Set position to jump to.
	 * @param position
	 */
	public void jumpTo(Vector2 position);
	
}
