package core.ingame.input.ai;

import gameObject.interaction.ICollisionable;
import gameObject.interaction.enemy.Enemy;

import com.badlogic.gdx.utils.JsonValue;

import core.ingame.input.IInputHandler;

public interface IEnemyAI extends IInputHandler, Runnable, ICollisionable {

	public Enemy getEnemy();

	public void setEnemy(Enemy enemy);

	public void init(JsonValue actions, JsonValue advanced);
	
}
