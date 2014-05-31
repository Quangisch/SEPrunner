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
	private TweenManager tweenManager; //tween-engine starter, stuff like fade-in/out animations
	//textButtonStyle pushed in json-file, put in skin
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);	//black background
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				
		stage.act(delta); //updates table also since thats in there
		stage.draw();
		
		tweenManager.update(delta);
		
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
		
		Gdx.input.setInputProcessor(stage);	//eventhandler in input, enables to push the button

		atlas = new TextureAtlas(Gdx.files.internal("res/ui/atlas.pack"));
		skin = new Skin(Gdx.files.internal("res/ui/menuSkin.json"), atlas);
		
		table = new Table(skin);
		table.setFillParent(true);
		
		//creating heading
		Label heading = new Label(Project.TITLE, skin, "big"); //menu.Project			default-value in res/ui.menu.Skin.json
		heading.setFontScale(1); //sizable headline
		
		//creating buttons
		TextButton buttonPlay = new TextButton("Spielen", skin);
		buttonPlay.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				((Game) Gdx.app.getApplicationListener()).setScreen(new MenuLevelSelect()); //further linking imgages		
			}
		});
		buttonPlay.pad(15);  //puffer zwischen buchstaben & buttonrand
		
		TextButton buttonOption = new TextButton("Optionen", skin);
		buttonOption.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				((Game) Gdx.app.getApplicationListener()).setScreen(new MenuOption());
			}
		});
		buttonOption.pad(15);  //puffer zwischen buchstaben & buttonrand
		
		TextButton buttonHighscore = new TextButton("Highscore", skin);
		buttonHighscore.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				((Game) Gdx.app.getApplicationListener()).setScreen(new MenuHighscore());
			}
		});
		buttonHighscore.pad(15);  //puffer zwischen buchstaben & buttonrand
		
		TextButton buttonExit = new TextButton("Beenden", skin);
		buttonExit.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				Gdx.app.exit();
			}
		});
		buttonExit.pad(15);
				/*	table.add(new Label("Levelauswahl", skin, "big")).colspan(3).expandX().spaceBottom(50).row();
					table.add(scrollPane).uniformX().expandY().top().left();
					table.add(play).uniformX();
					table.add(back).uniformX().bottom().right();	
				*/
		//putting stuff together
		table.add(heading).spaceBottom(100).row(); //100 abstand, neue zeile
		table.add(buttonPlay).spaceBottom(15).row();
		table.add(buttonOption).spaceBottom(15).row();
		table.add(buttonHighscore).spaceBottom(15).row();
		table.add(buttonExit);
//table.debug();            // case debuglines needed 2/2
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
		
		tweenManager.update(Gdx.graphics.getDeltaTime());
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
		atlas.dispose();
		skin.dispose();
	}

}
