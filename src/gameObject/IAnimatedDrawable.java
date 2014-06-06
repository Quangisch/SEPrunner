package gameObject;

import gameObject.interaction.IInteractionStates.InteractionState;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

interface IAnimatedDrawable {

//	TODO split interface?
	public void draw(SpriteBatch batch, float deltaTime);

	void setVisible(boolean visible);

	boolean isVisible();
	
	void flip();

	void setFlip(boolean flip);

	boolean isFlipped();

	/** @return the drawing layer */
	int getLayer();

	/** @param layer set the drawing layer */
	void setLayer(int layer);

	/** @return the alpha */
	float getAlpha();

	void setAlpha(float alpha);

	float getScale();

	void setScale(float scale);
	
//	InteractionStates
	
	InteractionState getInteractionState();
	
	InteractionState getDefaultInteractionState();
	
	void setAnimation(int index, Animation animation, int playMode);

	boolean isAnimationFinished();
	
	boolean applyAnimation();
	
	boolean setInteractionState(InteractionState state);
	
	boolean setInteractionState(InteractionState state, boolean force);
}
