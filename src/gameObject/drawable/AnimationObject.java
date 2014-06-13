package gameObject.drawable;

import gameObject.interaction.InteractionState;

import java.util.HashMap;
import java.util.Map;

import misc.Debug;
import misc.Debug.Mode;
import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class AnimationObject extends DrawableObject implements IAnimatedDrawable {

	private Map<InteractionState, Animation> animationMap;
	private Animation animation;
	private float stateTime;

	public AnimationObject(RayHandler rayHandler, Vector2 position) {
		super(rayHandler, position);
		animationMap = new HashMap<InteractionState, Animation>();
		stateTime = 0;
	}
	
	public AnimationObject(Vector2 position) {
		this(null, position);
	}
	
	@Override
	public void draw(SpriteBatch batch, float deltaTime) {
		if(animation == null)
			return;
		
		stateTime += deltaTime;
		draw(batch, animation.getKeyFrame(stateTime));
	}
	

	@Override
	public void addAnimation(InteractionState state, Animation animation) {
		if(animation != null) {
			animation.setPlayMode(state.getPlayMode());
			animationMap.put(state, animation);
		} else
			System.err.println(this.getClass()+"@addAnimation(...)  Invalid Argument : animation == null");
	}
	
	@Override
	public boolean applyAnimation(InteractionState state) {
		if(animation == animationMap.get(state))
			return true;
		
		animation = animationMap.get(state);
		stateTime = 0;

		Debug.println(">>apply " + state.toString(), Mode.CONSOLE);
		
		return true;
	}

	@Override
	public boolean isAnimationFinished() {
		return animation == null || animation.isAnimationFinished(stateTime);
	}

}
