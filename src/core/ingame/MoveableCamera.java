package core.ingame;

import gameObject.body.IBodyInitializer;

import com.badlogic.gdx.math.Vector2;

public interface MoveableCamera {

	void update();
	
	/**
	 * Set moveable target to follow.
	 * @param moveable
	 */
	void setToFollowMoveable(IBodyInitializer moveable);
	
	Vector2 unproject(Vector2 vec);
	
	Vector2 project(Vector2 vec);
	
}
