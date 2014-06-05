package gameObject.player;

import gameObject.ObjectInteraction;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

abstract class PlayerCollision extends ObjectInteraction {

	protected PlayerCollision(World world, Vector2 position) {
		super(world, position);
		// TODO Auto-generated constructor stub
	}

}
