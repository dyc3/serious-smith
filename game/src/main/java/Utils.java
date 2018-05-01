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
		return new Point2D(radius * Math.cos(theta), radius * Math.sin(theta));
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
		ArrayList<Point2D> points = new ArrayList<Point2D>();
		for (int i = 0; i < parts; i++)
		{
			double angle = i * (360.0 / parts);
			points.add(polarToCartesian(radius, angle));
		}
		return points;
	}
}
