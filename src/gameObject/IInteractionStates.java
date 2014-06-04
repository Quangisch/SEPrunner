package gameObject;

import com.badlogic.gdx.graphics.g2d.Animation;

public interface IInteractionStates {

	enum InteractionState {
		STAND(true, Animation.LOOP), WALK(true, Animation.LOOP), RUN(true, Animation.LOOP),

		CROUCH_STAND(true, Animation.LOOP), CROUCH_DOWN(false, Animation.NORMAL), CROUCH_SNEAK(true, Animation.LOOP),

		JUMP(true, Animation.LOOP, "WALK"), JUMP_MOVE(true, Animation.LOOP, "WALK"),

		HIDE_START(false, Animation.NORMAL), HIDE(true, Animation.LOOP), HIDE_END(false, Animation.NORMAL),

		HOOK_FLY(true, Animation.LOOP), HOOK(false, Animation.NORMAL, "THROW"), THROW(false, Animation.NORMAL),

		GRAB(true, Animation.NORMAL), GRAB_PULL(true, Animation.LOOP), GRAB_DISPOSE(false, Animation.NORMAL),

		STUNNED(true, Animation.LOOP);

		private String animation;
		private int animationIndex;
		private final boolean interruptable;
		private final int playMode;
		
		InteractionState(boolean interruptable, int playMode) {
			this.interruptable = interruptable;
			this.animation = this.toString();
			this.playMode = playMode;
		}
		
		InteractionState(boolean interruptable, int playMode, String animation) {
			this(interruptable, playMode);
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
		
		public int getPlayMode() {
			return playMode;
		}
	}
}
