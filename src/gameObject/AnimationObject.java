package gameObject;

import gameObject.interaction.IInteractionStates;
import gameWorld.GameWorld;
import misc.Debug;
import misc.Debug.Mode;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

abstract class AnimationObject extends BodyObject implements IDrawable, IInteractionStates {

	// DRAW
	private boolean flip = false;
	private boolean visible = true;
	private int layer = 0;
	private float alpha = 1;
	private float scale = 1;
	private float rotation = 0;

	// ANIMATIONS
	private Animation[] animations;
	private int aniDraw;

	private InteractionState defaultState;
	private InteractionState currentState;

	public AnimationObject(GameWorld gameWorld, Vector2 position) {
		super(gameWorld, position);
		animations = new Animation[InteractionState.values().length];
		aniDraw = 0;
	}
	
	public boolean setInteractionState(InteractionState state) {
		return setInteractionState(state, false);
	}

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

	public boolean applyAnimation() {
		aniDraw = getInteractionState().getAnimationIndex();
		stateTime = 0;

		Debug.println(">>apply " + currentState.toString(), Mode.CONSOLE);
		setFixture(currentState);
		return true;
	}

	public boolean isAnimationFinished() {
		if (currentState.isInterruptable() || currentState == null) return true;
		return animations[aniDraw].isAnimationFinished(stateTime);
	}

	private float stateTime = 0;

	@Override
	public void draw(SpriteBatch batch, float deltaTime) {
		if (!visible) return;

		stateTime += deltaTime;

		TextureRegion frame = new TextureRegion(animations[aniDraw].getKeyFrame(stateTime));

		batch.setColor(1, 1, 1, alpha);
		batch.draw(frame.getTexture(), getX(), getY(), frame.getRegionWidth() / 2, frame.getRegionHeight() / 2, /* origin */
				frame.getRegionWidth(), frame.getRegionHeight(), scale, scale, rotation, frame.getRegionX(),
				frame.getRegionY(), frame.getRegionWidth(), frame.getRegionHeight(), flip, false);
	}
	
	protected void setAnimation(int index, Animation animation, int playMode) {
		animation.setPlayMode(playMode);
		animations[index] = animation;
	}

	@Override
	public void setFlip(boolean flip) {
		if (this.flip == flip) return;
		flip();
	}

	@Override
	public void flip() {
		flip = !flip;
	}

	@Override
	public boolean isFlipped() {
		return flip;
	}
	
	/** @return the drawing layer */
	public int getLayer() {
		return layer;
	}

	/** @param layer set the drawing layer */
	protected void setLayer(int layer) {
		this.layer = layer;
	}

	/** @return the alpha */
	protected float getAlpha() {
		return alpha;
	}

	/** @param alpha the alpha to set */
	protected void setAlpha(float alpha) {
		this.alpha = Math.abs(alpha);
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		// BodyFunctions.scaleShape(primaryFixture.getShape(), getLocalCenterInWorld(), scale / this.scale, true);
		this.scale = scale;
	}
	
	protected void setDefaultInteractionState(InteractionState defaultState) {
		this.defaultState = defaultState;
		this.currentState = defaultState;
	}
	
	protected InteractionState getInteractionState() {
		return currentState;
	}
	
	protected InteractionState getDefaultInteractionState() {
		return defaultState;
	}
	
	public void disposeUnsafe() {
		super.disposeUnsafe();
		animations = null;
		defaultState = currentState = null;
	}

}
