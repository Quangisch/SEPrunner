package gameObject;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface IDrawable {

	public void draw(SpriteBatch batch, float deltaTime);

	public void flip();

	public void setFlip(boolean flip);

	public boolean isFlipped();

	public void setVisible(boolean visible);

	public boolean isVisible();
	// setFlashing
}
