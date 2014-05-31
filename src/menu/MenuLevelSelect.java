package menu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import core.ingame.GameProperties;
import core.ingame.GameRender;

public class MenuLevelSelect implements Screen {
	
	private Stage stage;
	private Table table;
	private Skin skin;
		
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1); 		//black screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act(delta);
		stage.draw();
		
//Table.drawDebug(stage);            // case debuglines needed 1/2
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height, false);
		table.invalidateHierarchy();
	}

	@Override
	public void show() {
		stage = new Stage();
		
		Gdx.input.setInputProcessor(stage);
		
		skin = new Skin(Gdx.files.internal("res/ui/menuSkin.json"),new TextureAtlas(Gdx.files.internal("res/ui/atlas.pack")));
		
		table = new Table(skin);
		table.setFillParent(true);
		
//table.debug();            // case debuglines needed 2/2
																			//looks in skin for liststyle named default
		List list = new List(new String[] {"Level 01", "Level 02", "Level 03", "Level 04", "Level 05", "Level 06", "Level 07", "Level 08", "Level 09", "Level 10", "Level 11", "Level 12", "Level 13", "Level 14", "Level 15", "Level 16", "Level 17", "Level 18", "Level 19", "Level 20", "VERYLONGDEMONSTRATINGSIDESCROLLABILITY AND TABLE STABILITY"}, skin);
		
		ScrollPane scrollPane = new ScrollPane(list, skin);
		
		TextButton play = new TextButton("Level starten", skin);
		play.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				GameProperties.switchMode(false, true);
				((Game) Gdx.app.getApplicationListener()).setScreen(GameRender.getInstance());
			}
		});
		play.pad(15);
		
		TextButton back = new TextButton("Zurueck", skin, "small"); //small da ungleich defaultgroesse
		back.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				((Game) Gdx.app.getApplicationListener()).setScreen(new MenuMain());
			}
		});
		back.pad(10);
		
		//putting stuff together
		table.add(new Label("Levelauswahl", skin, "big")).colspan(3).expandX().spaceBottom(50).row();
		table.add(scrollPane).uniformX().expandY().top().left();
		table.add(play).uniformX();
		table.add(back).uniformX().bottom().right();
		
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
		stage.dispose();
		skin.dispose();
	}

}
