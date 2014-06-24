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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
	
	private String name = "New User", rank = "Noob";
	private int shuriken, hookLevel, stylePoints, exp;
	
	private void resetProfile() {
		name = "New User";
		rank = "Noob";
		shuriken = stylePoints = exp = 0;
		hookLevel = 1;
	}
	
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
		
//		label_buy.addListener(clickHandler);
//		label_newProfile.addListener(clickHandler);
//		label_moreShu.addListener(clickHandler);
//		label_lessShu.addListener(clickHandler);
//		label_moreHook.addListener(clickHandler);
//		label_lessHook.addListener(clickHandler);
	
//		TODO testing
		addListener(label_buy);
		addListener(label_newProfile);
		addListener(label_moreShu);
		addListener(label_lessShu);
		addListener(label_lessHook);
		addListener(label_moreHook);
	}
	
	private void addListener(Label label) {
		label.addListener(clickHandler);
		label.setTouchable(Touchable.enabled);
		clickHandler.hoverCandidateList.add(label);
	}
	
	
	private void iniShop() {
		shopTable.add(getLabel64("Shop")).padTop(height/20).row();
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
		itemTable.add(getLabel44("Hook")).left().padRight(width/20);
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
		System.out.println("Profile Saved");
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
			if(event.getListenerActor().equals(label_newProfile)) {
//				TODO warning
				resetProfile();
				saveProfile();
				loadProfile();
			} else if(event.getListenerActor().equals(label_buy)) {
//				TODO
			}
		}
		
	}

}
