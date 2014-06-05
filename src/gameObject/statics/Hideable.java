package gameObject.statics;

import gameObject.ICollisionable;

public interface Hideable {
	
	/**
	 * Check whether MoveableObject is in position to hide.
	 * @param ICollisionable
	 * @return hideable
	 */
	public boolean canHide(ICollisionable moveableObject);
	
	
	
	

}
