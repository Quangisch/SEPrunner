package core.menu;

import net.HighscoreServer;
import misc.ShaderBatch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import core.GameProperties;
import core.Highscore;
import core.Highscore.Score;
import core.PlayerProfile;

public class MenuHighscore implements Screen {
	
	private ShaderBatch shaderBatch;
	private Stage stage;
	private Table mainTable, scoreTable;
	private Skin skin;
	private Label prevLevel, nextLevel;
	private int levelIndex = 0;
	private Label levelName;
	private InputHandler iHandler;
	private ScrollPane scrollPane;
	
	private int width, height;
	private PlayerProfile profile;
	
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
		
		scroll();
//		Table.drawDebug(stage);            // case debuglines needed 1/2
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(this.width*2, this.height*2, true);
		mainTable.invalidateHierarchy();
	}

	@Override
	public void show() {
		width = GameProperties.SCALE_WIDTH;
		height = GameProperties.SCALE_HEIGHT;
		
		shaderBatch = new ShaderBatch(100);
		stage = new Stage(width*2, height*2, true, shaderBatch);
		iHandler = new InputHandler(stage);
		profile = new PlayerProfile();
		Gdx.input.setInputProcessor(stage);
		
		skin = new Skin(Gdx.files.internal("res/ui/menuSkin.json"),new TextureAtlas(Gdx.files.internal("res/ui/atlas.pack")));
		
		mainTable = new Table(skin);
		mainTable.setFillParent(true);
		mainTable.debug();
		mainTable.top();
		
		scoreTable = new Table(skin);
		iHandler.setBackButton(skin);
		
//		HEAD
		nextLevel = new Label(">>", skin, "baoli32", Color.WHITE);
		iHandler.addToListener(nextLevel);
		
		
		prevLevel = new Label("<<", skin, "baoli32", Color.WHITE);
		iHandler.addToListener(prevLevel);

		mainTable.add(new Label("Highscore", skin, "big")).padTop(height/5).colspan(3).row();
		mainTable.add();
		mainTable.add().size(width/2, 0);
		mainTable.add().row();
		

		levelName = new Label(GameProperties.IMPLEMENTED_LEVEL[0], skin);
		
		mainTable.add(prevLevel).right();
		mainTable.add(levelName).center();
		mainTable.add(nextLevel).left().row();

		scrollPane = new ScrollPane(scoreTable);
		mainTable.add().size(width/10, 0);
		mainTable.add().size(width, 0);
		mainTable.add().size(width/3, 0).row().padTop(height/10);
		
		mainTable.add(new Label("Place", skin));
		mainTable.add(new Label("Name", skin));
		mainTable.add(new Label("Time", skin)).row();
		mainTable.top().add(scrollPane).height(stage.getHeight()/2).colspan(3);
		
		iniScoreTable();
		
		//putting stuff together
		
		stage.addActor(mainTable);
		stage.addListener(iHandler);

	}
	
	
	private void iniScoreTable() {
		if(Highscore.getInstance().getHighscoreList(levelIndex) == null)
			return;
		
		scoreTable.clear();
		scoreTable.debug();
		
		levelName.setText(GameProperties.IMPLEMENTED_LEVEL[levelIndex]);
		scoreTable.add().size(width/10, 0);
		scoreTable.add().size(width, 0);
		scoreTable.add().size(width/3, 0).row();
		
		iniScoreTableList(Highscore.getInstance().getHighscoreList(levelIndex));
	}
	
	private void iniScoreTableList(java.util.List<Score> scores) {
		if(scores != null)
			for(int i = 0; i < scores.size(); i++) {
				scoreTable.add(new Label(Integer.toString(i+1), skin));
				scoreTable.add(scores.get(i).PLAYER_NAME, "baoli44", 
						profile.name.equals(scores.get(i).PLAYER_NAME) ? Color.YELLOW : Color.WHITE);
				scoreTable.add(scores.get(i).TIME_STRING).row();
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
	}
	
	private void nextLevelPage(int next) {
		levelIndex = ((levelIndex + next % GameProperties.IMPLEMENTED_LEVEL.length) + GameProperties.IMPLEMENTED_LEVEL.length) % GameProperties.IMPLEMENTED_LEVEL.length;
		iniScoreTable();
	}
	
	private int scrollY;
	private void scroll() {
		scrollPane.setScrollY(scrollPane.getScrollY()+scrollY);
	}
	
	private class InputHandler extends ClickHandler {
		private InputHandler(Stage stage) {
			super(stage);
		}
		
		public void clicked(InputEvent event, float x, float y){
			int next = 0;
			
			if(event.getListenerActor() == prevLevel)
				next = -1;
			else if(event.getListenerActor() == nextLevel)
				next = 1;
			
			if(next == 0)
				return;
			
			nextLevelPage(next);
		}
		
		public boolean keyDown(InputEvent event, int keycode) {
			super.keyDown(event, keycode);
			
			switch(keycode) {
			case Keys.UP:
				scrollY = -10;
				break;
			case Keys.DOWN:
				scrollY = 10;
				break;
			case Keys.LEFT:
				nextLevelPage(-1);
				break;
			case Keys.RIGHT:
				nextLevelPage(1);
				break;
			}
			
			return false;
		}
		
		public boolean keyUp(InputEvent event, int keycode) {
			switch(keycode) {
			case Keys.UP:
			case Keys.DOWN:
				scrollY = 0;
				break;
			}
			
			return super.keyUp(event, keycode);
		}
	}

}
