package gameObject.enemy.ai;

import gameObject.BodyObject;
import gameObject.Sensor;
import gameObject.enemy.Enemy;

import com.badlogic.gdx.utils.JsonValue;

import core.ingame.IInputHandler;

public interface IEnemyAI extends IInputHandler, Runnable {

	public boolean handleCollision(boolean start, Sensor mySender, BodyObject other, Sensor otherSensor);

	public Enemy getEnemy();

	public void setEnemy(Enemy enemy);

	public void init(JsonValue jsonValue);
}
