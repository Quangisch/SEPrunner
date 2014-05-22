package gameObject;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface DrawableAnimated extends DrawableStatic {

	/**
	 * Updates the texture of the animationFrame depending on given stateTime and draw current Frame.
	 * @param stateTime
	 */
	public void draw(float stateTime, SpriteBatch batch);
}
