package gameObject.enemy.ai;

import gameObject.GameObject;
import gameObject.IGameObjectTypes;
import gameObject.ISensorTypes;
import gameObject.Sensor;
import gameObject.player.Shuriken;
import misc.Debug;
import misc.Debug.Mode;

import com.badlogic.gdx.utils.JsonValue;

import core.ingame.InputHandler.Click;
import core.ingame.KeyMap;
import core.ingame.KeyMap.ActionKey;

public class SimplePatrolAI extends EnemyAI {

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
	public boolean handleCollision(boolean start, Sensor sender, GameObject other, Sensor otherSensor) {
		if (sender != null && sender.getSensorType() == ISensorTypes.SensorTypes.VISION)
			Debug.print("Seeingln", Mode.CONSOLE);
		if(other.getGameObjectType() == IGameObjectTypes.GameObjectTypes.SHURIKEN)
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
