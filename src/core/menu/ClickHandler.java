package core.menu;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import core.GameProperties;

public class ClickHandler extends ClickListener {
	private List<Actor> hoverList = new CopyOnWriteArrayList<Actor>();
	private List<Actor> hoverCandidateList = new LinkedList<Actor>();
	
	final public static Color COLOR_NORMAL = new Color(1,1,0.90f,1);
	final public static Color COLOR_HOVER = new Color(1,1,0f,1);
	final public static Color COLOR_SELECT = new Color(1,0.8f,0,1);
	final private Stage STAGE;
	
	public ClickHandler(Stage stage) {
		this.STAGE = stage;
	}
	
	protected void addToListener(Actor a) {
		a.addListener(this);
		a.setTouchable(Touchable.enabled);
		hoverCandidateList.add(a);
		a.setColor(COLOR_NORMAL);
	}
	
	protected void resetLabel(Actor label) {
		if(label != null) {
			label.setColor(COLOR_NORMAL);
			hoverList.remove(label);
		}
		
	}
	
	protected void setAsSelected(Actor a) {
		a.setColor(COLOR_SELECT);
	}
	
	public boolean mouseMoved(InputEvent event, float x, float y) {
		for(Actor h : hoverList) {
			if(h.getColor().equals(COLOR_HOVER))
				resetLabel(h);
		}
		
		Actor a = STAGE.hit(x, y, true);
		if(a != null && hoverCandidateList.contains(a) && !a.getColor().equals(COLOR_SELECT)) {
			hoverList.add(a);
			a.setColor(COLOR_HOVER);
		} 
	
		return false;
	}
	
	public boolean keyDown(InputEvent event, int keycode) {
		if(keycode == Keys.BACKSPACE) {
			Gdx.app.postRunnable(new GameProperties.GameScreenSwitcher(GameProperties.GameScreen.MENU_MAIN));
		} else if(keycode == Keys.ESCAPE)
			GameProperties.toogleFullScreen();
		return false;
	}
	
	protected void setBackButton(Skin skin) {
		TextButton button = new TextButton("Back", skin);
		button.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				GameProperties.switchGameScreen(GameProperties.GameScreen.MENU_MAIN);
			}
		});
		button.pad(10);
		button.setPosition(GameProperties.SCALE_WIDTH/10, GameProperties.SCALE_HEIGHT/10);
		STAGE.addActor(button);
	}
	
	
}