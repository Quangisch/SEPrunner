package gameObject.interaction.player;

import gameObject.body.BodyObject;
import gameObject.body.GameObjectType;
import gameObject.body.ISensorTypes.SensorTypes;
import gameObject.body.Sensor;
import gameObject.interaction.GameObject;
import gameWorld.GameWorld;

import com.badlogic.gdx.math.Vector2;

import core.ingame.GameProperties;

abstract class PlayerCollision extends GameObject {

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
		
		if(other.getGameObjectType().equals(GameObjectType.Goal))
			GameProperties.setWin();
		
		if(mySensor != null) {
			if(mySensor.getSensorType() == SensorTypes.BODY 
					&& other.getGameObjectType().equals(GameObjectType.Enemy)
//					&& otherSensor != null
//					&& otherSensor.getSensorType() == SensorTypes.BODY
					)
				GameProperties.setGameOver();
		}
		
		return false;
	}

}
