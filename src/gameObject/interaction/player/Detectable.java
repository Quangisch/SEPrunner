package gameObject.interaction.player;

import gameObject.interaction.enemy.Enemy;

public interface Detectable {
	
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
