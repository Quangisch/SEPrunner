package gameObject.interaction;


public interface IInteractable {

	boolean isRunning();

	boolean isThrowing();
	
	/**
	 * Check whether Player is hiding.
	 * @return hiding as boolean
	 */
	boolean isHiding();

	boolean isCrouching();

	boolean isHooking();

	boolean isJumping();

	boolean isGrabbing();
	
}
