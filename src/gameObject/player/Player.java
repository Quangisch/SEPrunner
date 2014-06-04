package gameObject.player;

import gameObject.Sensor;
import gameWorld.Map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape.Type;
import com.badlogic.gdx.physics.box2d.World;

import core.ingame.Camera;
import core.ingame.GameProperties;
import core.ingame.GameProperties.Debug;

public class Player extends PlayerCollision {

	public Player(World world, Vector2 position) {
		super(world, position);
		Camera.getInstance().setToFollowMoveable(this);
	}

	public static Player getInstance() {
		return Map.getInstance().getPlayer();
	}

	public void run() {
		if(GameProperties.debugMode.equals(Debug.CAMERA))
			return;
		
		System.out.println("isBodyBlocked "+isBodyBlocked());
		processInput();
		processStates();
	}

	private void processStates() {
		
		Vector2 baseForce;
		switch (getInteractionState()) {
		// case STAND: case CROUCH_STAND: case THROW: case HIDE: case GRAB: 
		// case GRAB_DISPOSE: case STUNNED: case HOOK_START: case HOOK_FLY:
		//	break;
		case WALK:
			baseForce = new Vector2(1, 0);
			break;
		case RUN:
			baseForce = new Vector2(1.7f, 0);
			break;
		case CROUCH_SNEAK:
			baseForce = new Vector2(0.7f, 0);
			break;
		case GRAB_PULL:
			baseForce = new Vector2(0.6f, 0);
			break;
		case JUMP:
			baseForce = new Vector2(0, 20);
			break;
		case JUMP_MOVE:
			baseForce = new Vector2(1, 20);
			break;
		default:
			baseForce = new Vector2(0, 0);
			break;
		}

		if (!isGrounded()) baseForce.y = 0;

		if (isFlipped()) baseForce.x *= -1;

		// tweak gravity
		if (baseForce.len() != 0)
			body.setGravityScale(0.7f);
		else if(getInteractionState().equals(InteractionState.HOOK_FLY))
			body.setGravityScale(0);
		else
			body.setGravityScale(1);

		// apply impulse
		body.applyLinearImpulse(baseForce.scl(isGrounded() ? 2 : 1.5f), body.getWorldCenter(), true);
	}

	@Override
	public void init(String name) {
		super.init(name);
		setGameObjectType(GameObjectTypes.PLAYER);
		setLayer(3);
		setAlpha(0.8f);
		body.setLinearDamping(2.5f);
		body.setFixedRotation(true);
		float[] verticesFoot = { 0.5f, 0.3f, 0.8f, 0.3f, 0.8f, 0.4f, 0.5f, 0.4f };
		addSensor(new Sensor(this, Type.Polygon, verticesFoot, SensorTypes.FOOT, Sensor.HANDLE_FIRST));
		
		float[] verticesBody = {0.395f, 0.40f, 0.395f, 1.17f, 0.90f, 1.17f, 0.90f, 0.40f};
		addSensor(new Sensor(this, Type.Polygon, verticesBody, SensorTypes.BODY, Sensor.HANDLE_SECOND));
	}

}
