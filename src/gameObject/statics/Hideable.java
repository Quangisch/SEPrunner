package gameObject.statics;

import gameObject.Moveable;

public interface Hideable {
	
	/**
	 * Check whether MoveableObject is in position to hide.
	 * @param Moveable
	 * @return hideable
	 */
	public boolean canHide(Moveable moveableObject);
	
	
	
	

}
