package gameObject.player;

import gameObject.Sensor;
import gameWorld.Map;
import misc.Debug;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape.Type;
import com.badlogic.gdx.physics.box2d.World;

import core.ingame.Camera;
import core.ingame.InputHandler;
import core.ingame.KeyMap.ActionKey;

public class Player extends PlayerCollision {

	public Player(World world, Vector2 position) {
		super(world, position);
		Camera.getInstance().setToFollowMoveable(this);
	}

	public static Player getInstance() {
		return Map.getInstance().getPlayer();
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
		
		iHandler = InputHandler.getInstance();
		
		iHandler.addActionKey(ActionKey.LEFT, Keys.A, Keys.LEFT);
		iHandler.addActionKey(ActionKey.RIGHT, Keys.D, Keys.RIGHT);
		iHandler.addActionKey(ActionKey.RUN, Keys.F, Keys.SHIFT_LEFT);
				
		iHandler.addActionKey(ActionKey.JUMP, Keys.SPACE, Keys.UP); 
		iHandler.addActionKey(ActionKey.CROUCH, Keys.S, Keys.DOWN);
				
		iHandler.addActionKey(ActionKey.ACTION, Keys.E, Keys.ENTER);
		iHandler.addActionKey(ActionKey.THROW, Input.Buttons.LEFT);
		iHandler.addActionKey(ActionKey.HOOK, Input.Buttons.RIGHT);
	}

}
