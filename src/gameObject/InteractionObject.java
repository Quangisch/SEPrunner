package gameObject;

import gameWorld.GameWorld;
import misc.Debug;
import misc.Debug.Mode;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class InteractionObject extends DrawableObject implements IInteractable, IAnimatedDrawable {

	// ANIMATIONS
	private Animation[] animations;
	private int animationIndex;
	private float stateTime = 0;

	private InteractionState defaultState;
	private InteractionState currentState;

	public InteractionObject(GameWorld gameWorld, Vector2 position) {
		super(gameWorld, position);
		animations = new Animation[InteractionState.values().length];
		animationIndex = 0;
	}
	
	@Override
	public void draw(SpriteBatch batch, float deltaTime) {
		stateTime += deltaTime;
		draw(batch, animations[animationIndex].getKeyFrame(stateTime));
	}
	

	@Override
	public void setAnimation(int index, Animation animation, int playMode) {
		animation.setPlayMode(playMode);
		animations[index] = animation;
	}
	
	@Override
	public InteractionState getInteractionState() {
		return currentState;
	}
	
	@Override
	public InteractionState getDefaultInteractionState() {
		return defaultState;
	}
	
	@Override
	public boolean setInteractionState(InteractionState state) {
		return setInteractionState(state, false);
	}

	@Override
	public boolean setInteractionState(InteractionState state, boolean force) {
		if (this.currentState == state) return true;
		if (currentState != null)
			Debug.println("try to set " + state.toString() + " @current " + currentState.toString(), Mode.CONSOLE);

		if (force || isAnimationFinished()) 
			this.currentState = state;
		else 
			return false;
		return true;
	}

	@Override
	public boolean applyAnimation() {
		animationIndex = getInteractionState().getAnimationIndex();
		stateTime = 0;

		Debug.println(">>apply " + currentState.toString(), Mode.CONSOLE);
		setFixture(currentState);
		return true;
	}

	@Override
	public boolean isAnimationFinished() {
		if (currentState.isInterruptable() || currentState == null) return true;
		return animations[animationIndex].isAnimationFinished(stateTime);
	}
	

	protected void setDefaultInteractionState(InteractionState defaultState) {
		this.defaultState = defaultState;
		this.currentState = defaultState;
	}
	
	public void disposeUnsafe() {
		super.disposeUnsafe();
		animations = null;
		defaultState = currentState = null;
	}

}
