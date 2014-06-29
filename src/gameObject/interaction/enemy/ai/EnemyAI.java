package gameObject.interaction.enemy.ai;

import gameObject.body.BodyObject;
import gameObject.body.BodyObjectType;
import gameObject.body.ISensorTypes;
import gameObject.body.ISensorTypes.SensorTypes;
import gameObject.body.Sensor;
import gameObject.interaction.enemy.Alarm;
import gameObject.interaction.enemy.Enemy;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import misc.Debug;

import com.badlogic.gdx.utils.JsonValue;

import core.ingame.input.InputHandler.Click;
import core.ingame.input.KeyMap.ActionKey;

public abstract class EnemyAI implements IEnemyAI {

	protected Enemy link;
	private Set<ActionKey> currentAction;
	private List<ScriptedAction> scriptedActions;

	protected UnresolvedAction unresolvedAction = UnresolvedAction.NORMAL;
	protected float[] advancedValues;

	protected BodyObject actionObj;
	private float lastX, lastY;
	protected int armor;
	
	
	protected EnemyAI() {
		currentAction = new HashSet<ActionKey>();
		scriptedActions = new LinkedList<ScriptedAction>();
	}

	@Override
	public Enemy getEnemy() {
		return link;
	}

	@Override
	public void setEnemy(Enemy enemy) {
		link = enemy;
		lastX = enemy.getBodyObject().getX();
		lastY = enemy.getBodyObject().getY();
	}
	
	
	@Override
	public void init(JsonValue actions, JsonValue advanced) {
		for (JsonValue action : actions) {
			float a = action.get("between").getFloat(0);
			float b = action.get("between").size == 2 ? action.get("between").getFloat(1) : a;
			ActionKey aKey = ActionKey.valueOf(action.getString("key"));
			boolean horizontal = action.getBoolean("horizontal");
			scriptedActions.add(new ScriptedAction(aKey, horizontal, a, b));
		}
		
		if(advanced != null && advanced.size > 0) {
			advancedValues = new float[advanced.size];
			for(int i = 0; i < advancedValues.length; i++)
				advancedValues[i] = advanced.getFloat(i);
		}
	}

	@Override
	public void run() {
		if (link == null) 
			return;
		
		boolean contScript = true;
		
		if(!unresolvedAction.equals(UnresolvedAction.NORMAL))
			contScript = resolveAction();
		
		if(contScript)
			applyScriptedAction();
		
	}
	
	private void applyScriptedAction() {
		if (Alarm.isActive())	keyDown(ActionKey.RUN);
		else					keyUp(ActionKey.RUN);
		
		link.getBodyObject().getSensors().get(1).setActive(!getEnemy().getAnimationObject().isFlipped());//deaktiviert rechten sensor
		link.getBodyObject().getSensors().get(0).setActive(getEnemy().getAnimationObject().isFlipped());

		float currentX = link.getBodyObject().getX();
		float currentY = link.getBodyObject().getY();

		for (ScriptedAction a : scriptedActions) {
			if (!currentAction.contains(a.ACTION_KEY)
					&& ((a.HORIZONTAL && a.tryToTrigger(lastX, currentX)) || (!a.HORIZONTAL && a.tryToTrigger(lastY,
							currentY)))) {
				addAction(a.ACTION_KEY);
				Debug.println("trigger " + a.ACTION_KEY, Debug.Mode.CONSOLE);
			} else if (a.trigger
					&& !((a.HORIZONTAL && a.tryToTrigger(lastX, currentX)) || (!a.HORIZONTAL && a.tryToTrigger(lastY,
							currentY)))) {
				currentAction.remove(a.ACTION_KEY);
				a.trigger = false;
				Debug.println("release " + a.ACTION_KEY, Debug.Mode.CONSOLE);
			}
		}

		lastX = link.getBodyObject().getX();
		lastY = link.getBodyObject().getY();
	}

	private void addAction(ActionKey action) {
		switch (action) {
		case HOOK:
		case ACTION:
			currentAction.clear();
			break;
		case CROUCH:
			currentAction.remove(ActionKey.JUMP);
			break;
		case JUMP:
			currentAction.remove(ActionKey.CROUCH);
			break;
		case LEFT:
			currentAction.remove(ActionKey.RIGHT);
			break;
		case RIGHT:
			currentAction.remove(ActionKey.LEFT);
			break;
		case RUN:System.out.println("RUN");
			currentAction.remove(ActionKey.CROUCH);
			break;
		case THROW:
			break;
		default:
			break;

		}

		currentAction.add(action);
	}

