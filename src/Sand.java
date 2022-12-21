import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sand {

	public static void main(String[] args) throws IOException {
		Sand sand = new Sand();
		sand.simulate();
	}
	
	private void simulate() throws IOException {
		Stream<String> lines = Files.lines(Paths.get("./input_sand.txt"));
		List<String> collect = lines.collect(Collectors.toList());

		List<List<Point>> rockLines = new ArrayList<List<Point>>();
		for (String lineAsText : collect) {
			String[] coordinatesAsText = lineAsText.split("->");
			List<Point> line = new ArrayList<Sand.Point>();
			for (String coordinates : coordinatesAsText) {
				coordinates = coordinates.trim();
				int x = Integer.parseInt(coordinates.split(",")[0]);
				int y = Integer.parseInt(coordinates.split(",")[1]);
				line.add(new Point(x,y));
			}
			rockLines.add(line);
		}
		
		Set<Point> rocks = new HashSet<Point>();
		for (List<Point> line : rockLines) {
			Point lastPoint = null;
			for (Point point : line) {
				if (lastPoint != null) {
					drawLine(rocks, lastPoint, point);
				}
				lastPoint = point;
			}
		}
		
		
		int minRockX = rocks.stream().mapToInt(p -> p.getX()).min().orElse(0);
		int maxRockX = rocks.stream().mapToInt(p -> p.getX()).max().orElse(0);
		int maxRockY = rocks.stream().mapToInt(p -> p.getY()).max().orElse(0);
		
		Set<Point> sand = new HashSet<Point>();
		boolean flowOut = false;
		int iterationCount = 0;
		while (!flowOut) {
			Point currentSand = new Point(500, 0);
			
			// simulate fall
			boolean rest = false;
			while (!rest && !flowOut) {
				if (isEmpty(rocks, sand, currentSand.getTranslated(0,1))) {
					currentSand = currentSand.getTranslated(0,1);
				}
				else if (isEmpty(rocks, sand, currentSand.getTranslated(-1,1))) {
					currentSand = currentSand.getTranslated(-1,1);
				}
				else if (isEmpty(rocks, sand, currentSand.getTranslated(1,1))) {
					currentSand = currentSand.getTranslated(1,1);
				}
				else {
					sand.add(currentSand);
					rest = true;
					iterationCount++;
				}
				flowOut |= currentSand.x < minRockX || currentSand.x > maxRockX || currentSand.y > maxRockY;
			}
		}
		System.out.println(iterationCount);
		
		sand.clear();
		boolean blocked = false;
		int floorY = maxRockY + 2;
		iterationCount = 0;
		while (!blocked) {
			Point currentSand = new Point(500, 0);
			// simulate fall
			boolean rest = false;
			while (!rest && !blocked) {
				if (isEmpty(rocks, sand, floorY, currentSand.getTranslated(0,1))) {
					currentSand = currentSand.getTranslated(0,1);
				}
				else if (isEmpty(rocks, sand, floorY, currentSand.getTranslated(-1,1))) {
					currentSand = currentSand.getTranslated(-1,1);
				}
				else if (isEmpty(rocks, sand, floorY, currentSand.getTranslated(1,1))) {
					currentSand = currentSand.getTranslated(1,1);
				}
				else {
					sand.add(currentSand);
					rest = true;
					iterationCount++;
				}
			}
			blocked = currentSand.x == 500 && currentSand.y == 0;
		}
		System.out.println(iterationCount);
	}
	
	private boolean isEmpty(Set<Point> rocks, Set<Point> sand, int floorY, Point translated) {
		if (translated.y == floorY) return false;
		return isEmpty(rocks, sand, translated);
	}

	private boolean isEmpty(Set<Point> rocks, Set<Point> sand, Point translated) {
		return !rocks.contains(translated) && !sand.contains(translated);
	}

	private void drawLine(Set<Point> rocks, Point startPoint, Point endPoint) {
		if (startPoint.y == endPoint.y) {
			for (int x = Math.min(startPoint.x, endPoint.x);x<= Math.max(startPoint.x, endPoint.x);x++) {
				rocks.add(new Point(x,startPoint.y));
			}
		}
		else if (startPoint.x == endPoint.x) {
			for (int y = Math.min(startPoint.y, endPoint.y);y<= Math.max(startPoint.y, endPoint.y);y++) {
				rocks.add(new Point(startPoint.x,y));
			}
		}
		else
			throw new RuntimeException();
	}

	private static class Point {
		private int x;
		private int y;
		public Point(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}
		public Point getTranslated(int dx, int dy) {
			// TODO Auto-generated method stub
			return new Point(x+dx, y+dy);
		}
		public int getX() {
			return x;
		}
		public int getY() {
			return y;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Point other = (Point) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
		
		
	}
}
