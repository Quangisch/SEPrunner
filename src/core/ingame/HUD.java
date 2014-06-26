package core.ingame;

import gameObject.drawable.AnimationObject;
import gameObject.interaction.enemy.Alarm;
import gameObject.interaction.player.Shuriken;
import gameWorld.GameWorld;
import misc.StringFunctions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class HUD implements IDrawable {

	private GameWorld world;
	private SpriteBatch b;
	private BitmapFont font;
	private AnimationObject shuriken;
	private Vector2 goal;

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

		goal = world.getGoal() == null ? null : world.getGoal().getPosition();
		// TODO
		// goal = new Vector2(108.7f, 0);
	}

	@Override
	public void draw(SpriteBatch batch, float deltaTime) {
		batch.end();
		b.setProjectionMatrix(b.getProjectionMatrix().setToOrtho2D(0, 0, //
				Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

		// font.scale(0.00005f);

		font.setScale((float) Gdx.graphics.getWidth() / 1280 * 0.7f);

		String shurikenQuantity = String.valueOf(world.getPlayer().getShurikenQuantity());
		String time = StringFunctions.getTimeAsString(world.getTime());
		String distance = String.valueOf((int) (goal.x - world.getPlayer().getBodyObject().getWorldPosition().x)) + "m";
		b.begin();

		shuriken.setPosition(new Vector2(0, Gdx.graphics.getHeight() - 30));
		shuriken.draw(b, deltaTime * 0.2f);
		font.draw(b, shurikenQuantity, 30, Gdx.graphics.getHeight() - 5);
		font.draw(b, time, Gdx.graphics.getWidth() / 2 - font.getBounds(time).width / 2, Gdx.graphics.getHeight() - 5);
		if (goal != null) font.draw(b, distance, //
				Gdx.graphics.getWidth() - font.getBounds(distance).width, Gdx.graphics.getHeight() - 5);

		Alarm.getInstance().draw(batch, deltaTime);

		b.end();
		batch.begin();
	}
}
