package gameObject.interaction.enemy.ai;

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
	
	private float lastX, lastY;
	
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
	public void run() {
		if(link == null)
			return;
		
		link.getBodyObject().getSensors().get(1).setActive(!getEnemy().getAnimationObject().isFlipped());//deaktiviert rechten sensor
		link.getBodyObject().getSensors().get(0).setActive(getEnemy().getAnimationObject().isFlipped());

		float currentX = link.getBodyObject().getX();
		float currentY = link.getBodyObject().getY();
		
		for(ScriptedAction a : scriptedActions) {
			if(!currentAction.contains(a.ACTION_KEY) && ((a.HORIZONTAL && a.tryToTrigger(lastX, currentX))
					|| (!a.HORIZONTAL && a.tryToTrigger(lastY, currentY)))) {
				addAction(a.ACTION_KEY);
				Debug.println("trigger "+a.ACTION_KEY, Debug.Mode.CONSOLE);
			} else if(a.trigger && !((a.HORIZONTAL && a.tryToTrigger(lastX, currentX))
							|| (!a.HORIZONTAL && a.tryToTrigger(lastY, currentY)))) {
				currentAction.remove(a.ACTION_KEY);
				a.trigger = false;
				Debug.println("release "+a.ACTION_KEY, Debug.Mode.CONSOLE);
			}
		}
		
		lastX = link.getBodyObject().getX();
		lastY = link.getBodyObject().getY();
	}

//	TODO testing
	@Override
	public void init(JsonValue actions) {
		for(JsonValue action : actions) {
			float a = action.get("between").getFloat(0);
			float b = action.get("between").size == 2 ? action.get("between").getFloat(1) : a;
			ActionKey aKey = ActionKey.valueOf(action.getString("key"));
			boolean horizontal = action.getBoolean("horizontal");
			scriptedActions.add(new ScriptedAction(aKey, horizontal, a, b));
		}
	}
	
	private void addAction(ActionKey action) {
		switch(action) {

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
			if(LOWER == HIGHER) //onPointTrigger
				trigger = (prev <= current && prev <= LOWER && LOWER <= current)
							|| (prev >= current && prev >= LOWER && LOWER >= current);
			else 			//inBetweenTrigger
				this.trigger = trigger = LOWER <= current && current <= HIGHER;
			
			return trigger;
		}
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

}
