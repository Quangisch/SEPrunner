package gameObject.interaction.player;

import gameObject.body.BodyObject;
import gameObject.body.BodyObjectType;
import gameObject.body.ISensorTypes.SensorTypes;
import gameObject.body.Sensor;
import gameObject.interaction.GameObject;

import com.badlogic.gdx.math.Vector2;

import core.FilePath;
import core.GameProperties;

public class Shuriken extends GameObject {

	private Vector2 direction;
	private int ttl = 70;

	public Shuriken(GameObject thrower, Vector2 clickPoint) {
		super(thrower.getGameWorld(), thrower.getBodyObject().getLocalCenterInWorld());

		String texturePath = FilePath.objectTexturePath + "shuriken.png";
		String jsonPath = FilePath.objectJsonPath + "shuriken.json";
		
		super.init(texturePath, jsonPath);
		getBodyObject().setBodyObjectType(BodyObjectType.Shuriken);

		direction = GameProperties.pixelToMeter(clickPoint.sub(getBodyObject().getPosition()));
		getGameWorld().addGameObject(this);

		getBodyObject().setGravityScale(0);
		getBodyObject().applyImpulse(direction.nor().scl(7));
	}

	public void run() {
		if (ttl-- <= 0)
			dispose();
		super.run();
	}

	@Override
	public boolean handleCollision(boolean start, boolean postSolve, Sensor mySensor, BodyObject other, Sensor otherSensor) {
		if(!postSolve) {
			if (start) {
				switch(other.getBodyObjectType()) {
				case Ground :
					ttl = 5;
					return true;
				case Enemy :
					if(otherSensor != null && otherSensor.getSensorType() == SensorTypes.CORE) {
//						((Enemy) other.getParent()).setStun();
						dispose();
						return true;
					}
				default:
					break;
				}
			}
		}

		return false;
	}

}
