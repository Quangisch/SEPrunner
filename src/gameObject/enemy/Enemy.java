package gameObject.enemy;

import javax.swing.JFrame;

import gameObject.GameObject;
import gameObject.Sensor;
import gameObject.IGameObjectTypes.GameObjectTypes;
import gameObject.ISensorTypes.SensorTypes;
import gameObject.player.InputHandler;
import gameObject.player.Player;
import gameObject.statics.Hideable;
import gameWorld.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.Shape.Type;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import core.ingame.Camera;
import core.ingame.GameProperties;

public class Enemy extends EnemyAI implements Runnable, Stunnable {
	
	protected boolean hasDetected;
	protected boolean Stunned;
	protected float sichtfeldX = 250;
	protected float sichtfeldY = 150;
	
	public Enemy(World world, Vector2 position) {
		super(world, position);
		//Camera.getInstance().setToFollowMoveable(this);
		// TODO Auto-generated constructor stub
	}
	
	public static Enemy getInstance() {
		return Map.getInstance().getEnemy();
	}
	
	//KI
	public void moveEnemy(Enemy enemy, Player player, SpriteBatch batch, float beginX, float beginY, float endX, float endY){
		Vector2 baseForce = new Vector2(0, 0);
		
		if(!hasDetected){
			if(enemy.getX()<endX && enemy.isFlipped()==false){
				baseForce.add(1, 0);//rechts
				setFlip(false);
			}
			if(enemy.getX()<endX && enemy.isFlipped()==true){
				baseForce.add(-1, 0);//links
				setFlip(true);
			}
			if(enemy.getX()>=endX){
				baseForce.add(-1,0);//links
				setFlip(true);
			}
			if(enemy.getX()<=beginX){
				baseForce.add(1,0);//rechts
				setFlip(false);
			}
			
			if(!Stunned){
				enemy.setCurrentState(1);
				//sichtfeld zeichnen
				if(!enemy.isFlipped()){
					Texture sichtfeld = new Texture(Gdx.files.internal("res/sprites/sichtfeld.png"));
					batch.draw(sichtfeld,enemy.getX()+100,enemy.getY()+30,sichtfeldX-50,sichtfeldY);
				}else{
					Texture sichtfeld = new Texture(Gdx.files.internal("res/sprites/sichtfeld180.png"));
					batch.draw(sichtfeld,enemy.getX()-sichtfeldX+60+30,enemy.getY()+30,sichtfeldX-50,sichtfeldY);
				}
				//
				enemy.draw(batch);
				enemy.body.applyLinearImpulse(baseForce.scl(2),enemy.body.getWorldCenter(), true);
				
				//player in sichtfeld?
				if(inSichtfeld(enemy, player, sichtfeldX, sichtfeldY)){
					hasDetected = true;
				}
			}else{//dazu muss stunned vom player gesetzt worden sein (z.B. durch shuriken)
				enemy.setCurrentState(0);//TODO auf state stunned setzen
				enemy.draw(batch);
			}
		//detectionHandler
		}else{
				System.out.println("GAME OVER");
				enemy.setCurrentState(0);//enemy bleibt stehen, wenn spieler erkannt
				//sichtfeld zeichnen
				if(!enemy.isFlipped()){
					Texture sichtfeld = new Texture(Gdx.files.internal("res/sprites/sichtfeld.png"));
					batch.draw(sichtfeld,enemy.getX()+100,enemy.getY()+30,sichtfeldX-50,sichtfeldY);
				}else{
					Texture sichtfeld = new Texture(Gdx.files.internal("res/sprites/sichtfeld180.png"));
					batch.draw(sichtfeld,enemy.getX()-sichtfeldX+60+30,enemy.getY()+30,sichtfeldX-50,sichtfeldY);
				}
				//
				enemy.draw(batch);
				//TODO player cant move
				//player.setCurrentState(0);
				//player.draw(batch);
				player.setCaptured(enemy);
				//TODO popup window mit "GAME OVER"
				//TODO spiel beenden
//				Dialog dialog = new Dialog("Game Over", skin, "dialog")
//				dialog.show(stage);
						}
		
	}
	
