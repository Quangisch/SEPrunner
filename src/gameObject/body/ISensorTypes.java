package gameObject.body;

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
}
