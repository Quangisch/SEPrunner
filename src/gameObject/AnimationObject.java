package gameObject;

import gameWorld.GameWorld;
import misc.Debug;
import misc.Debug.Mode;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class AnimationObject extends DrawableObject implements IAnimatedDrawable {

	private Animation[] animations;
	private int animationIndex;
	private float stateTime = 0;

	public AnimationObject(GameWorld gameWorld, Vector2 position) {
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
	public boolean applyAnimation(InteractionState currentState) {
		animationIndex = currentState.getAnimationIndex();
		stateTime = 0;

		Debug.println(">>apply " + currentState.toString(), Mode.CONSOLE);
		setFixture(currentState);
		return true;
	}

	protected boolean isAnimationFinished() {
		return animations[animationIndex].isAnimationFinished(stateTime);
	}
	
	@Override
	public void disposeUnsafe() {
		super.disposeUnsafe();
		animations = null;
	}
	

}
