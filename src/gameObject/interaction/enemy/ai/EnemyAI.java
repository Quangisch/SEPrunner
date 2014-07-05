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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonValue;

import core.ingame.input.InputHandler.Click;
import core.ingame.input.KeyMap.ActionKey;

public abstract class EnemyAI implements IEnemyAI {

	protected Enemy link;
	protected Set<ActionKey> currentAction;
	private List<ScriptedAction> scriptedActions;
	
	protected UnresolvedAction unresolvedAction = UnresolvedAction.NORMAL;
	protected float[] advancedValues;

	protected float lastX, lastY, triggerX, triggerY;
	private float INITIAL_STUN_TIME;
	private float armor, stunTime;
	protected List<ActionKey> storedActions;
	
	protected EnemyAI(float armor, float initialStunTime) {
		INITIAL_STUN_TIME = initialStunTime;
		this.armor = armor;
		
		currentAction = new HashSet<ActionKey>();
		scriptedActions = new LinkedList<ScriptedAction>();
		storedActions = new LinkedList<ActionKey>();
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
		
		if(Math.abs(lastX - getEnemy().getGameWorld().getPlayer().getBodyObject().getX()) < 1000)
			Debug.println("Nearby Enemy, distance@"+Math.abs(lastX - getEnemy().getGameWorld().getPlayer().getBodyObject().getX())+" armor@"+armor+" type@"+this.getClass());
		
		boolean contScript = true;
		if(stunTime > 0) {
			
			if(stunTime == INITIAL_STUN_TIME) {
				for(ActionKey a : currentAction)
					storedActions.add(a);
				currentAction.clear();
			}
			
			currentAction.add(ActionKey.CROUCH);
			stunTime -= Gdx.graphics.getDeltaTime();
			if(Gdx.graphics.isGL20Available())
				getEnemy().getView().setActive(stunTime <= 0);
			
			if(stunTime <= 0) {

				currentAction.clear();
				for(ActionKey a : storedActions)
					currentAction.add(a);
				storedActions.clear();
			}
			
			return;
		}
		
		if(!unresolvedAction.equals(UnresolvedAction.NORMAL))
			contScript = resolveAction();
		else
			getEnemy().resetView();
		if(contScript) {
			applyScriptedAction();
		}
		

	}
	
	private void applyScriptedAction() {
		if(Math.abs(lastX - getEnemy().getGameWorld().getPlayer().getBodyObject().getX()) < 1000)
		
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
		case RUN:
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
			if(Alarm.isActive()) {
				getEnemy().scanArea(getEnemy().getGameWorld().getPlayer().getBodyObject().getX(), 
						getEnemy().getGameWorld().getPlayer().getBodyObject().getY());
				boolean action = actionAfterAlarm();
				if(!action)
					break;
				return !action;
			} else
				addAction(lastX < getEnemy().getBodyObject().getX() ? ActionKey.LEFT : ActionKey.RIGHT);
			
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
		
	protected boolean actionAfterAlarm() {
		if(advancedValues == null || getEnemy().getGameWorld().getPlayer().isHiding())
			return false;
		
		currentAction.add(ActionKey.RUN);
		float playerX = getEnemy().getGameWorld().getPlayer().getBodyObject().getX();		
		//
		if(playerX < getEnemy().getBodyObject().getX()){
			
			if(advancedValues[0] < getEnemy().getBodyObject().getX())
				addAction(ActionKey.LEFT);
			
			else if(advancedValues[0] > getEnemy().getBodyObject().getX())
				keyUp(ActionKey.LEFT);
			
		} else {
			
			if(advancedValues[1] > getEnemy().getBodyObject().getX())
				addAction(ActionKey.RIGHT);
			
			else if(advancedValues[1] < getEnemy().getBodyObject().getX())
					keyUp(ActionKey.RIGHT);
		}
		return true;
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
					
					if((other.getBodyObjectType().equals(BodyObjectType.Player) && 
							!other.getParent().isHiding()) && !(currentAction.size() == 1 && getEnemy().isCrouching())	//triggered by player
						|| (other.getBodyObjectType().equals(BodyObjectType.Enemy) && other.getParent().isStunned())) {	//triggered by stunned fellow enemy
						
						Alarm.trigger();
						unresolvedAction = UnresolvedAction.ALARM_TRIGGERD;
						return true;
					}
				} //sensor == viewSensor
				
//				bodySensor triggered by shuriken
				else if(mySensor.getSensorType() == SensorTypes.BODY && other.getBodyObjectType().equals(BodyObjectType.Shuriken)) {
					
					armor -= currentAction.size() == 1 && currentAction.contains(ActionKey.CROUCH) ? 0.4f : 1;   
					if(armor <= 0)	getEnemy().setStun();
					else			unresolvedAction = UnresolvedAction.HIT_BY_SHURIKEN;
				
					triggerX = other.getX();
					triggerY = other.getY();
					stunTime = INITIAL_STUN_TIME;
					
					other.getParent().dispose();
					return true;
				}
				
			}  // end if (sensor != null)
			
		} //postSolve
		return false;
	}

}
