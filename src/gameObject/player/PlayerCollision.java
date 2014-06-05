package gameObject.player;

import gameObject.ObjectInteraction;
import gameWorld.GameWorld;

import com.badlogic.gdx.math.Vector2;

abstract class PlayerCollision extends ObjectInteraction {

	protected PlayerCollision(GameWorld gameWorld, Vector2 position) {
		super(gameWorld, position);
		// TODO Auto-generated constructor stub
	}

}
