package gameWorld;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

// TODO Merge IAnimationDrawable, IDrawableMap
public interface IDrawableMap {

	public void draw(SpriteBatch batch, float deltaTime);
//	public void initMap(int level);
}
