package gameObject.player;

import gameObject.GameObject;
import gameObject.Sensor;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

abstract class PlayerCollision extends GameObject {

	protected PlayerCollision(World world, Vector2 position) {
		super(world, position);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean handleCollision(Sensor mySensor, GameObject other, Sensor otherSensor) {
		if (mySensor != null) {
			if (mySensor.getSensorType() == SensorTypes.FOOT && other.getGameObjectType() == GameObjectTypes.GROUND) {
				setGrounded(true);
				return true;
			}
		}

		return false;
	}

}
