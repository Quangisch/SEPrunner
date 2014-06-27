package core.menu;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import misc.ShaderBatch;
import sk.maniacs.KeyCodeMap;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import core.FilePath;
import core.GameProperties;
import core.Project;
import core.ResourceManager;
import core.ingame.input.KeyMap;
import core.ingame.input.KeyMap.ActionKey;

public class MenuOption implements Screen {
	
	private Stage stage;
	
	private Table mainTable, leftMainTable, rightMainTable,
					graphicTable, checkboxTable, soundTable, inputTable, displayModeTable, displaySelectTable;
	private Skin skin;
	
	private CheckBox vSyncCheckBox, fullScreenCheckBox;
	private Slider brightnessSlider, contrastSlider, musicSlider, soundSlider;
	private TextButton backButton;
	private ClickHandler clickHandler;
	
	private KeyMap keyMap;
	private KeyLabel kLeft, kRight, kRun, kJump, kCrouch, kAction, remapLabel;
	private List<KeyLabel> keyLabelList;

	private Label displayModeLabel, selectedModeLabel, selectModeNow;
	private ScrollPane scrollPane;
	private com.badlogic.gdx.scenes.scene2d.ui.List displayModeList;
	
	final private Color HOVER = new Color(1,1,0.6f,1);
	final private Color SELECT = new Color(1,0.8f,0,1);
	
	private Actor warning;
	
//	TMP
	private Texture backgroundTexture;
	private Sprite backgroundSprite;
	private ShaderBatch shaderBatch;
	
	private boolean debug = false;
	private int width, height;

	@Override
	public void show() {
		shaderBatch = new ShaderBatch(100);
		backgroundTexture = new Texture(Gdx.files.internal(FilePath.graphic_menuMain));
		backgroundSprite = new Sprite(backgroundTexture);
		backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		width = GameProperties.SCALE_WIDTH;
		height = GameProperties.SCALE_HEIGHT;
		
		skin = new Skin(Gdx.files.internal("res/ui/menuSkin.json"), 
				new TextureAtlas(Gdx.files.internal("res/ui/atlas.pack")));
		
		stage = new Stage();
		clickHandler = new ClickHandler();
		Gdx.input.setInputProcessor(stage);
		stage.addListener(clickHandler);
		stage.setViewport(GameProperties.SCALE_WIDTH*2, GameProperties.SCALE_HEIGHT*2, true);
		
		keyMap = new KeyMap();
		keyLabelList = new LinkedList<KeyLabel>();
		keyMap.initFromFile();
		
		displayModeLabel = new Label("DisplayMode", skin, "baoli44", Color.WHITE);
		selectedModeLabel = new Label(GameProperties.prefDisplayMode.width+"x"+
									GameProperties.prefDisplayMode.height, skin, "baoli32", Color.WHITE);
		selectModeNow = new Label("Select", skin, "baoli32", Color.WHITE);
		
		displayModeLabel.setTouchable(Touchable.enabled);
		selectModeNow.setTouchable(Touchable.enabled);
	
		displayModeLabel.addListener(clickHandler);
		selectModeNow.addListener(clickHandler);


		iniKeyLabels();

		mainTable = new Table(skin);
		leftMainTable = new Table(skin);
		rightMainTable = new Table(skin);
		graphicTable = new Table(skin);
		checkboxTable = new Table(skin);
		soundTable = new Table(skin);
		inputTable = new Table(skin);
		displayModeTable = new Table(skin);
		displaySelectTable = new Table(skin);
		displaySelectTable.setFillParent(true);
		
		stage.addActor(mainTable);
		
		
//		NESTED TABLES
		mainTable.setFillParent(true);
		mainTable.top();
		mainTable.add().space(height/10, width, height/5, width).row();
		mainTable.add(new Label("Options", skin, "big")).center().colspan(2).padBottom(height/10).row();
		mainTable.add(leftMainTable);
		mainTable.add(rightMainTable);
		
		leftMainTable.add(graphicTable).pad(0, width/20, 0, width/20);
		leftMainTable.add(checkboxTable).padRight(width/10).row();
		leftMainTable.add(displayModeTable);
		leftMainTable.add(soundTable).row();
		rightMainTable.add(inputTable);
		
		graphicTable.left();
		soundTable.right().bottom();
		
//		DEBUG
		mainTable.debug();
		leftMainTable.debug();
		rightMainTable.debug();
		graphicTable.debug();
		soundTable.debug();
		inputTable.debug();
		checkboxTable.debug();
		displayModeTable.debug();
		
		backButton = new TextButton("back", skin);
		backButton.setPosition(width/10, height/10);
		stage.addActor(backButton);
		backButton.addListener(clickHandler);
		
		displayModeTable.add(displayModeLabel).row();
		displayModeTable.add(selectedModeLabel);
		
		iniGraphicSection();
		iniAudioSection();
		iniKeyMapSection();
		iniDisplaySelectTable();
	
	}
	
