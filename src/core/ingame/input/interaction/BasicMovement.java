package core.ingame.input.interaction;

import gameObject.interaction.GameObject;
import gameObject.interaction.InteractionState;

import java.util.Set;

import core.ingame.input.KeyMap.ActionKey;

public class BasicMovement {

	private GameObject gameObject;
	
	protected BasicMovement(GameObject gameObject) {
		this.gameObject = gameObject;
	}
	
	protected InteractionState process(Set<ActionKey> actions) {
		InteractionState nextState = null;
		nextState = end(actions);
		if(nextState == null)
			nextState = begin(actions);
		return nextState;
	}
	
//	BEGIN
	private InteractionState begin(Set<ActionKey> actions) {
		boolean inAction = gameObject.isInAction();
		InteractionState nextState = null;
		
		if(!inAction && nextState == null && actions.contains(ActionKey.JUMP))
			nextState = processJump();
		if(!inAction && nextState == null && actions.contains(ActionKey.CROUCH))
			nextState = processCrouch();
		if(nextState == null && (actions.contains(ActionKey.LEFT) || actions.contains(ActionKey.RIGHT))) {
			nextState = processMovement();
			
			if(nextState != null && nextState.equals(InteractionState.WALK) && actions.contains(ActionKey.RUN))
				nextState = InteractionState.RUN;
		}
		
		if(triggerRun(nextState))
			nextState = InteractionState.RUN;
			
		return nextState;
	}
	
//	END
	private InteractionState end(Set<ActionKey> actions) {
		switch(gameObject.getInteractionState()) {
		case CROUCH_DOWN:
		case CROUCH_SNEAK:
		case CROUCH_STAND:
			if(!gameObject.isBodyBlocked() && !actions.contains(ActionKey.CROUCH))
				return InteractionState.STAND;
			else
				return null;
			
		case WALK:
		case RUN:
			if(!(actions.contains(ActionKey.LEFT) || actions.contains(ActionKey.RIGHT)) && gameObject.isGrounded())
				return InteractionState.STAND;
			else
				return null;
			
		case JUMP:
		case JUMP_MOVE:
			if(!actions.contains(ActionKey.JUMP) && gameObject.isGrounded())
				return InteractionState.STAND;

		default:
			return null;
		
		}
	}
	
//	JUMP
	private InteractionState processJump() {
		if(gameObject.isGrounded() && !gameObject.isCrouching())
			return InteractionState.JUMP;
		return null;
	}
	
//	CROUCH
	private InteractionState processCrouch() {
		if(gameObject.isGrounded() && !gameObject.isCrouching())
			return InteractionState.CROUCH_DOWN;
		return null;
	}
	
//	MOVEMENT
	private InteractionState processMovement() {

		
		switch(gameObject.getInteractionState()) {
		case CROUCH_STAND:
		case CROUCH_DOWN:
			return InteractionState.CROUCH_SNEAK;
		case GRAB:
			return InteractionState.GRAB_PULL;
		case JUMP:
			return InteractionState.JUMP_MOVE;
		case STAND:
			return InteractionState.WALK;
		default:
			return null;
		}	
	}
	
//	RUN
	private int RUN_TAP_TIMER_MAX = 20,
			RUN_TAP_TIMER_MIN = 5;
	private int runTapTimer = 0;
	private boolean triggerRun(InteractionState nextState) {

		System.out.println(runTapTimer);
		if(nextState != null && nextState.equals(InteractionState.WALK) && runTapTimer == 0)
			runTapTimer++;
		else if(nextState == null && runTapTimer >= 1 && runTapTimer <= RUN_TAP_TIMER_MAX)
			runTapTimer++;
		else if(nextState != null && nextState.equals(InteractionState.WALK) && runTapTimer > RUN_TAP_TIMER_MIN) {
			runTapTimer = 0;
			return true;
		}
		
		if(runTapTimer >= RUN_TAP_TIMER_MAX)
			runTapTimer = 0;
		
		return false;
	}
}
