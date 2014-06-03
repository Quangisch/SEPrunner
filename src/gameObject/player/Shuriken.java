package gameObject.player;

import gameObject.Collisionable;
import gameObject.GameObject;
import gameObject.Sensor;
import gameWorld.Map;

import com.badlogic.gdx.math.Vector2;

public class Shuriken extends GameObject {

	private Vector2 direction;
	private int ttl = 70;

	public Shuriken(Collisionable thrower, Vector2 direction) {
		super(thrower.getWorld(), thrower.getLocalCenterInWorld());

		this.init("shuriken");
		this.setGameObjectType(GameObjectTypes.SHURIKAN);

		body.setGravityScale(0);
		this.direction = direction;
		this.direction.nor();
		this.direction.scl(7);

		Map.getInstance().addGameObject(this);

		System.out.println("Init Shurikan@" + getX() + "x" + getY() + " direction:" + this.direction.toString());
		body.applyLinearImpulse(direction, getWorldPosition(), true);
	}

	public void run() {
		if (ttl <= 0)
			dispose();
		else
			ttl--;
	}

	@Override
	public boolean handleCollision(boolean start, Sensor mySensor, GameObject other, Sensor otherSensor) {
		if (start) {
			if (other.getGameObjectType() == GameObjectTypes.GROUND) dispose();
		}

		return false;
	}

}
