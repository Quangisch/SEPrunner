package gameObject.interaction.enemy.ai;

import gameObject.body.BodyObject;
import gameObject.body.BodyObjectType;
import gameObject.body.ISensorTypes;
import gameObject.body.Sensor;
import gameObject.body.ISensorTypes.SensorTypes;
import gameObject.interaction.GameObject;
import gameObject.interaction.InteractionState;
import gameObject.interaction.enemy.Enemy;
import misc.Debug;

import com.badlogic.gdx.utils.JsonValue;

import core.ingame.input.InputHandler.Click;
import core.ingame.input.KeyMap.ActionKey;

public class MediumAI extends EnemyAI {

	//NILS
	private ActionKey currentAction;
	private boolean alarm;
	private InteractionState walkStyle;
	private int armour = 2;//Nötige Shuriken Treffer
//	private GameObject player;
	//NILS

	float leftX, rightX;

	@Override
	public void init(JsonValue jsonValue) {
		leftX = jsonValue.getFloat(0);
		rightX = jsonValue.getFloat(1);
		walkStyle = InteractionState.WALK_ENEMY;
	}

	@Override
	public void run() {
		if (getEnemy() == null) return;
		//NILS
		//PATROL
		if(getEnemy().getBodyObject().getX()<=leftX){
			getEnemy().getAnimationObject().flip();
			currentAction = ActionKey.RIGHT;
			getEnemy().applyInteraction(walkStyle);
			getEnemy().getBodyObject().getSensors().get(0).setActive(false);//deaktiviert linken sensor
			getEnemy().getBodyObject().getSensors().get(1).setActive(true);
		}
		if(getEnemy().getBodyObject().getX()>=rightX){
			getEnemy().getAnimationObject().flip();
			currentAction = ActionKey.LEFT;
			getEnemy().applyInteraction(walkStyle);
			getEnemy().getBodyObject().getSensors().get(1).setActive(false);//deaktiviert rechten sensor
			getEnemy().getBodyObject().getSensors().get(0).setActive(true);
		}

		//BEWEGUNGSABFOLGE
		if(getEnemy().getBodyObject().getX()>leftX && getEnemy().getBodyObject().getX()<1250 && !getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(walkStyle);
			currentAction = ActionKey.RIGHT;
		}
		if(getEnemy().getBodyObject().getX()>leftX && getEnemy().getBodyObject().getX()<1250 && getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(walkStyle);
			currentAction = ActionKey.LEFT;
		}
		if(getEnemy().getBodyObject().getX()>1250 && getEnemy().getBodyObject().getX()<1300 && !getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(InteractionState.JUMP_MOVE);
			currentAction = ActionKey.JUMP;
		}
		if(getEnemy().getBodyObject().getX()>1310 && getEnemy().getBodyObject().getX()<1500 && !getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(walkStyle);
			currentAction = ActionKey.RIGHT;
		}
		if(getEnemy().getBodyObject().getX()>1310 && getEnemy().getBodyObject().getX()<1500 && getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(walkStyle);
			currentAction = ActionKey.LEFT;
		}
		if(getEnemy().getBodyObject().getX()>1250 && getEnemy().getBodyObject().getX()<1400 && getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(InteractionState.JUMP_MOVE);
			currentAction = ActionKey.JUMP;
		}
		if(getEnemy().getBodyObject().getX()>(1500<leftX ? leftX : 1500) && getEnemy().getBodyObject().getX()<(2000>rightX ? rightX : 2000)){// && !getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(InteractionState.CROUCH_SNEAK);//BUG
			currentAction = ActionKey.CROUCH;
		}
		if(getEnemy().getBodyObject().getX()>(2000<leftX ? leftX : 2000) && getEnemy().getBodyObject().getX()<(2300>rightX ? rightX : 2300) && !getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(walkStyle);
			currentAction = ActionKey.RIGHT;
		}
		if(getEnemy().getBodyObject().getX()>(2000<leftX ? leftX : 2000) && getEnemy().getBodyObject().getX()<(2340>rightX ? rightX : 2340) && getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(walkStyle);
			currentAction = ActionKey.LEFT;
		}
		if(getEnemy().getBodyObject().getX()>(2310<leftX ? leftX : 2310) && getEnemy().getBodyObject().getX()<(2650>rightX ? rightX : 2650)){// && !getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(InteractionState.CROUCH_SNEAK);//BUG
			currentAction = ActionKey.CROUCH;
		}
		if(getEnemy().getBodyObject().getX()>(2650<leftX ? leftX : 2650) && getEnemy().getBodyObject().getX()<(4330>rightX ? rightX : 4330) && !getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(walkStyle);
			currentAction = ActionKey.RIGHT;
		}
		if(getEnemy().getBodyObject().getX()>(2650<leftX ? leftX : 2650) && getEnemy().getBodyObject().getX()<(4330>rightX ? rightX : 4330) && getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(walkStyle);
			currentAction = ActionKey.LEFT;
		}
		if(getEnemy().getBodyObject().getX()>(4330<leftX ? leftX : 4330) && getEnemy().getBodyObject().getX()<(4750>rightX ? rightX : 4750)){// && !getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(InteractionState.CROUCH_SNEAK);//BUG
			currentAction = ActionKey.CROUCH;
		}
		if(getEnemy().getBodyObject().getX()>(4750<leftX ? leftX : 4750) && getEnemy().getBodyObject().getX()<(5330>rightX ? rightX : 5430) && !getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(walkStyle);
			currentAction = ActionKey.RIGHT;
		}
		if(getEnemy().getBodyObject().getX()>(4750<leftX ? leftX : 4750) && getEnemy().getBodyObject().getX()<(5430>rightX ? rightX : 5430) && getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(walkStyle);
			currentAction = ActionKey.LEFT;
		}
		if(getEnemy().getBodyObject().getY()<700){//Ebene 1 & 2
			if(getEnemy().getBodyObject().getY()<500){//Ebene 1
				if(getEnemy().getBodyObject().getX()>5430 && getEnemy().getBodyObject().getX()<5500 && !getEnemy().getAnimationObject().isFlipped()){
					getEnemy().applyInteraction(InteractionState.JUMP_MOVE);
					currentAction = ActionKey.JUMP;
				}
				if(getEnemy().getBodyObject().getX()>(5500<leftX ? leftX : 5500) && getEnemy().getBodyObject().getX()<(5750>rightX ? rightX : 5750) && !getEnemy().getAnimationObject().isFlipped()){
					getEnemy().applyInteraction(walkStyle);
					currentAction = ActionKey.RIGHT;
				}
				if(getEnemy().getBodyObject().getX()>5750 && getEnemy().getBodyObject().getX()<5820 && !getEnemy().getAnimationObject().isFlipped()){
					getEnemy().applyInteraction(InteractionState.JUMP_MOVE);
					currentAction = ActionKey.JUMP;
				}
				if(getEnemy().getBodyObject().getX()>(5820<leftX ? leftX : 5820) && getEnemy().getBodyObject().getX()<(6200>rightX ? rightX : 6200) && !getEnemy().getAnimationObject().isFlipped()){
					getEnemy().applyInteraction(walkStyle);
					currentAction = ActionKey.RIGHT;
				}
			}else{//Ebene 2
				if(getEnemy().getBodyObject().getX()>(5430<leftX ? leftX : 5430) && getEnemy().getBodyObject().getX()<(6100>rightX ? rightX : 6100) && !getEnemy().getAnimationObject().isFlipped()){
					getEnemy().applyInteraction(walkStyle);
						currentAction = ActionKey.RIGHT;
				}
			}
			if(getEnemy().getBodyObject().getX()>(5430<leftX ? leftX : 5430) && getEnemy().getBodyObject().getX()<(6200>rightX ? rightX : 6200) && getEnemy().getAnimationObject().isFlipped()){
				getEnemy().applyInteraction(walkStyle);
				currentAction = ActionKey.LEFT;
			}
		}else{//Ebene 3
			if(getEnemy().getBodyObject().getX()>(5830<leftX ? leftX : 5830) && getEnemy().getBodyObject().getX()<(6450>rightX ? rightX : 6450) && !getEnemy().getAnimationObject().isFlipped()){
				getEnemy().applyInteraction(walkStyle);
				currentAction = ActionKey.RIGHT;
			}
			if(getEnemy().getBodyObject().getX()>(5830<leftX ? leftX : 5830) && getEnemy().getBodyObject().getX()<(6450>rightX ? rightX : 6450) && getEnemy().getAnimationObject().isFlipped()){
				getEnemy().applyInteraction(walkStyle);
				currentAction = ActionKey.LEFT;
			}
		}
		if(getEnemy().getBodyObject().getX()>(6950<leftX ? leftX : 6950) && getEnemy().getBodyObject().getX()<(7850>rightX ? rightX : 7850) && !getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(walkStyle);
			currentAction = ActionKey.RIGHT;
		}
		if(getEnemy().getBodyObject().getX()>(6950<leftX ? leftX : 6950) && getEnemy().getBodyObject().getX()<(7850>rightX ? rightX : 7850) && getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(walkStyle);
			currentAction = ActionKey.LEFT;
		}
		if(getEnemy().getBodyObject().getX()>(8120<leftX ? leftX : 8120) && getEnemy().getBodyObject().getX()<(8400>rightX ? rightX : 8400) && !getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(walkStyle);
			currentAction = ActionKey.RIGHT;
		}
		if(getEnemy().getBodyObject().getX()>(8120<leftX ? leftX : 8120) && getEnemy().getBodyObject().getX()<(8400>rightX ? rightX : 8400) && getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(walkStyle);
			currentAction = ActionKey.LEFT;
		}
		if(getEnemy().getBodyObject().getX()>(8400<leftX ? leftX : 8400) && getEnemy().getBodyObject().getX()<(8750>rightX ? rightX : 8750)){// && !getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(InteractionState.CROUCH_SNEAK);//BUG
			currentAction = ActionKey.CROUCH;
		}
		if(getEnemy().getBodyObject().getX()>(8750<leftX ? leftX : 8750) && getEnemy().getBodyObject().getX()<(10500>rightX ? rightX : 10500) && !getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(walkStyle);
			currentAction = ActionKey.RIGHT;
		}	
		if(getEnemy().getBodyObject().getX()>(9950<leftX ? leftX : 9950) && getEnemy().getBodyObject().getX()<(10500>rightX ? rightX : 10500) && getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(walkStyle);
			currentAction = ActionKey.LEFT;
		}
		if(getEnemy().getBodyObject().getX()>9550 && getEnemy().getBodyObject().getX()<9950 && getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(InteractionState.JUMP_MOVE);
			currentAction = ActionKey.JUMP;
		}
		if(getEnemy().getBodyObject().getX()>(9450<leftX ? leftX : 9450) && getEnemy().getBodyObject().getX()<(9550>rightX ? rightX : 9550) && getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(walkStyle);
			currentAction = ActionKey.LEFT;
		}
		if(getEnemy().getBodyObject().getX()>9150 && getEnemy().getBodyObject().getX()<9450 && getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(InteractionState.JUMP_MOVE);
			currentAction = ActionKey.JUMP;
		}
		if(getEnemy().getBodyObject().getX()>(9050<leftX ? leftX : 9050) && getEnemy().getBodyObject().getX()<(9150>rightX ? rightX : 9150) && getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(walkStyle);
			currentAction = ActionKey.LEFT;
		}
		if(getEnemy().getBodyObject().getX()>8800 && getEnemy().getBodyObject().getX()<9050 && getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(InteractionState.JUMP_MOVE);
			currentAction = ActionKey.JUMP;
		}
		if(getEnemy().getBodyObject().getX()>(8750<leftX ? leftX : 8750) && getEnemy().getBodyObject().getX()<(8800>rightX ? rightX : 8800) && getEnemy().getAnimationObject().isFlipped()){
			getEnemy().applyInteraction(walkStyle);
			currentAction = ActionKey.LEFT;
		}
		//BEWEGUNGSABFOLGE ENDE
				
		//ALARM -> enemy verfolgt player
		if(alarm && !getEnemy().isStunned()){
			walkStyle = InteractionState.WALK;
//			if(player.getX()>getEnemy().getX()){
//				currentAction = ActionKey.RIGHT;
//			}else{
//				currentAction = ActionKey.LEFT;
//			}		
		}
//		if(alarm && !getEnemy().isStunned()){
//			if(player.getBodyObject().getX()>getEnemy().getBodyObject().getX()){
//				currentAction = ActionKey.RIGHT;
//			}else{
//				currentAction = ActionKey.LEFT;
//			}
//		}
		//NILS
	}

