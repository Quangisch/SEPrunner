package gameObject.enemy.ai;

import com.badlogic.gdx.utils.JsonValue;

import core.ingame.IPlayerInput;
import gameObject.GameObject;
import gameObject.Sensor;
import gameObject.enemy.Enemy;

public interface IEnemyAI extends IPlayerInput, Runnable {

	public boolean handleCollision(boolean start, Sensor mySender, GameObject other, Sensor otherSensor);

	public Enemy getEnemy();

	public void setEnemy(Enemy enemy);

	public void init(JsonValue jsonValue);
}
