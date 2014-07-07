package core.ingame;

import gameObject.interaction.enemy.Alarm;
import gameWorld.GameWorld;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import core.GameProperties;
import core.Highscore;
import core.GameProperties.GameScreen;
import core.Highscore.Score;

public class WinMenu implements Screen {
//	TODO
	private GameWorld world;
	private Stage stage;
	private Table mainTable, scoreTable, highscoreTable, pointsTable, bonusTable, buttonTable;
	private Label label_continue, label_retry, label_menu;
	private ScrollPane scrollPane;
	private Skin skin;
	private ClickHandler clickHandler;
	
	final private Color NORMAL = new Color(1,1,0.90f,1);
	final private Color HOVER = new Color(1,1,0f,1);
	private int width, height;
	private SpriteBatch batch;
	
	public WinMenu(GameWorld world, SpriteBatch batch) {
		this.world = world;
		show();
	}
	
	public void show() {
		width = GameProperties.SCALE_WIDTH;
		height = GameProperties.SCALE_HEIGHT;
		world.getPlayer().saveHighscore();
		
		stage = new Stage(10,10, true, batch);
		stage.setCamera(world.getCamera());
		
		clickHandler = new ClickHandler();
		world.getCamera().zoom = 3;
		Gdx.input.setInputProcessor(stage);
		
		skin = new Skin(Gdx.files.internal("res/ui/menuSkin.json"), new TextureAtlas(Gdx.files.internal("res/ui/atlas.pack")));
		mainTable = new Table(skin);
		mainTable.setFillParent(true);
		
		stage.addActor(mainTable);
		stage.addListener(clickHandler);
		
		pointsTable = new Table(skin);
		bonusTable = new Table(skin);
		scoreTable = new Table(skin);
		highscoreTable = new Table(skin);
		buttonTable = new Table(skin);
		
		scrollPane = new ScrollPane(scoreTable, skin);
		mainTable.add(new Label("Level Cleared", skin, "baoli96", Color.WHITE)).colspan(3).padBottom(height/10).row();
		mainTable.add(pointsTable);
		mainTable.add().size(width/10, 0);
		mainTable.add(highscoreTable).row();
		mainTable.add(buttonTable).colspan(3);
		
		
		pointsTable.add(new Label("StylePoints", skin, "baoli44", Color.WHITE)).colspan(4).row();
		
		pointsTable.add(new Label("Hidden from Enemy", skin, "baoli32", Color.WHITE)).left();
		pointsTable.add(new Label(world.getPlayer().getUnseenFrom()+"", skin, "baoli32", Color.WHITE));
		pointsTable.add(new Label("x", skin, "baoli32", Color.WHITE));
		pointsTable.add(new Label(GameProperties.POINTS_HIDDEN_MUL+"", skin, "baoli32", Color.WHITE)).row();
		
		pointsTable.add(new Label("Hidden Bodies", skin, "baoli32", Color.WHITE)).left();
		pointsTable.add(new Label(world.getPlayer().getEnemiesHidden()+"", skin, "baoli32", Color.WHITE));
		pointsTable.add(new Label("x", skin, "baoli32", Color.WHITE));
		pointsTable.add(new Label(GameProperties.POINTS_DISPOSED_MUL+"", skin, "baoli32", Color.WHITE)).row();
		
		pointsTable.add(new Label("Total Alarm Time", skin, "baoli32", Color.WHITE)).left();
		pointsTable.add(new Label((int)Alarm.getTotalAlarmTime()+"", skin, "baoli32", Color.WHITE));
		pointsTable.add(new Label("x", skin, "baoli32", Color.WHITE));
		pointsTable.add(new Label(GameProperties.POINTS_ALARM_MUL+"", skin, "baoli32", Color.WHITE)).row();
		
		pointsTable.add(bonusTable).left().colspan(4);
		
		bonusTable.add(new Label("Bonus", skin, "baoli32", Color.WHITE)).left().row();
		if(Alarm.getTotalAlarmTime() <= 0) {
			bonusTable.add(new Label("Unseen", skin, "baoli32", Color.WHITE)).left();
			bonusTable.add(new Label(GameProperties.POINTS_UNSEEN+"", skin, "baoli32", Color.WHITE)).row();
		}
		if(world.getPlayer().getShurikenThrown() <= 0) {
			bonusTable.add(new Label("Untouched", skin, "baoli32", Color.WHITE)).left();
			bonusTable.add(new Label(GameProperties.POINTS_WITHOUT_SHURIKENS+"", skin, "baoli32", Color.WHITE)).row();
		}
		bonusTable.add(new Label("Remaining Time", skin, "baoli32", Color.WHITE)).left();
		bonusTable.add(new Label((int)(world.getTimeLimit()-world.getTime())+"", skin, "baoli32", Color.WHITE)).row();
		bonusTable.add(new Label("Earned Stylepoints ", skin, "baoli44", Color.YELLOW)).left().padLeft(width/10).padTop(height/10);
		bonusTable.add(new Label(GameProperties.calcStylePoints(world)+"", skin, "baoli44", Color.YELLOW)).padTop(height/10);
		
		highscoreTable.add(new Label("Highscore", skin, "baoli64", Color.WHITE)).colspan(3).row();
		highscoreTable.add().size(width/10, 0);
		highscoreTable.add().size(width/3, 0);
		highscoreTable.add().size(width/4, 0).row();
		highscoreTable.add(new Label("Rank", skin, "baoli32", Color.WHITE));
		highscoreTable.add(new Label("Name", skin, "baoli32", Color.WHITE));
		highscoreTable.add(new Label("Time", skin, "baoli32", Color.WHITE)).row();
		highscoreTable.add(scrollPane).colspan(3);

		addToListener(label_continue = new Label("Continue", skin, "baoli44", Color.WHITE));
		addToListener(label_retry = new Label("Retry", skin, "baoli44", Color.WHITE));
		addToListener(label_menu = new Label("Back To Menu", skin, "baoli44", Color.WHITE));
		
		
		
		buttonTable.add(label_retry);
		buttonTable.add(label_menu).pad(height/10, width/3, height/10, width/3);
		buttonTable.add(label_continue);
		
		initScoreTable();
		mainTable.setBounds(stage.getCamera().position.x, stage.getCamera().position.y, width, height);
		world.getPlayer().saveProfile();
	}
	
