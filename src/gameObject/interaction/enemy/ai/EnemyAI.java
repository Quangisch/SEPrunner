package gameObject.interaction.enemy.ai;

import gameObject.interaction.enemy.Enemy;

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
