package gameObject.interaction;

import gameWorld.GameWorld;

public interface IGameObject {

	GameWorld getGameWorld();
	
	boolean decShuriken();
	
	int getShurikenQuantity();
	
}
