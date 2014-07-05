package core.menu;

import misc.ShaderBatch;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import core.FilePath;
import core.GameProperties;
import core.GameProperties.GameScreen;
import core.PlayerProfile;

public class MenuProfile implements Screen {
	
	private Stage stage;
	private Skin skin;
	
	private Table mainTable, contentTable, shopTable, shopTableContent, shopTableBuy, profileTable, itemTable, 
					newProfileTable, delProfileTable, warningBuyTable, warningMaxProfileTable, chooseProfileTable;
	
	private TextButton backButton;
	private int width, height;
	
	private InputHandler iHandler;
	private ShaderBatch shaderBatch;
	
	private Label label_quantityshuriken, label_quantityHook, label_price;
	private Label label_name, label_rank, label_shuriken, label_stylePoints, label_hookRadius;
	private Label label_buy, label_newProfile, label_moreShu, label_lessShu, label_moreHook, label_lessHook;
	private Label label_warningCosts, label_warningMaxProfile, label_delProfile_accept, label_delProfile_decline;
	
	private com.badlogic.gdx.scenes.scene2d.ui.List profileList;
	private ScrollPane scrollPane;
	private Label label_delProfile, label_chooseProfile, label_selectProfile;
	
	private PlayerProfile profile;
	private Sound sound_buy;

//	SHOP
	private float warningCostsAlpha = 0f, warningProfileAlpha = 0f;
	private float buy_shu, buy_hook, buy_costs, incShu, incHook;
	private final float INC_SPEED = 0.3f;
	
	@Override
	public void show() {
		width = GameProperties.SCALE_WIDTH;
		height = GameProperties.SCALE_HEIGHT;
		
		shaderBatch = new ShaderBatch(100);
		stage = new Stage(width*2, height*2, true, shaderBatch);
		iHandler = new InputHandler(stage);
		
		skin = new Skin(Gdx.files.internal("res/ui/menuSkin.json"),new TextureAtlas(Gdx.files.internal("res/ui/atlas.pack")));
		profile = new PlayerProfile();
		sound_buy = Gdx.audio.newSound(Gdx.files.local(FilePath.sound_get));
		
//		BACK BUTTON
		backButton = new TextButton("Back", skin);
		backButton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				GameProperties.switchGameScreen(GameScreen.MENU_MAIN);
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
		
		
		stage.setViewport(GameProperties.SCALE_WIDTH*2, GameProperties.SCALE_HEIGHT*2, true);
		stage.addActor(mainTable);
		mainTable.setFillParent(true);
		stage.addListener(iHandler);
		Gdx.input.setInputProcessor(stage);
		
		mainTable.debug();
		contentTable.debug();
		shopTable.debug();
		shopTableContent.debug();
		shopTableBuy.debug();
		profileTable.debug();
		itemTable.debug();
		delProfileTable.debug();
		newProfileTable.debug();
		warningBuyTable.debug();
		chooseProfileTable.debug();
		
//		HEAD
		mainTable.top().add(new Label("Profile", skin, "big")).pad(height/5, 0, height/5, 0).row();
		mainTable.add(contentTable);
		contentTable.add(shopTable);
		contentTable.add().size(width/20, 0);
		contentTable.add(profileTable).padRight(width/20);
		
		iniLabel();
		iniShopTable();
		iniProfileTable();
		iniWarningLabels();

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
		iHandler.addToListener(label_buy);
		iHandler.addToListener(label_moreShu);
		iHandler.addToListener(label_lessShu);
		iHandler.addToListener(label_lessHook);
		iHandler.addToListener(label_moreHook);
		
		iHandler.addToListener(label_newProfile);
		iHandler.addToListener(label_delProfile);
		iHandler.addToListener(label_chooseProfile);
		iHandler.addToListener(label_selectProfile);
		
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
		
		iHandler.addToListener(label_delProfile_accept);
		iHandler.addToListener(label_delProfile_decline);
	}
	