	@Override
	public boolean handleCollision(boolean start, boolean postSolve, Sensor mySensor, BodyObject other, Sensor otherSensor) {
		//NILS
		//Achtung es muss abgefragt werden ob sender != null ist
		if(!postSolve) {
			
			if(other.getBodyObjectType().equals(BodyObjectType.Player) 
					&& !other.getParent().isHiding()
					&& getEnemy().getBodyObjectType().equals(BodyObjectType.Enemy)
					&& mySensor != null) {
				
				boolean meFlipped = mySensor.getBodyObject().getParent().getAnimationObject().isFlipped();
				
				//Player berührt sichtfeld -> Alarm
				if((mySensor.getSensorType() == ISensorTypes.SensorTypes.VISION_LEFT && meFlipped)
							|| (mySensor.getSensorType() == ISensorTypes.SensorTypes.VISION_RIGHT && !meFlipped)){
						alarm = true;
//						player = other.getParent();
						Debug.println("Alarm");
				}
			} //player<->enemy
			
			if(other.getBodyObjectType().equals(BodyObjectType.Enemy) 
					&& !other.getParent().isHiding()
					&& getEnemy().getBodyObjectType().equals(BodyObjectType.Enemy)
					&& mySensor != null
					&&other.getParent().isStunned()){
//				core.ingame.HUD.setAlarm(true);//funktioniert nicht
				alarm = true;
				Debug.println("Alarm");
			} //enemy<->enemy	

			//STUN
			if(mySensor != null 
					&& mySensor.getSensorType() == SensorTypes.BODY
					&& other.getBodyObjectType().equals(BodyObjectType.Shuriken)){
					armour--;
					if(armour ==0){
						getEnemy().setStun();
					}
			}
			
		} //postSolve
		return false;
	}

	@Override
	public void addActionKey(ActionKey action, int... keys) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isKeyDown(ActionKey action) {
		//NILS
		//Abfrage ob action gleich gesetzter interactionstate
		if(action == currentAction){
			return true;
		}else{
			return false;
		}
		//NILS
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

	@Override
	public void keyDown(ActionKey action) {
		// TODO Auto-generated method stub
		
	}
}
