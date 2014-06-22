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
	private BitmapFont font;
	private AnimationObject shuriken;
	private Vector2 goal;
	private static boolean alarm = false;

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
		goal = new Vector2(108.7f, 0);
	}

	@Override
	public void draw(SpriteBatch batch, float deltaTime) {
		batch.end();
		b.setProjectionMatrix(b.getProjectionMatrix().setToOrtho2D(0, 0, //
				Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		b.begin();

		shuriken.setPosition(new Vector2(0, Gdx.graphics.getHeight() - 30));
		shuriken.draw(b, deltaTime * 0.2f);
		font.draw(b, String.valueOf(world.getPlayer().getShurikenQuantity()), 30, Gdx.graphics.getHeight() - 5);

		if (goal != null)
			font.draw(b, String.valueOf((int) (goal.x - world.getPlayer().getBodyObject().getWorldPosition().x)), //
					Gdx.graphics.getWidth() - 50, Gdx.graphics.getHeight() - 5);
		
		if(alarm){
			font.draw(b, "ALARM!", //
					Gdx.graphics.getWidth() - 365, Gdx.graphics.getHeight() - 5);
			System.out.println("ALARM");
		}

		b.end();
		batch.begin();
	}
	
	public static void setAlarm(boolean a){//funktioniert nicht aus simpleAI heraus line 154
		alarm = a;
		System.out.println("setAlarm");
	}

}