	private void updateDisplaySelect() {
		String[] modes = new String[LwjglApplicationConfiguration.getDisplayModes().length];
		for(int i = 0; i < modes.length; i++)
			modes[i] = LwjglApplicationConfiguration.getDisplayModes()[i].width+"x"+LwjglApplicationConfiguration.getDisplayModes()[i].height;
		
		displayModeList = new com.badlogic.gdx.scenes.scene2d.ui.List(modes, skin);

		if(scrollPane != null)
			scrollPane.setWidget(displayModeList);
	}
	
	private void updateDisplayModeSection() {
		selectedModeLabel.setText(GameProperties.prefDisplayMode.width+"x"+
				GameProperties.prefDisplayMode.height);
		GameProperties.refreshDisplayMode();
		stage.setViewport(GameProperties.SCALE_WIDTH*2, GameProperties.SCALE_HEIGHT*2);
	}
	
	private void iniDisplaySelectTable() {
		updateDisplaySelect();
		updateDisplayModeSection();
		
		scrollPane = new ScrollPane(displayModeList, skin);
		displaySelectTable.add(new Label("Display Mode", skin, "baoli96", Color.WHITE)).padBottom(height/5).colspan(2).row();
		displaySelectTable.add(scrollPane).size(width/3,height*0.6f).padBottom(height/10).row();
		displaySelectTable.add(selectModeNow);
	}
	
	private void iniGraphicSection() {
//		ACTORS
		brightnessSlider = new Slider(-0.5f, 0.5f, 0.01f, false, skin);
		brightnessSlider.setValue(GameProperties.brightness);
		contrastSlider = new Slider(0.5f, 1.5f, 0.01f, false, skin);
		contrastSlider.setValue(GameProperties.contrast);
		
		vSyncCheckBox = new CheckBox("", skin);
		vSyncCheckBox.setChecked(Gdx.app.getPreferences(Project.TITLE).getBoolean("vsync"));
		fullScreenCheckBox = new CheckBox("", skin);
		fullScreenCheckBox.setChecked(Gdx.graphics.isFullscreen());
		
//		LAYOUT
		graphicTable.add(getLabel64("Graphics")).colspan(2).row();
		graphicTable.add("Brightness").right().padRight(width/30);
		graphicTable.add(brightnessSlider).fillX().size(width*0.4f, height/10).row();
		graphicTable.add("Contrast").right().padRight(width/30);
		graphicTable.add(contrastSlider).fillX().row();
		graphicTable.add().pad(height/20, 0, 0, 0).row();
		
		checkboxTable.padTop(height/10).padLeft(width/20).add("vSync").right();
		checkboxTable.add(vSyncCheckBox).row();
		checkboxTable.add("Fullscreen").right();
		checkboxTable.add(fullScreenCheckBox);
		
//		HANDLER
		brightnessSlider.addListener(clickHandler);
		contrastSlider.addListener(clickHandler);
		vSyncCheckBox.addListener(clickHandler);
		fullScreenCheckBox.addListener(clickHandler);
	}
	
	private void iniAudioSection() {
//		ACTORS
		musicSlider = new Slider(0, 1f, 0.01f, false, skin);
		musicSlider.setValue(GameProperties.musicVolume);
		soundSlider = new Slider(0, 1, 0.01f, false, skin);
		soundSlider.setValue(GameProperties.soundVolume);
		
//		LAYOUT
		soundTable.add(getLabel64("Audio")).colspan(2).row();
		soundTable.add("Music").right().padRight(width/30);
		soundTable.add(musicSlider).fillX().size(width*0.4f, height/10).row();
		soundTable.add("Sound").right().padRight(width/30);
		soundTable.add(soundSlider).fillX();
		
//		SLIDER
		musicSlider.addListener(clickHandler);
		soundSlider.addListener(clickHandler);
	}
	
