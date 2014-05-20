package gameObject.enemy;

import gameObject.player.Player;
import gameObject.statics.Hideable;

import com.badlogic.gdx.math.Vector2;

public interface Stunnable {

	/**
	 * Check whether Object is stunned.
	 * @return
	 */
	public boolean isStunned();
	
	/**
	 * Set stunned.
	 * @param shuriken
	 */
	public void setStun();
	
	/**
	 * Check whether Object is in position to be carried.
	 * @return
	 */
	public boolean isCarriable(Vector2 position);
	
	/**
	 * Attach Object to Player to carry.
	 * @param player
	 */
	public void attachToCarrier(Player player);
	
	/**
	 * Detach Object from Carrier.
	 * @param player
	 */
	public void detachFromCarrier(Player player);
	
	/**
	 * Dispose/Hide behind Hideable-Object.
	 * @param hideable
	 * @return
	 */
	public boolean disposeAndHide(Hideable hideable);
	
	
	
}
