package gameObject.player;

import gameObject.Sensor;
import gameWorld.Map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape.Type;
import com.badlogic.gdx.physics.box2d.World;

import core.ingame.Camera;
import core.ingame.GameProperties;

public class Player extends PlayerInteraction implements Runnable {

	public Player(World world, Vector2 position) {
		super(world, position);
		Camera.getInstance().setToFollowMoveable(this);
	}

	public static Player getInstance() {
		return Map.getInstance().getPlayer();
	}
	
	public void run() {
		processInput();
		processStates();
	}

	private void processStates() {

		Vector2 baseForce = new Vector2(0, 0);

		//		basic movement
		if (InputHandler.getInstance().isKeyDown(GameProperties.keyRight)) {
			baseForce.add(1, 0);
			setFlip(false);
			setCurrentState(1);
		} else if (InputHandler.getInstance().isKeyDown(GameProperties.keyLeft)) {
			baseForce.add(-1, 0);
			setFlip(true);
			setCurrentState(1);
		} else if (!isGrounded())
			setCurrentState(1);
		else
			setCurrentState(0);

		//		tweak gravity
		if (baseForce.len() != 0)
			body.setGravityScale(0.7f);
		else
			body.setGravityScale(1);

		//		run
		if ((getCurrentState() == 1 || getCurrentState() == 3)
				&& isGrounded() && isRunning()) {
			setCurrentState(3);
			baseForce.scl(1.7f);
		}

		//		jump
		if (InputHandler.getInstance().isKeyDown(GameProperties.keyJump) && isGrounded()) {
			setGrounded(false);
			body.applyLinearImpulse(new Vector2(body.getLocalCenter().x, body.getLocalCenter().y + 100),
					body.getWorldCenter(), true);
			setCurrentState(0);
		}

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
