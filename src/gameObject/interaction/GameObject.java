package gameObject.interaction;

import gameObject.body.BodyObject;
import gameObject.body.BodyObjectType;
import gameObject.body.IIdentifiable;
import gameObject.drawable.AnimationObject;
import gameWorld.GameWorld;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public class GameObject extends ObjectInitializer 
	implements  IIdentifiable, IGameObject,
	Comparable<GameObject>, Runnable, Disposable {

	
//	TODO -> texture loading to ResourceManager

	public GameObject(GameWorld gameWorld, Vector2 position) {
		super(gameWorld);
		iniLink(new AnimationObject(position), 
				new BodyObject(gameWorld.getWorld(), position, this));
	}
	
	public void run() {
		// update position, where to draw depending on position of bodyObject
		getAnimationObject().setPosition(getBodyObject().getPosition());
	}

	@Override
	public void init(String name) {
		init(name, "res/sprites/" + name + ".json");
	}
	
	@Override
	public int compareTo(GameObject other) {
		return this.getAnimationObject().getLayer() - other.getAnimationObject().getLayer();
	}
	
	public void dispose() {
		getBodyObject().dispose();
		getGameWorld().removeGameObject(this);
//		disposeTextures(); TODO
	}

	
	@Override
	public GameWorld getGameWorld() {
		return super.getGameWorld();
	}
	
	@Override
	public AnimationObject getAnimationObject() {
		return super.getAnimationObject();
	}
	
	@Override
	public BodyObject getBodyObject() {
		return super.getBodyObject();
	}
	
	@Override
	public BodyObjectType getBodyObjectType() {
		return getBodyObject().getBodyObjectType();
	}

	@Override
	public void setBodyObjectType(BodyObjectType bodyObjectType) {
		getBodyObject().setBodyObjectType(bodyObjectType);
	}
	
	@Override
	public void removeFromGameWorld() {
		super.getGameWorld().removeGameObject(this);
		getBodyObject().destroyBody();
	}

}
