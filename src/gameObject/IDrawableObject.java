package gameObject;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

interface IDrawableObject {

	/**
	 * Draw textureRegion with given SpriteBatch.
	 * @param spriteBatch
	 * @param textureRegion
	 */
	public void draw(SpriteBatch batch, TextureRegion textureRegion);

	/**
	 * Set Visibility.
	 * @param visible
	 */
	void setVisible(boolean visible);

	/**
	 * Get Visibility.
	 * @return
	 */
	boolean isVisible();
	
	/**
	 * Flip Texture, toogles flipping based on current flip.
	 */
	void flip();

	/**
	 * Set flip for Texture.
	 * @param flip as int
	 */
	void setFlip(boolean flip);

	/**
	 * Check whether Texture is flipped.
	 * @return flip as boolean
	 */
	boolean isFlipped();

	/** 
	 * Get Layer. 
	 * @return layer as int
	 */
	int getLayer();

	/** 
	 * Set the layer in which the Texture will be drawn.
	 * Lower layer are painted in the background.
	 * @param layer as int
	*/
	void setLayer(int layer);

	/**
	 * Get current alpha value. 
	 * @return alpha as float
	 */
	float getAlpha();

	/**
	 * Set alpha value.
	 * @param alpha as float
	 */
	void setAlpha(float alpha);

	/**
	 * Get current scaling based on Texture size.
	 * @return scale factor as float
	 */
	float getScale();

	/**
	 * Set scaling based on Texture size.
	 * @param scale factor as float
	 */
	void setScale(float scale);
	
}