	//player in sichtfeld? Vorsicht: in y: +50 damit von der mitte von enemy aus geprüft wird
	private boolean inSichtfeld(Enemy enemy, Player player, float x, float y){
		if(((((enemy.getX()-x < player.getX()) && (player.getX() < enemy.getX())) 
				&& (enemy.isFlipped()))
				|| (((enemy.getX()+x > player.getX()) && (player.getX() > enemy.getX())) 
				&& (!enemy.isFlipped()))) && 
				((enemy.getY()+50-y/2 < player.getY()+50) && (player.getY()+50 < enemy.getY()+50+y/2))){
			return true;
		}else{
			return false;
		}
	}
	
	public void run() {
		processInput();
	}
	
	private void processInput() {

		Vector2 baseForce = new Vector2(0, 0);

		//		basic movement
		if (InputHandler.getInstance().isKeyDown(GameProperties.keyRight)) {
			baseForce.add(1, 0);
			setFlip(false);
			setCurrentState(1);
		} else if (InputHandler.getInstance().isKeyDown(GameProperties.keyLeft)) {
			baseForce.add(-1, 0);
			setFlip(true);
			setCurrentState(1);
		} else if (!isGrounded())
			setCurrentState(1);
		else
			setCurrentState(0);

		//		tweak gravity
		if (baseForce.len() != 0)
			body.setGravityScale(0.7f);
		else
			body.setGravityScale(1);

		//		run
		if ((getCurrentState() == 1 || getCurrentState() == 3)
				&& InputHandler.getInstance().isKeyDown(GameProperties.keyRun) && isGrounded()) {
			setCurrentState(3);
			baseForce.scl(1.7f);
		}

		//		jump
		if (InputHandler.getInstance().isKeyDown(GameProperties.keyJump) && isGrounded()) {
			setGrounded(false);
			body.applyLinearImpulse(new Vector2(body.getLocalCenter().x, body.getLocalCenter().y + 100),
					body.getWorldCenter(), true);
			setCurrentState(0);
		}

		//		apply impulse
		body.applyLinearImpulse(baseForce.scl(isGrounded() ? 2 : 1.5f), body.getWorldCenter(), true);

	}
	
	@Override
	public void init(String name) {
		super.init(name);
		setGameObjectType(GameObjectTypes.ENEMY);
		body.setLinearDamping(2.5f);
		body.setFixedRotation(true);
		float[] vertices = { 0.5f, 0.3f, 0.8f, 0.3f, 0.8f, 0.4f, 0.5f, 0.4f };
		addSensor(new Sensor(this, Type.Polygon, vertices, SensorTypes.FOOT, Sensor.HANDLE_FIRST));
	}
	
	public boolean handleCollision(Sensor mySensor, GameObject other, Sensor otherSensor) {
		//player nur nicht entdeckt, wenn player den rücken von enemy berührt
		if(other.getGameObjectType() == GameObjectTypes.PLAYER && 
				(other.isFlipped() && this.isFlipped() && (this.getX() < other.getX())) 
				|| (!other.isFlipped() && !this.isFlipped() && (this.getX() > other.getX()))){	
			//hasDetected = false;
		}else if(other.getGameObjectType() == GameObjectTypes.PLAYER){
			hasDetected = true;
		}
		
		if (mySensor != null) {
			if (mySensor.getSensorType() == SensorTypes.FOOT && other.getGameObjectType() == GameObjectTypes.GROUND) {
				setGrounded(true);
				return true;
			}
		}
		return false;
	}


	@Override
	public boolean isStunned() {
		// TODO Auto-generated method stub
		return Stunned;
	}

	@Override
	public boolean isCarriable(Vector2 position) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setStun() {
		// TODO Auto-generated method stub
		Stunned = true;
	}

	@Override
	public void attachToCarrier(Player player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void detachFromCarrier(Player player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean disposeAndHide(Hideable hideable) {
		// TODO Auto-generated method stub
		return false;
	}

	
}
