package gameObject.interaction.enemy;

import gameObject.body.BodyObject;
import gameObject.body.BodyObjectType;
import gameObject.body.ISensorTypes.SensorTypes;
import gameObject.body.Sensor;
import gameObject.interaction.GameObject;
import gameObject.interaction.InteractionState;
import gameObject.interaction.enemy.ai.HardAI;
import gameObject.interaction.enemy.ai.IEnemyAI;
import gameObject.interaction.enemy.ai.MediumAI;
import gameObject.interaction.enemy.ai.SimplePatrolAI;
import gameWorld.GameWorld;
import misc.StringFunctions;
import box2dLight.ConeLight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape.Type;
import com.badlogic.gdx.utils.JsonValue;

import core.ingame.input.interaction.InteractionHandler;

public class Enemy extends GameObject {

	protected IEnemyAI AI;
	private InteractionHandler interactionHandler;
	private ConeLight view;
	
	public Enemy(GameWorld world, Vector2 position) {
		super(world, position);
		view = new ConeLight(world.getRayHandler(), 32, new Color(1,1,0.5f,0.8f), 200, 0, 0, 0, 30);
	}

	@Override
	public void init(String name) {
		super.init(name);
		setBodyObjectType(BodyObjectType.Enemy);
		getAnimationObject().setLayer(3);
//		getBodyObject().setLinearDamping(2.5f);
		
		getBodyObject().addSensor(Type.Circle, new float[] { 0, 1, 0.5f },
				SensorTypes.VISION_LEFT, Sensor.HANDLE_FIRST);
		// NILS
		getBodyObject().addSensor(Type.Circle, new float[] { 1.3f, 1, 0.5f },
				SensorTypes.VISION_RIGHT, Sensor.HANDLE_FIRST);
		float[] verticesBody = { 0.4f, 0.3f, 0.9f, 0.3f, 0.9f, 1.15f, 0.4f, 1.15f };
		getBodyObject().addSensor(Type.Polygon, verticesBody,
				SensorTypes.BODY, Sensor.HANDLE_FIRST);
		// NILS

		float[] verticesFoot = { 0.5f, 0.3f, 0.8f, 0.3f, 0.8f, 0.4f, 0.5f, 0.4f };
		getBodyObject().addSensor(Type.Polygon, verticesFoot,
				SensorTypes.FOOT, Sensor.HANDLE_FIRST);
	}

	@Override
	public void run() {
		super.run();
		if(isStunned())
			return;
		if (AI != null)
			AI.run();
		if(interactionHandler != null)
			interactionHandler.run();
		
//		TODO hack
		Vector2 head = getBodyObject().getPosition();
		
		head.x += getAnimationObject().isFlipped() ? 40 : 110;
		head.y += 100;
		view.setPosition(head);
		view.setDirection(getAnimationObject().isFlipped() ? 180 : 0);
	}

	public void setAI(IEnemyAI ai) {
		if (AI == ai)
			return;
		AI = ai;
		interactionHandler = new InteractionHandler(ai, this);
		AI.setEnemy(this);
	}

	public IEnemyAI getAI() {
		return AI;
	}

	public void setStun() {
		applyInteraction(InteractionState.STUNNED);
		view.setActive(false);
	}

	public boolean isCarriable(Vector2 position) {
		// TODO
		return false;
	}

	public enum Pattern {
		STAND, WALK_RIGHT, WALK_LEFT, RUN_RIGHT, RUN_LEFT, JUMP, JUMP_LEFT, JUMP_RIGHT;
	}

	@Override
	public boolean handleCollision(boolean start, boolean postSolve, Sensor mySensor,
			BodyObject other, Sensor otherSensor) {
		// return super.handleCollision(start, sender, other, otherSensor) //
		// || getAI() != null || getAI().handleCollision(start, sender, other,
		// otherSensor);
		// NILS
		return !isStunned() && (AI.handleCollision(start, postSolve, mySensor, other, otherSensor) //
				|| getAI() != null
				|| getAI().handleCollision(start, postSolve, mySensor, other, otherSensor));
		// NILS
	}

	public void setNewAI(JsonValue jAI) {
		IEnemyAI ai = null;
		switch (StringFunctions.getMostEqualIndexIgnoreCase(
				jAI.getString("ID", ""), new String[] //
				{ "SimplePatrolAI","MediumAI","HardAI" })) {
		case 0:
			ai = new SimplePatrolAI();
			break;
		case 1:
			ai = new MediumAI();
			break;
		case 2:
			ai = new HardAI();
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