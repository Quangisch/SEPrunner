package gameObject.enemy.ai;

import gameObject.GameObject;
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
	public boolean handleCollision(boolean start, Sensor mySender, GameObject other, Sensor otherSensor) {
		// TODO Auto-generated method stub
		return false;
	}

}
