package gameWorld;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface DrawableMap {

	public void draw(SpriteBatch batch, float deltaTime);
	public void initMap(int level);
	public void resetMap();
}
