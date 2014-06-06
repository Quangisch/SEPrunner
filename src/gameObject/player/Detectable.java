package gameObject.player;

import gameObject.IInteractionHandler;
import gameObject.enemy.Enemy;

public interface Detectable extends IInteractionHandler {
	
	/**
	 * Check whether Object is detectable by enemy.
	 * Object not detectable if in hiding or outside of enemy sight.
	 * @param enemy
	 * @return detectable
	 */
	public boolean isDetectable(Enemy enemy);
	
	/**
	 * Set Player captured.
	 * @param enemy
	 */
	public void setCaptured(Enemy enemy);
	
}
