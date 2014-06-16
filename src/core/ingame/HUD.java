package core.ingame;

import gameObject.drawable.AnimationObject;
import gameObject.interaction.player.Shuriken;
import gameWorld.GameWorld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class HUD implements IDrawable {

	private GameWorld world;
	private SpriteBatch b;
	BitmapFont font;
	AnimationObject shuriken;

	public HUD(GameWorld world) {
		this.world = world;
		b = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("res/font/baoli64.fnt"), //
				Gdx.files.internal("res/font/baoli64.png"), false);
		font.setScale(0.4f);
		Shuriken s = new Shuriken(world.getPlayer(), new Vector2());
		s.dispose();
		shuriken = s.getAnimationObject();
		shuriken.setPosition(new Vector2(0, Gdx.graphics.getHeight() - 30));
	}

	@Override
	public void draw(SpriteBatch batch, float deltaTime) {
		batch.end();
		b.setProjectionMatrix(b.getProjectionMatrix().setToOrtho2D(0, 0, //
				Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		b.begin();

		font.draw(b, String.valueOf(world.getPlayer().getShurikenQuantity()), 30, Gdx.graphics.getHeight() - 5);
		shuriken.draw(b, deltaTime*0.2f);

		b.end();
		batch.begin();
	}

}
