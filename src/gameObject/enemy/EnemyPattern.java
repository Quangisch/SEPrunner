package gameObject.enemy;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import gameObject.ObjectInteraction;

public class EnemyPattern extends ObjectInteraction {
	
	protected EnemyPattern(World world, Vector2 position) {
		super(world, position);
		// TODO Auto-generated constructor stub
	}

	public enum Move {
		STAND, WALK, RUN;
	}
	
	public enum State {
		NORMAL, CROUCH, STAND;
	}

	private Move move;
	private State state;
	
	public void tryToJump() {
		
	}
	
}
