package gameObject.body;

import com.badlogic.gdx.physics.box2d.FixtureDef;

public interface ISensorTypes {

	public abstract class SensorTypes {

		// MISC
		public static final int DEFAULT = 0;

		// PLAYER
		public static final int FOOT = 1;
		public static final int HEAD = 2;
		public static final int BODY = 3;
		public static final int LEFT_FOOT = 6;
		public static final int RIGHT_FOOT = 7;
		public static final int CORE = 10;

		// ENEMY
		public static final int VISION_LEFT = 11;
		public static final int VISION_RIGHT = 12;
	}
	
	/** Set if collision is handled
	 * 
	 * @param active true, if collision raises event */
	void setActive(boolean active);

	/** @return the BodyObject linked to */
	BodyObject getBodyObject();

	/** FixtureDef for internal use
	 * 
	 * @return FixtureDef */
	FixtureDef getFixtureDef();

	/** custom event data
	 * 
	 * @return event data */
	int getSensorType();

	/** Set custom event data
	 * 
	 * @param eventData the event data */
	void setSensorType(int sensorType);

	/** @return the priority */
	int getPriority();

	/** @param priority the priority to set */
	void setPriority(int priority);
}
