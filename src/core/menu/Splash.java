package core.menu;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import core.Project;
import core.menu.tween.SpriteAccessor;

public class Splash implements Screen{

	private SpriteBatch batch;
	private Sprite splash;
	private TweenManager tweenManager;
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		splash.draw(batch);
		batch.end();
		
		tweenManager.update(delta);
		AnimatedBackground.getInstance().run();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {

		batch = new SpriteBatch();
		//apply preferences
		Gdx.graphics.setVSync(Gdx.app.getPreferences(Project.TITLE).getBoolean("vsync"));
		
		tweenManager = new TweenManager();
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());
		
		splash = new Sprite(new Texture(Gdx.files.internal("res/img/splash.png")));
		
		splash.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getWidth() * splash.getHeight()/splash.getWidth());
		splash.setY((Gdx.graphics.getHeight()-splash.getHeight())/2);
		
		Tween.set(splash, SpriteAccessor.ALPHA).target(0).start(tweenManager);
		Tween.to(splash, SpriteAccessor.ALPHA, 2).target(1).repeatYoyo(1,.25f).setCallback(new TweenCallback(){
			public void onEvent(int type, BaseTween<?> source){
				((Game) Gdx.app.getApplicationListener()).setScreen(new MenuMain());  //link to 2nd img
			}
		}).start(tweenManager);
		
		tweenManager.update(Float.MIN_VALUE);
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
		batch.dispose();
		splash.getTexture().dispose();
	}

}
