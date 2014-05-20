package gameObject.player;

import gameObject.enemy.Enemy;

public interface Captureable {
	
	/**
	 * Set Player captured.
	 * @param enemy
	 */
	public void setCaptured(Enemy enemy);

}
