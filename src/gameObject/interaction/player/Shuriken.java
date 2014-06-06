package gameObject.interaction.player;

import gameObject.body.BodyObject;
import gameObject.body.GameObjectType;
import gameObject.body.ICollisionable;
import gameObject.body.Sensor;
import gameObject.interaction.GameObject;
import gameObject.interaction.enemy.Enemy;

import com.badlogic.gdx.math.Vector2;

import core.ingame.GameProperties;

public class Shuriken extends GameObject {

	private Vector2 direction;
	private int ttl = 70;

	public Shuriken(ICollisionable thrower, Vector2 clickPoint) {
		super(thrower.getGameWorld(), thrower.getLocalCenterInWorld());

		this.init("shuriken");
		this.setGameObjectType(GameObjectType.Shuriken);

		direction = GameProperties.pixelToMeter(clickPoint.sub(getPosition()));
		addToGameWorld(this);

		setGravityScale(0);
		applyImpulse(direction.nor().scl(7));
	}

	public void run() {
		if (ttl-- <= 0)
			dispose();
	}

	@Override
	public boolean handleCollision(boolean start, Sensor mySensor, BodyObject other, Sensor otherSensor) {
		if (start) {
			
			switch(other.getGameObjectType()) {
			
			case Ground :
				ttl = 5;
				return true;
			case Enemy :
				((Enemy) other).setStun();
				dispose();
				return true;
			default:
				break;
			}
		}

		return false;
	}

}
