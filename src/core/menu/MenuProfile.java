package core.menu;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import misc.ShaderBatch;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import core.FilePath;
import core.GameProperties;

public class MenuProfile implements Screen {


	private Texture backgroundTexture = new Texture(Gdx.files.internal("res/img/main menu.png"));
	private Sprite backgroundSprite = new Sprite(backgroundTexture);
	private Stage stage;
	
	private Table mainTable, contentTable, shopTable, shopTableContent, shopTableBuy, profileTable, itemTable;
	private Skin skin;
	
	private TextButton backButton;
	private int width, height;
	final private Color NORMAL = new Color(1,1,0.90f,1);
	final private Color HOVER = new Color(1,1,0f,1);
	private ClickHandler clickHandler;
	private ShaderBatch shaderBatch;
	
	private Label label_quantityshuriken, label_quantityLevelUp, label_price;
	private Label label_name, label_rank, label_shuriken, label_stylePoints, label_hookLevel;
	private Label label_buy, label_newProfile, label_moreShu, label_lessShu, label_moreHook, label_lessHook;
	private Label label_warningCosts, label_warningProfile, label_warningProfile_accept, label_warningProfile_decline;
	
	private Label label_createProfile;
	private TextField nameField;
	
	private Pixmap pixmap;
	private Texture texture;
	
	private String name = "New User", rank = "Noob";
	private int shuriken, hookLevel, stylePoints, exp;
	private int buy_shu, buy_hook, buy_costs;
	private final int PRICE_SHU = 5, PRICE_HOOK = 100;
	private float warningCostsAlpha = 0f, warningProfileAlpha = 0f;
	
	@Override
	public void show() {
		width = GameProperties.SCALE_WIDTH;
		height = GameProperties.SCALE_HEIGHT;
		
		clickHandler = new ClickHandler();
		shaderBatch = new ShaderBatch(100);
		backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		stage = new Stage();
		stage.setViewport(GameProperties.SIZE_WIDTH, GameProperties.SIZE_HEIGHT, true);
		Gdx.input.setInputProcessor(stage);
		
		skin = new Skin(Gdx.files.internal("res/ui/menuSkin.json"),new TextureAtlas(Gdx.files.internal("res/ui/atlas.pack")));
		
		mainTable = new Table(skin);
		mainTable.setFillParent(true);
		mainTable.debug();
		
		backButton = new TextButton("Back", skin);
		backButton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				((Game) Gdx.app.getApplicationListener()).setScreen(new MenuMain());
			}
		});
		backButton.pad(10);
		backButton.setPosition(width/10, height/10);
		stage.addActor(backButton);
		
		stage.addActor(mainTable);
		stage.addListener(clickHandler);
		
		contentTable = new Table(skin);
		shopTable = new Table(skin);
		shopTableContent = new Table(skin);
		shopTableBuy = new Table(skin);
		profileTable = new Table(skin);
		itemTable = new Table(skin);
		
		contentTable.debug();
		shopTable.debug();
		shopTableContent.debug();
		shopTableBuy.debug();
		profileTable.debug();
		itemTable.debug();
		
		
//		HEAD
		mainTable.top().add(new Label("Profile", skin, "big")).pad(height/5, 0, height/5, 0).row();
		mainTable.add(contentTable);
		contentTable.add(shopTable).left().padRight(width/10);
		contentTable.add(profileTable).right();
		
		iniLabel();
		
		iniShop();
		iniProfile();
		iniWarningLabels();
		
