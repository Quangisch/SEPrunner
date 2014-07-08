package core.menu;

import gameWorld.GameWorld;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

import core.GameProperties;
import core.GameProperties.GameScreen;
import core.ingame.Camera;
import core.ingame.IDrawable;
import core.ingame.input.IInputHandler;
import core.ingame.input.player.InputHandler.Click;
import core.ingame.input.player.KeyMap.ActionKey;

public class AnimatedBackground implements IInputHandler, Runnable, Disposable, IDrawable {
	
	private static AnimatedBackground background;

	private GameWorld world;
	private List<WanderingLight> wLights;
	
	private final int MIN_X, MAX_X, MIN_Y, MAX_Y, MIN_DISTANCE, MAX_DISTANCE;
	private final int NUM_LIGHT = 4;
	
	private AnimatedBackground() {
		world = new GameWorld(GameScreen.MENU_BACKGROUND, this, new Camera());
		
		MIN_X = (int)-world.getWidth()/2; 
		MAX_X = (int)world.getWidth(); 
		MIN_Y = (int)(world.getHeight() * 0.4f); 
		MAX_Y = (int)(world.getHeight() * 0.8f);
		MIN_DISTANCE = 100;
		MAX_DISTANCE = 500;
		
		world.moveMapTextures(0, -200);
		
		if(Gdx.graphics.isGL20Available()) {
			
			new PointLight(world.getRayHandler(), 64, new Color(1,1,0.3f,0.7f), 1000, 0, 300);
			wLights = new LinkedList<AnimatedBackground.WanderingLight>();
			
			for(int i = 0; i < NUM_LIGHT; i++)
				wLights.add(new WanderingLight(world.getRayHandler(), 32));
		}

	}

	public void draw(SpriteBatch batch, float delta) {
		batch.setProjectionMatrix(world.getCamera().combined);
		world.draw(batch, delta);
		run();
	}
	
	private int moved;
	private boolean moveZero = false;
	private int moveSpeed = 1;
	
	public void run() {
		world.run();
		world.step(Gdx.graphics.getDeltaTime(), 6, 4);
		world.getCamera().update();
		world.getCamera().zoom = 0.8f;
		
		
		if(Gdx.graphics.isGL20Available())
			for(WanderingLight l : wLights)
				l.run();
		
		if(moved > world.getWidth()/2) {
			world.moveMapTextures(moveZero ? 1 : 0, moved*2, 0);
			moved = 0;
			moveZero = !moveZero;
		}
		
		world.moveMapTextures(-moveSpeed, 0);
		moved += moveSpeed;
		
	}
	
	public void updateAndRenderRays() {
		world.getRayHandler().updateAndRender();
	}

	
	@Override
	public void addActionKey(ActionKey action, int... keys) {
	}

	@Override
	public boolean isKeyDown(ActionKey action) {
		switch(GameProperties.gameScreen) {
		case LEVEL1:
		case LEVEL2:
		case LEVEL3:
		case MENU_BACKGROUND:
			return false;


		case MENU_SPLASH:
		case MENU_MAIN:
			moveSpeed = 3;
			return action.equals(ActionKey.RIGHT);

		case MENU_HIGHSCORE:
		case MENU_LEVELSELECT:	
			moveSpeed = 10;
			return  action.equals(ActionKey.RIGHT) || action.equals(ActionKey.RUN);

		case MENU_PROFILE:
		case MENU_OPTION:
			moveSpeed = 1;
			return action.equals(ActionKey.RIGHT) || action.equals(ActionKey.CROUCH);
			
		default:
			return false;
		
		}
	}

	@Override
	public boolean isButtonDown(ActionKey action) {
		return false;
	}

	@Override
	public boolean keyUp(ActionKey action) {
		return false;
	}

	@Override
	public void keyDown(ActionKey action) {
	}

	@Override
	public Click popClick() {
		return null;
	}

	@Override
	public Click getClick() {
		return null;
	}
	
	@Override
	public boolean isKeyDown(int[] keys) {
		return false;
	}
	
	public void dispose() {
		world.dispose();
	}
	
	public static AnimatedBackground getInstance() {
		if(background == null)
			background = new AnimatedBackground();
		return background;
	}
	
	private class WanderingLight extends PointLight implements Runnable {
		private WanderingLight(RayHandler handler, int rays) {
			super(handler, rays);
			int x = new Random().nextInt(MAX_X)+MIN_X;
			int y = new Random().nextInt(MAX_Y-MIN_Y)+MIN_Y;
			int distance = new Random().nextInt(MAX_DISTANCE-MIN_DISTANCE)+MIN_DISTANCE;
			Color c = new Color(new Random().nextFloat(), 
								new Random().nextFloat(), 
								new Random().nextFloat(), 0.6f);
			
			this.setColor(c);
			this.setPosition(x, y);
			this.setDistance(distance);
		}
		
		private final int FLICKER = 2;
		public void run() {
			setPosition(getX() < MIN_X ? MAX_X : getX()-distance/200, getY());
			float distance = getDistance() + (new Random().nextBoolean() ? FLICKER : -FLICKER);
			distance = Math.min(distance, MAX_DISTANCE);
			distance = Math.max(distance, MIN_DISTANCE);
			setDistance(distance);
		}
		
	}

}
