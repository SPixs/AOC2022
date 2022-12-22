import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MonkeyMap {
	
	private enum Direction {
		right { Point getNext(Point p) { return new Point(p.x+1, p.y); }},
		down { Point getNext(Point p) { return new Point(p.x, p.y+1); }} ,
		left { Point getNext(Point p) { return new Point(p.x-1, p.y); }},
		up { Point getNext(Point p) { return new Point(p.x, p.y-1); }};
		
		abstract Point getNext(Point p);
	}
	
	public static class Point {

		int x, y;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public Point() {}

		public Point(Point other) {
			x=other.x;
			y=other.y;
		}
		
		@Override
		public String toString() {
			return "["+x+","+y+"]";
		}
	}
	
	public static class Agent {
		
		Point location = new Point();
		Direction direction = Direction.right;
		static List<Direction> allDirections = Arrays.asList(Direction.values());

		public Agent(Agent agent) {
			location = new Point(agent.location);
			direction = agent.direction;
		}

		public Agent() {
			// TODO Auto-generated constructor stub
		}

		public void advance(int[][] map, int width, int height, int steps) {
			while(steps-- > 0) {
				// Find next valid location in the heading direction 
				Point nextPoint = direction.getNext(location);
				nextPoint.x = (nextPoint.x + width) % width;
				nextPoint.y = (nextPoint.y + height) % height;
				while (map[nextPoint.x][nextPoint.y] == 0) {
					nextPoint = direction.getNext(nextPoint);
					nextPoint.x = (nextPoint.x + width) % width;
					nextPoint.y = (nextPoint.y + height) % height;
				}
				if (map[nextPoint.x][nextPoint.y] == 1) {
					location = nextPoint;
				}
				else {
//					System.out.println("Blocked at " + this);
					steps = 0;
				}
			}
		}
		
		public void advancePart2(int[][] map, int width, int height, int steps) {
			while(steps-- > 0) {
				// Find next valid location in the heading direction 
				Point nextPoint = direction.getNext(location);

				Direction newDirection = direction;
				
				// (1)
				if (nextPoint.x  > 99 && direction == Direction.up && nextPoint.y < 0) {
					nextPoint.y = 199;
					nextPoint.x = nextPoint.x - 100;
					newDirection = Direction.up;
				}
				// (1*)
				else if (nextPoint.y > 199 && nextPoint.x < 50 && direction == Direction.down) {
					nextPoint.x = 100 + nextPoint.x;
					nextPoint.y = 0;
					newDirection = Direction.down;
				}
				
				// (2)
				else if (nextPoint.x > 99 && direction == Direction.down && nextPoint.y == 50) {
					nextPoint.y = 50 + nextPoint.x - 100;
					nextPoint.x = 99;
					newDirection = Direction.left;
				}
				// (2*)
				else if (nextPoint.x == 100 && nextPoint.y > 49 && nextPoint.y < 100 && direction == Direction.right) {
					nextPoint.x = 100 + nextPoint.y - 50;
					nextPoint.y = 49;
					newDirection = Direction.up;
				}
				
				// (3)
				else if (nextPoint.x == 150 && direction == Direction.right && nextPoint.y < 50) {
					nextPoint.y = 100 + 49 - nextPoint.y;
					nextPoint.x = 99;
					newDirection = Direction.left;
				}
				// (3*)
				else if (nextPoint.x == 100 && nextPoint.y > 99 && nextPoint.y < 150 && direction == Direction.right) {
					nextPoint.x = 149;
					nextPoint.y = 149-nextPoint.y;
					newDirection = Direction.left;
				}
	
				// (4)
				else if (nextPoint.x  > 49 && direction == Direction.down && nextPoint.y == 150) {
					nextPoint.y = 150 + nextPoint.x - 50;
					nextPoint.x = 49;
					newDirection = Direction.left;
				}
				// (4*)
				else if (nextPoint.x == 50 && nextPoint.y > 149 && direction == Direction.right) {
					nextPoint.x = 50 + nextPoint.y - 150;
					nextPoint.y = 149;
					newDirection = Direction.up;
				}

				// (5)
				else if (nextPoint.x > 49 && nextPoint.x < 100 && direction == Direction.up && nextPoint.y < 0) {
					nextPoint.y = 150 + nextPoint.x - 50;
					nextPoint.x = 0;
					newDirection = Direction.right;
				}
				// (5*)
				else if (nextPoint.x < 0 && nextPoint.y > 149 && direction == Direction.left) {
					nextPoint.x = 50 + nextPoint.y - 150;
					nextPoint.y = 0;
					newDirection = Direction.down;
				}

				// (6)
				else if (nextPoint.y > 49 && nextPoint.y < 100 && direction == Direction.left && nextPoint.x == 49) {
					nextPoint.x = nextPoint.y - 50;
					nextPoint.y = 100;
					newDirection = Direction.down;
				}
				// (6*)
				else if (nextPoint.x < 50 && nextPoint.y < 100 && direction == Direction.up) {
					nextPoint.y = 50 + nextPoint.x;
					nextPoint.x = 50;
					newDirection = Direction.right;
				}

				// (7)
				else if (nextPoint.y < 50 && nextPoint.x == 49 && direction == Direction.left) {
					nextPoint.x = 0;
					nextPoint.y = 100 + 49 - nextPoint.y;
					newDirection = Direction.right;
				}
				// (7*)
				else if (nextPoint.x < 0 && nextPoint.y > 99 && nextPoint.y < 150 && direction == Direction.left) {
					nextPoint.x = 50;
					nextPoint.y = 149 - nextPoint.y;
					newDirection = Direction.right;
				}
				
				if (map[nextPoint.x][nextPoint.y] == 0) {
					throw new IllegalStateException();
				}
				
				if (map[nextPoint.x][nextPoint.y] == 1) {
					location = nextPoint;
					direction = newDirection;
				}
				else {
//					System.out.println("Blocked at " + this);
					steps = 0;
				}
			}
		}
		
		public void turnRight() {
			direction = allDirections.get((allDirections.indexOf(direction) +1) % allDirections.size());
		}
		
		public void turnLeft() {
			direction = allDirections.get((allDirections.indexOf(direction) -1 + allDirections.size()) % allDirections.size());
		}
		
		@Override
		public String toString() {
			return "["+(location.x+1)+","+(location.y+1)+"] facing " + direction;
		}
	}

	public static void main(String[] args) throws IOException {
		Stream<String> lines = Files.lines(Paths.get("./input_monkeyMap.txt"));
		List<String> collect = lines.collect(Collectors.toList());
		
		int height = 0;
		int width = 0;
		String path = null;
		int[][] map = null;
		
		Agent agent = new Agent();

		for (String line : collect) {
			if (line.contains("#") || line.contains(".")) {
				width = Math.max(width, line.length());
				height++;
			}
			else {
				if (!line.isEmpty()) {
					path = line;
				}
			}
		}
		
		map = new int[width][height];
		
		for (int i=0;i<height;i++) {
			String line = collect.get(i);
			if (i == 0) {
				agent.location.x = line.indexOf(".");
				agent.location.y = 0;
			}
			for (int j=0;j<line.length();j++) {
				map[j][i] = line.charAt(j) == ' ' ? 0 : line.charAt(j) == '.' ? 1 : 2; 
			}
		}
		
		Agent initialAgent = new Agent(agent);
		System.out.println("Start position : " + (agent.location.x+1) +"," + (agent.location.y+1));
		System.out.println("Path : " + path);
		System.out.println();
		
		int steps = 0;
		for (int i=0;i<path.length();i++) {
			char c = path.charAt(i);
			if (Character.isDigit(c)) {
				steps = steps * 10 + ( c - '0');
			}
			else {
				agent.advance(map, width, height, steps);
//				System.out.println(agent);
				steps = 0;
				switch (c) {
				case 'L':
					agent.turnLeft();
					break;
				case 'R':
					agent.turnRight();
					break;
				default:
					throw new IllegalArgumentException();
				}
			}
		}
		agent.advance(map, width, height, steps);
		
		int result = 1000 * (agent.location.y + 1) + 4 * (agent.location.x + 1 ) + Agent.allDirections.indexOf(agent.direction);
		System.out.println("Final location : " + agent);
		System.out.println("Result : " + result);
		
		System.out.println();
		
		// part 2
		steps = 0;
		agent = new Agent(initialAgent);
		for (int i=0;i<path.length();i++) {
			char c = path.charAt(i);
			if (Character.isDigit(c)) {
				steps = steps * 10 + ( c - '0');
			}
			else {
//				System.out.println("Advance " + steps);
				agent.advancePart2(map, width, height, steps);
//				System.out.println(agent);
				steps = 0;
				switch (c) {
					case 'L':
//						System.out.println("Turn left");
						agent.turnLeft();
						break;
					case 'R':
//						System.out.println("Turn right");
						agent.turnRight();
						break;
					default:
						throw new IllegalArgumentException();
				}
//				System.out.println();
			}
		}
		agent.advance(map, width, height, steps);

		result = 1000 * (agent.location.y + 1) + 4 * (agent.location.x + 1 ) + Agent.allDirections.indexOf(agent.direction);
		System.out.println("Final location : " + agent);
		System.out.println("Result : " + result);

		// 15265 (too low)
	}
}
