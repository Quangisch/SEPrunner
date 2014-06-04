package gameObject.enemy.ai;

import gameObject.GameObject;
import gameObject.IInteractionStates.InteractionState;
import gameObject.Sensor;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;

import core.ingame.KeyMap;
import core.ingame.InputHandler.Click;
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
		if(getEnemy() == null)
			return;
		
		Vector2 basicForce = new Vector2(1,0);

		if(!getEnemy().getInteractionState().equals(InteractionState.WALK)) {
			getEnemy().setInteractionState(InteractionState.WALK);
			getEnemy().applyAnimation();
		}
		getEnemy().getBody().applyLinearImpulse(basicForce.scl(2), getEnemy().getLocalCenterInWorld(), true);
	}

	@Override
	public boolean handleCollision(boolean start, Sensor mySender, GameObject other, Sensor otherSensor) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addActionKey(ActionKey action, int... keys) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isKeyDown(ActionKey action) {
		// TODO Auto-generated method stub
		return false;
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
