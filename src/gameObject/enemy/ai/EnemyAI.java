package gameObject.enemy.ai;

import gameObject.enemy.Enemy;

public abstract class EnemyAI implements IEnemyAI {

	protected Enemy link;
	
	@Override
	public Enemy getEnemy() {
		return link;
	}

	@Override
	public void setEnemy(Enemy enemy) {
		link = enemy;
	}

}
