package gameObject.interaction;

import gameObject.interaction.IInteractionStates.InteractionState;

import com.badlogic.gdx.graphics.g2d.Animation;

public interface IInteractable {
	
	void setDefaultInteractionState(InteractionState defaultState);
	
	InteractionState getInteractionState();
	
	InteractionState getDefaultInteractionState();
	
	void setAnimation(int index, Animation animation, int playMode);

	boolean isInteractionFinished();
	
	boolean applyInteraction();
	
	boolean tryToSetInteractionState(InteractionState state);
	
	boolean setInteractionState(InteractionState state, boolean force);
	
	
//	InteractionState Getter
	boolean isRunning();

	boolean isThrowing();

	boolean isHiding();

	boolean isCrouching();

	boolean isHooking();

	boolean isJumping();

	boolean isGrabbing();
}
