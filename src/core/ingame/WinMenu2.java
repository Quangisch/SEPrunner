package core.ingame;

import gameObject.interaction.enemy.Alarm;
import gameWorld.GameWorld;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import core.GameProperties;
import core.Highscore;
import core.Highscore.Score;

public class WinMenu2 implements Screen {
//	TODO
	private GameWorld world;
	private Stage stage;
	private Table mainTable, scoreTable, highscoreTable, pointsTable, bonusTable;
	private ScrollPane scrollPane;
	private Skin skin;
	private ClickHandler clickHandler;
	
	final private Color NORMAL = new Color(1,1,0.90f,1);
	final private Color HOVER = new Color(1,1,0f,1);
	private int width, height;
	
	public WinMenu2(GameWorld world) {
		this.world = world;
		show();
	}
	
	public void show() {
		stage = new Stage();
		width = GameProperties.SCALE_WIDTH;
		height = GameProperties.SCALE_HEIGHT;
		stage.setViewport(GameProperties.SCALE_WIDTH*2, GameProperties.SCALE_HEIGHT*2, true);
		
		clickHandler = new ClickHandler();
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
		
		initScoreTable();
		
		scrollPane = new ScrollPane(scoreTable, skin);
		mainTable.add(new Label("Level Cleared", skin, "baoli96", Color.WHITE)).colspan(3).row();
		mainTable.add(pointsTable);
		mainTable.add().size(width/10, 0);
		mainTable.add(highscoreTable);
		
		pointsTable.add(new Label("StylePoints", skin, "baoli64", Color.WHITE)).colspan(4).row();
		
		pointsTable.add("Hidden from Enemy").left();
		pointsTable.add(world.getPlayer().getUnseenFrom()+"");
		pointsTable.add("x");
		pointsTable.add(GameProperties.P_HIDDEN_MUL+"").row();
		
		pointsTable.add("Hidden Bodies").left();
		pointsTable.add(world.getPlayer().getEnemiesHidden()+"");
		pointsTable.add("x");
		pointsTable.add(GameProperties.P_DISPOSED_MUL+"").row();
		
		pointsTable.add("Total Alarm Time").left();
		pointsTable.add((int)Alarm.getTotalAlarmTime()+"");
		pointsTable.add("x");
		pointsTable.add(GameProperties.P_ALARM_MUL+"").row();
		
		pointsTable.add(bonusTable).left().colspan(4);
		
		bonusTable.add("Bonus").left().row();
		if(Alarm.getTotalAlarmTime() <= 0) {
			bonusTable.add("Unseen").left();
			bonusTable.add(GameProperties.P_UNSEEN+"").row();
		}
		if(world.getPlayer().getShurikenThrown() <= 0) {
			bonusTable.add("Untouched").left();
			bonusTable.add(GameProperties.P_WITHOUT_SHURIKENS+"").row();
		}
		bonusTable.add("Remaining Time").left();
		bonusTable.add((int)(world.getTimeLimit()-world.getTime())+"").row();
		bonusTable.add("Level Cleared").left();
		bonusTable.add(GameProperties.P_LEVEL_COMPLETE+"").row();
		
		int earnedPoints = world.getPlayer().getEnemiesHidden()*GameProperties.P_DISPOSED_MUL 
				+ world.getPlayer().getUnseenFrom() * GameProperties.P_HIDDEN_MUL 
				+ (int)(world.getTimeLimit()-world.getTime())
				+ GameProperties.P_LEVEL_COMPLETE;
		
		if(Alarm.getTotalAlarmTime() <= 0)
			earnedPoints += GameProperties.P_UNSEEN;
		if(world.getPlayer().getShurikenThrown() == 0)
			earnedPoints += GameProperties.P_WITHOUT_SHURIKENS;
		
		bonusTable.add("Earned Stylepoints").left().padLeft(width/5).padTop(height/10);
		bonusTable.add(earnedPoints+"").padTop(height/10);
		
		highscoreTable.add(new Label("Highscore", skin, "baoli64", Color.WHITE)).colspan(3).row();
		highscoreTable.add(new Label("Rank", skin, "baoli44", Color.WHITE));
		highscoreTable.add(new Label("Name", skin, "baoli44", Color.WHITE));
		highscoreTable.add(new Label("Time", skin, "baoli44", Color.WHITE)).row();
		highscoreTable.add(scrollPane).colspan(3);
	}
	
	private void initScoreTable() {
		int rank = 0;
		for(Score s : Highscore.getHighscoreList(GameProperties.gameScreen.INDEX)) {
			scoreTable.add(rank+++"");
			scoreTable.add(s.PLAYER_NAME);
			scoreTable.add(s.TIME_STRING).row();
			System.out.println(s.PLAYER_NAME);
		}
	}

	public void dispose() {
		skin.dispose();
		stage.dispose();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);	//black background
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(delta);
		stage.draw();
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
			System.out.println("click");
		}
		

		
	}
	
}
