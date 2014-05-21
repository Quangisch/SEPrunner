package gameObject.statics;

import com.badlogic.gdx.math.Vector2;

public interface Hookable {

	/**
	 * Check whether object is in Position to hook.
	 * @param currentPosition
	 * @return hookable
	 */
	public boolean isHookable(Vector2 position);
	
	/**
	 * Get Hooking Point.
	 * @return hookPoint
	 */
	public Vector2 getHookPoint();
	
}
