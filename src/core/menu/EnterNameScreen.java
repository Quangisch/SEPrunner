package core.menu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import core.GameProperties;
import core.PlayerProfile;

public class EnterNameScreen implements Screen {

	private final Screen PREVIOUS_SCREEN;

	private Stage stage;
	private Table mainTable;
	private Skin skin;
	private Texture texture;
	private Pixmap pixmap;
	private TextField nameField;
	
	public EnterNameScreen(Screen currentScreen) {
		PREVIOUS_SCREEN = currentScreen;
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Table.drawDebug(stage);
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void show() {
		stage = new Stage();
		stage.setViewport(GameProperties.SCALE_WIDTH*2, GameProperties.SCALE_HEIGHT*2, true);
		skin = new Skin(Gdx.files.internal("res/ui/menuSkin.json"),new TextureAtlas(Gdx.files.internal("res/ui/atlas.pack")));
		mainTable = new Table();
		mainTable.setFillParent(true);
		stage.addActor(mainTable);
		Gdx.input.setInputProcessor(stage);
		

		pixmap = new Pixmap(2, GameProperties.SCALE_HEIGHT*5, Format.RGB888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		texture = new Texture(pixmap);
		SpriteDrawable spriteDrawable = new SpriteDrawable(new Sprite(texture));
		
		TextFieldStyle style = new TextFieldStyle();
		style.fontColor = Color.YELLOW;
		style.focusedFontColor = Color.YELLOW;
		style.font = skin.getFont("baoli96");
		style.cursor = spriteDrawable;
		style.selection = spriteDrawable;

		nameField = new TextField("", skin);
		nameField.setStyle(style);
		nameField.setMaxLength(10);
		nameField.setText("NewPlayer");
		nameField.setCursorPosition(nameField.getText().length());
		nameField.setCursorPosition(9);
		stage.setKeyboardFocus(nameField);
		nameField.addListener(new ClickListener() {
			public boolean keyDown(InputEvent event, int keycode) {
				if(keycode == Keys.ENTER && event.getListenerActor().equals(nameField)) {
					
					PlayerProfile profile = new PlayerProfile();
					profile.name = nameField.getText();
					profile.saveProfile();
					((Game) Gdx.app.getApplicationListener()).setScreen(PREVIOUS_SCREEN);
					return true;
				}
				return false;
			}
		});

		mainTable.add(new Label("Enter your Name", skin, "baoli96", Color.WHITE)).row();
		mainTable.add(nameField).size(GameProperties.SCALE_WIDTH, GameProperties.SCALE_HEIGHT/4).row();
		
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		stage.dispose();
		skin.dispose();
		pixmap.dispose();
		texture.dispose();
	}

}
