package gameObject.interaction.enemy;

import gameObject.body.BodyObject;
import gameObject.body.GameObjectType;
import gameObject.body.ISensorTypes.SensorTypes;
import gameObject.body.Sensor;
import gameObject.interaction.GameObject;
import gameObject.interaction.enemy.ai.IEnemyAI;
import gameObject.interaction.enemy.ai.SimplePatrolAI;
import gameWorld.GameWorld;
import misc.StringFunctions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape.Type;
import com.badlogic.gdx.utils.JsonValue;

import core.ingame.input.InteractionHandler;

public class Enemy extends GameObject implements Runnable {

	protected IEnemyAI AI;
	protected boolean stunned;
	private InteractionHandler interactionHandler;

	public Enemy(GameWorld gameWorld, Vector2 position) {
		super(gameWorld, position);
	}

	@Override
	public void init(String name) {
		super.init(name);
		
		getBodyObject().setGameObjectType(GameObjectType.Enemy);
		getBodyObject().addSensor(Type.Circle, new float[] { 0, 1, 0.5f }, SensorTypes.VISION_LEFT, Sensor.HANDLE_FIRST);
		getAnimationObject().setLayer(3);

//		float[] verticesFoot = { 0.5f, 0.3f, 0.8f, 0.3f, 0.8f, 0.4f, 0.5f, 0.4f };
//		addSensor(new Sensor(this, Type.Polygon, verticesFoot, SensorTypes.FOOT, Sensor.HANDLE_FIRST));
	}

	@Override
	public void run() {
		if (AI != null) AI.run();
		
		if(interactionHandler != null)
			interactionHandler.run();
		
	}

	public void setAI(IEnemyAI ai) {
		if (AI == ai) return;
		
		if(interactionHandler == null || !interactionHandler.equals(ai)) {
			AI = ai;
			interactionHandler = new InteractionHandler(ai, this);
			AI.setEnemy(this);
		}
	}

	public IEnemyAI getAI() {
		return AI;
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

	public enum Pattern {
		STAND, WALK_RIGHT, WALK_LEFT, RUN_RIGHT, RUN_LEFT, JUMP, JUMP_LEFT, JUMP_RIGHT;
	}

	@Override
	public boolean handleCollision(boolean start, Sensor mySensor, BodyObject other, Sensor otherSensor) {
		return super.handleCollision(start, mySensor, other, otherSensor) //
				|| getAI() != null || getAI().handleCollision(start, mySensor, other, otherSensor);
	}

	public void setNewAI(JsonValue jAI) {
		IEnemyAI ai = null;
		switch (StringFunctions.getMostEqualIndexIgnoreCase(jAI.getString("ID", ""), new String[] //
				{ "SimplePatrolAI" })) {
		case 0:
			ai = new SimplePatrolAI();
			break;
		case 1:
			break;
		case -1:
		default:
			break;
		}
		if (ai != null) ai.init(jAI.get("Param"));
		setAI(ai);
	}
}
