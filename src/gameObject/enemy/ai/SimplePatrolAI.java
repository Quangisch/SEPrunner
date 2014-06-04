package gameObject.enemy.ai;

import misc.Debug;
import misc.Debug.Mode;
import gameObject.GameObject;
import gameObject.ISensorTypes;
import gameObject.Sensor;

import com.badlogic.gdx.utils.JsonValue;

public class SimplePatrolAI extends EnemyAI {

	float leftX, rightX;

	@Override
	public void init(JsonValue jsonValue) {
		leftX = jsonValue.getFloat(0);
		rightX = jsonValue.getFloat(1);
	}

	@Override
	public void run() {

	}

	@Override
	public boolean handleCollision(boolean start, Sensor sender, GameObject other, Sensor otherSensor) {
		if (sender != null && sender.getSensorType() == ISensorTypes.SensorTypes.VISION)
			Debug.print("Seeingln", Mode.CONSOLE);
		return false;
	}

}
