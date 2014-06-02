package gameObject;

public interface IInteractionStates {

	enum InteractionState {
		STAND, WALK, RUN,

		CROUCH_STAND("CROUCH_DOWN", "CROUCH_STAND"), CROUCH_SNEAK,

		JUMP("WALK"), JUMP_MOVE("WALK"),

		HIDE("HIDE_START", "HIDE"), THROW,

		HOOK("THROW", "FLY"),

		GRAB, GRAB_PULL, GRAB_DISPOSE,

		STUNNED;

		private String[] animation;
		private int[] animationIndex;

		InteractionState(String... animation) {
			this.animation = animation;
			if (animation == null || animation.length <= 0) this.animation = new String[] { this.name() };
			animationIndex = new int[this.animation.length];
		}

		public String[] getAnimation() {
			return animation;
		}

		public int[] getAnimationIndex() {
			return animationIndex;
		}
	}
}
