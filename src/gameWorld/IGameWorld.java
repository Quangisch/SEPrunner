package gameWorld;

import gameObject.body.BodyObject;
import gameObject.interaction.GameObject;
import gameObject.interaction.player.Player;
import box2dLight.RayHandler;

import com.badlogic.gdx.physics.box2d.World;

import core.ingame.Camera;
import core.ingame.IDrawable;

public interface IGameWorld extends IDrawable {

	void step(float timeStep, int velocityIterations, int positionIterations);
	boolean addGameObject(GameObject object);
	boolean removeGameObject(GameObject object);
	
	Player getPlayer();
	World getWorld();
	Camera getCamera();
	RayHandler getRayHandler();
	BodyObject getGoal();
	
	void moveMapTextures(int index, float dx, float dy);
	void moveMapTextures(float dx, float dy);
	float getMapTextureX(int index);
	
	float getTime();
	float getTimeLimit();
	float getWidth();
	float getHeight();
	
}
