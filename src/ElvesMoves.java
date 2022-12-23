import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ElvesMoves {

	public enum Direction {
		NORTH {
			boolean canConsider(Map<Point, Elve> map, Point location) {
				if (map.containsKey(new Point(location.x-1, location.y-1))) return false;
				if (map.containsKey(new Point(location.x, location.y-1))) return false;
				if (map.containsKey(new Point(location.x+1, location.y-1))) return false;
				return true;
			}

			protected Point getFrom(Point location) {
				return new Point(location.x, location.y-1);
			}
		},
		SOUTH {
			boolean canConsider(Map<Point, Elve> map, Point location) {
				if (map.containsKey(new Point(location.x-1, location.y+1))) return false;
				if (map.containsKey(new Point(location.x, location.y+1))) return false;
				if (map.containsKey(new Point(location.x+1, location.y+1))) return false;
				return true;
			}

			protected Point getFrom(Point location) {
				return new Point(location.x, location.y+1);
			}
		},
		WEST {
			boolean canConsider(Map<Point, Elve> map, Point location) {
				if (map.containsKey(new Point(location.x-1, location.y-1))) return false;
				if (map.containsKey(new Point(location.x-1, location.y))) return false;
				if (map.containsKey(new Point(location.x-1, location.y+1))) return false;
				return true;
			}

			protected Point getFrom(Point location) {
				return new Point(location.x-1, location.y);
			}
		},
		EAST {
			boolean canConsider(Map<Point, Elve> map, Point location) {
				if (map.containsKey(new Point(location.x+1, location.y-1))) return false;
				if (map.containsKey(new Point(location.x+1, location.y))) return false;
				if (map.containsKey(new Point(location.x+1, location.y+1))) return false;
				return true;
			}

			protected Point getFrom(Point location) {
				return new Point(location.x+1, location.y);
			}
		};

		abstract boolean canConsider(Map<Point, Elve> map, Point location);

		protected abstract Point getFrom(Point location);
	}

	public static class Elve {

		Point location;
		Direction proposedDirection = null;
		
		List<Direction> directionList = new ArrayList<Direction>(Arrays.asList(Direction.values()));
		int index;
		
		public Elve(int x, int y, int index) {
			location = new Point(x, y);
			this.index = index;
		}

		public Point getLocation() {
			return location;
		}

		public boolean needToMove(Map<Point, Elve> map) {
			if (map.containsKey(new Point(location.x-1, location.y))) return true;
			if (map.containsKey(new Point(location.x+1, location.y))) return true;
			if (map.containsKey(new Point(location.x, location.y-1))) return true;
			if (map.containsKey(new Point(location.x, location.y+1))) return true;
			if (map.containsKey(new Point(location.x-1, location.y-1))) return true;
			if (map.containsKey(new Point(location.x+1, location.y-1))) return true;
			if (map.containsKey(new Point(location.x-1, location.y+1))) return true;
			if (map.containsKey(new Point(location.x+1, location.y+1))) return true;
			return false;
		}

		public void considerDirection(Map<Point, Elve> map) {
			Direction consideredDirection = null;
			for (Direction direction : directionList) {
				if (consideredDirection == null && direction.canConsider(map, location)) {
					consideredDirection = direction;
				}
			}
			proposedDirection = consideredDirection;
		}

		public Point getConsideredDestination() {
			return proposedDirection.getFrom(location);
		}

		public Direction getProposedDirection() {
			return proposedDirection;
		}

		public void setLocation(Point consideredDestination) {
			location = consideredDestination;
		}

		public void startRound() {
			proposedDirection = null;
		}

		public void rotate() {
			Direction remove = directionList.remove(0);
			directionList.add(directionList.size(), remove);
		}
	}

	public static void main(String[] args) throws IOException {

		
		Stream<String> lines = Files.lines(Paths.get("./input_elvesMoves.txt"));
		List<String> collect = lines.collect(Collectors.toList());

		List<Elve> elves = new ArrayList<Elve>();
		
		Map<Point, Elve> map = new HashMap<Point, Elve>();
		
		int y = 0;
		for (String s : collect) {
			for (int x=0;x<s.length();x++) {
				if (s.charAt(x) == '#') {
					Elve elve = new Elve(x, y, elves.size());
					elves.add(elve);
					map.put(elve.getLocation(), elve);
				}
			}
			y++;
		}
		
		boolean needToMove = true;
		int count = 0;
		int round = 0;
		while (needToMove) {
			needToMove = false;
			// first half
			Map<Point, Integer> consideredDestination = new HashMap<Point, Integer>();
			for (Elve elve : elves) {
				if (elve.index == 7) {
					Thread.yield();
				}
				elve.startRound();
				boolean elveNeedToMove = elve.needToMove(map);
				needToMove |= elveNeedToMove;
				if (elveNeedToMove) {
					elve.considerDirection(map);
					if (elve.getProposedDirection() != null) {
						Point destination = elve.getConsideredDestination();
						Integer considerationCount = consideredDestination.getOrDefault(destination, 0);
						consideredDestination.put(destination, considerationCount+1);
					}
				}
				elve.rotate();
			}
			
			Thread.yield();
			
			// second half
			for (Elve elve : elves) {
				if (elve.getProposedDirection() != null && consideredDestination.get(elve.getConsideredDestination()) < 2) {
					map.remove(elve.getLocation());
					map.put(elve.getConsideredDestination(), elve);
					elve.setLocation(elve.getConsideredDestination());
				}
			}
			round++;
			if (round == 10) { count = count(map); }
		}
		
		dump(map);
		System.out.println("Count = " + count);
		System.out.println("Round = " + round);

	}

	private static int count(Map<Point, Elve> map) {
		int count = 0;
		
		AtomicInteger minX = new AtomicInteger(Integer.MAX_VALUE);
		AtomicInteger maxX = new AtomicInteger(Integer.MIN_VALUE);
		AtomicInteger minY = new AtomicInteger(Integer.MAX_VALUE);
		AtomicInteger maxY = new AtomicInteger(Integer.MIN_VALUE);
		
		map.keySet().forEach(p -> {
			minX.set(Math.min(p.x, minX.get()));
			minY.set(Math.min(p.y, minY.get()));
			maxX.set(Math.max(p.x, maxX.get()));
			maxY.set(Math.max(p.y, maxY.get()));
		});
		
		for (int y=minY.get();y<=maxY.get();y++) {
			for (int x=minX.get();x<=maxX.get();x++) {
				if (!map.containsKey(new Point(x,y))) count++;
			}
		}
		
		return count;
	}

	private static void dump(Map<Point, Elve> map) {
		
		AtomicInteger minX = new AtomicInteger(Integer.MAX_VALUE);
		AtomicInteger maxX = new AtomicInteger(Integer.MIN_VALUE);
		AtomicInteger minY = new AtomicInteger(Integer.MAX_VALUE);
		AtomicInteger maxY = new AtomicInteger(Integer.MIN_VALUE);
		
		map.keySet().forEach(p -> {
			minX.set(Math.min(p.x, minX.get()));
			minY.set(Math.min(p.y, minY.get()));
			maxX.set(Math.max(p.x, maxX.get()));
			maxY.set(Math.max(p.y, maxY.get()));
		});
		
		for (int y=minY.get();y<=maxY.get();y++) {
			for (int x=minX.get();x<=maxX.get();x++) {
//				if (map.containsKey(new Point(x,y))) System.out.print((char)('0'+(map.get(new Point(x,y)).index)));
				if (map.containsKey(new Point(x,y))) System.out.print('#');
				else {
					 System.out.print('.');
				}
			}
			System.out.println();
		}
		
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
		
		@Override
		public String toString() {
			return "["+x+","+y+"]";
		}
	}

}
