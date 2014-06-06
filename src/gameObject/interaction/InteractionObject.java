package gameObject.interaction;

import gameObject.AnimationObject;
import gameWorld.GameWorld;
import misc.Debug;
import misc.Debug.Mode;

import com.badlogic.gdx.math.Vector2;

public class InteractionObject extends AnimationObject implements IInteractable {
	
	private InteractionState defaultState;
	private InteractionState currentState;
	
	public InteractionObject(GameWorld gameWorld, Vector2 position) {
		super(gameWorld, position);
	}
	
	@Override
	public void setDefaultInteractionState(InteractionState defaultState) {
		this.defaultState = defaultState;
		
		if(currentState == null)
			this.currentState = defaultState;

	}
	
	@Override
	public boolean tryToSetInteractionState(InteractionState state) {
		return setInteractionState(state, false);
	}
	
	@Override
	public boolean setInteractionState(InteractionState state, boolean force) {
		if (this.currentState == state) return true;
		if (currentState != null)
			Debug.println("try to set " + state.toString() + " @current " + currentState.toString(), Mode.CONSOLE);

		if (force || isAnimationFinished()) 
			this.currentState = state;
		else 
			return false;
		return true;
	}
	
	@Override
	public boolean applyInteraction() {
		return super.applyAnimation(currentState);
	}
	

	@Override
	public boolean isInteractionFinished() {
		return currentState.isInterruptable() 
				|| currentState == null
				|| isAnimationFinished();
		
	}
	
	@Override
	public InteractionState getInteractionState() {
		return currentState;
	}
	
	@Override

	public InteractionState getDefaultInteractionState() {
		return defaultState;
	}
	
	
//	GETTER-Methods
	
	@Override
	public boolean isRunning() {
		return getInteractionState() == InteractionState.RUN;
	}

	@Override
	public boolean isThrowing() {
		return getInteractionState() == InteractionState.THROW;
	}


	@Override
	public boolean isHiding() {
		return getInteractionState().equals(InteractionState.HIDE_START)
				|| getInteractionState().equals(InteractionState.HIDE)
				|| getInteractionState().equals(InteractionState.HIDE_END);
	}

	@Override
	public boolean isCrouching() {
		return getInteractionState().equals(InteractionState.CROUCH_DOWN)
				|| getInteractionState().equals(InteractionState.CROUCH_STAND)
				|| getInteractionState().equals(InteractionState.CROUCH_SNEAK);
	}

	@Override
	public boolean isHooking() {
		return getInteractionState().equals(InteractionState.HOOK)
				|| getInteractionState().equals(InteractionState.HOOK_FLY);
	}

	@Override
	public boolean isJumping() {
		return getInteractionState().equals(InteractionState.JUMP)
				|| getInteractionState().equals(InteractionState.JUMP_MOVE);
	}

	@Override
	public boolean isGrabbing() {
		return getInteractionState().equals(InteractionState.GRAB) 
				|| getInteractionState().equals(InteractionState.GRAB_PULL);
	}

	@Override
	public void disposeUnsafe() {
		super.disposeUnsafe();
		defaultState = currentState = null;
	}

}
