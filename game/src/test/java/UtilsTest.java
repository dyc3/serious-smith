package test.java;

import main.java.Utils;
import org.junit.jupiter.api.Test;
import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UtilsTest
{
	@Test
	public void testPointsOnCircleCount()
	{
		ArrayList<Point2D> points;
		points = Utils.pointsOnCircle(1, 2);
		assert points.size() == 2;

		points = Utils.pointsOnCircle(5, 65);
		assert points.size() == 65;
	}

	@Test
	public void testPointsOnCirclePositions()
	{
		List<Point2D> points, targetPoints;
		targetPoints = Arrays.asList(
				new Point2D(1, 0),
				new Point2D(-1, 0));
		points = Utils.pointsOnCircle(1, 2);
//		System.out.println("points: " + points + "\ntarget: " + targetPoints);
		assertEquals(points, targetPoints);

		targetPoints = Arrays.asList(
				new Point2D(1, 0),
				new Point2D(0, 1),
				new Point2D(-1, 0),
				new Point2D(0, -1));
		points = Utils.pointsOnCircle(1, 4);
//		System.out.println("points: " + points + "\ntarget: " + targetPoints);
		assertEquals(points, targetPoints);
	}

	public static void assertEquals(List<?> array1, List<?> array2)
	{
		assert array1.size() == array2.size();
		if (array1.isEmpty() && array2.isEmpty())
		{
			return;
		}
		for (int i = 0; i < array1.size(); i++)
		{
			assert array1.get(i).equals(array2.get(i));
		}
	}

	public static void printPointList(List<Point2D> points)
	{
		for (Point2D point : points)
		{
			System.out.println(point.getX() + ", " + point.getY());
		}
	}
}
