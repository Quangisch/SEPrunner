package gameObject.interaction;


public interface IInteractionStateHandler {

	InteractionState getInteractionState();

	InteractionState getDefaultInteractionState();

	boolean isInteractionFinished();

	void applyInteraction(InteractionState state);
	
	boolean tryToApplyInteraction(InteractionState state);

	// InteractionState Getter
	boolean isRunning();

	boolean isThrowing();

	boolean isHiding();

	boolean isCrouching();

	boolean isHooking();

	boolean isJumping();

	boolean isGrabbing();
	
	boolean isStunned();
	
	boolean isInAction();
}
