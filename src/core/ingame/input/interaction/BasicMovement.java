package core.ingame.input.interaction;

import gameObject.GameObject;
import gameObject.body.BodyObjectType;
import gameObject.interaction.InteractionState;

import java.util.Set;

import core.ingame.input.IInputHandler;
import core.ingame.input.player.KeyMap.ActionKey;

public class BasicMovement {

	private IInputHandler iHandler;
	private GameObject gameObject;
	
	protected BasicMovement(IInputHandler iHandler, GameObject gameObject) {
		this.iHandler = iHandler;
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
		if(nextState == null && gameObject.isInteractionFinished() 
				&& (actions.contains(ActionKey.LEFT) || actions.contains(ActionKey.RIGHT)))
			nextState = processMovement();
		
		if(triggerRun(nextState) || (gameObject.getInteractionState().equals(InteractionState.WALK) && iHandler.isKeyDown(ActionKey.RUN)))
			nextState = InteractionState.RUN;
		
		if(gameObject.isRunning() && !actions.contains(ActionKey.RUN))
			nextState = InteractionState.WALK;
			
		return nextState;
	}
	
	private boolean autoCrouch;
//	END
	private InteractionState end(Set<ActionKey> actions) {
		switch(gameObject.getInteractionState()) {
		case CROUCH_SNEAK:
			if(actions.contains(ActionKey.CROUCH) && 
					!(actions.contains(ActionKey.LEFT) || actions.contains(ActionKey.RIGHT)))
				return InteractionState.CROUCH_STAND;
		case CROUCH_DOWN:
		case CROUCH_STAND:
			if(!gameObject.isBodyBlocked() && (autoCrouch || !actions.contains(ActionKey.CROUCH))) {
				autoCrouch = false;
				iHandler.keyUp(ActionKey.CROUCH);
				return InteractionState.STAND;
			}
			
			if(gameObject.isBodyBlocked() && !actions.contains(ActionKey.CROUCH)) {
				autoCrouch = true;
				iHandler.keyDown(ActionKey.CROUCH);
				return InteractionState.CROUCH_STAND;
			}
			
			if(autoCrouch) {
				if(!iHandler.isKeyDown(ActionKey.CROUCH))
					iHandler.keyDown(ActionKey.CROUCH);
				if(iHandler.isKeyDown(ActionKey.LEFT) || iHandler.isKeyDown(ActionKey.RIGHT))
					processMovement();
				else
					return InteractionState.CROUCH_STAND;
			}
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
		InteractionState nextState = null;
		switch(gameObject.getInteractionState()) {
		case CROUCH_STAND:
		case CROUCH_DOWN:
			nextState = InteractionState.CROUCH_SNEAK;
			break;
		
		case GRAB:
			if((!gameObject.getAnimationObject().isFlipped() && iHandler.isKeyDown(ActionKey.LEFT))
					|| (gameObject.getAnimationObject().isFlipped() && iHandler.isKeyDown(ActionKey.RIGHT)))
				nextState = InteractionState.GRAB_PULL;
			break;
		case JUMP:
			nextState = InteractionState.JUMP_MOVE;
			break;
		case STAND:
			nextState = InteractionState.WALK;
			break;
		default:
			;
		}	
		
		
		
		return nextState;
	}
	
//	RUN
	private int RUN_TAP_TIMER_MAX = 20, RUN_TAP_TIMER_MIN = 5;
	private int runTapTimer = 0;
	private boolean triggerRun(InteractionState nextState) {
		
		if(gameObject.isJumping() || gameObject.getBodyObjectType().equals(BodyObjectType.Enemy)) {
			runTapTimer = 0;
			return false;
		}
		
		boolean trigger = false;

		if (nextState == InteractionState.WALK && runTapTimer == 0)
			runTapTimer++;
		else if (nextState == null && runTapTimer >= 1 && runTapTimer < RUN_TAP_TIMER_MAX) {
			runTapTimer++;
		} else if (nextState == InteractionState.WALK && runTapTimer > RUN_TAP_TIMER_MIN) {
			runTapTimer = RUN_TAP_TIMER_MAX;
			trigger = true;
			iHandler.keyDown(ActionKey.RUN);
		}
		
		if((runTapTimer >= RUN_TAP_TIMER_MAX 
				&& !(iHandler.isKeyDown(ActionKey.LEFT) || iHandler.isKeyDown(ActionKey.RIGHT)))
				|| gameObject.isCrouching() || gameObject.isInAction()) {
			runTapTimer = 0;
			
			iHandler.keyUp(ActionKey.RUN);
		}

		return trigger;
	}
	
}
