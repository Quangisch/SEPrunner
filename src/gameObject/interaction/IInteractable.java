package gameObject.interaction;

import com.badlogic.gdx.graphics.g2d.Animation;

public interface IInteractable {

	void setDefaultInteractionState(InteractionState defaultState);

	InteractionState getInteractionState();

	InteractionState getDefaultInteractionState();

	void addAnimation(InteractionState state, Animation animation);

	boolean isInteractionFinished();

	boolean applyInteraction();

	boolean tryToSetInteractionState(InteractionState state);

	boolean setInteractionState(InteractionState state, boolean force);

	// InteractionState Getter
	boolean isRunning();

	boolean isThrowing();

	boolean isHiding();

	boolean isCrouching();

	boolean isHooking();

	boolean isJumping();

	boolean isGrabbing();
}