	private void iniShopTable() {
		shopTable.add(getLabel64("Shop")).row();
		shopTable.add(shopTableContent).row();
		shopTable.add(shopTableBuy).row();
		
		shopTableContent.add(getLabel44("Items"));
		shopTableContent.add(getLabel44("Price"));//.pad(0, width/20, 0, width/20);
		shopTableContent.add(getLabel44("Quantity")).colspan(3).row();
		
		shopTableContent.add(getLabel44("shuriken"));
		shopTableContent.add(getLabel44(Integer.toString(GameProperties.PRICE_SHURIKEN)));
		shopTableContent.add(label_lessShu);
		shopTableContent.add(label_quantityshuriken);
		shopTableContent.add(label_moreShu).row();
		
		shopTableContent.add(getLabel44("Hook Radius")).left();
		shopTableContent.add(getLabel44(Integer.toString(GameProperties.PRICE_HOOK)));
		
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
		
		profileTable.add(getLabel44("Name")).left().padRight(width/20);
		profileTable.add(label_name).left().row();
		profileTable.add(getLabel44("Rank")).left().padRight(width/20);
		profileTable.add(label_rank).left().row();
		
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
		AnimatedBackground.getInstance().draw(shaderBatch, delta);
		shaderBatch.end();
		
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
			}
		}
		
		processBuySelection();
		
//		Table.drawDebug(stage);
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(this.width*2, this.height*2, true);
		mainTable.invalidateHierarchy();
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
		stage.dispose();
		skin.dispose();
		shaderBatch.dispose();
		sound_buy.dispose();
	}
	
	private void updateShopLabels() {
		label_quantityshuriken.setText(Integer.toString((int)buy_shu));
		label_quantityHook.setText(String.format("%.2f m",Math.floor(buy_hook)/100));
		label_price.setText(Integer.toString((int)buy_costs));	
		
	}
	
	private void purchaseItems() {
		if(buy_costs > 0) {
			sound_buy.play(GameProperties.soundVolume);
			
			profile.stylePoints -= (int)buy_costs;
			profile.shuriken += (int)buy_shu;
			profile.hookRadius += (int)buy_hook;
			buy_costs = buy_shu = buy_hook = 0;
			profile.updateAndSaveProfile();
			updateProfileLabels();
			updateShopLabels();
		}
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
			else if(incHook > 0 && profile.hookRadius + buy_hook <= GameProperties.MAX_HOOK_RADIUS) {
				if(buy_costs + GameProperties.PRICE_HOOK <= profile.stylePoints)	
					buy_hook += INC_SPEED;
				else									
					warningCostsAlpha = 1f;
			}

		} else if(incShu != 0) {
			if(incShu < 0)
				buy_shu = Math.max(0, buy_shu - INC_SPEED);
			else if(incShu > 0) {
				if(buy_costs + GameProperties.PRICE_SHURIKEN <= profile.stylePoints)
					buy_shu += INC_SPEED;
				else
					warningCostsAlpha = 1f;
			}

		}
		
		if(incShu != 0 || incHook != 0) {
			buy_costs = (int) buy_shu * GameProperties.PRICE_SHURIKEN + (int) buy_hook * GameProperties.PRICE_HOOK;
			updateShopLabels();
		}

	}
	
	
	private class InputHandler extends ClickHandler {
		
		private InputHandler(Stage stage) {
			super(stage);
		}
		
		public void clicked(InputEvent event, float x, float y){
		
//			SHOP
			if(event.getListenerActor().equals(label_buy)) {
				purchaseItems();
				
//			PROFILES
			} else if(event.getListenerActor().equals(label_newProfile)) {
				if(PlayerProfile.getProfileCount() < GameProperties.MAX_PROFILE_COUNT)
					((Game) Gdx.app.getApplicationListener()).setScreen(new EnterNameScreen(GameProperties.GameScreen.MENU_PROFILE));
				else
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
					profile = new PlayerProfile(0);
					updateProfileLabels();
					stage.addActor(mainTable);
				} else
					((Game) Gdx.app.getApplicationListener()).setScreen(new EnterNameScreen(GameProperties.GameScreen.MENU_PROFILE));
				
				
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
