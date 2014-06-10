package gameObject.interaction.player;

import gameObject.body.BodyObject;
import gameObject.body.GameObjectType;
import gameObject.body.Sensor;
import gameObject.interaction.GameObject;
import gameObject.interaction.enemy.Enemy;

import com.badlogic.gdx.math.Vector2;

import core.ingame.GameProperties;

public class Shuriken extends GameObject {

	private Vector2 direction;
	private int ttl = 70;

	public Shuriken(GameObject thrower, Vector2 clickPoint) {
		super(thrower.getBodyObject().getGameWorld(), thrower.getBodyObject().getLocalCenterInWorld());

		this.init("shuriken");
		getBodyObject().setGameObjectType(GameObjectType.Shuriken);

		direction = GameProperties.pixelToMeter(clickPoint.sub(getBodyObject().getPosition()));
		getBodyObject().addToGameWorld(this);

		getBodyObject().setGravityScale(0);
		getBodyObject().applyImpulse(direction.nor().scl(7));
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
				((Enemy) other.getParent()).setStun();
				dispose();
				return true;
			default:
				break;
			}
		}

		return false;
	}

}
