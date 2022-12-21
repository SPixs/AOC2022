import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Lava {

	public static class Point {

		public int x;
		public int y;
		public int z;

		public Point(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getZ() {
			return z;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			result = prime * result + z;
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
			if (z != other.z)
				return false;
			return true;
		}

		
	}

	public static void main(String[] args) throws IOException {
		Stream<String> lines = Files.lines(Paths.get("./input_lava.txt"));
		List<String> collect = lines.collect(Collectors.toList());

		int sum = 0;
		List<Point> points = new ArrayList<Lava.Point>();
		for (String s : collect) {
			String[] split = s.split(",");
			int x  = Integer.parseInt(split[0]);
			int y  = Integer.parseInt(split[1]);
			int z  = Integer.parseInt(split[2]);
			Point p = new Point(x,y,z);
			points.add(p);
		}
		
		int width = points.stream().mapToInt(Point::getX).max().orElse(0)+1; 
		int height = points.stream().mapToInt(Point::getY).max().orElse(0)+1; 
		int depth = points.stream().mapToInt(Point::getZ).max().orElse(0)+1; 
		Point[][][] space = new Point[width][height][depth];
		points.stream().forEach(p -> space[p.x][p.y][p.z]=p);
		
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				for (int z=0;z<depth;z++) {
					if (space[x][y][z] != null) {
						List<Point> adjacentLocations = Arrays.asList(
								new Point(x-1,y,z), 
								new Point(x+1,y,z), 
								new Point(x,y-1,z), 
								new Point(x,y+1,z), 
								new Point(x,y,z-1), 
								new Point(x,y,z+1)
								);
						for (Point p : adjacentLocations) {
							int sx = p.x;
							int sy = p.y;
							int sz = p.z;
							
							if (sx < 0 || sx >= width) sum++;
							else if (sy < 0 || sy >= height) sum++;
							else if (sz < 0 || sz >= depth) sum++;
							else if (space[sx][sy][sz] == null) sum++;
						}
					}
				}
			}
		}
		
		System.out.println(sum);
		
		sum = 0;
		Set<Point> processedLocations = new HashSet<Point>();
		List<Point> toProcess = new ArrayList<Point>();

		Point p = new Point(-1, -1, -1);
		toProcess.add(p);
		
		while (!toProcess.isEmpty()) {
			p = toProcess.remove(0);
			if (processedLocations.contains(p)) continue;
			
			processedLocations.add(p);
			
			Point otherP = null;
			
			otherP = new Point(p.x-1,p.y,p.z);
			if (!processedLocations.contains(otherP)) {
				if (inSpace(otherP, width, height, depth)) {
					if (hasCube(space, otherP, width, height, depth)) sum++;
					else {
						toProcess.add(otherP);
					}
				}
			}
			
			otherP = new Point(p.x+1,p.y,p.z);
			if (!processedLocations.contains(otherP)) {
				if (inSpace(otherP, width, height, depth)) {
					if (hasCube(space, otherP, width, height, depth)) sum++;
					else {
						toProcess.add(otherP);
					}
				}
			}
			
			otherP = new Point(p.x,p.y-1,p.z);
			if (!processedLocations.contains(otherP)) {
				if (inSpace(otherP, width, height, depth)) {
					if (hasCube(space, otherP, width, height, depth)) sum++;
					else {
						toProcess.add(otherP);
					}
				}
			}
			
			otherP = new Point(p.x,p.y+1,p.z);
			if (!processedLocations.contains(otherP)) {
				if (inSpace(otherP, width, height, depth)) {
					if (hasCube(space, otherP, width, height, depth)) sum++;
					else {
						toProcess.add(otherP);
					}
				}
			}
			otherP = new Point(p.x,p.y,p.z-1);
			if (!processedLocations.contains(otherP)) {
				if (inSpace(otherP, width, height, depth)) {
					if (hasCube(space, otherP, width, height, depth)) sum++;
					else {
						toProcess.add(otherP);
					}
				}
			}
			otherP = new Point(p.x,p.y,p.z+1);
			if (!processedLocations.contains(otherP)) {
				if (inSpace(otherP, width, height, depth)) {
					if (hasCube(space, otherP, width, height, depth)) sum++;
					else {
						toProcess.add(otherP);
					}
				}
			}
		}
		
//		sum = recurse(p, processedLocations, space, width, height, depth);
		System.out.println(sum);
//		System.out.println((width+2)*(height+2)*(depth+2)-processedLocations.size());
	}

	private final static boolean hasCube(Point[][][] space, Point p, int width, int height, int depth) {
		if (p.x < 0 || p.y < 0 || p.z < 0) return false;
		if (p.x >= width || p.y >= height || p.z >= depth) return false;
		return space[p.x][p.y][p.z] != null;
	}

	private final static boolean inSpace(Point p, int width, int height, int depth) {
		return (p.x >= -1 && p.y >= -1 && p.z >= -1) && (p.x <= width && p.y <= height && p.z <= depth);
	}

	private static int recurse(Point p, Set<Point> processedLocations, Point[][][] space, int width, int height, int depth) {
		if (processedLocations.contains(p)) return 0;
		int result = 0;
		
		if (p.x < -1 || p.y < -1 || p.z < -1) return 0;
		if (p.x > width || p.y > height || p.z > depth) return 0;


		if ( (p.x >= 0 && p.y >= 0 && p.z >= 0) && (p.x < width && p.y < height && p.z < depth)) {
			if (space[p.x][p.y][p.z] != null) return 1;
		}
		
		processedLocations.add(p);
//		System.out.println(p.x +" "+p.y+" "+p.z);
		
		Point op = new Point(p.x-1,p.y,p.z);
		result += recurse(op, processedLocations, space, width, height, depth);
		op = new Point(p.x+1,p.y,p.z);
		result += recurse(op, processedLocations, space, width, height, depth);
		op = new Point(p.x,p.y-1,p.z);
		result += recurse(op, processedLocations, space, width, height, depth);
		op = new Point(p.x,p.y+1,p.z);
		result += recurse(op, processedLocations, space, width, height, depth);
		op = new Point(p.x,p.y,p.z-1);
		result += recurse(op, processedLocations, space, width, height, depth);
		op = new Point(p.x,p.y,p.z+1);
		result += recurse(op, processedLocations, space, width, height, depth);
		return result;
	}


	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================
}