	private void iniKeyMapSection() {
		inputTable.clear();
		Table iTaction = new Table(skin);
		Table iTkey = new Table(skin);
		inputTable.add(iTaction).padBottom(height/5);
		inputTable.add(iTkey).padBottom(height/5);
		
		iTaction.add(getLabel64("Action")).row();	
		
		iTaction.add("Left").row();			
		iTaction.add("Right").row();	
		iTaction.add("Run").row();
		iTaction.add("Jump").row();			
		iTaction.add("Crouch").row();	
		iTaction.add("Action").row();	
		iTaction.add("Throw").row();	
		iTaction.add("Hook");			
		
		iTkey.setSize(width, 0);
		iTkey.add(getLabel64("Key")).row();
		iTkey.add(kLeft).row();
		iTkey.add(kRight).row();
		iTkey.add(kRun).row();
		iTkey.add(kJump).row();
		iTkey.add(kCrouch).row();
		iTkey.add(kAction).row();
		
		iTkey.add("L-Mouse").row();
		iTkey.add("R-Mouse").row();	
		
	}
	
	
	private void iniKeyLabels() {
		kLeft = getKeyLabel(ActionKey.LEFT);
		kRight = getKeyLabel(ActionKey.RIGHT);
		kRun = getKeyLabel(ActionKey.RUN);
		kJump = getKeyLabel(ActionKey.JUMP);
		kCrouch = getKeyLabel(ActionKey.CROUCH);
		kAction = getKeyLabel(ActionKey.ACTION);
		
		keyLabelList.clear();
		keyLabelList.add(kLeft);
		keyLabelList.add(kRight);
		keyLabelList.add(kRun);
		keyLabelList.add(kJump);
		keyLabelList.add(kCrouch);
		keyLabelList.add(kAction);
		
		
		for(KeyLabel l : keyLabelList)
			l.addListener(clickHandler);
	}
	
	private KeyLabel getKeyLabel(ActionKey action) {
		return new KeyLabel(action, keyMap.getSingle(action), skin, "baoli44", Color.WHITE);
	}
	
	private Label getLabel64(String text) {
		return new Label(text, skin, "baoli64", Color.WHITE);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1); 		//black screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		shaderBatch.brightness = GameProperties.brightness; // 0.0 -> no change
		shaderBatch.contrast = GameProperties.contrast; // 1.0 -> no change
		shaderBatch.begin();
		backgroundSprite.draw(shaderBatch);
		shaderBatch.end();	
		
