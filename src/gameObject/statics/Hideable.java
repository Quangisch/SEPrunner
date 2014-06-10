package gameObject.statics;

import gameObject.body.IBodyInitializer;

public interface Hideable {
	
	/**
	 * Check whether MoveableObject is in position to hide.
	 * @param IBodyInitializer
	 * @return hideable
	 */
	public boolean canHide(IBodyInitializer moveableObject);
	
	
	
	

}
