package gameObject;

import gameObject.interaction.IInteractionStates.InteractionState;

import com.badlogic.gdx.graphics.g2d.Animation;

public interface IInteractionHandler {
	
	InteractionState getInteractionState();
	
	InteractionState getDefaultInteractionState();
	
	void setAnimation(int index, Animation animation, int playMode);

	boolean isAnimationFinished();
	
	boolean applyAnimation();
	
	boolean setInteractionState(InteractionState state);
	
	boolean setInteractionState(InteractionState state, boolean force);
	
}
