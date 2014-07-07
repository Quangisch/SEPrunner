package gameObject.interaction;

import gameObject.body.BodyObject;
import gameObject.drawable.AnimationObject;
import gameWorld.GameWorld;
import misc.Debug;

abstract class InteractionManager implements IGameObject {
	
	private GameWorld gameWorld;
	private AnimationObject aniObject;
	private BodyObject bodyObject;
	
	private InteractionState defaultState;
	private InteractionState currentState;
	
	protected InteractionManager(GameWorld gameWorld) {
		this.gameWorld = gameWorld;
	}

	protected void iniLink(AnimationObject aniObject, BodyObject bodyObject) {
		this.aniObject = aniObject;
		this.bodyObject = bodyObject;
	}
	
	public GameWorld getGameWorld() {
		return gameWorld;
	}
	
	public AnimationObject getAnimationObject() {
		return aniObject;
	}
	
	public BodyObject getBodyObject() {
		return bodyObject;
	}
	
	protected void setDefaultInteractionState(InteractionState defaultState) {
		if(defaultState != null) {
			this.defaultState = defaultState;
			if(currentState == null)
				this.currentState = defaultState;
	
		} else
			System.err.println(this.getClass()+"@setDefaultInteractionState(...) Invalid Argument : defaultState == null");
	}
	
	@Override
	public boolean tryToApplyInteraction(InteractionState state) {
		Debug.print("tryToApply "+state+" @current "+currentState, Debug.Mode.CONSOLE);
		if(isInteractionFinished()) {
			applyInteraction(state);
			return true;
		}
		return false;
	}
	
	@Override
	public void applyInteraction(InteractionState state) {
		Debug.print(">>apply state "+state, Debug.Mode.CONSOLE);
		this.currentState = state;
		bodyObject.setFixture(state);
		aniObject.applyAnimation(state);
	}

	@Override
	public boolean isInteractionFinished() {
		return currentState == null 
				|| currentState.isInterruptable() 
				|| getAnimationObject().isAnimationFinished();
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
				|| currentState.equals(InteractionState.GRAB_PULL)
				|| currentState.equals(InteractionState.GRAB_START);
	}
	
	@Override
	public boolean isStunned() {
		return currentState.equals(InteractionState.STUNNED)
				|| currentState.equals(InteractionState.PULLED);
	}
	
	@Override
	public boolean isInAction() {
		if(isGrabbing() || isHiding() || isHooking() || isThrowing())
			return true;
		return false;
	}

}
