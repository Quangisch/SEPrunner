package gameObject;

public interface IInteractionStates {

	enum InteractionState {
		STAND, WALK, RUN,

		CROUCH_STAND, CROUCH_SNEAK,

		JUMP("WALK"), JUMP_MOVE("WALK"),

		HIDE, THROW,

		HOOK_START("THROW"), HOOK_FLY,

		GRAB, GRAB_PULL, GRAB_DISPOSE,

		STUNNED;

		private String animation;
		private int animationIndex;

		InteractionState() {
			this.animation = this.name();
			animationIndex = 0;
		}

		InteractionState(String animation) {
			this.animation = animation;
			animationIndex = 0;
		}

		public String getAnimation() {
			return animation;
		}

		protected void setAnimationIndex(int animationIndex) {
			this.animationIndex = animationIndex;
		}

		public int getAnimationIndex() {
			return animationIndex;
		}
	}
}
