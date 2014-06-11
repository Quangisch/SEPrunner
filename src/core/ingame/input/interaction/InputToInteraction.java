package core.ingame.input.interaction;

import gameObject.interaction.GameObject;
import gameObject.interaction.InteractionState;

import java.util.HashSet;
import java.util.Set;

import core.ingame.input.IInputHandler;
import core.ingame.input.KeyMap.ActionKey;

public class InputToInteraction implements Runnable{
	
	private IInputHandler iHandler;
	private GameObject gameObject;
	private ActionMovement actionMovement;
	private BasicMovement basicMovement;
	
	private InteractionState nextState, interruptedState;

	private Set<ActionKey> pressedActionKeys;
	
	public InputToInteraction(IInputHandler iHandler, GameObject gameObject) {
		this.iHandler = iHandler;
		this.gameObject = gameObject;
		
		pressedActionKeys = new HashSet<ActionKey>();
		actionMovement = new ActionMovement(gameObject);
		basicMovement = new BasicMovement(gameObject);
	}
	
	public void run() {
		
		processInput();
		mapInputToState();
		tryToSetState();
	}
	
	private void processInput() {
		for(ActionKey aK : ActionKey.values()) {
			if(iHandler.isKeyDown(aK) || iHandler.isButtonDown(aK))
				pressedActionKeys.add(aK);
			else
				pressedActionKeys.remove(aK);
		}
	}
	
	private void mapInputToState() {
		nextState = processInteractionTransition();
		if(nextState == null)
			nextState = actionMovement.process(pressedActionKeys, iHandler.popClick());
		if(nextState == null)
			nextState = basicMovement.process(pressedActionKeys);
	}
	
	private InteractionState processInteractionTransition() {
		if(gameObject.isInteractionFinished()) {
			switch(gameObject.getInteractionState()) {
			case CROUCH_DOWN:
				return InteractionState.CROUCH_STAND;
				
			case THROW:
			case GRAB_DISPOSE:
			case HIDE_END:
				return InteractionState.STAND;
				
			case HIDE_START:
				return InteractionState.HIDE;
				
			case HOOK:
				return InteractionState.HOOK_FLY;
				
			default:
				return null;
			}
		}
		
		return null;
	}
	
	private void tryToSetState() {
		
		if(interruptedState != null) {
			if(gameObject.tryToApplyInteraction(nextState))
				interruptedState = null;
			
		} else if(nextState != null && !nextState.equals(gameObject.getInteractionState())) {
			final InteractionState currentState = gameObject.getInteractionState();
			
			if(gameObject.tryToApplyInteraction(nextState)) {
//				System.out.println(gameObject.getInteractionState());
				
				if(nextState.equals(InteractionState.THROW))
					interruptedState = currentState;
				nextState = null;
			}
		}
	}
	
}
