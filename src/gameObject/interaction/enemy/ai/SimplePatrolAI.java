package gameObject.interaction.enemy.ai;

import gameObject.body.BodyObject;
import gameObject.body.GameObjectType;
import gameObject.body.ISensorTypes;
import gameObject.body.ISensorTypes.SensorTypes;
import gameObject.body.Sensor;
import misc.Debug;
import misc.Debug.Mode;

import com.badlogic.gdx.utils.JsonValue;

import core.ingame.input.InputHandler.Click;
import core.ingame.input.KeyMap;
import core.ingame.input.KeyMap.ActionKey;

public class SimplePatrolAI extends EnemyAI {

	@SuppressWarnings("unused")
	private KeyMap keyMap;

	float leftX, rightX;

	@Override
	public void init(JsonValue jsonValue) {
		leftX = jsonValue.getFloat(0);
		rightX = jsonValue.getFloat(1);
	}

	@Override
	public void run() {
		if (getEnemy() == null) return;
	}

	@Override
	public boolean handleCollision(boolean start, Sensor sender, BodyObject other, Sensor otherSensor) {
		if (sender != null 
				&& sender.getSensorType() == SensorTypes.VISION_LEFT
				&& sender.getSensorType() == SensorTypes.VISION_RIGHT)
			Debug.print("Seeingln", Mode.CONSOLE);
		if(other.getGameObjectType().equals(GameObjectType.Shuriken))
			Debug.println("hit by Shuriken");
		return false;
	}

	@Override
	public void addActionKey(ActionKey action, int... keys) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isKeyDown(ActionKey action) {
		return action == ActionKey.CROUCH //
				|| action == ActionKey.RIGHT; // || action == ActionKey.RUN;
	}

	@Override
	public boolean isButtonDown(ActionKey action) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(ActionKey action) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Click popClick() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Click getClick() {
		// TODO Auto-generated method stub
		return null;
	}

}
