package gameObject.interaction.enemy.ai;

import gameObject.body.BodyObject;
import gameObject.body.Sensor;
import gameObject.interaction.enemy.Enemy;

import com.badlogic.gdx.utils.JsonValue;

import core.ingame.input.IInputHandler;

public interface IEnemyAI extends IInputHandler, Runnable {

	public boolean handleCollision(boolean start, Sensor mySender, BodyObject other, Sensor otherSensor);

	public Enemy getEnemy();

	public void setEnemy(Enemy enemy);

	public void init(JsonValue jsonValue);
}
