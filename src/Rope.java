import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Rope {

	public static void main(String[] args) throws IOException {
		Rope rope = new Rope();
		rope.simulate();
	}
	
	Point head = new Point();
	Point tail = new Point();
	
	private void simulate() throws IOException {
		Stream<String> lines = Files.lines(Paths.get("./input_rope.txt"));
		List<String> collect = lines.collect(Collectors.toList());

		HashSet<Point> tailPositions = new HashSet<Rope.Point>();
		tailPositions.add(new Point(tail.getX(), tail.getY()));
		
		for (String move : collect) {
			char direction = move.split(" ")[0].toLowerCase().charAt(0);
			int count = Integer.parseInt(move.split(" ")[1]);
			
			for (int i=0;i<count;i++) {
				switch (direction) {
					case 'u' : head.move(0,1); break;
					case 'd' : head.move(0,-1); break;
					case 'r' : head.move(1,0); break;
					case 'l' : head.move(-1,0); break;
				}
				
				if (head.getY() - tail.getY() > 1) {
					tail.setX(head.getX());
					tail.setY(head.getY()-1);
				}
				if (tail.getY() - head.getY() > 1) {
					tail.setX(head.getX());
					tail.setY(head.getY()+1);
				}
				if (head.getX() - tail.getX() > 1) {
					tail.setX(head.getX()-1);
					tail.setY(head.getY());
				}
				if (tail.getX() - head.getX() > 1) {
					tail.setX(head.getX()+1);
					tail.setY(head.getY());
				}
				tailPositions.add(new Point(tail.getX(), tail.getY()));
			}
		}
		
		System.out.println(tailPositions.size());
		
		tailPositions.clear();
		List<Point> rope = new ArrayList<Point>();
		for (int i=0;i<10;i++) rope.add(new Point());
		Point head = rope.get(0);
		Point tail = rope.get(9);
		tailPositions.add(new Point(tail.getX(), tail.getY()));
		for (String move : collect) {
			char direction = move.split(" ")[0].toLowerCase().charAt(0);
			int count = Integer.parseInt(move.split(" ")[1]);
			
			for (int i=0;i<count;i++) {
				switch (direction) {
					case 'u' : head.move(0,1); break;
					case 'd' : head.move(0,-1); break;
					case 'r' : head.move(1,0); break;
					case 'l' : head.move(-1,0); break;
				}
				for (int j=1;j<10;j++) {
					Point knot = rope.get(j);
					Point previous = rope.get(j-1);
					int newX = knot.getX();
					int newY = knot.getY();
					if (previous.getY() - knot.getY() > 1) {
						knot.setX(previous.getX());
						knot.setY(previous.getY()-1);
					}
					if (knot.getY() - previous.getY() > 1) {
						knot.setX(previous.getX());
						knot.setY(previous.getY()+1);
					}
					if (previous.getX() - knot.getX() > 1) {
						knot.setX(previous.getX()-1);
						knot.setY(previous.getY());
					}
					if (knot.getX() - previous.getX() > 1) {
						knot.setX(previous.getX()+1);
						knot.setY(previous.getY());
					}
				}
				tailPositions.add(new Point(tail.getX(), tail.getY()));
				dumpRope(rope);
				System.out.println();
			}
		}
		
		System.out.println(tailPositions.size());
	}

	private void dumpRope(List<Point> rope) {
		for (int y=20;y>=0;y--) {
			for (int x=0;x<20;x++) {
				int found = -1;
				for (int i=0;i<10;i++) {
					if (rope.get(i).getX()==x && rope.get(i).getY()==y) {
						found = i; break;					}
 				}
				System.out.print(found >= 0 ? (""+found) : '.');
			}
			System.out.println();
		}
		
	}

	public static class Point {
		int x;
		int y;
		
		public Point(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}

		public void move(int dx, int dy) {
			x += dx;
			y += dy;
		}

		public Point() {
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
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
