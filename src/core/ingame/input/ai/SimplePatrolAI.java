package core.ingame.input.ai;

import gameObject.body.BodyObject;
import gameObject.body.Sensor;

public class SimplePatrolAI extends EnemyAI {

	public SimplePatrolAI() {
		super(1,1);
	}
	
	@Override
	public void run() {
		super.run();
		if (getEnemy() == null) 
			return;
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
