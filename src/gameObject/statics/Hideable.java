package gameObject.statics;

import gameObject.Collisionable;

public interface Hideable {
	
	/**
	 * Check whether MoveableObject is in position to hide.
	 * @param Collisionable
	 * @return hideable
	 */
	public boolean canHide(Collisionable moveableObject);
	
	
	
	

}
