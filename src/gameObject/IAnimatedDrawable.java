package gameObject;

import gameObject.interaction.InteractionState;

import com.badlogic.gdx.graphics.g2d.Animation;

import core.ingame.IDrawable;

public interface IAnimatedDrawable extends IDrawable, IDrawableObject {

	public void addAnimation(InteractionState state, Animation animation);
	
	public boolean applyAnimation(InteractionState currentState); 
	
}
