package core.menu;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import misc.ShaderBatch;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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

import core.GameProperties;
import core.Highscore;
import core.Highscore.Score;

public class MenuHighscore implements Screen {
	
	private Texture backgroundTexture = new Texture(Gdx.files.internal("res/img/main menu.png"));
	private Sprite backgroundSprite = new Sprite(backgroundTexture);
	private ShaderBatch shaderBatch;
	private Stage stage;
	private Table mainTable, scoreTable;
	private Skin skin;
	private TextButton backButton;
	private Label prevLevel, nextLevel;
	private int levelIndex = 0;
	private Label levelName;
	private ClickHandler clickHandler;
	
	private int width, height;
	final private Color NORMAL = new Color(1,1,0.90f,1);
	final private Color HOVER = new Color(1,1,0f,1);
		
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1); 		//black screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		shaderBatch.brightness = GameProperties.brightness;
		shaderBatch.contrast = GameProperties.contrast;
		
		shaderBatch.begin();
		backgroundSprite.draw(shaderBatch);
		shaderBatch.end();
		
		stage.act(delta);
		stage.draw();
		
//		Table.drawDebug(stage);            // case debuglines needed 1/2
	}

	@Override
	public void resize(int width, int height) {
//		backgroundSprite.setSize(width, height);
//		stage.setViewport(1280, 800, true);
//		table.invalidateHierarchy();
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
		mainTable.top();
		
		scoreTable = new Table(skin);
		
		
		backButton = new TextButton("Zurueck", skin);
		backButton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				((Game) Gdx.app.getApplicationListener()).setScreen(new MenuMain());
			}
		});
		backButton.pad(10);
		backButton.setPosition(width/10, height/10);
		stage.addActor(backButton);
		
//		HEAD
		nextLevel = new Label(">>", skin, "baoli32", NORMAL);
		nextLevel.addListener(clickHandler);
		nextLevel.setTouchable(Touchable.enabled);
		
		prevLevel = new Label("<<", skin, "baoli32", NORMAL);
		prevLevel.addListener(clickHandler);
		prevLevel.setTouchable(Touchable.enabled);

		mainTable.add(new Label("Highscore", skin, "big")).padTop(height/5).colspan(3).row();
		mainTable.add();
		mainTable.add().size(width/2, 0);
		mainTable.add().row();
		

		levelName = new Label(Highscore.getInstance().getHighscoreList(levelIndex).get(0).LEVEL.NAME, skin);
		
		mainTable.add(prevLevel).right();
		mainTable.add(levelName).center();
		mainTable.add(nextLevel).left().row();
		mainTable.add(scoreTable).padTop(height/10).colspan(3);
		
		iniScoreTable();
		
		//putting stuff together
		
		stage.addActor(mainTable);
		stage.addListener(clickHandler);
		
		
	}
	
	
	private void iniScoreTable() {
		if(Highscore.getInstance().getHighscoreList(levelIndex) == null)
			return;
		
		scoreTable.clear();
		scoreTable.debug();
		
		levelName.setText(Highscore.getInstance().getHighscoreList(levelIndex).get(0).LEVEL.NAME);
		scoreTable.add().size(width/10, 0);
		scoreTable.add().size(width/3, 0);
		scoreTable.add().size(width, 0).row();
		
		scoreTable.add(new Label("Place", skin));
		scoreTable.add(new Label("Time", skin));
		scoreTable.add(new Label("Name", skin)).row();
		
		iniScoreTableList(Highscore.getInstance().getHighscoreList(levelIndex));
		
		scoreTable.setSize(10, 0);
		scoreTable.invalidate();
		
	}
	
	private void iniScoreTableList(java.util.List<Score> scores) {
		if(scores != null)
		for(int i = 0; i < scores.size() && i < 5; i++) {
			scoreTable.add(new Label(Integer.toString(i+1), skin));
			scoreTable.add(scores.get(i).TIME_STRING);
			scoreTable.add(scores.get(i).PLAYER_NAME).row();
		}
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
		shaderBatch.dispose();
		backgroundSprite.getTexture().dispose();
	}
	
	private class ClickHandler extends ClickListener {
		private List<Actor> hoverList = new CopyOnWriteArrayList<Actor>();
		
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
			if(a != null && (a.equals(nextLevel) || a.equals(prevLevel))) {
				hoverList.add(a);
				a.setColor(HOVER);
			} 
		
			return false;
		}
		
		public void clicked(InputEvent event, float x, float y){
			int next = 0;
			
			if(event.getListenerActor() == prevLevel)
				next = 1;
			else if(event.getListenerActor() == nextLevel)
				next = -1;
			
			if(next == 0)
				return;
			
			levelIndex = ((levelIndex + next % GameProperties.IMPLEMENTED_LEVEL) + GameProperties.IMPLEMENTED_LEVEL) % GameProperties.IMPLEMENTED_LEVEL;
			iniScoreTable();
		}
		
	}

}
