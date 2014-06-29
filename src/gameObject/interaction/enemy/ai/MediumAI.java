package gameObject.interaction.enemy.ai;

import gameObject.body.BodyObject;
import gameObject.body.Sensor;

public class MediumAI extends EnemyAI {

	public MediumAI() {
		armor = 1;
	}
	
	@Override
	public void run() {
		super.run();
		if (getEnemy() == null) 
			return;

	}
	
	protected void actionAfterHit() {
		System.out.println(this.getClass().toString()+" actionTime");
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
