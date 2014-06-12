package core.ingame.input.interaction;

import gameObject.interaction.GameObject;
import gameObject.interaction.InteractionState;

public class InputToInteraction {
	
	private InteractionHandler interactionHandler;
	private ActionMovement actionMovement;
	private BasicMovement basicMovement;
	
	private GameObject gameObject;
	private InteractionState nextState, interruptedState;
	
	protected InputToInteraction(InteractionHandler interactionHandler) {
		this.interactionHandler = interactionHandler;
		this.gameObject = interactionHandler.getGameObject();
		
		actionMovement = new ActionMovement(gameObject);
		basicMovement = new BasicMovement(gameObject);
	}
	
	protected void process() {
		mapInputToState();
		tryToSetState();
	}
	
	
	private void mapInputToState() {
		nextState = processInteractionTransition();
		if(nextState == null)
			nextState = actionMovement.process(interactionHandler.getPressedActionKeys(), interactionHandler.popClick());
		if(nextState == null)
			nextState = basicMovement.process(interactionHandler.getPressedActionKeys());
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
				System.out.println(gameObject.getInteractionState());
				
				if(nextState.equals(InteractionState.THROW))
					interruptedState = currentState;
				nextState = null;
			}
		}
	}
	
}
