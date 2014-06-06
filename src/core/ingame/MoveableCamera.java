package core.ingame;

import gameObject.body.ICollisionable;

import com.badlogic.gdx.math.Vector2;

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
