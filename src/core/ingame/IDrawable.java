package core.ingame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface IDrawable {

	/**
	 * Draw with given SpriteBatch.
	 * @param batch
	 * @param deltaTime
	 */
	public void draw(SpriteBatch batch, float deltaTime);
}
