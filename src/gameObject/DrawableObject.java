package gameObject;

import gameObject.interaction.IInteractionStates;
import gameWorld.GameWorld;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

abstract class DrawableObject extends BodyObject implements IAnimatedDrawable, IInteractionStates {

	// DRAW
	private boolean flip = false;
	private boolean visible = true;
	private int layer = 0;
	private float alpha = 1;
	private float scale = 1;
	private float rotation = 0;
	
	public DrawableObject(GameWorld gameWorld, Vector2 position) {
		super(gameWorld, position);
	}

	protected void draw(SpriteBatch batch, TextureRegion textureRegion) {
		if (!visible || textureRegion == null) 
			return;

		batch.setColor(1, 1, 1, alpha);
		batch.draw(textureRegion.getTexture(), getX(), getY(), textureRegion.getRegionWidth() / 2, textureRegion.getRegionHeight() / 2, /* origin */
				textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), scale, scale, rotation, textureRegion.getRegionX(),
				textureRegion.getRegionY(), textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), flip, false);
	}
	
	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public boolean isVisible() {
		return visible;
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

	@Override
	public void setLayer(int layer) {
		this.layer = layer;
	}

	@Override
	public float getAlpha() {
		return alpha;
	}

	@Override
	public void setAlpha(float alpha) {
		this.alpha = Math.abs(alpha);
	}

	@Override
	public float getScale() {
		return scale;
	}

	@Override
	public void setScale(float scale) {
		// BodyFunctions.scaleShape(primaryFixture.getShape(), getLocalCenterInWorld(), scale / this.scale, true);
		this.scale = scale;
	}

}
