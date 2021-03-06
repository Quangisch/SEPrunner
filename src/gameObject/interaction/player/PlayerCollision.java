package gameObject.interaction.player;

import gameObject.GameObject;
import gameObject.body.BodyObject;
import gameObject.body.BodyObjectType;
import gameObject.body.ISensorTypes.SensorTypes;
import gameObject.body.Sensor;
import gameObject.interaction.InteractionState;
import gameObject.interaction.enemy.Enemy;
import gameWorld.GameWorld;

import com.badlogic.gdx.math.Vector2;

import core.GameProperties;

abstract class PlayerCollision extends GameObject {

	protected PlayerCollision(GameWorld gameWorld, Vector2 position) {
		super(gameWorld, position);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean handleCollision(boolean start, boolean postSolve, Sensor mySensor,
			BodyObject other, Sensor otherSensor) {
		boolean handled = super.handleCollision(start, postSolve, mySensor, other, otherSensor);
		
		if(!postSolve) {
			if(handled)
				return true;
			
			if(other != null && other.getBodyObjectType().equals(BodyObjectType.Goal)) {
				applyInteraction(InteractionState.WIN);
				GameProperties.setWin();
			}
			
			if(mySensor != null) {
				if(mySensor.getSensorType() == SensorTypes.BODY
						&& other.getBodyObjectType().equals(BodyObjectType.Enemy)
						&& otherSensor != null
						&& otherSensor.getSensorType() == SensorTypes.BODY
						&& !other.getParent().isStunned()
						&& !isHiding()
						&& !((Enemy) other.getParent()).getInteractionHandler().isOnlyCrouching()) {//

					GameProperties.setGameOver("You were caught!");
					applyInteraction(InteractionState.LOSE);
					getAnimationObject().setLayer(5);
					return true;
				}
			}
		}
		
		
		return false;
	}

}
