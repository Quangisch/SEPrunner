package gameObject.player;

import gameObject.BodyObject;
import gameObject.Sensor;
import gameObject.interaction.ObjectInteraction;
import gameWorld.GameWorld;

import com.badlogic.gdx.math.Vector2;

import core.ingame.GameProperties;

abstract class PlayerCollision extends ObjectInteraction {

	protected PlayerCollision(GameWorld gameWorld, Vector2 position) {
		super(gameWorld, position);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean handleCollision(boolean start, Sensor mySensor,
			BodyObject other, Sensor otherSensor) {
		boolean handled = super.handleCollision(start, mySensor, other, otherSensor);
		if(handled)
			return true;
		
		if(other.getGameObjectType() == GameObjectTypes.GOAL)
			GameProperties.setWin();
		
		if(mySensor != null) {
			if(mySensor.getSensorType() == SensorTypes.BODY 
					&& other.getGameObjectType() == GameObjectTypes.ENEMY
//					&& otherSensor != null
//					&& otherSensor.getSensorType() == SensorTypes.BODY
					)
				GameProperties.setGameOver();
		}
		
		return false;
	}

}
