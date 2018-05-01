package main.java;

import javafx.geometry.Point2D;
import javafx.util.Pair;

/** Misc helper functions. **/
public class Utils
{
	/** Convert polar coordinates (r, theta) to cartesian coordinates (x, y)
	 * @param radius Radius of the circle.
	 * @param theta Angle on the circle in degrees.
	 * @return Cartesian coordinate. **/
	public static Point2D polarToCartesian(double radius, double theta)
	{
		return new Point2D(radius * Math.cos(theta), radius * Math.sin(theta));
	}

	/** Convert polar coordinates (r, theta) to cartesian coordinates (x, y)
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
		return new Pair<Double, Double>(Math.sqrt(x*x + y*y), Math.atan2(y, x));
	}

	/** Convert cartesian coordinates to polar coordinates.
	 * @param point A cartesian coordinate.
	 * @return A pair of 2 double values, the radius, and theta (angle in degrees). **/
	public static Pair<Double, Double> cartesianToPolar(Point2D point)
	{
		return cartesianToPolar(point.getX(), point.getY());
	}
}
