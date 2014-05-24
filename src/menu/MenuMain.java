package menu;

import tween.ActorAccessor;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MenuMain implements Screen {

	private Stage stage;
	private TextureAtlas atlas; //defining regions
	private Skin skin;
	private Table table; //objects get organized on here
	private TextButton buttonPlay, buttonOption, buttonHighscore, buttonExit; //buttonlist
	private Label heading;
	private TweenManager tweenManager; //tween-engine starter, stuff like fade-in/out animations
	//textButtonStyle pushed in json-file, put in skin
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);	//black background
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				
		tweenManager.update(delta);
		
		stage.act(delta); //updates table also since thats in there
		stage.draw();
		
//Table.drawDebug(stage);             case debuglines needed 2/2
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height, true);
		table.invalidateHierarchy();
		table.setSize(width, height);
	}

	@Override
	public void show() {
		stage = new Stage();
		
		Gdx.input.setInputProcessor(stage);	//eventhandler in input, enables to push the button

		atlas = new TextureAtlas(Gdx.files.internal("res/ui/button.pack"));
		skin = new Skin(Gdx.files.internal("res/ui/menuSkin.json"), atlas);
		
		table = new Table(skin);
		table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		//creating heading
		heading = new Label(Project.TITLE, skin, "default"); //menu.Project			default-value in res/ui.menu.Skin.json
		heading.setFontScale(1); //sizable headline
		
		//creating buttons
		buttonPlay = new TextButton("Spiel starten", skin, "default");
		buttonPlay.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				((Game) Gdx.app.getApplicationListener()).setScreen(new MenuLevelSelect()); //further linking imgages
			}
		});
		buttonPlay.pad(15);  //puffer zwischen buchstaben & buttonrand
		
		buttonOption = new TextButton("Optionen", skin, "default");
		buttonOption.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				((Game) Gdx.app.getApplicationListener()).setScreen(new MenuOption());
			}
		});
		buttonOption.pad(15);  //puffer zwischen buchstaben & buttonrand
		
		buttonHighscore = new TextButton("Highscore", skin, "default");
		buttonHighscore.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				((Game) Gdx.app.getApplicationListener()).setScreen(new MenuHighscore());
			}
		});
		buttonHighscore.pad(15);  //puffer zwischen buchstaben & buttonrand
		
		buttonExit = new TextButton("Beenden", skin, "default");
		buttonExit.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				Gdx.app.exit();
			}
		});
		buttonExit.pad(15);
				
		//putting stuff together
		table.add(heading);
		table.getCell(heading).spaceBottom(100); //luecke unter heading legen = Abstand
		table.row();
		table.add(buttonPlay);
		table.getCell(buttonPlay).spaceBottom(20);
		table.row();
		table.add(buttonOption);
		table.getCell(buttonOption).spaceBottom(20);
		table.row();
		table.add(buttonHighscore);
		table.getCell(buttonHighscore).spaceBottom(20);
		table.row();
		table.add(buttonExit);
//table.debug();             case debuglines needed 2/2
		stage.addActor(table);
		
		//creating animations
		tweenManager = new TweenManager();
		Tween.registerAccessor(Actor.class, new ActorAccessor());
		
		//heading color animation
		Timeline.createSequence().beginSequence()
			//.push(Tween.to(heading, ActorAccessor.RGB, .5f).target(0, 0, 1))			//dunkelblau
			//.push(Tween.to(heading, ActorAccessor.RGB, .5f).target(0, 1, 0))			//gruen
			//.push(Tween.to(heading, ActorAccessor.RGB, .5f).target(0, 1, 1))			//hellblau
			//.push(Tween.to(heading, ActorAccessor.RGB, .5f).target(1, 0, 0))			//rot
			//.push(Tween.to(heading, ActorAccessor.RGB, .5f).target(1, 1, 0))			//gelb
			.push(Tween.to(heading, ActorAccessor.RGB, .5f).target(1, 1, 1))			//weiss
			.end().start(tweenManager);
			//.end().repeat(Tween.INFINITY, 0).start(tweenManager);
		//alternativcode wuerde headline durch farben kontinuierlich wechseln
	
		//heading and buttons fade-in
		Timeline.createSequence().beginSequence()
			.push(Tween.set(buttonPlay, ActorAccessor.ALPHA).target(0))
			.push(Tween.set(buttonOption, ActorAccessor.ALPHA).target(0))
			.push(Tween.set(buttonHighscore, ActorAccessor.ALPHA).target(0))
			.push(Tween.set(buttonExit, ActorAccessor.ALPHA).target(0))
			.push(Tween.from(heading, ActorAccessor.ALPHA, .25f).target(0))
			.push(Tween.to(buttonPlay, ActorAccessor.ALPHA, .25f).target(1))
			.push(Tween.to(buttonOption, ActorAccessor.ALPHA, .25f).target(1))
			.push(Tween.to(buttonHighscore, ActorAccessor.ALPHA, .25f).target(1))
			.push(Tween.to(buttonExit, ActorAccessor.ALPHA, .25f).target(1))
			.end().start(tweenManager);
		
		//table fade-in
		Tween.from(table, ActorAccessor.ALPHA, .5f).target(0).start(tweenManager);
		Tween.from(table, ActorAccessor.Y, .5f).target(Gdx.graphics.getHeight()/8).start(tweenManager);
		
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