	public class ScriptedAction {

		private final ActionKey ACTION_KEY;
		private final float LOWER, HIGHER;
		private final boolean HORIZONTAL;
		private boolean trigger;

		protected ScriptedAction(ActionKey actionKey, boolean horizontal, float a, float b) {
			this.ACTION_KEY = actionKey;
			this.HORIZONTAL = horizontal;
			LOWER = b < a ? b : a;
			HIGHER = b > a ? b : a;
		}

		protected ScriptedAction(ActionKey action, boolean horizontal, float a) {
			this(action, horizontal, a, a);
		}

		protected boolean tryToTrigger(float prev, float current) {
			boolean trigger = false;
			if (LOWER == HIGHER) //onPointTrigger
				trigger = (prev <= current && prev <= LOWER && LOWER <= current)
						|| (prev >= current && prev >= LOWER && LOWER >= current);
			else
				//inBetweenTrigger
				this.trigger = trigger = LOWER <= current && current <= HIGHER;

			return trigger;
		}
	}
	
//	UNRESOLVED ACTION
	/**
	 * Overwrite for correct usage
	 * @return continue with regular scriptedActions
	 */
	protected boolean resolveAction() {
		switch(unresolvedAction) {
		case ALARM_TRIGGERD:
			break;
		case HIT_BY_SHURIKEN:
			break;
		case NORMAL:
			break;
		case SEE_PLAYER:
			break;
		case SEE_STUNNED_ENEMY:
			break;
		default:
			break;
		
		}
		unresolvedAction = UnresolvedAction.NORMAL;
		return true;
	}
	
//	TODO
	protected boolean scanArea(float radius) {
		
		return false;
	}
	
//	TODO
	protected void actionAfterHit() {
		
	}

	//	IInputHandler
	@Override
	public void addActionKey(ActionKey action, int... keys) {

	}

	@Override
	public boolean isKeyDown(ActionKey action) {
		return currentAction.contains(action);
	}

	@Override
	public boolean isButtonDown(ActionKey action) {
		return false;
	}

	@Override
	public boolean keyUp(ActionKey action) {
		currentAction.remove(action);
		return false;
	}

	@Override
	public Click popClick() {
		return null;
	}

	@Override
	public Click getClick() {
		return null;
	}

	@Override
	public void keyDown(ActionKey action) {
		currentAction.add(action);
	}
	
	protected enum UnresolvedAction {
		NORMAL, HIT_BY_SHURIKEN, SEE_PLAYER, SEE_STUNNED_ENEMY, ALARM_TRIGGERD 
	}
	
	@Override
	public boolean handleCollision(boolean start, boolean postSolve, Sensor mySensor, BodyObject other, Sensor otherSensor) {
	
		if(!postSolve) {
			
			if(mySensor != null) {
				
				boolean meFlipped = mySensor.getBodyObject().getParent().getAnimationObject().isFlipped();
				
//				viewSensor triggered
				if((mySensor.getSensorType() == ISensorTypes.SensorTypes.VISION_LEFT && meFlipped)
							|| (mySensor.getSensorType() == ISensorTypes.SensorTypes.VISION_RIGHT && !meFlipped)) {
					
					if((other.getBodyObjectType().equals(BodyObjectType.Player) && !other.getParent().isHiding())		//triggered by player
						|| (other.getBodyObjectType().equals(BodyObjectType.Enemy) && other.getParent().isStunned())) {	//triggered by stunned fellow enemy
						
						Alarm.trigger();
						return true;
					}
				} //sensor == viewSensor
				
//				bodySensor triggered by shuriken
				else if(mySensor.getSensorType() == SensorTypes.BODY && other.getBodyObjectType().equals(BodyObjectType.Shuriken)) {
					
					armor--;
					if(armor < 0)	getEnemy().setStun();
					else {
						unresolvedAction = UnresolvedAction.HIT_BY_SHURIKEN;
						actionObj = other;
					}
					
					other.getParent().dispose();
					return true;
				}
				
			}  // end if (sensor != null)
			
		} //postSolve
		return false;
	}

}
