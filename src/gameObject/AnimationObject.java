package gameObject;

import gameObject.interaction.InteractionState;
import gameWorld.GameWorld;

import java.util.HashMap;
import java.util.Map;

import misc.Debug;
import misc.Debug.Mode;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class AnimationObject extends DrawableObject implements IAnimatedDrawable {

	private Map<InteractionState, Animation> animationMap;
	private Animation animation;
	private float stateTime;

	public AnimationObject(GameWorld gameWorld, Vector2 position) {
		super(gameWorld, position);
		
		animationMap = new HashMap<InteractionState, Animation>();
		stateTime = 0;
	}
	
	@Override
	public void draw(SpriteBatch batch, float deltaTime) {
		stateTime += deltaTime;
		draw(batch, animation.getKeyFrame(stateTime));
	}
	

	@Override
	public void addAnimation(InteractionState state, Animation animation) {
		if(animation != null)
			animationMap.put(state, animation);
		else
			System.err.println(this.getClass()+"@addAnimation(...)  Invalid Argument : animation == null");
	}
	
	@Override
	public boolean applyAnimation(InteractionState currentState) {
		animation = animationMap.get(currentState);
		stateTime = 0;

		Debug.println(">>apply " + currentState.toString(), Mode.CONSOLE);
		setFixture(currentState);
		return true;
	}

	protected boolean isAnimationFinished() {
		return animation.isAnimationFinished(stateTime);
	}
	
	@Override
	public void disposeUnsafe() {
		super.disposeUnsafe();
		animationMap.clear();
		animation = null;
	}
	

}
