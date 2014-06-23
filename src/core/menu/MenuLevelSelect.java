package core.menu;

import java.util.LinkedList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import core.FilePath;
import core.GameProperties;
import core.GameProperties.GameState;
import core.exception.LevelNotFoundException;

public class MenuLevelSelect implements Screen {
	
	private Stage stage;
	private Table table, previewTable;
	private Image previewImage;
	private Skin skin;
	private List levelSelectionList;
	private int width, height;
	private java.util.List<Texture> previewTextures;
	private String[] levelList = {"Level 1", "Level 2", "Level 3"};
		
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1); 		//black screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act(delta);
		stage.draw();
		
//		Table.drawDebug(stage);            // case debuglines needed 1/2
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void show() {
		width = GameProperties.SCALE_WIDTH;
		height = GameProperties.SCALE_HEIGHT;
		
		stage = new Stage();
		stage.setViewport(GameProperties.SIZE_WIDTH, GameProperties.SIZE_HEIGHT, true);
		Gdx.input.setInputProcessor(stage);
		
		skin = new Skin(Gdx.files.internal("res/ui/menuSkin.json"),new TextureAtlas(Gdx.files.internal("res/ui/atlas.pack")));
		
		table = new Table(skin);
		table.setFillParent(true);
		
		table.debug();            // case debuglines needed 2/2
		
		levelSelectionList = new List(levelList, skin);
		levelSelectionList.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				previewImage = new Image(previewTextures.get(levelSelectionList.getSelectedIndex()));
				previewTable.clear();
				previewImage.setSize(width, height);
				previewTable.add(previewImage);
			}
			
		});
		
		ScrollPane scrollPane = new ScrollPane(levelSelectionList, skin);
		
		TextButton playButton = new TextButton("Level starten", skin);
		playButton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				try {
					GameProperties.setGameState(GameState.INGAME, levelSelectionList.getSelectedIndex());
				} catch (LevelNotFoundException e) {
					GameProperties.setGameState(GameState.MENU);
					((Game) Gdx.app.getApplicationListener()).setScreen(new MenuLevelSelect());
					e.printStackTrace();
				}
			}
		});
		playButton.setPosition(width*2 - playButton.getWidth(), height/10);
		stage.addActor(playButton);
		
		TextButton backButton = new TextButton("Zurueck", skin); //small da ungleich defaultgroesse
		backButton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				((Game) Gdx.app.getApplicationListener()).setScreen(new MenuMain());
			}
		});
		backButton.setPosition(width/10, height/10);
		stage.addActor(backButton);
		
		previewTextures = new LinkedList<Texture>();
		previewTextures.add(new Texture(Gdx.files.internal(FilePath.preview_level1)));
		previewTextures.add(new Texture(Gdx.files.internal(FilePath.preview_level2)));
		previewTextures.add(new Texture(Gdx.files.internal(FilePath.preview_level3)));
		previewImage = new Image(previewTextures.get(0));
				
		
		//putting stuff together
		previewTable = new Table();

		table.top().add().size(width,0).row();
		table.add(new Label("Levelauswahl", skin, "big")).pad(height/10, 0, height/2, 0).colspan(3).row();
//		table.add().size(width/2, 0);
		table.add().size(width/10, 0);
		table.add().size(width*2, 0).row();
		table.add(scrollPane).right();
		table.add(previewTable).pad(0, width/20, 0, width/10).left();
		previewTable.add(previewImage);
		
		
		
		stage.addActor(table);
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
		for(Texture t : previewTextures)
			t.dispose();
		stage.dispose();
		skin.dispose();
	}

}
