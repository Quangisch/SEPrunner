package core.menu;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import misc.ShaderBatch;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import core.GameProperties;
import core.PlayerProfile;

public class MenuProfile implements Screen {


	private Texture backgroundTexture = new Texture(Gdx.files.internal("res/img/main menu.png"));
	private Sprite backgroundSprite = new Sprite(backgroundTexture);
	private Stage stage;
	
	private Table mainTable, contentTable, shopTable, shopTableContent, shopTableBuy, profileTable, itemTable, 
					newProfileTable, delProfileTable, warningBuyTable, warningMaxProfileTable, chooseProfileTable;
	private Skin skin;
	
	private TextButton backButton;
	private int width, height;
	final private Color NORMAL = new Color(1,1,0.90f,1);
	final private Color HOVER = new Color(1,1,0f,1);
	private ClickHandler clickHandler;
	private ShaderBatch shaderBatch;
	
	private Label label_quantityshuriken, label_quantityHook, label_price;
	private Label label_name, label_rank, label_shuriken, label_stylePoints, label_hookRadius;
	private Label label_buy, label_newProfile, label_moreShu, label_lessShu, label_moreHook, label_lessHook;
	private Label label_warningCosts, label_warningMaxProfile, label_delProfile_accept, label_delProfile_decline;
	
	private com.badlogic.gdx.scenes.scene2d.ui.List profileList;
	private ScrollPane scrollPane;
	private Label label_createProfile, label_delProfile, label_chooseProfile, label_selectProfile;
	private TextField nameField;
	private Pixmap pixmap;
	private Texture texture;
	
	private PlayerProfile profile;

//	SHOP
	private final int PRICE_SHU = 5, PRICE_HOOK = 1;
	private float warningCostsAlpha = 0f, warningProfileAlpha = 0f;
	private float buy_shu, buy_hook, buy_costs, incShu, incHook;
	private final float INC_SPEED = 0.1f;
	
	@Override
	public void show() {
		width = GameProperties.SCALE_WIDTH;
		height = GameProperties.SCALE_HEIGHT;
		
		clickHandler = new ClickHandler();
		shaderBatch = new ShaderBatch(100);
		stage = new Stage();
		skin = new Skin(Gdx.files.internal("res/ui/menuSkin.json"),new TextureAtlas(Gdx.files.internal("res/ui/atlas.pack")));
		profile = new PlayerProfile();
		
//		BACK BUTTON
		backButton = new TextButton("Back", skin);
		backButton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				((Game) Gdx.app.getApplicationListener()).setScreen(new MenuMain());
			}
		});
		backButton.pad(10);
		backButton.setPosition(width/10, height/10);
		stage.addActor(backButton);
		
//		INIT TABLES
		mainTable = new Table(skin);
		contentTable = new Table();
		shopTable = new Table();
		shopTableContent = new Table();
		shopTableBuy = new Table();
		profileTable = new Table();
		itemTable = new Table();
		newProfileTable = new Table();
		warningBuyTable = new Table();
		warningMaxProfileTable = new Table();
		delProfileTable = new Table();
		chooseProfileTable = new Table();
		
		delProfileTable.setFillParent(true);
		newProfileTable.setFillParent(true);
		warningBuyTable.setFillParent(true);
		chooseProfileTable.setFillParent(true);
		warningMaxProfileTable.setFillParent(true);
		
		
		backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		stage.setViewport(GameProperties.SIZE_WIDTH, GameProperties.SIZE_HEIGHT, true);
		stage.addActor(mainTable);
		mainTable.setFillParent(true);
		stage.addListener(clickHandler);
		Gdx.input.setInputProcessor(stage);
		
//		mainTable.debug();
//		contentTable.debug();
//		shopTable.debug();
//		shopTableContent.debug();
//		shopTableBuy.debug();
//		profileTable.debug();
//		itemTable.debug();
//		delProfileTable.debug();
//		newProfileTable.debug();
//		warningBuyTable.debug();
//		chooseProfileTable.debug();
		
