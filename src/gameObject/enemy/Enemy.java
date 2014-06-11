package gameObject.enemy;

import gameObject.GameObject;
import gameObject.ObjectInteraction;
import gameObject.Sensor;
import gameObject.enemy.ai.IEnemyAI;
import gameObject.enemy.ai.SimplePatrolAI;
import gameWorld.GameWorld;
import misc.StringFunctions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape.Type;
import com.badlogic.gdx.utils.JsonValue;

public class Enemy extends ObjectInteraction {

	protected IEnemyAI AI;
	protected boolean stunned;
	protected Texture sichtfeld;
	
	public Enemy(GameWorld world, Vector2 position) {
		super(world, position);
	}

	@Override
	public void init(String name) {
		super.init(name);
		setGameObjectType(GameObjectTypes.ENEMY);
		setLayer(3);
		body.setLinearDamping(2.5f);
		body.setFixedRotation(true);
		addSensor(new Sensor(this, Type.Circle, new float[] { 0, 1, 0.5f },
				SensorTypes.VISION, Sensor.HANDLE_FIRST));
		// NILS
		addSensor(new Sensor(this, Type.Circle, new float[] { 1.3f, 1, 0.5f },
				SensorTypes.VISION, Sensor.HANDLE_FIRST));
		float[] verticesBody = { 0.4f, 0.3f, 0.9f, 0.3f, 0.9f, 1.15f, 0.4f, 1.15f };
		addSensor(new Sensor(this, Type.Polygon, verticesBody,
				SensorTypes.BODY, Sensor.HANDLE_FIRST));
		// NILS

		float[] verticesFoot = { 0.5f, 0.3f, 0.8f, 0.3f, 0.8f, 0.4f, 0.5f, 0.4f };
		addSensor(new Sensor(this, Type.Polygon, verticesFoot,
				SensorTypes.FOOT, Sensor.HANDLE_FIRST));
		sichtfeld = new Texture("res/sprites/sichtfeld.png");
	}

	@Override
	public void run() {
		if (AI != null)
			AI.run();
		super.run();
	}

	@Override
	//TODO: sichtfeld darf player nicht überdecken
	public void draw(SpriteBatch batch, float deltaTime) {
		float x,y;
		if(!stunned){
			if(this.isFlipped()){
				x = this.getX()-55;
				y = this.getY()+50;
				batch.draw(sichtfeld, x, y, 100, 100, 0, 0, sichtfeld.getWidth(), sichtfeld.getHeight(), true, false);
			}else{
				x = this.getX()+105;
				y = this.getY()+50;
				batch.draw(sichtfeld, x, y, 100, 100, 0, 0, sichtfeld.getWidth(), sichtfeld.getHeight(), false, false);
			}
		}
		super.draw(batch, deltaTime);
	}

	public void setAI(IEnemyAI ai) {
		if (AI == ai)
			return;
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
	public boolean handleCollision(boolean start, Sensor sender,
			GameObject other, Sensor otherSensor) {
		// return super.handleCollision(start, sender, other, otherSensor) //
		// || getAI() != null || getAI().handleCollision(start, sender, other,
		// otherSensor);
		// NILS
		return AI.handleCollision(start, sender, other, otherSensor) //
				|| getAI() != null
				|| getAI().handleCollision(start, sender, other, otherSensor);
		// NILS
	}

	public void setNewAI(JsonValue jAI) {
		IEnemyAI ai = null;
		switch (StringFunctions.getMostEqualIndexIgnoreCase(
				jAI.getString("ID", ""), new String[] //
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
		if (ai != null)
			ai.init(jAI.get("Param"));
		setAI(ai);
	}
}
