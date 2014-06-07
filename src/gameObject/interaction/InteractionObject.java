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
		if(defaultState != null) {
			this.defaultState = defaultState;
			if(currentState == null)
				this.currentState = defaultState;
	
		} else
			System.err.println(this.getClass()+"@setDefaultInteractionState(...) Invalid Argument : defaultState == null");
		
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

		if (force || isInteractionFinished()) 
			this.currentState = state;
		else 
			return false;
		return true;
	}
	
	@Override
	public boolean applyInteraction() {
		if(isInteractionFinished())
			return applyAnimation(currentState);
		return false;
	}
	

	@Override
	public boolean isInteractionFinished() {
		return currentState == null || currentState.isInterruptable();
		
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
		return currentState.equals(InteractionState.RUN);
	}

	@Override
	public boolean isThrowing() {
		return currentState.equals(InteractionState.THROW);
	}


	@Override
	public boolean isHiding() {
		return currentState.equals(InteractionState.HIDE_START)
				|| currentState.equals(InteractionState.HIDE)
				|| currentState.equals(InteractionState.HIDE_END);
	}

	@Override
	public boolean isCrouching() {
		return currentState.equals(InteractionState.CROUCH_DOWN)
				|| currentState.equals(InteractionState.CROUCH_STAND)
				|| currentState.equals(InteractionState.CROUCH_SNEAK);
	}

	@Override
	public boolean isHooking() {
		return currentState.equals(InteractionState.HOOK)
				|| currentState.equals(InteractionState.HOOK_FLY);
	}

	@Override
	public boolean isJumping() {
		return currentState.equals(InteractionState.JUMP)
				|| currentState.equals(InteractionState.JUMP_MOVE);
	}

	@Override
	public boolean isGrabbing() {
		return currentState.equals(InteractionState.GRAB) 
				|| currentState.equals(InteractionState.GRAB_PULL);
	}

	@Override
	public void disposeUnsafe() {
		super.disposeUnsafe();
		defaultState = currentState = null;
	}

}