	private void addToListener(Actor a) {
		a.setTouchable(Touchable.enabled);
		a.addListener(clickHandler);
		clickHandler.hoverCandidateList.add(a);
	}
	
	private float scrollToY = 0;
	private void initScoreTable() {
		int rank = 1;
		int i = 0;
		
		Score ps = world.getPlayer().getScore();
		Color c = Color.WHITE;
		
		for(Score s : Highscore.getHighscoreList(GameProperties.gameScreen.INDEX)) {
			c = s.equals(ps) ? Color.YELLOW : Color.WHITE;
			
			scoreTable.add(new Label(rank+++"", skin, "baoli32", c));
			scoreTable.add(new Label(s.PLAYER_NAME, skin, "baoli32", c)).pad(0, width/30, 0, width/30);
			scoreTable.add(new Label(s.TIME_STRING, skin, "baoli32", c)).row();
			
			if(s.equals(ps) && scrollToY == 0)
				scrollToY = i * new Label("+", skin, "baoli32", Color.WHITE).getHeight();
			i++;
		}
		
	}

	public void dispose() {
		skin.dispose();
		stage.dispose();
	}

	@Override
	public void render(float delta) {
		stage.act(delta);
		stage.draw();
		
		if(scrollToY != 0) {
			scrollPane.setScrollY(scrollToY-scrollPane.getHeight()/2);
			scrollToY = 0;
		}
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void hide() {
		dispose();
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		show();
	}
	
	private class ClickHandler extends ClickListener {
		private List<Actor> hoverList = new CopyOnWriteArrayList<Actor>();
		private List<Actor> hoverCandidateList = new LinkedList<Actor>();
		
		private void resetLabel(Actor label) {
			if(label != null) {
				label.setColor(NORMAL);
				hoverList.remove(label);
			}
			
		}
		
		public boolean mouseMoved(InputEvent event, float x, float y) {
			for(Actor h : hoverList) {
				if(h.getColor().equals(HOVER))
					resetLabel(h);
			}
			
			Actor a = stage.hit(x, y, true);
			if(a != null && hoverCandidateList.contains(a)) {
				hoverList.add(a);
				a.setColor(HOVER);
			} 
		
			return false;
		}
		
		public void clicked(InputEvent event, float x, float y){
			if(event.getListenerActor() == label_continue) {
				GameProperties.switchGameScreen(GameProperties.gameScreen.getNext());
			} else if(event.getListenerActor() == label_retry) {
				GameProperties.switchGameScreen(GameProperties.gameScreen);
			} else if(event.getListenerActor() == label_menu) {
				GameProperties.switchGameScreen(GameScreen.MENU_MAIN);
			}
				
		}
	}
	
}
