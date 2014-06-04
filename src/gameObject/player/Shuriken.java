package gameObject.player;

import gameObject.Collisionable;
import gameObject.GameObject;
import gameObject.Sensor;
import gameObject.enemy.Enemy;
import gameWorld.Map;

import com.badlogic.gdx.math.Vector2;

import core.ingame.GameProperties;

public class Shuriken extends GameObject {

	private Vector2 direction;
	private int ttl = 70;

	public Shuriken(Collisionable thrower, Vector2 clickPoint) {
		super(thrower.getWorld(), thrower.getLocalCenterInWorld());

		this.init("shuriken");
		this.setGameObjectType(GameObjectTypes.SHURIKEN);

		direction = GameProperties.pixelToMeter(clickPoint.sub(getPosition()));
		Map.getInstance().addGameObject(this);

		body.setGravityScale(0);
		body.applyLinearImpulse(direction.nor().scl(7), getWorldPosition(), true);
	}

	public void run() {
		if (ttl-- <= 0)
			dispose();
	}

	@Override
	public boolean handleCollision(boolean start, Sensor mySensor, GameObject other, Sensor otherSensor) {
		if (start) {
			
			switch(other.getGameObjectType()) {
			
			case GameObjectTypes.GROUND :
				ttl = 5;
				return true;
			case GameObjectTypes.ENEMY :
				((Enemy) other).setStun();
				dispose();
				return true;
			}
		}

		return false;
	}

}
