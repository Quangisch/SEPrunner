package core.ingame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import core.GameProperties;
import core.GameProperties.GameState;
import core.ingame.input.InputHandler;
import core.ingame.input.InputHandler.Click;

public class LoseMenu implements IDrawable {

	private SpriteBatch b;
	private BitmapFont font;

	public LoseMenu() {
		b = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("res/font/baoli64.fnt"), //
				Gdx.files.internal("res/font/baoli64.png"), false);
		font.setScale(2f);
	}

	@Override
	public void draw(final SpriteBatch batch, float delta) {
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

		//
		// Point m = MouseInfo.getPointerInfo().getLocation();
		// System.out.println(m.x + "," + m.y);

		Vector2 v = new Vector2(Gdx.input.getX(), Gdx.input.getY()).scl(1, -1).add(0, height);
		// new GeometricObject(new Circle(v.add(-5, -5), 5), Color.GREEN).draw(b);
		//

		InputHandler in = (InputHandler) Gdx.app.getInput().getInputProcessor();
		Click cl = in.popClick();

		if (cl != null) {
			v = new Vector2(cl.screenX, height - cl.screenY);
			// System.out.println("Click: (" + cl.screenX + "|" + cl.screenY + ")");
			// new GeometricObject(new Circle(v.add(-5, -5), 5), Color.GREEN).draw(b);
		}

		String title = "Sie wurden gefangen!";
		TextBounds tB = font.getBounds(title);
		font.draw(b, title, (width - tB.width) / 2, /* height * 0.75f */(height + tB.height) / 2);

		font.setScale(font.getScaleY() * 0.5f);

		//
		Button cont = new Button() {

			public String getText() {
				return "Nochmal";
			}

			public void onClick() {
				Gdx.app.postRunnable(new GameProperties.GameStateSwitcher(GameState.INGAME, GameProperties.currentLevel));
			}
		};
		tB = font.getBounds(cont.getText());
		if (new Rectangle((width - tB.width) * 0.2f, height * 0.25f, tB.width, tB.height).contains(v)) {
			font.setColor(cont.getHoverColor());
			if (cl != null) cont.onClick();
		} else
			font.setColor(cont.getTextColor());
		font.draw(b, cont.getText(), (width - tB.width) * 0.2f, height * 0.25f + tB.height);
		//
		Button back = new Button() {

			public String getText() {
				return "Menu";
			}

			public void onClick() {
				Gdx.app.postRunnable(new GameProperties.GameStateSwitcher(GameState.MENU, -1));
			}
		};
		tB = font.getBounds(back.getText());
		if (new Rectangle((width - tB.width) * 0.8f, height * 0.25f, tB.width, tB.height).contains(v)) {
			font.setColor(back.getHoverColor());
			if (cl != null) back.onClick();
		} else
			font.setColor(back.getTextColor());
		font.draw(b, back.getText(), (width - tB.width) * 0.8f, height * 0.25f + tB.height);
		//
		Button restart = new Button() {

			public String getText() {
				return "Level";
			}

			public void onClick() {
				// TODO Level Auswahl
				Gdx.app.postRunnable(new GameProperties.GameStateSwitcher(GameState.MENU, -1));
			}
		};
		tB = font.getBounds(restart.getText());
		if (new Rectangle((width - tB.width) * 0.5f, height * 0.25f, tB.width, tB.height).contains(v)) {
			font.setColor(restart.getHoverColor());
			if (cl != null) restart.onClick();
		} else
			font.setColor(restart.getTextColor());
		font.draw(b, restart.getText(), (width - tB.width) * 0.5f, height * 0.25f + tB.height);
		//
		font.setColor(Color.WHITE);

		b.end();
		batch.begin();
	}

	public abstract static class Button {

		public String getText() {
			return "Button";
		}

		public Color getTextColor() {
			return Color.WHITE;
		}

		public Color getHoverColor() {
			return Color.YELLOW;
		}

		public abstract void onClick();
	}
}
