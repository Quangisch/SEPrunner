package gameObject.body;

public interface ICollisionable {
	
	/** 
	 * Handle collision on impact.
	 * 
	 * @param start true on new impact, false on ending collision
	 * @param mySensor Sensor owned by <code>this</code> that raised the event,
	 *            maybe <code>null</code>
	 * @param other the other Object
	 * @param otherSensor the hit Sensor owned by <code>other</code>, maybe
	 *            <code>null</code>
	 * @return return whether the handling is done or should be passed to the
	 *         other */
	boolean handleCollision(boolean start, Sensor mySensor, BodyObject other, Sensor otherSensor);

}
