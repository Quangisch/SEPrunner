package gameObject.enemy;

import gameObject.GameObject;
import gameObject.Sensor;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape.Type;
import com.badlogic.gdx.physics.box2d.World;

public class Enemy extends GameObject {

	protected boolean stunned;

	public Enemy(World world, Vector2 position) {
		super(world, position);
	}

	@Override
	public void init(String name) {
		super.init(name);
		setGameObjectType(GameObjectTypes.ENEMY);
		setLayer(3);
		body.setLinearDamping(2.5f);
		body.setFixedRotation(true);
		addSensor(new Sensor(this, Type.Circle, new float[] { 0, 1, 0.5f }, SensorTypes.VISION, Sensor.HANDLE_FIRST));
	}

	@Override
	public void run() {
		if (AI != null) AI.run();
	}

	public boolean isStunned() {
		return stunned;
	}

	public void setStun() {
		stunned = true;
	}

	public boolean isCarriable(Vector2 position) {
		// TODO
		return false;
	}

	@Override
	public boolean handleCollision(boolean start, Sensor sender, GameObject other, Sensor otherSensor) {
		return getAI() == null || getAI().handleCollision(start, sender, other, otherSensor);
	}
}
