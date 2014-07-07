package gameObject.drawable;

import gameObject.interaction.InteractionState;

import com.badlogic.gdx.graphics.g2d.Animation;

import core.ingame.IDrawable;

public interface IAnimatedDrawable extends IDrawable {

	void addAnimation(InteractionState state, Animation animation);
	
	boolean applyAnimation(InteractionState currentState); 
	
	boolean isAnimationFinished();
	
}
