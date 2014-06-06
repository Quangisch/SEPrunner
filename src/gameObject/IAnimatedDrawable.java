package gameObject;

import gameObject.interaction.IInteractionStates.InteractionState;

import com.badlogic.gdx.graphics.g2d.Animation;

import core.ingame.IDrawable;

public interface IAnimatedDrawable extends IDrawable, IDrawableObject {

	public void setAnimation(int index, Animation animation, int playMode);
	
	public boolean applyAnimation(InteractionState currentState); 
	
}
