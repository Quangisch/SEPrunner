package core.ingame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import core.GameProperties;
import core.GameProperties.GameScreen;
import core.ingame.input.player.InputHandler;
import core.ingame.input.player.InputHandler.Click;
import core.ingame.input.player.KeyMap.ActionKey;

public class LoseMenu implements IDrawable {

	private SpriteBatch b;
	private BitmapFont font;

	private int selection = 1;
	private int buttons = 3;
	private boolean acceptKey = false;

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
		b.setColor(201, 201, 201, 0.8f);
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
		Object cl = in.popClick();

		if (in.isKeyDown(ActionKey.LEFT)) {
			if (acceptKey) selection--;
			acceptKey = false;
		} else if (in.isKeyDown(ActionKey.RIGHT)) {
			if (acceptKey) selection++;
			acceptKey = false;
		} else
			acceptKey = true;

		selection = (selection + buttons) % buttons;

		if (cl != null) {
			v = new Vector2(((Click) cl).screenX, height - ((Click) cl).screenY);
			// System.out.println("Click: (" + cl.screenX + "|" + cl.screenY + ")");
			// new GeometricObject(new Circle(v.add(-5, -5), 5), Color.GREEN).draw(b);
		} else if (in.isKeyDown(Keys.ENTER)) cl = "";

		TextBounds tB = font.getBounds(GameProperties.loseMessage);
		font.draw(b, GameProperties.loseMessage, (width - tB.width) / 2, /* height * 0.75f */(height + tB.height) / 2);

		font.setScale(font.getScaleY() * 0.5f);

		//
		Button cont = new Button() {

			{
				id = 1;
			}

			public String getText() {
				return "Retry";
			}

			public void onClick() {
				Gdx.app.postRunnable(new GameProperties.GameScreenSwitcher(GameProperties.gameScreen));
			}
		};
		tB = font.getBounds(cont.getText());
		if (new Rectangle((width - tB.width) * 0.5f, height * 0.25f, tB.width, tB.height).contains(v)
				|| cont.id == selection % buttons) {
			font.setColor(cont.getHoverColor());
			selection = cont.id;
			if (cl != null) cont.onClick();
		} else
			font.setColor(cont.getTextColor());
		font.draw(b, cont.getText(), (width - tB.width) * 0.5f, height * 0.25f + tB.height);
		//
		Button back = new Button() {

			{
				id = 2;
			}

			public String getText() {
				return "Back To Menu";
			}

			public void onClick() {
				Gdx.app.postRunnable(new GameProperties.GameScreenSwitcher(GameScreen.MENU_MAIN));
			}
		};
		tB = font.getBounds(back.getText());
		if (new Rectangle((width - tB.width) * 0.95f, height * 0.25f, tB.width, tB.height).contains(v)
				|| back.id == selection % buttons) {
			font.setColor(back.getHoverColor());
			selection = back.id;
			if (cl != null) back.onClick();
		} else
			font.setColor(back.getTextColor());
		font.draw(b, back.getText(), (width - tB.width) * 0.95f, height * 0.25f + tB.height);
		//
		Button restart = new Button() {

			{
				id = 0;
			}

			public String getText() {
				return "Select Level";
			}

			public void onClick() {
				// TODO Level Auswahl
				Gdx.app.postRunnable(new GameProperties.GameScreenSwitcher(GameScreen.MENU_LEVELSELECT));
			}
		};
		tB = font.getBounds(restart.getText());
		if (new Rectangle((width - tB.width) * 0.05f, height * 0.25f, tB.width, tB.height).contains(v)
				|| restart.id == selection % buttons) {
			font.setColor(restart.getHoverColor());
			selection = restart.id;
			if (cl != null) restart.onClick();
		} else
			font.setColor(restart.getTextColor());
		font.draw(b, restart.getText(), (width - tB.width) * 0.05f, height * 0.25f + tB.height);

		font.setColor(Color.WHITE);

		b.end();
		batch.begin();
	}

	public abstract class Button {

		protected int id = -1;

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
