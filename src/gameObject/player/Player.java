package gameObject.player;

import gameObject.Sensor;
import gameWorld.GameWorld;
import misc.Debug;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape.Type;

import core.ingame.Camera;
import core.ingame.IInputHandler;

public class Player extends PlayerCollision {

	public Player(GameWorld gameWorld, IInputHandler iHandler, Vector2 position) {
		super(gameWorld, position);
		setInputHandler(iHandler);
		Camera.getInstance().setToFollowMoveable(this);
	}

	public void run() {
		if(Debug.isMode(Debug.Mode.CAMERA))
			return;
		super.run();
	}

	
	@Override
	public void init(String name) {
		super.init(name);
		setGameObjectType(GameObjectTypes.PLAYER);
		setLayer(3);
		body.setLinearDamping(2.5f);
		body.setFixedRotation(true);
		
		float[] verticesFoot = { 0.5f, 0.3f, 0.8f, 0.3f, 0.8f, 0.4f, 0.5f, 0.4f };
		addSensor(new Sensor(this, Type.Polygon, verticesFoot, SensorTypes.FOOT, Sensor.HANDLE_FIRST));
		
		float[] verticesBody = {0.395f, 0.40f, 0.395f, 1.17f, 0.90f, 1.17f, 0.90f, 0.40f};
		addSensor(new Sensor(this, Type.Polygon, verticesBody, SensorTypes.BODY, Sensor.HANDLE_SECOND));
	}

}
