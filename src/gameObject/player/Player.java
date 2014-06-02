package gameObject.player;

import gameObject.Sensor;
import gameWorld.Map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape.Type;
import com.badlogic.gdx.physics.box2d.World;

import core.ingame.Camera;

public class Player extends PlayerInteraction implements Runnable {

	public Player(World world, Vector2 position) {
		super(world, position);
		Camera.getInstance().setToFollowMoveable(this);
		Map.getInstance().addRunnable(this);
	}

	public static Player getInstance() {
		return Map.getInstance().getPlayer();
	}
	
	public void run() {
		processInput();
		processStates();
	}

	private void processStates() {

		Vector2 baseForce;

		
		
		switch(getInteractionState()) {
//		case STAND: case CROUCH_STAND: case THROW: case HIDE: case GRAB: 
//		case GRAB_DISPOSE: case STUNNED: case HOOK_START: case HOOK_FLY:
//			break;
		case WALK:
			baseForce = new Vector2(1,0);
			break;
		case RUN:
			baseForce = new Vector2(1.7f,0);
			break;
		case CROUCH_SNEAK:
			baseForce = new Vector2(0.7f,0);
			break;
		case GRAB_PULL:
			baseForce = new Vector2(0.6f,0);
			break;
		case JUMP:
			baseForce = new Vector2(0,1);
			break;
		case JUMP_MOVE:
			baseForce = new Vector2(1,1);
			break;
		default:
			baseForce = new Vector2(0, 0);
			break;
		}
		
		
		if(isFlipped())
			baseForce.x *= -1;

//		tweak gravity
		if (baseForce.len() != 0)
			body.setGravityScale(0.7f);
		else
			body.setGravityScale(1);
		
//		apply impulse
		body.applyLinearImpulse(baseForce.scl(isGrounded() ? 2 : 1.5f), body.getWorldCenter(), true);

	}

	@Override
	public void init(String name) {
		super.init(name);
		setGameObjectType(GameObjectTypes.PLAYER);
		body.setLinearDamping(2.5f);
		body.setFixedRotation(true);
		float[] vertices = { 0.5f, 0.3f, 0.8f, 0.3f, 0.8f, 0.4f, 0.5f, 0.4f };
		addSensor(new Sensor(this, Type.Polygon, vertices, SensorTypes.FOOT, Sensor.HANDLE_FIRST));
	}

}
