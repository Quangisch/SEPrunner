package gameObject;

public interface IInteractionStates {

	enum InteractionState {
		STAND(true), WALK(true), RUN(true),

		CROUCH_STAND(true), CROUCH_DOWN(false), CROUCH_SNEAK(true),

		JUMP(true, "WALK"), JUMP_MOVE(true, "WALK"),

		HIDE_START(false), HIDE(true), THROW(false),

		FLY(true), HOOK(false, "THROW"),

		GRAB(true), GRAB_PULL(true), GRAB_DISPOSE(false),

		STUNNED(true);

		private String animation;
		private int animationIndex;
		private boolean interruptable;
		
		InteractionState(boolean interruptable) {
			this.interruptable = interruptable;
			this.animation = this.toString();
		}
		
		InteractionState(boolean interruptable, String animation) {
			this(interruptable);
			this.animation = animation;
		}

		public String getAnimation() {
			return animation;
		}
		
		public int getAnimationIndex() {
			return animationIndex;
		}
		
		public void setAnimationIndex(int animationIndex) {
			this.animationIndex = animationIndex;
		}
		
		public boolean isInterruptable() {
			return interruptable;
		}
	}
}
