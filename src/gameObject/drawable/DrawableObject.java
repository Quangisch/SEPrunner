package gameObject.drawable;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

abstract class DrawableObject implements IDrawableObject, Comparable<DrawableObject> {

	// DRAW
	private boolean flip = false;
	private boolean visible = true;
	private int layer = 0;
	private float alpha = 1;
	private float scale = 1;
	private float rotation = 0;
	private Vector2 position;
	private RayHandler rayHandler;
	
	public DrawableObject(RayHandler rayHandler, Vector2 position) {
		this.rayHandler = rayHandler;
		this.position = position;
		if(rayHandler != null)
			iniLight();
	}
	
	PointLight p;
	private void iniLight() {
		p = new PointLight(rayHandler, 32, Color.GREEN, 1, 0, 0);
	}
	
	public DrawableObject(Vector2 position) {
		this(null, position);
	}

	public void draw(SpriteBatch batch, TextureRegion textureRegion) {
		if (!visible || textureRegion == null) 
			return;

		
		batch.setColor(1, 1, 1, alpha);
		batch.draw(textureRegion.getTexture(), position.x, position.y, textureRegion.getRegionWidth() / 2, textureRegion.getRegionHeight() / 2, /* origin */
				textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), scale, scale, rotation, textureRegion.getRegionX(),
				textureRegion.getRegionY(), textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), flip, false);
		if(p != null)
			p.setPosition(position);
		
		batch.setColor(Color.WHITE);
	}
	
	@Override
	public void setPosition(Vector2 position) {
		this.position = position;
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
		this.alpha = Math.abs(alpha > 1 ? 1 : alpha < 0 ? 0 : alpha);
	}

	@Override
	public float getScale() {
		return scale;
	}
	
	@Override
	public int compareTo(DrawableObject other) {
		return this.getLayer() - other.getLayer();
	}

	@Override
	public void setScale(float scale) {
		// TODO
		// BodyFunctions.scaleShape(primaryFixture.getShape(), getLocalCenterInWorld(), scale / this.scale, true);
		this.scale = scale;
	}
}