//		TEXTFIELD
		label_createProfile = new Label("Enter your name", skin, "baoli96", Color.WHITE);
		label_createProfile.setPosition(width-label_createProfile.getWidth()/3, height*1.3f);
		
		pixmap = new Pixmap(2, height*5, Format.RGB888);
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
		nameField.setSize(width*0.8f, height/3);
		nameField.setPosition(width-width/5, height);
		nameField.setStyle(style);
		nameField.setMaxLength(10);
		nameField.setText("NewPlayer");
		nameField.setCursorPosition(9);
		nameField.addListener(clickHandler);
	
	}
	
	private void iniLabel() {
		
//		SHOP
		label_quantityLevelUp = getLabel44(Integer.toString(0));
		label_quantityshuriken = getLabel44(Integer.toString(0));
		label_price = getLabel44(Integer.toString(0));
		label_buy = getLabel44("Buy");
		label_newProfile = getLabel44("New Profile");
		
		label_moreShu = getLabel32(">");
		label_lessShu = getLabel32("<");
		label_moreHook  = getLabel32(">");
		label_lessHook = getLabel32("<");
		
//		PROFILE
		label_name = getLabel44(name);
		label_rank = getLabel44(rank);
		label_shuriken = getLabel44(Integer.toString(shuriken));
		label_stylePoints = getLabel44(Integer.toString(stylePoints));
		label_hookLevel = getLabel44(Integer.toString(hookLevel));
		
//		LABEL BUTTONS
		label_buy.addListener(clickHandler);
		label_newProfile.addListener(clickHandler);
		label_moreShu.addListener(clickHandler);
		label_lessShu.addListener(clickHandler);
		label_moreHook.addListener(clickHandler);
		label_lessHook.addListener(clickHandler);
		

		
//		LISTENER
		addListener(label_buy);
		addListener(label_newProfile);
		addListener(label_moreShu);
		addListener(label_lessShu);
		addListener(label_lessHook);
		addListener(label_moreHook);
		
	}
	
	
	
	private void iniWarningLabels() {
		label_warningCosts = new Label("Not enough StylePoints", skin, "baoli44", Color.RED);
		label_warningCosts.setPosition(width/2-label_warningCosts.getWidth()/2, 
				height/3-label_warningCosts.getHeight()/2);
		
		label_warningProfile = new Label("Delete current Profile?", skin, "baoli64", Color.RED);
		label_warningProfile.setPosition(width/2-label_warningProfile.getWidth()/2, height/2);
		
		label_warningProfile_accept = new Label("YES", skin, "baoli96", Color.WHITE);
		label_warningProfile_accept.setPosition(width+width/10+label_warningProfile_accept.getWidth()/2, 
				height-height/10);
		label_warningProfile_accept.setVisible(false);
		
		label_warningProfile_decline = new Label("NO", skin, "baoli96", Color.WHITE);
		label_warningProfile_decline.setPosition(width-width/10-label_warningProfile_decline.getWidth()/2
				, height-height/10);
		label_warningProfile_decline.setVisible(false);
		
		addListener(label_warningProfile_accept);
		addListener(label_warningProfile_decline);
		stage.addActor(label_warningProfile_accept);
		stage.addActor(label_warningProfile_decline);
	}
	
	private void addListener(Label label) {
		label.addListener(clickHandler);
		label.setTouchable(Touchable.enabled);
		clickHandler.hoverCandidateList.add(label);
	}
	
	
	private void iniShop() {
		shopTable.add(getLabel64("Shop")).row();
		shopTable.add(shopTableContent).row();
		shopTable.add(shopTableBuy).row();
		
		shopTableContent.add(getLabel44("Items"));
		shopTableContent.add(getLabel44("Price")).pad(0, width/20, 0, width/20);
		shopTableContent.add(getLabel44("Quantity")).colspan(3).row();
		
		shopTableContent.add(getLabel44("shuriken")).left();
		shopTableContent.add(getLabel44("5"));
		shopTableContent.add(label_lessShu);
		shopTableContent.add(label_quantityshuriken);
		shopTableContent.add(label_moreShu).row();
		
		shopTableContent.add(getLabel44("Hook LevelUp")).left();
		shopTableContent.add(getLabel44("50"));
		shopTableContent.add(label_lessHook);
		shopTableContent.add(label_quantityLevelUp);
		shopTableContent.add(label_moreHook).row();
		
		shopTableBuy.add().size(width/2, height/10);
		shopTableBuy.add().size(width/20, height/10);
		shopTableBuy.add().size(width/5, height/10).row();
		shopTableBuy.add(getLabel44("Price"));
		shopTableBuy.add(label_price);
		shopTableBuy.add(label_buy);
	}
	
	private void iniProfile() {
		loadProfile();
		
		profileTable.add(getLabel44("Name")).left();
		profileTable.add(label_name).size(width/10, height/10).row();
		profileTable.add(getLabel44("Rank")).left();
		profileTable.add(label_rank).size(width/10, height/10).row();
		
		profileTable.add().size(0, height/10);
		profileTable.add().row();
		
		profileTable.add(getLabel44("Items")).left().row();
		profileTable.add(itemTable).colspan(2).row();
		
		itemTable.add(getLabel44("shuriken")).left().padRight(width/20);
		itemTable.add(label_shuriken).row();
		itemTable.add(getLabel44("Hook Level")).left().padRight(width/20);
		itemTable.add(label_hookLevel).row();
		itemTable.add(getLabel44("StylePoints")).left().padRight(width/20);
		itemTable.add(label_stylePoints).row();
		
		profileTable.add();
		profileTable.add(label_newProfile).padBottom(height/5);
	}
	
	private Label getLabel32(String text) {
		return new Label(text, skin, "baoli32", Color.WHITE);
	}
	
	private Label getLabel44(String text) {
		return new Label(text, skin, "baoli44", Color.WHITE);
	}
	
	private Label getLabel64(String text) {
		return new Label(text, skin, "baoli64", Color.WHITE);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1); 		//black screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		shaderBatch.brightness = GameProperties.brightness;
		shaderBatch.contrast = GameProperties.contrast;
		
		shaderBatch.begin();
		backgroundSprite.draw(shaderBatch);
		shaderBatch.end();
		
