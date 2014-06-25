package core.ingame;

import gameWorld.GameWorld;

import java.awt.MouseInfo;
import java.awt.Point;

import misc.GeometricObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

import core.ingame.input.InputHandler;
import core.ingame.input.InputHandler.Click;

public class PauseMenu implements IDrawable {

	private GameWorld world;
	private SpriteBatch b;
	private BitmapFont font;

	public PauseMenu(GameWorld gameWorld) {
		this.world = gameWorld;
		b = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("res/font/baoli64.fnt"), //
				Gdx.files.internal("res/font/baoli64.png"), false);
		font.setScale(2f);
	}

	@Override
	public void draw(SpriteBatch batch, float delta) {
		batch.end();
		b.setProjectionMatrix(b.getProjectionMatrix().setToOrtho2D(0, 0, //
				Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		font.setScale((float) Gdx.graphics.getWidth() / 1280 * 2f);
		b.begin();

		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();

		final byte[] colordot = { 0x42, 0x4D, 0x3A, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x36, 0x00, 0x00, 0x00,
				0x28, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01, 0x00, 0x18, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00, -60, 0x0E, 0x00, 0x00, -60, 0x0E, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, -1, -1, -1, 0x00 };

		// TODO ??
		b.setColor(205, 25, 201, 0.8f);
		Pixmap pixmap = new Pixmap(colordot, 0, colordot.length);
		Texture texture = new Texture(pixmap);
		b.draw(texture, 0, height * 0.2f, width, height * 0.6f);
		pixmap.dispose();
		texture.dispose();
		b.setColor(Color.WHITE);

		InputHandler in = (InputHandler) Gdx.app.getInput().getInputProcessor();
		Click cl = in.popClick();
		if (cl != null) {
			Vector2 v = new Vector2(cl.screenX, height - cl.screenY);
			System.out.println("Click: (" + cl.screenX + "|" + cl.screenY + ")");
			new GeometricObject(new Circle(v.add(-5, -5), 5), Color.GREEN).draw(b);
		}

		Point m = MouseInfo.getPointerInfo().getLocation();
		System.out.println(m.x + "," + m.y);

		Vector2 v = new Vector2(Gdx.input.getX(), Gdx.input.getY()).scl(1, -1).add(0, height);
		new GeometricObject(new Circle(v.add(-5, -5), 5), Color.GREEN).draw(b);

		TextBounds tB = font.getBounds("Pause");
		font.draw(b, "Pause", (width - tB.width) / 2, /* height * 0.75f */(height + tB.height) / 2);

		b.end();
		batch.begin();
	}
}
