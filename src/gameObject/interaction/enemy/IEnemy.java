package gameObject.interaction.enemy;

import gameObject.IGameObject;
import box2dLight.Light;

import com.badlogic.gdx.utils.JsonValue;

import core.ingame.input.ai.IEnemyAI;
import core.ingame.input.interaction.InteractionHandler;

public interface IEnemy extends IGameObject {

	void setNewAI(JsonValue jAI, JsonValue jMul);
	void setAI(IEnemyAI ai);
	IEnemyAI getAI();
	InteractionHandler getInteractionHandler();
	
	void setStun();
	
	boolean scanArea(float triggerX, float triggerY);
	boolean scanArea(float triggerX, float triggerY, int scanLength, int scanSpeed);
	
	void resetView();
	Light getView();
}
