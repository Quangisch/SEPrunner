package gameObject.interaction;


public interface IInteractable {

	void setDefaultInteractionState(InteractionState defaultState);

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
}
