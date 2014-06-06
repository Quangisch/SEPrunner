package core.ingame;

import gameObject.IAnimatedDrawable;
import gameObject.body.BodyObject;
import gameObject.body.GameObjectType;
import gameObject.body.ICollisionable;
import gameObject.body.Sensor;
import gameObject.interaction.InteractionState;
import gameObject.interaction.player.Shuriken;
import gameWorld.GameWorld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;

public class HUD implements IAnimatedDrawable {

	private GameWorld world;
	private SpriteBatch b;
	BitmapFont font;
	Shu shu;

	public HUD(GameWorld world) {
		this.world = world;
		b = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("res/font/black64.fnt"), //
				Gdx.files.internal("res/font/black64_0.png"), false);
		font.setScale(0.4f);
		shu = new Shu(new Vector2(0, Gdx.graphics.getHeight() - 30));
	}

	@Override
	public void draw(SpriteBatch batch, float deltaTime) {
		batch.end();
		b.setProjectionMatrix(b.getProjectionMatrix().setToOrtho2D(0, 0, //
				Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		b.begin();

		font.draw(b, String.valueOf(world.getPlayer().getShuriken()), 30, Gdx.graphics.getHeight() - 5);
		shu.draw2(b, deltaTime);

		b.end();
		batch.begin();
	}

	private class Shu extends Shuriken {

		public Shu(Vector2 point) {
			super(new Dummy(GameProperties.pixelToMeter(point)), //
					GameProperties.pixelToMeter(point));
		}

		@Override
		public void draw(SpriteBatch batch, float deltaTime) {}

		public void draw2(SpriteBatch batch, float deltaTime) {
			super.draw(batch, deltaTime * .2f);
		}

		@Override
		public boolean handleCollision(boolean start, Sensor mySensor, BodyObject other, Sensor otherSensor) {
			return false;
		}

		@Override
		public void run() {}

		@Override
		public void applyImpulse(Vector2 impulse) {}
	}

	private class Dummy implements ICollisionable {

		private Vector2 pos;

		public Dummy(Vector2 pos) {
			this.pos = pos;
		}

		@Override
		public boolean handleCollision(boolean start, Sensor mySensor, BodyObject other, Sensor otherSensor) {
			return false;
		}

		@Override
		public float getX() {
			return 0;
		}

		@Override
		public float getY() {
			return 0;
		}

		@Override
		public GameWorld getGameWorld() {
			return world;
		}

		@Override
		public Vector2 getWorldPosition() {
			return null;
		}

		@Override
		public Vector2 getPosition() {
			return null;
		}

		@Override
		public Vector2 getLocalCenterInWorld() {
			return pos;
		}

		@Override
		public void applyImpulse(Vector2 impulse) {}

		@Override
		public Fixture setFixture(float density, float friction,
				float restitution, boolean sensor, Shape shape,
				boolean disposeShape) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Fixture addFixture(float density, float friction,
				float restitution, boolean sensor, Shape shape,
				boolean disposeShape) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Fixture addFixture(FixtureDef fixtureDef) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void addSensor(Sensor sensor) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean removeSensor(Sensor sensor) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public GameObjectType getGameObjectType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setGameObjectType(GameObjectType gameObjectType) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setGravityScale(float scale) {
			// TODO Auto-generated method stub
			
		}
	}

	@Override
	public void draw(SpriteBatch batch, TextureRegion textureRegion) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setVisible(boolean visible) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void flip() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFlip(boolean flip) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isFlipped() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getLayer() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setLayer(int layer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getAlpha() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAlpha(float alpha) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getScale() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setScale(float scale) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean applyAnimation(InteractionState currentState) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addAnimation(InteractionState state, Animation animation) {
		// TODO Auto-generated method stub
		
	}
}
