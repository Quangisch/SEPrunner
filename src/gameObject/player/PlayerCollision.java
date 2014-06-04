package gameObject.player;

import gameObject.GameObject;
import gameObject.ObjectInteraction;
import gameObject.Sensor;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

abstract class PlayerCollision extends ObjectInteraction {

	protected PlayerCollision(World world, Vector2 position) {
		super(world, position);
		// TODO Auto-generated constructor stub
	}
	
	public boolean handleCollision(boolean start, Sensor mySensor, GameObject other, Sensor otherSensor) {
		boolean handled = super.handleCollision(start, mySensor, other, otherSensor);
		if(handled)
			return handled;
		
		return false;
	}

}
