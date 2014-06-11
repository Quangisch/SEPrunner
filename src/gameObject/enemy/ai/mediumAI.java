package gameObject.enemy.ai;

import gameObject.GameObject;
import gameObject.IGameObjectTypes;
import gameObject.ISensorTypes;
import gameObject.Sensor;
import misc.Debug;

import com.badlogic.gdx.utils.JsonValue;

import core.ingame.GameProperties;
import core.ingame.GameProperties.GameState;
import core.ingame.InputHandler.Click;
import core.ingame.KeyMap.ActionKey;

public class mediumAI extends EnemyAI {


	private ActionKey currentAction;
	private boolean alarm;
	private gameObject.GameObject player;


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
		if(getEnemy().getX()<=leftX){
			getEnemy().flip();
			currentAction = ActionKey.RIGHT;
			getEnemy().sensors.get(0).setActive(false);//deaktiviert linken sensor
			getEnemy().sensors.get(1).setActive(true);
		}
		if(getEnemy().getX()>=rightX){
			getEnemy().flip();
			currentAction = ActionKey.LEFT;
			getEnemy().sensors.get(1).setActive(false);//deaktiviert rechten sensor
			getEnemy().sensors.get(0).setActive(true);
		}
		if(getEnemy().getX()>leftX && getEnemy().getX()<rightX && !getEnemy().isFlipped()){
			currentAction = ActionKey.RIGHT;		
		}
		if(getEnemy().getX()>leftX && getEnemy().getX()<rightX && getEnemy().isFlipped()){
			currentAction = ActionKey.LEFT;
		}
		
		//STUN
		if(getEnemy().isStunned()){
			Debug.println("stunned");
			currentAction = ActionKey.CROUCH;
			//TODO enemy hat kein sichtfeld mehr
		}
		
		//ALARM -> enemy verfolgt player TODO: kann aber nicht springen, kommt keine steigungen hoch
		if(alarm && !getEnemy().isStunned()){
			if(player.getX()>getEnemy().getX()){
				currentAction = ActionKey.RIGHT;
			}else{
				currentAction = ActionKey.LEFT;
			}
		}
		//NILS
	}

	@Override
	public boolean handleCollision(boolean start, Sensor sender, GameObject other, Sensor otherSensor) {
		//NILS
		//Achtung es muss abgefragt werden ob sender != null ist
		
		//Player berührt Enemy -> Game Over
		if(other.getGameObjectType() == IGameObjectTypes.GameObjectTypes.PLAYER 
				&& getEnemy().getGameObjectType() == IGameObjectTypes.GameObjectTypes.ENEMY
				&& sender != null
				&& sender.getSensorType() == ISensorTypes.SensorTypes.FOOT){//TODO: hier einfügen: && other.isDetectable
			Debug.println("Game Over");
			GameProperties.setGameState(GameState.INGAME_LOSE);
		}
		
		//Player berührt sichtfeld -> Alarm, TODO: aber nicht, wenn player versteckt
		if(other.getGameObjectType() == IGameObjectTypes.GameObjectTypes.PLAYER 
				&& getEnemy().getGameObjectType() == IGameObjectTypes.GameObjectTypes.ENEMY
				&& sender != null
				&& sender.getSensorType() == ISensorTypes.SensorTypes.VISION){//TODO: hier einfügen: && other.isDetectable
			alarm = true;
			player = other;
			Debug.println("Alarm");
		}

		
		//Enemy berührt shuriken -> stunned
		if(other.getGameObjectType() == IGameObjectTypes.GameObjectTypes.SHURIKEN
				&& sender != null
				&& sender.getSensorType() == ISensorTypes.SensorTypes.BODY){
			//NILS
			getEnemy().setStun();
			//NILS
			Debug.println("hit by Shuriken");
		}
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
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
