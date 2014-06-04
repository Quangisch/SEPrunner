package gameObject.enemy;

import gameObject.GameObject;
import gameObject.ObjectInteraction;
import gameObject.Sensor;
import gameObject.enemy.ai.IEnemyAI;
import gameObject.enemy.ai.SimplePatrolAI;
import misc.StringFunctions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape.Type;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonValue;

public class Enemy extends ObjectInteraction {

	protected IEnemyAI AI;
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
		super.run();
	}

	public void setAI(IEnemyAI ai) {
		if (AI == ai) return;
		iHandler = AI = ai;
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
	public boolean handleCollision(boolean start, Sensor sender, GameObject other, Sensor otherSensor) {
		return getAI() != null || getAI().handleCollision(start, sender, other, otherSensor);
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
