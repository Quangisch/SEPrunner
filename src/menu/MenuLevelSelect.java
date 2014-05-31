package menu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MenuLevelSelect implements Screen {
	
	private Stage stage;
	private Table table;
	private TextureAtlas atlas;
	private Skin skin;
	private List list;
	private ScrollPane scrollPane;
	private TextButton play, back;
	
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

	}

	@Override
	public void show() {
		stage = new Stage();
		
		Gdx.input.setInputProcessor(stage);
		
		atlas = new TextureAtlas(Gdx.files.internal("res/ui/atlas.pack"));
		skin = new Skin(Gdx.files.internal("res/ui/menuSkin.json"),atlas);
		
		table = new Table(skin);
		table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//table.debug();            // case debuglines needed 2/2
																			//looks in skin for liststyle named default
		list = new List(new String[] {"Level 01", "Level 02", "Level 03", "Level 04", "Level 05", "Level 06", "Level 07", "Level 08", "Level 09", "Level 10", "Level 11", "Level 12", "Level 13", "Level 14", "Level 15", "Level 16", "Level 17", "Level 18", "Level 19", "Level 20", "VERYLONGDEMONSTRATINGSIDESCROLLABILITY AND TABLE STABILITY"}, skin);
		
		scrollPane = new ScrollPane(list, skin);
		
		play = new TextButton("Spielen", skin);
		play.pad(15);
															//siehe atlas.pack
		back = new TextButton("Zurück", skin, "small");		//small weil wir hier nicht die defaultgroeße wollen
		back.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				((Game) Gdx.app.getApplicationListener()).setScreen(new MenuMain());
			}
		});
		back.pad(10);
		
		//putting stuff together
		table.add().width(table.getWidth()/3);
		table.add("Levelauswahl").width(table.getWidth()/3);
		table.add().width(table.getWidth()/3).row();
		table.add(scrollPane).left().expandY();
		table.add(play);
		table.add(back).bottom().right();
		
		stage.addActor(table);
	}

	@Override
	public void hide() {

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
		atlas.dispose();
		skin.dispose();
	}

}
