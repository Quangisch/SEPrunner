package gameObject.interaction.enemy.ai;

import gameObject.body.BodyObject;
import gameObject.body.Sensor;
import gameObject.interaction.enemy.Alarm;

import com.badlogic.gdx.Gdx;

import core.ingame.input.KeyMap.ActionKey;

public class HardAI extends EnemyAI {

	private final int SCAN_SPEED = 3, SCAN_LENGTH = 600;
	private float scanTime;
	private final float SCAN_TIME_MAX = 5;
	
	public HardAI() {
		super(3, 1.5f);
	}
	
	@Override
	public void run() {
		super.run();
		if (getEnemy() == null) 
			return;
	}
	
	protected boolean resolveAction() {
		switch(unresolvedAction) {
		case ALARM_TRIGGERD:
			break;
		case HIT_BY_SHURIKEN:
			
			if(Alarm.isActive()) {
				scanTime = 0;
				Alarm.trigger(3);
				currentAction.clear();
				storedActions.clear();
				unresolvedAction = UnresolvedAction.ALARM_TRIGGERD;
				keyDown(getEnemy().getGameWorld().getPlayer().getBodyObject().getX() 
						< getEnemy().getBodyObject().getX() ? ActionKey.LEFT : ActionKey.RIGHT);
				break;
			} 
			
			if(scanTime == 0) {
				getEnemy().getAnimationObject().setFlip(triggerX > lastX);
				
				for(ActionKey a : currentAction)
					storedActions.add(a);
				currentAction.clear();
				scanTime += Gdx.graphics.getDeltaTime();
				return false;
				
			} else if(scanTime < SCAN_TIME_MAX) {
				getEnemy().scanArea(getEnemy().getGameWorld().getPlayer().getBodyObject().getX(), getEnemy().getGameWorld().getPlayer().getBodyObject().getY(), SCAN_LENGTH, SCAN_SPEED);
				scanTime += Gdx.graphics.getDeltaTime();
				return false;
				
			} else {
				scanTime = 0;
				for(ActionKey a : storedActions)
					currentAction.add(a);
				storedActions.clear();
				
				unresolvedAction = UnresolvedAction.NORMAL;
				return true;
			}
		case NORMAL:
			break;
		case SEE_PLAYER:
			break;
		case SEE_STUNNED_ENEMY:
			break;
		default:
			break;
		
		}
		
		scanTime = 0;
		return super.resolveAction();
	}

	@Override
	public boolean handleCollision(boolean start, boolean postSolve, Sensor mySensor, BodyObject other, Sensor otherSensor) {
		boolean handled =  super.handleCollision(start, postSolve, mySensor, other, otherSensor); //overwrite if necessery
		
		if(!handled) {
			
			if(!postSolve) {
			
				
				
			}
		}
		
		return handled;
	}
	
}
