package gameObject.interaction.enemy.ai;

import gameObject.body.BodyObject;
import gameObject.body.BodyObjectType;
import gameObject.body.ISensorTypes;
import gameObject.body.ISensorTypes.SensorTypes;
import gameObject.body.Sensor;
import gameObject.interaction.enemy.Alarm;
import misc.Debug;
import core.ingame.input.KeyMap.ActionKey;

public class SimplePatrolAI extends EnemyAI {

	//NILS
	
	private int armour = 1;//N�tige Shuriken Treffer
//	private GameObject player;
	//NILS

	float leftX, rightX;

	
int z = 0;
	@Override
	public void run() {
		super.run();
		if (getEnemy() == null) 
			return;
				
		//ALARM -> enemy bewegt sich schneller
		if(Alarm.isActive()){
//			getEnemy().setAI(this, 0.1f, 1.2f, 0.8f, 1.5f);//funktioniert nicht
//			getEnemy().getInteractionHandler().setForceMultiplier(1, 1.2f, 0.8f, 1.5f);
			
		}
		
		if (Alarm.isActive()) {
			keyDown(ActionKey.RUN);
		} else
			keyUp(ActionKey.RUN);

	}

	@Override
	public boolean handleCollision(boolean start, boolean postSolve, Sensor mySensor, BodyObject other, Sensor otherSensor) {
		//NILS
		//Achtung es muss abgefragt werden ob sender != null ist
		if(!postSolve) {
			
			if(other.getBodyObjectType().equals(BodyObjectType.Player) 
					&& getEnemy().getBodyObjectType().equals(BodyObjectType.Enemy)
					&& mySensor != null) {
				
				boolean meFlipped = mySensor.getBodyObject().getParent().getAnimationObject().isFlipped();
				
				//Player ber�hrt sichtfeld -> Alarm
				if((mySensor.getSensorType() == ISensorTypes.SensorTypes.VISION_LEFT && meFlipped)
							|| (mySensor.getSensorType() == ISensorTypes.SensorTypes.VISION_RIGHT && !meFlipped)){
					
						if(!other.getParent().isHiding())
							Alarm.trigger();
						else
							other.getParent().wasHiddenFrom(getEnemy());
				}
			} //player<->enemy
			
			if(other.getBodyObjectType().equals(BodyObjectType.Enemy) 
					&& !other.getParent().isHiding()
					&& getEnemy().getBodyObjectType().equals(BodyObjectType.Enemy)
					&& mySensor != null
					&&other.getParent().isStunned()){
//				core.ingame.HUD.setAlarm(true);//funktioniert nicht
//				alarm = true;
				Debug.println("Alarm");
			} //enemy<->enemy	

			//STUN
			if(mySensor != null 
					&& mySensor.getSensorType() == SensorTypes.BODY
					&& other.getBodyObjectType().equals(BodyObjectType.Shuriken)){

					other.getParent().dispose();
					armour--;
					if(armour ==0){
						getEnemy().setStun();
					}
			}
			
		} //postSolve
		return false;
	}

	
	
}
