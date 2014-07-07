package core.ingame.input.interaction;

public interface IInteractionHandler extends Runnable {

	boolean isOnlyCrouching();
	void setForceMultiplier(float walkMul, float runMul, float sneakMul, float pullMull);
	
}