//		HEAD
		mainTable.top().add(new Label("Profile", skin, "big")).pad(height/5, 0, height/5, 0).row();
		mainTable.add(contentTable);
		contentTable.add(shopTable).left().padRight(width/10);
		contentTable.add(profileTable).right();
		
		iniLabel();
		iniShopTable();
		iniProfileTable();
		iniWarningLabels();
		iniNewProfileTable();

		profileList = new com.badlogic.gdx.scenes.scene2d.ui.List(PlayerProfile.getNameList().toArray(), skin);
		scrollPane = new ScrollPane(profileList, skin);
		chooseProfileTable.add(new Label("Profile Selection", skin, "baoli96", Color.WHITE)).padBottom(height/5).row();
		chooseProfileTable.add(scrollPane).padBottom(height/10).row();
		chooseProfileTable.add(label_selectProfile);

	}
	
	private void updateProfileSelection() {
		profileList = new com.badlogic.gdx.scenes.scene2d.ui.List(PlayerProfile.getNameList().toArray(), skin);
		scrollPane.clear();
		scrollPane.setWidget(profileList);
	}
	
	
	private void iniNewProfileTable() {
//		TEXTFIELD
		label_createProfile = new Label("Enter your name", skin, "baoli96", Color.WHITE);
		
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
		nameField.setStyle(style);
		nameField.setMaxLength(10);
		nameField.setText("NewPlayer");
		nameField.setCursorPosition(nameField.getText().length());
		nameField.addListener(clickHandler);

		newProfileTable.add(label_createProfile).row();
		newProfileTable.add(nameField).size(width, height/4).row();
		newProfileTable.add(label_selectProfile);

	}
	
	private void iniLabel() {
		
//		SHOP
		label_quantityHook = getLabel44(String.format("%.2f m",0f));
		label_quantityshuriken = getLabel44(Integer.toString(0));
		label_price = getLabel44(Integer.toString(0));
		label_buy = getLabel44("Buy");
		
		label_moreShu = getLabel32(">");
		label_lessShu = getLabel32("<");
		label_moreHook  = getLabel32(">");
		label_lessHook = getLabel32("<");
		
//		PROFILE
		label_name = getLabel44(profile.name);
		label_rank = getLabel44(GameProperties.Rank.getRank(profile.experience).toString());
		label_shuriken = getLabel44(Integer.toString(profile.shuriken));
		label_stylePoints = getLabel44(Integer.toString(profile.stylePoints));
		label_hookRadius = getLabel44(String.format("%.2f m",(float)profile.hookRadius/100)+" m");

		label_newProfile = getLabel32("New Profile");
		label_delProfile = getLabel32("Delete Profile");
		label_chooseProfile = getLabel32("Choose Profile");
		label_selectProfile = getLabel32("Select");


//		LISTENER
		addListener(label_buy);
		addListener(label_moreShu);
		addListener(label_lessShu);
		addListener(label_lessHook);
		addListener(label_moreHook);
		
		addListener(label_newProfile);
		addListener(label_delProfile);
		addListener(label_chooseProfile);
		addListener(label_selectProfile);
		
	}
	
	
	
	private void iniWarningLabels() {
		label_warningCosts = new Label("Not enough StylePoints", skin, "baoli64", Color.RED);
		warningBuyTable.add(label_warningCosts);
		label_warningMaxProfile = new Label("Maximal 5 Profiles", skin, "baoli64", Color.RED);
		warningMaxProfileTable.add(label_warningMaxProfile);
		
		label_delProfile_decline = new Label("NO", skin, "baoli96", Color.WHITE);
		label_delProfile_accept = new Label("YES", skin, "baoli96", Color.WHITE);

		delProfileTable.center().add(new Label("Delete current Profile?", skin, "baoli64", Color.RED)).colspan(2).row();
		delProfileTable.add(label_delProfile_accept).padRight(width/10);
		delProfileTable.add(label_delProfile_decline).padLeft(width/10);
		
		addListener(label_delProfile_accept);
		addListener(label_delProfile_decline);
	}
	
	private void addListener(Label label) {
		label.addListener(clickHandler);
		label.setTouchable(Touchable.enabled);
		clickHandler.hoverCandidateList.add(label);
	}
	
	
	private void iniShopTable() {
		shopTable.add(getLabel64("Shop")).row();
		shopTable.add(shopTableContent).row();
		shopTable.add(shopTableBuy).row();
		
		shopTableContent.add(getLabel44("Items"));
		shopTableContent.add(getLabel44("Price")).pad(0, width/20, 0, width/20);
		shopTableContent.add(getLabel44("Quantity")).colspan(3).row();
		
		shopTableContent.add(getLabel44("shuriken")).left();
		shopTableContent.add(getLabel44(Integer.toString(PRICE_SHU)));
		shopTableContent.add(label_lessShu);
		shopTableContent.add(label_quantityshuriken);
		shopTableContent.add(label_moreShu).row();
		
		shopTableContent.add(getLabel44("Hook Radius")).left();
		shopTableContent.add(getLabel44(Integer.toString(PRICE_HOOK)));
		
		shopTableContent.add(label_lessHook);
		shopTableContent.add(label_quantityHook).pad(0, width/20, 0, width/20);
		shopTableContent.add(label_moreHook).row();
		
		shopTableBuy.add().size(width/2, height/10);
		shopTableBuy.add().size(width/20, height/10);
		shopTableBuy.add().size(width/5, height/10).row();
		shopTableBuy.add(getLabel44("Price"));
		shopTableBuy.add(label_price);
		shopTableBuy.add(label_buy);
	}
	
	private void iniProfileTable() {
		updateProfileLabels();
		
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
		itemTable.add(getLabel44("Hook Radius")).left().padRight(width/20);
		itemTable.add(label_hookRadius).row();
		itemTable.add(getLabel44("StylePoints")).left().padRight(width/20);
		itemTable.add(label_stylePoints).row();
		
		profileTable.add();
		profileTable.add(label_newProfile).row();
		
		profileTable.add();
		profileTable.add(label_chooseProfile).row();
		
		profileTable.add();
		profileTable.add(label_delProfile).row();
		
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
		
		Table.drawDebug(stage);
		stage.act(delta);
		stage.draw();
		
		if(warningCostsAlpha > 0) {
			if(!warningBuyTable.hasParent())
				stage.addActor(warningBuyTable);
			Color c = label_warningCosts.getColor();
			label_warningCosts.setColor(c.r, c.g, c.b, warningCostsAlpha);
			warningCostsAlpha = Math.max(0, warningCostsAlpha-0.01f);
			if(warningCostsAlpha == 0) {
				warningBuyTable.remove();
				stage.addActor(mainTable);
			}
			
		} else if(warningProfileAlpha > 0) {
			if(!warningMaxProfileTable.hasParent())
				stage.addActor(warningMaxProfileTable);
			Color c = label_warningMaxProfile.getColor();
			label_warningMaxProfile.setColor(c.r, c.g, c.b, warningProfileAlpha);
			warningProfileAlpha = Math.max(0, warningProfileAlpha-0.01f);
			if(warningProfileAlpha == 0) {
				warningMaxProfileTable.remove();
				stage.addActor(mainTable);
			}
		}
		
		

		processBuySelection();
		
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
		show();
	}

	@Override
	public void dispose() {
		profile.saveProfile();
		stage.dispose();
		skin.dispose();
		shaderBatch.dispose();
		backgroundTexture.dispose();
		pixmap.dispose();
		texture.dispose();
	}
	
	private void updateShopLabels() {
		label_quantityshuriken.setText(Integer.toString((int)buy_shu));
		label_quantityHook.setText(String.format("%.2f m",Math.floor(buy_hook)/100));
		label_price.setText(Integer.toString((int)buy_costs));	
		
	}
	
	private void purchaseItems() {
		profile.stylePoints -= (int)buy_costs;
		profile.shuriken += (int)buy_shu;
		profile.hookRadius += (int)buy_hook;
		buy_costs = buy_shu = buy_hook = 0;
		profile.saveProfile();
		updateProfileLabels();
		updateShopLabels();
	}

	private void updateProfileLabels() {
		
		label_name.setText(profile.name);
		label_rank.setText(GameProperties.Rank.getRank(profile.experience).toString());
		label_shuriken.setText(Integer.toString(profile.shuriken));
		label_stylePoints.setText(Integer.toString(profile.stylePoints));
		label_hookRadius.setText(String.format("%.2f m", (float)profile.hookRadius/100));
	}
	
	private void processBuySelection() {
		if(incHook != 0) {
			if(incHook < 0)
				buy_hook = Math.max(0, buy_hook - INC_SPEED);
			else if(incHook > 0 && profile.hookRadius + buy_hook <= GameProperties.HOOK_RADIUS_MAX) {
				if(buy_costs <= profile.stylePoints)	buy_hook += INC_SPEED;
				else									warningCostsAlpha = 1f;
			}	
			
			buy_costs = (int)buy_hook * PRICE_HOOK;
			updateShopLabels();
	
		} else if(incShu != 0) {
			if(incShu < 0)
				buy_shu = Math.max(0, buy_shu - INC_SPEED);
			else if(incShu > 0) {
				if(buy_costs <= profile.stylePoints)	buy_shu += INC_SPEED;
				else									warningCostsAlpha = 1f;
			}

			buy_costs = (int)buy_shu * PRICE_SHU;
			updateShopLabels();
		}
		
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
		
//			SHOP
			if(event.getListenerActor().equals(label_buy)) {
				purchaseItems();
				
//			PROFILES
			} else if(event.getListenerActor().equals(label_newProfile)) {
				if(PlayerProfile.getProfileCount() < GameProperties.MAX_PROFILE_COUNT) {
					 mainTable.remove();
					backButton.remove();
					stage.addActor(newProfileTable);
					stage.setKeyboardFocus(nameField);
					
				 } else
					 warningProfileAlpha = 1f;
				
			} else if(event.getListenerActor().equals(label_chooseProfile)) {
				mainTable.remove();
				updateProfileSelection();
				stage.addActor(chooseProfileTable);
				
			} else if(event.getListenerActor().equals(label_delProfile)) {
				mainTable.remove();
				stage.addActor(delProfileTable);
				
//			SUBMENU
			} else if(event.getListenerActor().equals(label_delProfile_accept)) {
				delProfileTable.remove();
				profile.deleteProfile();
				
				if(PlayerProfile.getProfileCount() > 0) {
					profile = new PlayerProfile();
					updateProfileLabels();
					stage.addActor(mainTable);
				} else {
					stage.addActor(newProfileTable);
					stage.setKeyboardFocus(nameField);
				}
				
			} else if(event.getListenerActor().equals(label_delProfile_decline)) {
				delProfileTable.remove();
				stage.addActor(mainTable);
				
			} else if(event.getListenerActor().equals(label_selectProfile)) {
				profile = new PlayerProfile(profileList.getSelectedIndex());
				profile.reorderToIndex(0);
				
				chooseProfileTable.remove();
				stage.addActor(mainTable);
				updateProfileLabels();
			}
		}
		
		
		public boolean keyDown(InputEvent event, int keycode) {
			if(keycode == Keys.ENTER && event.getListenerActor().equals(nameField)) {
				
				profile = PlayerProfile.createNewProfile(nameField.getText());
				
				newProfileTable.remove();
				stage.addActor(backButton);
				stage.addActor(mainTable);
				
				updateProfileLabels();

				nameField.setText("NewPlayer");
				nameField.setCursorPosition(9);
				return true;
			}
			return false;
		}
		
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			if(event.getListenerActor().equals(label_lessHook))
				incHook = -1;
			else if(event.getListenerActor().equals(label_lessShu))
				incShu = -1;
			else if(event.getListenerActor().equals(label_moreHook))
				incHook = 1;
			else if(event.getListenerActor().equals(label_moreShu))
				incShu = 1;
			
			if(!(incShu != 0 || incHook != 0))
				clicked(event, x, y);
			
			return incShu != 0 || incHook != 0;
		}
		
		public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			incShu = incHook = 0;
		}
		
	}

}
