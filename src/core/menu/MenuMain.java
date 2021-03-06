package core.menu;

import misc.ShaderBatch;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import core.GameProperties;
import core.GameProperties.GameScreen;
import core.Project;
import core.menu.tween.ActorAccessor;

public class MenuMain implements Screen {

	private ShaderBatch shaderBatch;
	private Stage stage;
	private Skin skin;
	private Table table; //objects get organized on here
	private TweenManager tweenManager; //tween-engine starter, stuff like fade-in/out animations
	 //textButtonStyle pushed in json-file, put in skin
	private InputHandler iHandler;
	private TextButton buttonPlay, buttonProfile, buttonOption, buttonHighscore, buttonExit; 
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);	//black background
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		shaderBatch.brightness = GameProperties.brightness; // 0.0 -> no change
		shaderBatch.contrast = GameProperties.contrast; // 1.0 -> no change
		shaderBatch.begin();					//dispose spriteBatch and backgroundSprite.getTexture()
		AnimatedBackground.getInstance().draw(shaderBatch, delta);
		shaderBatch.end();						//resize backgroundSprite.setSize(width, height);
		
		stage.act(delta); //updates table as well since thats in there
		stage.draw();
		
		tweenManager.update(delta);
		
//Table.drawDebug(stage);            // case debuglines needed 1/2
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(GameProperties.SCALE_WIDTH*2, GameProperties.SCALE_HEIGHT*2, true);
		table.invalidate();
	}

	@Override
	public void show() {

		shaderBatch = new ShaderBatch(100);		//background laden
		stage = new Stage(GameProperties.SCALE_WIDTH*2, GameProperties.SCALE_HEIGHT*2, true, shaderBatch);
		Gdx.input.setInputProcessor(stage);	//eventhandler in input, enables to push the button
		iHandler = new InputHandler(stage);
		
		skin = new Skin(Gdx.files.internal("res/ui/menuSkin.json"), new TextureAtlas(Gdx.files.internal("res/ui/atlas.pack")));
		table = new Table(skin);
		table.setFillParent(true);
		
		//creating heading
		BitmapFont f = skin.getFont("baoli96");
		f.scale(1);
		Label heading = new Label(Project.TITLE, skin, "big"); //menu.Project	size-values in res/ui.menu.Skin.json
		heading.setFontScale(1); //sizable headline
		
		
		//creating buttons
		buttonPlay = new TextButton("Play", skin);
		buttonPlay.addListener(iHandler);
		buttonPlay.pad(15);  //puffer zwischen buchstaben & buttonrand
		
		buttonProfile = new TextButton("Profile", skin);
		buttonProfile.addListener(iHandler);
		buttonProfile.pad(15);
		
		buttonOption = new TextButton("Options", skin);
		buttonOption.addListener(iHandler);
		buttonOption.pad(15);
		
		buttonHighscore = new TextButton("   Highscore   ", skin);
		buttonHighscore.addListener(iHandler);
		buttonHighscore.pad(15);
		
		buttonExit = new TextButton("Quit", skin);
		buttonExit.addListener(iHandler);
		buttonExit.pad(15);

		//putting stuff together
		table.add(heading).spaceBottom(50).colspan(3).expandX().row(); //100 abstand, neue zeile
		table.defaults().width(buttonHighscore.getWidth()+15); //setzt alle buttons auf groesse vom Highscore button
		table.add().uniformX();		table.add(buttonPlay).spaceBottom(15);			table.add().row().uniformX();
		table.add();				table.add(buttonProfile).spaceBottom(15);		table.add().row();
		table.add();				table.add(buttonOption).spaceBottom(15);		table.add().row();
		table.add();				table.add(buttonHighscore).spaceBottom(15);		table.add().row();
		table.add();				table.add(buttonExit);							table.add();
		
//table.debug();            // case debuglines needed 2/2
		
		stage.addListener(iHandler);
		stage.addActor(table);
		
		//creating animations
		tweenManager = new TweenManager();
		Tween.registerAccessor(Actor.class, new ActorAccessor());
	

		
		//heading and buttons fade-in
		Timeline.createSequence().beginSequence()
			.push(Tween.set(heading, ActorAccessor.ALPHA).target(0))
			.push(Tween.set(buttonPlay, ActorAccessor.ALPHA).target(0))
			.push(Tween.set(buttonProfile, ActorAccessor.ALPHA).target(0))
			.push(Tween.set(buttonOption, ActorAccessor.ALPHA).target(0))
			.push(Tween.set(buttonHighscore, ActorAccessor.ALPHA).target(0))
			.push(Tween.set(buttonExit, ActorAccessor.ALPHA).target(0))
			.push(Tween.to(heading, ActorAccessor.ALPHA, .3f).target(1))
			.push(Tween.to(buttonPlay, ActorAccessor.ALPHA, .15f).target(1))
			.push(Tween.to(buttonProfile, ActorAccessor.ALPHA, .15f).target(1))
			.push(Tween.to(buttonOption, ActorAccessor.ALPHA, .15f).target(1))
			.push(Tween.to(buttonHighscore, ActorAccessor.ALPHA, .15f).target(1))
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
		show();
	}

	@Override
	public void dispose() {
		stage.dispose();
		skin.dispose();
		shaderBatch.dispose();
	}
	
	private class InputHandler extends ClickHandler {
		private InputHandler(Stage stage) {
			super(stage);
		}
		
		public void clicked(InputEvent event, float x, float y){
			if(event.getListenerActor().equals(buttonPlay))
				GameProperties.switchGameScreen(GameScreen.MENU_LEVELSELECT);
			
			else if(event.getListenerActor().equals(buttonProfile))
				GameProperties.switchGameScreen(GameScreen.MENU_PROFILE);
			
			else if(event.getListenerActor().equals(buttonOption))
				GameProperties.switchGameScreen(GameScreen.MENU_OPTION);
			
			else if(event.getListenerActor().equals(buttonHighscore))
				GameProperties.switchGameScreen(GameScreen.MENU_HIGHSCORE);
			
			else if(event.getListenerActor().equals(buttonExit))
				Gdx.app.exit();
			
		}
		
		public boolean keyDown(InputEvent event, int keycode) {
			super.keyDown(event, keycode);
			switch(keycode) {
			case Keys.NUM_1:
				Gdx.app.postRunnable(new GameProperties.GameScreenSwitcher(GameScreen.MENU_LEVELSELECT));
				break;
			case Keys.NUM_2:
				Gdx.app.postRunnable(new GameProperties.GameScreenSwitcher(GameScreen.MENU_PROFILE));
				break;
			case Keys.NUM_3:
				Gdx.app.postRunnable(new GameProperties.GameScreenSwitcher(GameScreen.MENU_OPTION));
				break;
			case Keys.NUM_4:
				Gdx.app.postRunnable(new GameProperties.GameScreenSwitcher(GameScreen.MENU_HIGHSCORE));
				break;
			case Keys.NUM_5:
				Gdx.app.exit();
				break;
			}
			
			return true;
		}
	}

}
