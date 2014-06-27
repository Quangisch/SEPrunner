package gameObject.interaction.enemy.ai;

import gameObject.body.ICollisionable;
import gameObject.interaction.enemy.Enemy;

import com.badlogic.gdx.utils.JsonValue;

import core.ingame.input.IInputHandler;

public interface IEnemyAI extends IInputHandler, Runnable, ICollisionable {

	public Enemy getEnemy();

	public void setEnemy(Enemy enemy);

	public void init(JsonValue jsonValue);
}
