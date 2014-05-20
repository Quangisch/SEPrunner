package gameObject.statics;

import gameObject.player.Player;

public interface Hookable {

	/**
	 * Check whether Player is in Position to hook.
	 * @param player
	 * @return hookable
	 */
	public boolean isHookable(Player player);
	
}
