package gameObject.interaction.enemy.ai;

import gameObject.body.BodyObject;
import gameObject.body.BodyObjectType;
import gameObject.body.ISensorTypes;
import gameObject.body.Sensor;
import gameObject.interaction.InteractionState;
import misc.Debug;

import com.badlogic.gdx.utils.JsonValue;

import core.GameProperties;
import core.GameProperties.GameState;
import core.ingame.input.InputHandler.Click;
import core.ingame.input.KeyMap.ActionKey;

public class SimplePatrolAI extends EnemyAI {

	//NILS
	private ActionKey currentAction;
	private boolean alarm;
//	private GameObject player;
	//NILS

	float leftX, rightX;

	@Override
	public void init(JsonValue jsonValue) {
		leftX = jsonValue.getFloat(0);
		rightX = jsonValue.getFloat(1);
	}

	@Override
	public void run() {
		if (getEnemy() == null) return;
		//NILS
		//PATROL
		if(getEnemy().getBodyObject().getX()<=leftX){
			getEnemy().getAnimationObject().flip();
			currentAction = ActionKey.RIGHT;
			getEnemy().getBodyObject().getSensors().get(0).setActive(false);//deaktiviert linken sensor
			getEnemy().getBodyObject().getSensors().get(1).setActive(true);
		}
		if(getEnemy().getBodyObject().getX()>=rightX){
			getEnemy().getAnimationObject().flip();
			currentAction = ActionKey.LEFT;
			getEnemy().getBodyObject().getSensors().get(1).setActive(false);//deaktiviert rechten sensor
			getEnemy().getBodyObject().getSensors().get(0).setActive(true);
		}
		if(getEnemy().getBodyObject().getX()>leftX && getEnemy().getBodyObject().getX()<rightX && !getEnemy().getAnimationObject().isFlipped()){
			currentAction = ActionKey.RIGHT;		
		}
		if(getEnemy().getBodyObject().getX()>leftX && getEnemy().getBodyObject().getX()<rightX && getEnemy().getAnimationObject().isFlipped()){
			currentAction = ActionKey.LEFT;
		}
		
		//STUN
		if(getEnemy().isStunned()){
			currentAction = ActionKey.CROUCH;
			//TODO enemy hat kein sichtfeld mehr
		}
		
		//ALARM -> enemy verfolgt player TODO: kann aber nicht springen, kommt keine steigungen hoch
		if(alarm && !getEnemy().isStunned()){
//			if(player.getX()>getEnemy().getX()){
//				currentAction = ActionKey.RIGHT;
//			}else{
//				currentAction = ActionKey.LEFT;
//			}		
		}
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
				
				//Player ber�hrt Enemy -> Game Over
				if(mySensor.getSensorType() == ISensorTypes.SensorTypes.BODY){//TODO: hier einf�gen: && other.isDetectable
					Debug.println("Game Over");
					other.getParent().applyInteraction(InteractionState.LOSE);
					GameProperties.setGameState(GameState.INGAME_LOSE);
				}
				
				//Player ber�hrt sichtfeld -> Alarm, TODO: aber nicht, wenn player versteckt
				if((mySensor.getSensorType() == ISensorTypes.SensorTypes.VISION_LEFT && meFlipped)
							|| (mySensor.getSensorType() == ISensorTypes.SensorTypes.VISION_RIGHT && !meFlipped)){//TODO: hier einf�gen: && other.isDetectable
					alarm = true;
//					player = other.getParent();
					Debug.println("Alarm");
				}
				
			} //player<->enemy

		} //postSolve

		return false;
	}

	@Override
	public void addActionKey(ActionKey action, int... keys) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isKeyDown(ActionKey action) {
//		return action == ActionKey.CROUCH //
//			|| action == ActionKey.RIGHT; // || action == ActionKey.RUN;
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
