package core.menu;

import java.util.LinkedList;

import misc.ShaderBatch;

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
import core.GameProperties.GameScreen;
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
	
	private ShaderBatch shaderBatch;
		
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
		
//		AnimatedBackground.getInstance().updateAndRenderRays();
		
		
//		Table.drawDebug(stage);            // case debuglines needed 1/2
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void show() {
		width = GameProperties.SCALE_WIDTH;
		height = GameProperties.SCALE_HEIGHT;
		
		shaderBatch = new ShaderBatch(50);
		
		stage = new Stage(GameProperties.SCALE_WIDTH*2, GameProperties.SCALE_HEIGHT*2, true, shaderBatch);
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
				previewTable.add(previewImage);
			}
			
		});
		
		ScrollPane scrollPane = new ScrollPane(levelSelectionList, skin);
		
		TextButton playButton = new TextButton("Start Level", skin);
		playButton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				try {
					GameProperties.switchGameScreen(GameScreen.getScreen(levelSelectionList.getSelectedIndex()));
				} catch (LevelNotFoundException e) {
					GameProperties.switchGameScreen(GameScreen.MENU_LEVELSELECT);
					e.printStackTrace();
				}
			}
		});
		playButton.setPosition(width*2 - playButton.getWidth(), height/10);
		stage.addActor(playButton);
		
		TextButton backButton = new TextButton("Back", skin); //small da ungleich defaultgroesse
		backButton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				GameProperties.switchGameScreen(GameScreen.MENU_MAIN);
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
		table.add(new Label("Level Selection", skin, "big")).pad(height/10, 0, height/2, 0).colspan(3).row();
		table.add(scrollPane).padRight(Gdx.graphics.getWidth()/20).right();
		table.add(previewTable).left();
		table.add().padLeft(Gdx.graphics.getWidth()/20).row();
		table.add().colspan(3).padTop(Gdx.graphics.getHeight()/2);
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
		show();
		System.out.println("yo");
	}

	@Override
	public void dispose() {
		for(Texture t : previewTextures)
			t.dispose();
		stage.dispose();
		skin.dispose();
	}

}
