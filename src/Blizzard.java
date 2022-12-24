import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Blizzard {

	public static class Environnement {

		private Set<Blizzard> blizzards;
		private Blizzard[][] map;
		private int startX;
		private int endX;
		private int height;
		private int width;
		private Agent agent;
		
		public void setSize(int width, int height) {
			this.width = width;
			this.height = height;
		}

		public void setStartX(int startX) {
			this.startX = startX;
		}

		public void setEndX(int endX) {
			this.endX = endX;
		}

		public void setBlizzards(Set<Blizzard> blizzards) {
			this.blizzards = blizzards;
		}

		public void setMap(Blizzard[][] map) {
			this.map = map;
		}

		public void update() {
			Arrays.stream(map).forEach(x -> Arrays.fill(x, null));
			blizzards.forEach(b -> {
				map[b.location.x][b.location.y] = b;
			});
		}
		
		public void performMoves() {
			blizzards.forEach(b -> {
				b.performMove(this);
			});
			update();
		}

		public void rollbackMoves() {
			blizzards.forEach(b -> {
				b.rollbackMove(this);
			});
			update();
		}

		public void setAgent(Agent agent) {
			this.agent = agent;
		}

		public void dumpMap() {
			for (int y=0;y<map[0].length;y++) {
				for (int x=0;x<map.length;x++) {
					if (map[x][y] == null) System.out.print('.');
					else { System.out.print(map[x][y].getDirectionChar()); }
				}
				System.out.println();
			}
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((agent == null) ? 0 : agent.hashCode());
			result = prime * result + ((blizzards == null) ? 0 : blizzards.hashCode());
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
			Environnement other = (Environnement) obj;
			if (agent == null) {
				if (other.agent != null)
					return false;
			} else if (!agent.equals(other.agent))
				return false;
			if (blizzards == null) {
				if (other.blizzards != null)
					return false;
			} else if (!blizzards.equals(other.blizzards))
				return false;
			return true;
		}
	}

	public static class Agent {

		private Point location;

		public Agent(Point location) {
			this.location = location;
		}

		public Set<Direction> getPossibleMove(Environnement env) {
			Set<Direction> result = new HashSet<Blizzard.Direction>();
			Point p = null;

			p = Direction.down.getNextLocation(location);
			if ((p.y < env.height-1 || (p.y == env.height-1 && p.x == env.endX)) && env.map[p.x][p.y] == null) { result.add(Direction.down); }
			
			p = Direction.right.getNextLocation(location);
			if (p.y > 0 && p.y <  env.height-1 && p.x < env.width-1 && env.map[p.x][p.y] == null) { result.add(Direction.right); }
			
			p = Direction.wait.getNextLocation(location);
			if (env.map[p.x][p.y] == null) { result.add(Direction.wait); }

			p = Direction.left.getNextLocation(location);
			if (p.y > 0 && p.y < env.height-1 && p.x > 0 && env.map[p.x][p.y] == null) { result.add(Direction.left); }
			
			p = Direction.up.getNextLocation(location);
			if ((p.y > 0 || (p.y == 0 && p.x == env.startX)) && env.map[p.x][p.y] == null) { result.add(Direction.up); }
			
			return result;
		}
		
		public void performMove(Direction direction) { location = direction.getNextLocation(location); }
		public void rollbackMove(Direction direction) { location = direction.getPreviousLocation(location); }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((location == null) ? 0 : location.hashCode());
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
			Agent other = (Agent) obj;
			if (location == null) {
				if (other.location != null)
					return false;
			} else if (!location.equals(other.location))
				return false;
			return true;
		}

		public void setLocation(Point location) {
			this.location = location;
		}
	}

	private enum Direction {
		down {
			char getChar() { return 'v'; }
			protected Point getNextLocation(Point location) { return new Point(location.x, location.y+1); }
			protected Point getPreviousLocation(Point location) { return new Point(location.x, location.y-1); }
		},
		right {
			char getChar() { return '>'; }
			protected Point getNextLocation(Point location) { return new Point(location.x+1, location.y); }
			protected Point getPreviousLocation(Point location) { return new Point(location.x-1, location.y); }
		},
		up {
			char getChar() { return '^'; }
			protected Point getNextLocation(Point location) { return new Point(location.x, location.y-1); }
			protected Point getPreviousLocation(Point location) { return new Point(location.x, location.y+1); }
		}, 
		left {
			char getChar() { return '<'; }
			protected Point getNextLocation(Point location) { return new Point(location.x-1, location.y); }
			protected Point getPreviousLocation(Point location) { return new Point(location.x+1, location.y); }
		},
		wait {
			char getChar() { return ' '; }
			protected Point getNextLocation(Point location) { return new Point(location.x, location.y); }
			protected Point getPreviousLocation(Point location) { return new Point(location.x, location.y); }
		};

		abstract char getChar();

		protected abstract Point getNextLocation(Point location);
		protected abstract Point getPreviousLocation(Point location);
	}
	
	private Direction direction;
	private Point location;

	public Blizzard(Point location, char c) {
		this.location = location;
		switch (c) {
			case '>' : direction = Direction.right; break;
			case '<' : direction = Direction.left; break;
			case '^' : direction = Direction.up; break;
			case 'v' : direction = Direction.down; break;
		}
	}
	
	public void rollbackMove(Environnement environnement) {
		location = direction.getPreviousLocation(location);
		if (location.x == environnement.width - 1) {
			location.x = 1;
		}
		else if (location.x == 0) {
			location.x = environnement.width - 2;
		}
		if (location.y == environnement.height - 1) {
			location.y = 1;
		}
		else if (location.y == 0) {
			location.y = environnement.height - 2;
		}
	}

	public void performMove(Environnement environnement) {
		location = direction.getNextLocation(location);
		if (location.x == environnement.width - 1) {
			location.x = 1;
		}
		else if (location.x == 0) {
			location.x = environnement.width - 2;
		}
		if (location.y == environnement.height - 1) {
			location.y = 1;
		}
		else if (location.y == 0) {
			location.y = environnement.height - 2;
		}
	}

	public char getDirectionChar() {
		return direction.getChar();
	}
	
	public static void main(String[] args) throws IOException {
		
		List<String> lines = Files.readAllLines(Path.of("input_blizzard.txt"));

		int width = 0;
		int height = 0;
		int startX = 0;
		int endX = 0;
		Set<Blizzard> blizzards = new HashSet<Blizzard>();
		
		Environnement env = new Environnement();
		
		String lastLine = null;
		for (String s : lines) {
			if (height == 0) { startX = s.indexOf('.'); }
			width = s.length();
			lastLine = s;
			for (int x=1;x<s.length()-1;x++) {
				char c = s.charAt(x);
				if (c == '>' || c == '<' || c == '^' || c == 'v') {
					blizzards.add(new Blizzard(new Point(x,height),c));
				}
			}
			height++;
		}
		endX = lastLine.indexOf('.'); 

		Blizzard[][] map = new Blizzard[width][height];

		env.setSize(width, height);
		env.setStartX(startX);
		env.setEndX(endX);
		env.setBlizzards(blizzards);
		env.setMap(map);
		env.update();

		Agent agent = new Agent(new Point(startX, 0));
		env.setAgent(agent);
		
		findTime(env, 0);
	}
	
	private static int findTime(Environnement env, int time) {
		Set<Point> locations = new HashSet<Blizzard.Point>();
		Point exit = new Point(env.endX, env.height-1);
		Point entry = new Point(env.startX, 0);
		locations.add(new Point(env.agent.location)); 
		while (!locations.contains(exit)) {
			final Set<Point> newLocations = new HashSet<Blizzard.Point>();
			env.performMoves();
			for (Point location : locations) {
				env.agent.setLocation(location);
				Set<Direction> possibleMoves = env.agent.getPossibleMove(env);
				possibleMoves.forEach(m -> newLocations.add(m.getNextLocation(location)));
			}
			locations = newLocations;
			time++;
		}
		System.out.println("Time exit 1 : " + time);
		
		locations.clear();
		locations.add(exit);
		while (!locations.contains(entry)) {
			final Set<Point> newLocations = new HashSet<Blizzard.Point>();
			env.performMoves();
			for (Point location : locations) {
				env.agent.setLocation(location);
				Set<Direction> possibleMoves = env.agent.getPossibleMove(env);
				possibleMoves.forEach(m -> newLocations.add(m.getNextLocation(location)));
			}
			locations = newLocations;
			time++;
		}
		System.out.println("Time snack : " + time);
		
		locations.clear();
		locations.add(entry);
		while (!locations.contains(exit)) {
			final Set<Point> newLocations = new HashSet<Blizzard.Point>();
			env.performMoves();
			for (Point location : locations) {
				env.agent.setLocation(location);
				Set<Direction> possibleMoves = env.agent.getPossibleMove(env);
				possibleMoves.forEach(m -> newLocations.add(m.getNextLocation(location)));
			}
			locations = newLocations;
			time++;
		}
		System.out.println("Time exit 2 : " + time);
		
		return time;
	}
	
	private static class Point {
		
		private int x;
		private int y;
		public Point(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}
		public Point(Point location) {
			x = location.x;
			y = location.y;
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((direction == null) ? 0 : direction.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
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
		Blizzard other = (Blizzard) obj;
		if (direction != other.direction)
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		return true;
	}
}