		stage.act(delta);
		stage.draw();
		if(debug)
			Table.drawDebug(stage);
		
		
		if(warning != null) {
			if(warning.getColor().a >= 0.01f)
				warning.setColor(warning.getColor().sub(0, 0, 0, 0.01f));
			else
				warning.remove();
		}
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void hide() {
		GameProperties.brightness = brightnessSlider.getValue();
		GameProperties.contrast = contrastSlider.getValue();
		GameProperties.musicVolume = musicSlider.getValue();
		GameProperties.soundVolume = soundSlider.getValue();
		
		keyMap.saveToFile();
		GameProperties.saveToFile();
		dispose();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {
		show();
	}

	@Override
	public void dispose() {
		backgroundTexture.dispose();
		stage.dispose();
		skin.dispose();
	}
	
	private class ClickHandler extends ClickListener {
		private List<Actor> hoverList = new CopyOnWriteArrayList<Actor>();
		
		private void resetLabel(Actor label) {
			if(label != null) {
				label.setColor(Color.WHITE);
				hoverList.remove(label);
			}
			if(remapLabel != null && label.equals(remapLabel))
				remapLabel = null;
			label = null;
			
		}
		
		public boolean mouseMoved(InputEvent event, float x, float y) {
			for(Actor h : hoverList) {
				if(h.getColor().equals(HOVER))
					resetLabel(h);
			}
			
			Actor a = stage.hit(x, y, true);
			if(a != null && ((remapLabel == null && a instanceof KeyLabel && a.getColor().equals(Color.WHITE))
					|| a == displayModeLabel || a == selectModeNow)) {
				hoverList.add(a);
				a.setColor(HOVER);
			} 
		
			return false;
		}
		
		public boolean keyDown(InputEvent event, int keycode) {
			if(keycode == Keys.TAB) {
				debug = !debug;
				return true;
			}
			
			if(keycode == Keys.ESCAPE || keycode == Keys.SYM) {
				resetLabel(remapLabel);
				return true;
			}
			
//			REMAP KEY
			if(remapLabel != null) {
				keyMap.remove(remapLabel.keycode);
				keyMap.add(remapLabel.action, keycode);
				remapLabel.keycode = keycode;
				remapLabel.setText(KeyCodeMap.valueOf(keycode).getHumanName());
				remapLabel.setColor(Color.WHITE);
				remapLabel = null;

				iniKeyLabels();
				iniKeyMapSection();
				
				return true;
			}
			return false;
		}
		
		@Override
		public void clicked(InputEvent event, float x, float y) {
			
			if(remapLabel != null) {
				resetLabel(remapLabel);
				return;
			
//			VSYNC
			} else if(event.getListenerActor() == vSyncCheckBox) {
				Gdx.app.getPreferences(Project.TITLE).putBoolean("vsync", vSyncCheckBox.isChecked());
				Gdx.graphics.setVSync(Gdx.app.getPreferences(Project.TITLE).getBoolean("vsync"));
				Gdx.app.log(Project.TITLE, "vSync " + (Gdx.app.getPreferences(Project.TITLE).getBoolean("vsync") ? "enabled" : "disabled"));
				
//			FULLSCREEN
			} else if(event.getListenerActor() == fullScreenCheckBox) {
				GameProperties.toogleFullScreen();
				stage.setViewport(GameProperties.SCALE_WIDTH*2, GameProperties.SCALE_HEIGHT*2);
				mainTable.invalidateHierarchy();
				
//			SLIDER
			} else if(event.getListenerActor() == brightnessSlider){
				GameProperties.brightness = brightnessSlider.getValue();
			} else if (event.getListenerActor() == contrastSlider){
				GameProperties.contrast = contrastSlider.getValue();
			} else if(event.getListenerActor() == musicSlider){
				GameProperties.musicVolume = musicSlider.getValue();
				ResourceManager.getInstance().adjustMusicVolume();
			} else if(event.getListenerActor() == soundSlider){
				GameProperties.soundVolume = soundSlider.getValue();
				ResourceManager.getInstance().adjustSoundVolume();
				
//			BACK
			} else if(event.getListenerActor() == backButton) {
				if(!keyMap.hasUnmappedActions())
					((Game) Gdx.app.getApplicationListener()).setScreen(new MenuMain());
				else {
					if(warning != null)
						warning.remove();
					warning = new Label("Unresolved\nAction Keys", skin, "baoli96", Color.RED);
					warning.setPosition(width-warning.getWidth()/2, height/2+warning.getHeight()/2);
					warning.toFront();
					stage.addActor(warning);
				}
				

				
//			DISPLAYMODE
			} else if(event.getListenerActor() == displayModeLabel) {
				updateDisplaySelect();
				mainTable.remove();
				stage.addActor(displaySelectTable);
				
				
			} else if(event.getListenerActor() == selectModeNow) {
				GameProperties.prefDisplayMode = LwjglApplicationConfiguration.getDisplayModes()[displayModeList.getSelectedIndex()];
				updateDisplayModeSection();
				displaySelectTable.remove();
				stage.addActor(mainTable);
				
//			KEYLABELS
			} else if(remapLabel == null) {
				for(KeyLabel l : keyLabelList)
					if(l == event.getListenerActor()) {
						remapLabel = l;
						remapLabel.setColor(SELECT);
					}	
			}
	
		}
	}
	
	private class KeyLabel extends Label {
		private final ActionKey action;
		private int keycode;
		public KeyLabel(ActionKey action, Integer keycode, Skin skin, String fontName,
				Color color) {
			super(keycode == null ? "undefined" : KeyCodeMap.valueOf(keycode).getHumanName(), skin, fontName, color);
			this.action = action;
			this.keycode = keycode == null ? -1 : keycode.intValue();
		}
		
		public boolean equals(Object object) {
			if(object == null || !(object instanceof KeyLabel))
				return false;
			KeyLabel other = (KeyLabel) object;
			return other.action.equals(action);
		}
		
	}
	
}
