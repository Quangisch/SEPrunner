package misc;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.Shape.Type;

public enum BodyFunctions {
	;

	public static final Shape getShape(String type, float[] points) {
		switch (type.toUpperCase().charAt(0)) {
		case 'B': // B ox
			float x = points[0],
			y = points[1],
			width = points[2],
			height = points[3];
			return getShape(Type.Polygon, new float[] { x, y, x + width, y, x + width, y + height, x, y + height });
		case 'C':
			switch (type.toUpperCase().charAt(1)) {
			case 'H': // CH ain
				return getShape(Type.Chain, points);
			case 'I': // CI rcle
				return getShape(Type.Circle, points);
			}
			break;
		case 'E': // E dge
			return getShape(Type.Edge, points);
		case 'P': // P olygon
			return getShape(Type.Polygon, points);
		}
		return getShape(Type.Circle, new float[] { 0, 0, 0 });
	}

	public static final Shape getShape(Shape.Type shapeType, float[] points) {
		Shape shape = null;
		switch (shapeType) {
		case Chain:
			shape = new ChainShape();
			((ChainShape) shape).createChain(points);
			break;
		case Circle:
			shape = new CircleShape();
			((CircleShape) shape).setPosition(new Vector2(points[0], points[1]));
			((CircleShape) shape).setRadius(points[2]);
			break;
		case Edge:
			shape = new EdgeShape();
			((EdgeShape) shape).set(points[0], points[1], points[2], points[3]);
			break;
		case Polygon:
			shape = new PolygonShape();
			((PolygonShape) shape).set(points);
			break;
		default:
			throw new NullPointerException();
		}

		return shape;
	}
}
