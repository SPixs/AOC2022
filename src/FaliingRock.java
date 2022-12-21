import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FaliingRock {

	public static void main(String[] args) throws IOException {
		Stream<String> lines = Files.lines(Paths.get("./input_fallingRocks.txt"));
		List<String> collect = lines.collect(Collectors.toList());

		String gasPattern = collect.get(0);
		
//		gasPattern = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>";
		System.out.println(gasPattern.length());
		
		PiecesFactory factory = new PiecesFactory();
		
		Playfield playfield = new Playfield(); 
		long rocksStopped = 0;

		Piece fallingPiece = factory.createNext();
		long highestY = 0;
		fallingPiece.move(2, highestY + 3);
		int gasIndex = -1;
		
		long max = 1000000000000l;
//		long max = 2022;
		
		Map<Long, Long> heightForHash = new HashMap<Long, Long>();
		Map<Long, Long> fallentForHash = new HashMap<Long, Long>();
		long heightToAdd = 0;
		boolean patternFound = false;
		
		// 1507692307710 too low ???
		
		while (rocksStopped < max) {
			
//			if (highestY == 102)
//				displayPlayfied(playfield);
				
			// get next gas direction
			gasIndex = (++gasIndex) % gasPattern.length();
			char direction = gasPattern.charAt(gasIndex);
			switch (direction) {
			case '<':
				if (canGoLeft(fallingPiece, playfield)) {
					fallingPiece.move(-1, 0);
				}
				break;
			case '>':
				if (canGoRight(fallingPiece, playfield)) {
					fallingPiece.move(1, 0);
				}
				break;
			default:
				throw new RuntimeException();
			}
			
//			displayPlayfied(playfield);
			
			if(canFall(fallingPiece, playfield)) {
				fallingPiece.move(0, -1);
//				displayPlayfied(playfield);
			}
			else {
//				int maxHeight = fallingPiece.y+fallingPiece.getHeight()-1;
//				System.out.println(maxHeight);
				rocksStopped++;
				updatePlayfield(playfield, fallingPiece);
				highestY =  playfield.getHeight();
				fallingPiece = factory.createNext();
				if (rocksStopped % 100000 == 0)
					System.out.println((rocksStopped*100.0f/max) + "% " + highestY);
				fallingPiece.move(2, highestY + 3);
				
				if (playfield.getLinesHash(12) == -306152858) {
					playfield.display(fallingPiece);
				}
				
				// check hash
				if (!patternFound) {
					long[] arrayToHash = new long[] { playfield.getLinesHash(12), factory.index, Integer.hashCode(gasIndex) };
					long playfieldHash = Arrays.hashCode(arrayToHash);
					
					if (playfield.getHeight() > 100 & heightForHash.containsKey(playfieldHash) && playfield.getHeight() > heightForHash.get(playfieldHash)) {
						patternFound = true;
						System.out.println("Known hash !"); 
						System.out.println(playfield.height + " " + playfield.getLinesHash(12) + " " + fallingPiece.type + " " + fallingPiece.x + " " + gasIndex);
						long deltaHeight = playfield.getHeight() - heightForHash.get(playfieldHash);
						long deltaFallen = rocksStopped - fallentForHash.get(playfieldHash);
						long patternRepeatCount = (max - rocksStopped) / deltaFallen;
						rocksStopped += patternRepeatCount * deltaFallen;
						heightToAdd = deltaHeight * patternRepeatCount;
					}
					else { 
						heightForHash.put(playfieldHash, playfield.getHeight()); 
						fallentForHash.put(playfieldHash, rocksStopped); 
					}
				}
			}
		}
		System.out.println(highestY + heightToAdd);
	}
	
	private static class Playfield {
		
		Map<Pair<Long, Long>, Integer> data = new HashMap<Pair<Long, Long>, Integer>();
		private long height;
		
		public void set(long x, long y, int val) {
			data.put(Pair.create(x, y), val);
		}
		
		public int get(long x, long y) {
			if (y < 0) return 1;
			return data.getOrDefault(Pair.create(x, y), 0);
		}
		
//		public void reduce(long maxY) {
//			Iterator<Pair<Long, Long>> iterator = data.keySet().iterator();
//			while (iterator.hasNext()) {
//				Pair<Long, Long> next = iterator.next();
//				if (next.value < maxY - 20) iterator.remove();
//			}
//		}

		public long getHeight() {
			return height;
		}

		public void setHeight(long height) {
			this.height = height;
		}
		
		public long getLinesHash(int lineCount) {
			int[] content = new int[lineCount*7];
			int index = 0;
			for (long l=getHeight();l>getHeight()-lineCount;l--) {
				for (int x=0;x<7;x++) {
					content[index++] = get(x,l-1);
				}
			}
			return Arrays.hashCode(content);
		}
		
		public void display(Piece piece) {
			for (int y=120;y>=-1;y--) {
				for (int x=-1;x<=7;x++) {
					char c = ' ';
					if (y<0) c=('+');
					else if (x==-1 || x == 7) c=('+');
					else c = (get(x,y) == 1 ? '#' : ' ');
					
					if (piece != null) {
						int px = (int) (x-piece.x);
						int py = (int) (y-piece.y);
						if (px >= 0 && px < piece.getWidht() && py >= 0 && py < piece.getHeight() && piece.shape[px][py] == 1) {
							c = ('@');
						}
					}
					System.out.print(c);
				}
				System.out.println();
			}
			System.out.println();
		}
	}
	
	private static boolean canGoRight(Piece fallingPiece, Playfield playfield) {
		for (int x =0;x<fallingPiece.getWidht();x++) {
			for (int y=0;y<fallingPiece.getHeight();y++) {
				long px = x+fallingPiece.x+1;
				long py = y+fallingPiece.y;
				if (fallingPiece.shape[x][y]==1 && (px>6 || playfield.get(px,py)==1)) {
					return false;
				}
			}
		}
		return true;
	}

	private static boolean canGoLeft(Piece fallingPiece, Playfield playfield) {
		for (int x =0;x<fallingPiece.getWidht();x++) {
			for (int y=0;y<fallingPiece.getHeight();y++) {
				long px = x+fallingPiece.x-1;
				long py = y+fallingPiece.y;
				if (fallingPiece.shape[x][y]==1 && (px<0 || playfield.get(px,py)==1)) {
					return false;
				}
			}
		}
		return true;
	}

	private static void updatePlayfield(Playfield playfield, Piece piece) {
		for (int x =0;x<piece.getWidht();x++) {
			for (int y=0;y<piece.getHeight();y++) {
				if (piece.shape[x][y]==1) {
					playfield.set(piece.x+x, piece.y+y, 1);
				}
			}
		}
		
		playfield.setHeight(Math.max(playfield.getHeight(), piece.y + piece.getHeight()));
		
//		displayPlayfied(playfield);
	}

	private static boolean canFall(Piece fallingPiece, Playfield playfield) {
		for (int x =0;x<fallingPiece.getWidht();x++) {
			for (int y=0;y<fallingPiece.getHeight();y++) {
				long px = x+fallingPiece.x;
				long py = y+fallingPiece.y-1;
				if (py < 0 || (fallingPiece.shape[x][y]==1 && playfield.get(px, py)==1)) {
					return false;
				}
			}
		}
		return true;
	}

//	private static int getHighest(int[][] playfield) {
//		int top = 0;
//		boolean empty = false;
//		while (!empty) {
//			for (int x=0;x<)
//		}
//				
//		while (playfield[2][top] != 0) {
//			top++;
//		}
//		return top;
//	}

	public static class PiecesFactory {
		
		int index = -1;
		
		public Piece createNext() {
			index = (++index) % 5;
			switch (index) {
				case 0: return new Piece1();
				case 1: return new Piece2();
				case 2: return new Piece3();
				case 3: return new Piece4();
				case 4: return new Piece5();
				default:
					throw new RuntimeException();
			}
		}
	}

	public static class Piece {
		
		private long x;
		private long y;
		protected long type;
		
		protected int[][] shape;

		private void move(long dx, long dy) {
			x += dx;
			y += dy;
		}
		
		public int getWidht() { return shape.length; }
		public int getHeight() { return shape[0].length; }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (type ^ (type >>> 32));
			result = prime * result + (int) (x ^ (x >>> 32));
			result = prime * result + (int) (y ^ (y >>> 32));
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
			Piece other = (Piece) obj;
			if (!Arrays.deepEquals(shape, other.shape))
				return false;
			if (type != other.type)
				return false;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
	}

	public static class Piece1 extends Piece {
		public Piece1() {
			type = 0;
			shape = new int[4][1];
			shape[0][0]=shape[1][0]=shape[2][0]=shape[3][0]=1;
		}
	}
	
	public static class Piece2 extends Piece {
		public Piece2() {
			type = 1;
			shape = new int[3][3];
			shape[1][0]=shape[1][1]=shape[1][2]=shape[0][1]=shape[2][1]=1;
		}
	}

	public static class Piece3 extends Piece {
		public Piece3() {
			type = 2;
			shape = new int[3][3];
			shape[0][0]=shape[1][0]=shape[2][0]=shape[2][1]=shape[2][2]=1;
		}
	}
	
	public static class Piece4 extends Piece {
		public Piece4() {
			type = 3;
			shape = new int[1][4];
			shape[0][0]=shape[0][1]=shape[0][2]=shape[0][3]=1;
		}
	}

	public static class Piece5 extends Piece {
		public Piece5() {
			type = 4;
			shape = new int[2][2];
			shape[0][0]=shape[0][1]=shape[1][0]=shape[1][1]=1;
		}
	}

	public static class Pair<K, V> {
	    
	    private final K key;
	    private final V value;

	    public Pair(K k, V v) {
	        key = k;
	        value = v;
	    }
	    public K getKey() { return key; }
	    public V getValue() { return value; }

	    public boolean equals(Object o) {
	        if (this == o) {
	            return true;
	        }
	        if (!(o instanceof Pair)) {
	            return false;
	        } else {
	            Pair<?, ?> oP = (Pair<?, ?>) o;
	            return (key == null ?
	                    oP.key == null :
	                    key.equals(oP.key)) &&
	                (value == null ?
	                 oP.value == null :
	                 value.equals(oP.value));
	        }
	    }

	    public int hashCode() {
	        int result = key == null ? 0 : key.hashCode();

	        final int h = value == null ? 0 : value.hashCode();
	        result = 37 * result + h ^ (h >>> 16);

	        return result;
	    }

	    @Override
	    public String toString() {
	        return "[" + getKey() + ", " + getValue() + "]";
	    }

	    public static <K, V> Pair<K, V> create(K k, V v) {
	        return new Pair<K, V>(k, v);
	    }
	}
}