//		Table.drawDebug(stage);
		stage.act(delta);
		stage.draw();
		
		shaderBatch.begin();
		if(warningCostsAlpha > 0) {
			label_warningCosts.draw(shaderBatch, warningCostsAlpha);
			warningCostsAlpha = Math.max(0, warningCostsAlpha-0.01f);
		}
		
		if(warningProfileAlpha == 1)
			label_warningProfile.draw(shaderBatch, warningProfileAlpha);
		
		shaderBatch.end();
		
	}

	@Override
	public void resize(int width, int height) {
		
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
		saveProfile();
		stage.dispose();
		skin.dispose();
		shaderBatch.dispose();
		backgroundTexture.dispose();
		pixmap.dispose();
		texture.dispose();
	}
	
	private void updateShopLabels() {
		label_quantityshuriken.setText(Integer.toString(buy_shu));
		label_quantityLevelUp.setText(Integer.toString(buy_hook));
		label_price.setText(Integer.toString(buy_costs));	
	}
	
	private void updateItems() {
		stylePoints -= buy_costs;
		shuriken += buy_shu;
		hookLevel += buy_hook;
		buy_costs = buy_shu = buy_hook = 0;
		saveProfile();
		loadProfile();
		updateShopLabels();
	}

	private void loadProfile() {
		JsonValue root = null;
		try {
			root = new JsonReader().parse(new FileReader(FilePath.profile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if(root != null) {
			name = root.getString("name");
			exp = root.getInt("experience");
			rank = GameProperties.Rank.getRank(exp).toString();
			shuriken = root.getInt("shuriken");
			hookLevel = root.getInt("hookRadius")/100;
			stylePoints = root.getInt("stylePoints");

		}
		
		label_name.setText(name);
		label_rank.setText(rank);
		label_shuriken.setText(Integer.toString(shuriken));
		label_stylePoints.setText(Integer.toString(stylePoints));
		label_hookLevel.setText(Integer.toString(hookLevel));
	}
	
	private void saveProfile() {
		JsonValue root = null;
		try {
			root = new JsonReader().parse(new FileReader(FilePath.profile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		root.get("name").set(name);
		root.get("experience").set(exp);
		root.get("shuriken").set(shuriken);
		root.get("hookRadius").set(hookLevel*100);
		root.get("stylePoints").set(stylePoints);
		
		FileHandle file = Gdx.files.local(FilePath.profile);
		file.writeString(root.toString(), false);
	}
	
	private void resetProfile() {
		name = "New User";
		rank = "Noob";
		shuriken = stylePoints = exp = 0;
		hookLevel = 1;
	}
	
	private class ClickHandler extends ClickListener {
		private List<Actor> hoverList = new CopyOnWriteArrayList<Actor>();
		private List<Actor> hoverCandidateList = new LinkedList<Actor>();
		
		private void resetLabel(Actor label) {
			if(label != null) {
				label.setColor(NORMAL);
				hoverList.remove(label);
			}
			
		}
		
		public boolean mouseMoved(InputEvent event, float x, float y) {
			for(Actor h : hoverList) {
				if(h.getColor().equals(HOVER))
					resetLabel(h);
			}
			
			Actor a = stage.hit(x, y, true);
			if(a != null && hoverCandidateList.contains(a)) {
				hoverList.add(a);
				a.setColor(HOVER);
			} 
		
			return false;
		}
		
		public void clicked(InputEvent event, float x, float y){
			if(warningProfileAlpha == 1f) {
				if(event.getListenerActor().equals(label_warningProfile_accept)) {
					backButton.remove();
					mainTable.remove();
					stage.addActor(label_createProfile);
					stage.addActor(nameField);
					stage.setKeyboardFocus(nameField);
					warningProfileAlpha = 0.5f;
				} else if(event.getListenerActor().equals(label_warningProfile_decline)) {
					warningProfileAlpha = 0f;
					stage.addActor(mainTable);
				}

				label_warningProfile_accept.setVisible(warningProfileAlpha == 1);
				label_warningProfile_decline.setVisible(warningProfileAlpha == 1);
			} else if(event.getListenerActor().equals(label_newProfile)) {
				warningProfileAlpha = 1f;
				mainTable.remove();
				label_warningProfile_accept.setVisible(true);
				label_warningProfile_decline.setVisible(true);
			} else if(event.getListenerActor().equals(label_buy)) {
				updateItems();
			} else if(event.getListenerActor().equals(label_lessHook)) {
				buy_hook = Math.max(0, buy_hook - 1);
				updateShopLabels();
			} else if(event.getListenerActor().equals(label_lessShu)) {
				buy_shu = Math.max(0, buy_shu - 1);
				updateShopLabels();
			} else if(event.getListenerActor().equals(label_moreHook) && (hookLevel + buy_hook <= 5)) {
				int costs = buy_costs + PRICE_HOOK;
				if(costs <= stylePoints) {
					buy_costs = costs;
					buy_hook++;
				} else
					warningCostsAlpha = 1f;
				updateShopLabels();
			} else if(event.getListenerActor().equals(label_moreShu)) {
				int costs = buy_costs + PRICE_SHU;
				if(costs <= stylePoints) {
					buy_costs = costs;
					buy_shu++;
				} else
					warningCostsAlpha = 1f;
				updateShopLabels();
			}
		}
		
		public boolean keyDown(InputEvent event, int keycode) {
			if(keycode == Keys.ENTER && event.getListenerActor().equals(nameField)) {
				resetProfile();
				name = nameField.getText();
				if(name.compareTo("Cheater") == 0) {
					shuriken = 999;
					hookLevel = 5;
					stylePoints = 999;
				}
				saveProfile();
				loadProfile();
				
				label_createProfile.remove();
				nameField.setText("NewPlayer");
				nameField.setCursorPosition(9);
				nameField.remove();
				warningProfileAlpha = 0f;
				
				stage.addActor(backButton);
				stage.addActor(mainTable);
				return true;
			}
			return false;
		}
		
	}

}
