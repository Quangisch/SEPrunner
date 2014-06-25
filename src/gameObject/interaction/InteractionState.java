package gameObject.interaction;

import com.badlogic.gdx.graphics.g2d.Animation;

public enum InteractionState {

	STAND(true, false, Animation.LOOP), WALK(true, false, Animation.LOOP), RUN(true, false, Animation.LOOP),

	CROUCH_STAND(true, false, Animation.LOOP), CROUCH_DOWN(false, true, Animation.NORMAL), CROUCH_SNEAK(true, false, Animation.LOOP),

	JUMP(true, false, Animation.LOOP, "WALK"), JUMP_MOVE(true, false, Animation.LOOP, "WALK"),

	HIDE_START(false, true, Animation.NORMAL), HIDE(true, true, Animation.LOOP), HIDE_END(false, true, Animation.NORMAL),

	HOOK_FLY(true, true, Animation.LOOP), HOOK(false, true, Animation.NORMAL, "THROW"), THROW(false, true, Animation.NORMAL),

	GRAB(true, true, Animation.NORMAL), GRAB_PULL(true, true, Animation.LOOP), //GRAB_DISPOSE(false, true, Animation.NORMAL, "GRAB"),
	
	GRAB_START(false, true, Animation.NORMAL), GRAB_END(false, true, Animation.NORMAL),

	STUNNED(true, true, Animation.LOOP), PULLED(true, false, Animation.LOOP),
	
	WIN(true, false, Animation.LOOP), LOSE(true, false, Animation.LOOP);
	
	private String animation;
	private final boolean interruptable, action;
	private final int playMode;
	
	InteractionState(boolean interruptable, boolean action, int playMode) {
		this.interruptable = interruptable;
		this.animation = this.toString();
		this.playMode = playMode;
		this.action = action;
	}
	
	InteractionState(boolean interruptable, boolean action, int playMode, String animation) {
		this(interruptable, action, playMode);
		this.animation = animation;
	}
	
	public String getAnimationAsString() {
		return animation;
	}
	
	public boolean isInterruptable() {
		return interruptable;
	}
	
	public boolean isAction() {
		return action;
	}
	
	public int getPlayMode() {
		return playMode;
	}
}
