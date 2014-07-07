package gameObject.interaction.enemy;


public interface IAlarmTriggerable {

	void trigger();
	void trigger(float time);
	float getTimer();
	boolean isActive();
	float getTotalAlarmTime();
	
}
