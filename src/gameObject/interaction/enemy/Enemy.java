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
import box2dLight.Light;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape.Type;
import com.badlogic.gdx.utils.JsonValue;

import core.ingame.input.interaction.InteractionHandler;

public class Enemy extends GameObject implements IEnemy {

	protected IEnemyAI AI;
	private InteractionHandler interactionHandler;
	private ConeLight view;
	private final int INITAL_VIEW_LENGTH = 150;
	private int viewLength = INITAL_VIEW_LENGTH;
	private final Color INITIAL_COLOR = new Color(1,1,0.5f,0.8f),
			SCAN_COLOR = new Color(1,1,0,1),
			DETECT_COLOR = new Color(1,0.5f,0,1f);
	private boolean scannedArea, seePlayer;
	
	public Enemy(GameWorld world, Vector2 position) {
		super(world, position);
		if(Gdx.graphics.isGL20Available())
			view = new ConeLight(world.getRayHandler(), 32, INITIAL_COLOR, INITAL_VIEW_LENGTH, 0, 0, 0, 30);
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

		if(Gdx.graphics.isGL20Available()) {
			Vector2 head = getBodyObject().getPosition();
			
			head.x += getAnimationObject().isFlipped() ? 40 : 110;
			head.y += 102;
			if(isCrouching())
				head.y -= 25;
			view.setPosition(head);
			
			if(viewLength == INITAL_VIEW_LENGTH)
				view.setDirection(getAnimationObject().isFlipped() ? 180 : 0);

			seePlayer = !getGameWorld().getPlayer().isHiding() && view.contains(getGameWorld().getPlayer().getBodyObject().getX(), 
					getGameWorld().getPlayer().getBodyObject().getY());
			
			if(seePlayer) {
				Alarm.getInstance().trigger(Gdx.graphics.getDeltaTime()*2);
				if(!scannedArea)
					scanArea(getGameWorld().getPlayer().getBodyObject().getX(), 
							getGameWorld().getPlayer().getBodyObject().getY(),
							viewLength, viewLength);
			}
			
			
			view.setDistance(viewLength);
			view.setColor(seePlayer ? DETECT_COLOR : viewLength != INITAL_VIEW_LENGTH ? SCAN_COLOR : INITIAL_COLOR);
		}

		scannedArea = false;
	}


	@Override
	public void init(JsonValue resources) {
		super.init(resources);
		setBodyObjectType(BodyObjectType.Enemy);
		getAnimationObject().setLayer(3);

		float lx = 0.6f, rx = 0.8f;
		float y = 1f, w = 1.5f, h = 0.5f; 
		getBodyObject().addSensor(Type.Polygon, new float[] { lx-w,y-h/2 , lx-w,y+h/2 , lx,y+h/2 , lx,y-h/2 },
				SensorTypes.VISION_LEFT, Sensor.HANDLE_FIRST);
		// NILS
		getBodyObject().addSensor(Type.Polygon, new float[] { rx,y-h/2 , rx,y+h/2 , rx+w,y+h/2 , rx+w,y-h/2 },
				SensorTypes.VISION_RIGHT, Sensor.HANDLE_FIRST);

	}
	
	public void setNewAI(JsonValue jAI, JsonValue jMul) {
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
			ai.init(jAI.get("Actions"), jAI.get("Advanced"));
		
		if(jMul == null)	setAI(ai);
		else				setAI(ai, jMul.getFloat(0), jMul.getFloat(1), 
										jMul.getFloat(2), jMul.getFloat(3));
		
	}

	private void setAI(IEnemyAI ai, float walkMul, float runMul, float sneakMul, float pullMul) {
		if (AI == ai)
			return;
		AI = ai;
		interactionHandler = new InteractionHandler(ai, this);
		interactionHandler.setForceMultiplier(walkMul, runMul, sneakMul, pullMul);
		AI.setEnemy(this);
	}
	
	public void setAI(IEnemyAI ai) {
		setAI(ai, 0.55f, 1.2f, 0.8f, 1.5f);
	}

	public IEnemyAI getAI() {
		return AI;
	}

	public void setStun() {
		applyInteraction(InteractionState.STUNNED);
		if(Gdx.graphics.isGL20Available())
			view.setActive(false);
	}

	@Override
	public boolean handleCollision(boolean start, boolean postSolve, Sensor mySensor,
			BodyObject other, Sensor otherSensor) {
		
		super.handleCollision(start, postSolve, mySensor, other, otherSensor);
		return !isStunned() && (AI.handleCollision(start, postSolve, mySensor, other, otherSensor)
				|| getAI() != null
				|| getAI().handleCollision(start, postSolve, mySensor, other, otherSensor));
	}
	
	public InteractionHandler getInteractionHandler(){
		return interactionHandler;
	}
	
	public boolean scanArea(float triggerX, float triggerY) {
		return scanArea(triggerX, triggerY, viewLength, viewLength);
	}
	
	public boolean scanArea(float triggerX, float triggerY, int scanLength, int scanSpeed) {
		if(!Gdx.graphics.isGL20Available())
			return false;
		
		scannedArea = true;
		float dx = triggerX - getBodyObject().getX();
		float dy = triggerY - getBodyObject().getY();
		
		float direction = (float)(Math.atan2(dy,  dx) / Math.PI * 180);
		
		viewLength = (int)Math.min(scanLength, view.getDistance()+scanSpeed);
		
		if((Math.abs(direction) < 90 && !getAnimationObject().isFlipped())
				||Math.abs(direction) > 90 && getAnimationObject().isFlipped())
			view.setDirection(direction);
		view.setDistance(viewLength);
		return view.contains(triggerX, triggerY);
	}
	
	public void resetView() {
		viewLength = INITAL_VIEW_LENGTH;
	}
	
	public Light getView() {
		return view;
	}
}