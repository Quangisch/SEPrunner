package misc;

import java.awt.geom.Line2D;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

import core.ingame.GameRender;

public class GeometricObject implements Disposable {
	
	private Texture texture;
	private float x, y;
	private int ttl = 20;

	public GeometricObject(Rectangle rect, Color color) {
		Pixmap pixmap = new Pixmap((int)rect.width, (int)rect.height, Format.RGBA8888 );
		pixmap.setColor(color);
		pixmap.fillRectangle(0, 0, (int)rect.width, (int)rect.height);
		
		this.x = rect.x;
		this.y = rect.y;
		init(pixmap);
		
	}
	
	public GeometricObject(Circle circle, Color color) {
		Pixmap pixmap = new Pixmap((int)circle.radius*2, (int)circle.radius*2, Format.RGBA8888 );
		pixmap.setColor(color);
		pixmap.fillCircle((int)circle.radius, (int)circle.radius, (int)circle.radius);
		
		this.x = circle.x;
		this.y = circle.y;
		init(pixmap);
	}
	
//	TODO 
	public GeometricObject(Line2D line, Color color) {
		Pixmap pixmap = new Pixmap((int) Math.abs(line.getX1() - line.getX2()), 
				(int) Math.abs(line.getY1() - line.getY2()), Format.RGBA8888);
		System.out.println(Math.abs(line.getX1() - line.getX2())+"---"+Math.abs(line.getY1() - line.getY2()));
		System.out.println(line.getX1()+"x"+ line.getY1()+" - "+ line.getX2()+"x"+ line.getY2());
		pixmap.setColor(color);
		pixmap.drawLine((int) line.getX1(), (int) line.getY1(), (int) line.getX2(), (int) line.getY2());
	
		this.x = (float) Math.min(line.getX1(), line.getX2());
		this.y = (float) Math.min(line.getY1(), line.getY2());
		init(pixmap);
	}
	
	
	
	private void init(Pixmap pixmap) {
		texture = new Texture(pixmap);
		pixmap.dispose();
		GameRender.getInstance().addGeometricObject(this);
	}
	
	public void draw(SpriteBatch batch) {
		if(ttl <= 0)
			GameRender.getInstance().removeGeometricObject(this);
		
		batch.draw(texture, x, y);
		ttl--;
	}

	@Override
	public void dispose() {
		texture.dispose();
	}

}
