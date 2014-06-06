package gameObject.enemy;

import gameObject.BodyObject;
import gameObject.Sensor;
import gameObject.enemy.ai.IEnemyAI;
import gameObject.enemy.ai.SimplePatrolAI;
import gameObject.interaction.InteractionObject;
import gameWorld.GameWorld;
import misc.StringFunctions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape.Type;
import com.badlogic.gdx.utils.JsonValue;

public class Enemy extends InteractionObject {

	protected IEnemyAI AI;
	protected boolean stunned;

	public Enemy(GameWorld gameWorld, Vector2 position) {
		super(gameWorld, position);
	}

	@Override
	public void init(String name) {
		super.init(name);
		setGameObjectType(GameObjectTypes.ENEMY);
		setLayer(3);
		
		addSensor(new Sensor(this, Type.Circle, new float[] { 0, 1, 0.5f }, SensorTypes.VISION, Sensor.HANDLE_FIRST));

//		float[] verticesFoot = { 0.5f, 0.3f, 0.8f, 0.3f, 0.8f, 0.4f, 0.5f, 0.4f };
//		addSensor(new Sensor(this, Type.Polygon, verticesFoot, SensorTypes.FOOT, Sensor.HANDLE_FIRST));
	}

	@Override
	public void run() {
		if (AI != null) AI.run();
		super.run();
	}

	public void setAI(IEnemyAI ai) {
		if (AI == ai) return;
		setInputHandler(AI = ai);
		AI.setEnemy(this);
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
