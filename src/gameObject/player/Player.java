package gameObject.player;

import gameObject.GameObject;
import gameObject.Sensor;
import gameObject.enemy.Enemy;
import gameWorld.Map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.Shape.Type;

import core.ingame.Camera;
import core.ingame.GameProperties;

public class Player extends GameObject implements Runnable, Detectable {

	protected enum State {
		STAND, SNEAK, COWER, RUN
	}

	public Player(World world, Vector2 position) {
		super(world, position);
		Camera.getInstance().setToFollowMoveable(this);
	}

	public static Player getInstance() {
		return Map.getInstance().getPlayer();
	}

	public void run() {
		processInput();
	}

	private void processInput() {

		Vector2 baseForce = new Vector2(0, 0);

		// basic movement
		if (InputHandler.getInstance().isKeyDown(GameProperties.keyRight)) {
			baseForce.add(1, 0);
			setFlip(false);
			setCurrentState(State.SNEAK);
		} else if (InputHandler.getInstance().isKeyDown(GameProperties.keyLeft)) {
			baseForce.add(-1, 0);
			setFlip(true);
			setCurrentState(State.SNEAK);
		} else if (!isGrounded())
			setCurrentState(State.SNEAK);
		else
			setCurrentState(State.STAND);

		// tweak gravity
		if (baseForce.len() != 0)
			body.setGravityScale(0.7f);
		else
			body.setGravityScale(1);

		// run
		if ((getCurrentState() == 1 || getCurrentState() == 3)
				&& InputHandler.getInstance().isKeyDown(GameProperties.keyRun) && isGrounded()) {
			setCurrentState(State.RUN);
			baseForce.scl(1.7f);
		}

		// jump
		if (InputHandler.getInstance().isKeyDown(GameProperties.keyJump) && isGrounded()) {
			setGrounded(false);
			body.applyLinearImpulse(new Vector2(body.getLocalCenter().x, body.getLocalCenter().y + 100),
					body.getWorldCenter(), true);
			setCurrentState(State.STAND);
		}

		// apply impulse
		body.applyLinearImpulse(baseForce.scl(isGrounded() ? 2 : 1.5f), body.getWorldCenter(), true);

	}

	@Override
	public void init(String name) {
		super.init(name, State.class);
		setGameObjectType(GameObjectTypes.PLAYER);
		setLayer(3);
		setAlpha(0.8f);
		body.setLinearDamping(2.5f);
		body.setFixedRotation(true);
		float[] vertices = { 0.5f, 0.3f, 0.8f, 0.3f, 0.8f, 0.4f, 0.5f, 0.4f };
		addSensor(new Sensor(this, Type.Polygon, vertices, SensorTypes.FOOT, Sensor.HANDLE_FIRST));
	}

	@Override
	public boolean isDetectable(Enemy enemy) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCaptured(Enemy enemy) {
		// TODO Auto-generated method stub
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
