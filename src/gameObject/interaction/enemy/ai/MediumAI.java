package gameObject.interaction.enemy.ai;

import gameObject.body.BodyObject;
import gameObject.body.Sensor;
import gameObject.interaction.enemy.Alarm;

import com.badlogic.gdx.Gdx;

import core.ingame.input.KeyMap.ActionKey;

public class MediumAI extends EnemyAI {

	public MediumAI() {
		super(2.5f,1.5f);
	}
	
	@Override
	public void run() {
		super.run();
		if (getEnemy() == null) 
			return;

	}
	
	private boolean hit;
	
	protected boolean resolveAction() {
		switch(unresolvedAction) {
		case ALARM_TRIGGERD:
			break;
		case HIT_BY_SHURIKEN:
			if(!hit){
				if(getEnemy().getGameWorld().getPlayer().isHiding())
					break;
				
				hit = true;
				Alarm.getInstance().trigger(3);
				
				storedActions.clear();
				for(ActionKey a : currentAction)
					storedActions.add(a);
				currentAction.clear();
				
				if(Gdx.graphics.isGL20Available())
					getEnemy().getView().setActive(false);
				
				return false;
			}

			currentAction.add(ActionKey.CROUCH);
			
			if(!Alarm.getInstance().isActive()){
				if(Gdx.graphics.isGL20Available())
					getEnemy().getView().setActive(true);
				
				currentAction.clear();
				for(ActionKey a : storedActions)
					currentAction.add(a);
				storedActions.clear();
				
				hit = false;
				break;
			} else
				return false;
		case NORMAL:
			break;
		case SEE_PLAYER:
			break;
		case SEE_STUNNED_ENEMY:
			break;
		default:
			break;

		}
		
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
