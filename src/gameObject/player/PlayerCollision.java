package gameObject.player;

import gameObject.GameObject;
import gameObject.Sensor;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

abstract class PlayerCollision extends PlayerInteraction {

	protected PlayerCollision(World world, Vector2 position) {
		super(world, position);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean handleCollision(boolean start, Sensor mySensor, GameObject other, Sensor otherSensor) {
		if(mySensor == null && other.getGameObjectType() == GameObjectTypes.GROUND 
				&& isHooking()) {
			System.out.println("resetHook");
			resetHook();
			return true;
		}
			
		if (mySensor != null) {
			if (mySensor.getSensorType() == SensorTypes.FOOT && other.getGameObjectType() == GameObjectTypes.GROUND) {
//				if(isJumping())
					calcGroundedContact(start);
				return true;
			}
			
			if(mySensor.getSensorType() == SensorTypes.BODY && other.getGameObjectType() == GameObjectTypes.GROUND) {
				calcBodyBlockedContact(start);
				return true;
			}
		}

		return false;
	}

}
