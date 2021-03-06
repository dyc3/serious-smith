package main.java;

import javafx.geometry.Point2D;
import javafx.util.Pair;
import java.util.ArrayList;

/** Misc helper functions. **/
public final class Utils
{
	/** This constructor should not be used. **/
	private Utils()
	{

	}

	/** Convert polar coordinates (r, theta) to cartesian coordinates (x, y).
	 * @param radius Radius of the circle.
	 * @param theta Angle on the circle in degrees.
	 * @return Cartesian coordinate. **/
	public static Point2D polarToCartesian(double radius, double theta)
	{
		double rad = Math.toRadians(theta);
		double x = radius * Math.cos(rad);
		double y = radius * Math.sin(rad);
		double precision = 100000.0;
		return new Point2D(Math.round(x * precision) / precision, Math.round(y * precision) / precision);
	}

	/** Convert polar coordinates (r, theta) to cartesian coordinates (x, y).
	 * @param polar A pair of 2 double values, the radius, and theta (angle in degrees).
	 * @return Cartesian coordinate. **/
	public static Point2D polarToCartesian(Pair<Double, Double> polar)
	{
		return polarToCartesian(polar.getKey(), polar.getValue());
	}

	/** Convert cartesian coordinates to polar coordinates.
	 * @param x Position on X axis.
	 * @param y Position on Y axis.
	 * @return A pair of 2 double values, the radius, and theta (angle in degrees). **/
	public static Pair<Double, Double> cartesianToPolar(double x, double y)
	{
		return new Pair<Double, Double>(Math.sqrt(x * x + y * y), Math.atan2(y, x));
	}

	/** Convert cartesian coordinates to polar coordinates.
	 * @param point A cartesian coordinate.
	 * @return A pair of 2 double values, the radius, and theta (angle in degrees). **/
	public static Pair<Double, Double> cartesianToPolar(Point2D point)
	{
		return cartesianToPolar(point.getX(), point.getY());
	}

	/** Generate a set of evenly spaced points on a circle.
	 * @param radius Radius of the circle.
	 * @param parts Number of points to return.
	 * @return List of `parts` cartesian coordinates. **/
	public static ArrayList<Point2D> pointsOnCircle(double radius, int parts)
	{
		return pointsOnCircle(radius, parts, 0);
	}

	/** Generate a set of evenly spaced points on a circle.
	 * @param radius Radius of the circle.
	 * @param parts Number of points to return.
	 * @param angleOffset Offset the points by degrees.
	 * @return List of `parts` cartesian coordinates. **/
	public static ArrayList<Point2D> pointsOnCircle(double radius, int parts, double angleOffset)
	{
		ArrayList<Point2D> points = new ArrayList<Point2D>();
		for (int i = 0; i < parts; i++)
		{
			double angle = i * (360 / parts) + angleOffset;
			Point2D point = polarToCartesian(radius, angle);
			points.add(point);
		}
		return points;
	}

	/** Offsets the point randomly inside the range specified.
	 * @param point The point.
	 * @param range The maximum range from the coordinate's original value.
	 * @return A randomized point. **/
	public static Point2D randomizePoint(Point2D point, double range)
	{
		Point2D offset = new Point2D((Math.random() * (range * 2)  - range),
				(Math.random() * (range * 2)  - range));
		return point.add(offset);
	}

	public static double lerp(double a, double b, double f)
	{
		return (a * (1.0f - f)) + (b * f);
	}

	public static Point2D lerpPoint2D(Point2D from, Point2D to, double amount)
	{
		double x = lerp(from.getX(), to.getX(), amount);
		double y = lerp(from.getY(), to.getY(), amount);
		return new Point2D(x, y);
	}
}
