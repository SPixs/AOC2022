import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Trees {

	public static void main(String[] args) throws IOException {
			Stream<String> lines = Files.lines(Paths.get("./input_trees.txt"));
			List<String> collect = lines.collect(Collectors.toList());
			
			int[][] trees = null;

			int rowIndex = 0;
			for (String s : collect) {
				int width = s.length();
				if (trees == null) {
					trees = new int[width][collect.size()];
				}
				for (int x=0;x<width;x++) {
					trees[x][rowIndex] = s.charAt(x)-'0';
				}
				rowIndex++;
			}			

			int width = trees.length;
			int height = trees[0].length;
			
			final int[][] finalTrees = trees;
			
			int visibleCount = 0;
			int highestScenicScore = 0;
			
			for (int x=0;x<width;x++) {
				for (int y=0;y<height;y++) {
					final int finalX = x;
					final int finalY = y;
					
					int treeHeight = trees[x][y];
					int maxLeft = IntStream.range(0, finalX).map(tmpX -> finalTrees[tmpX][finalY]).max().orElse(-1);
					int maxRight = IntStream.range(finalX+1, width).map(tmpX -> finalTrees[tmpX][finalY]).max().orElse(-1);
					int maxUp = IntStream.range(0, finalY).map(tmpY -> finalTrees[finalX][tmpY]).max().orElse(-1);
					int maxDown = IntStream.range(finalY+1, height).map(tmpY -> finalTrees[finalX][tmpY]).max().orElse(-1);
					
					boolean visible = maxLeft < treeHeight || maxRight < treeHeight || maxUp < treeHeight || maxDown < treeHeight;
					if (visible) visibleCount++;
					
//					System.out.println(x + " " + y + " " + visible);
					
					int visibleTreesUp = 0;
					for (int vy=y-1;vy>=0;vy--) {
						int vheight = finalTrees[x][vy];
						visibleTreesUp++;
						if (vheight >= treeHeight) break;
					}
					
					int visibleTreesDown = 0;
					for (int vy=y+1;vy<height;vy++) {
						int vheight = finalTrees[x][vy];
						visibleTreesDown++;
						if (vheight >= treeHeight) break;
					}

					int visibleTreesLeft = 0;
					for (int vx=x-1;vx>=0;vx--) {
						int vheight = finalTrees[vx][y];
						visibleTreesLeft++;
						if (vheight >= treeHeight) break;
					}

					int visibleTreesRight = 0;
					for (int vx=x+1;vx<width;vx++) {
						int vheight = finalTrees[vx][y];
						visibleTreesRight++;
						if (vheight >= treeHeight) break;
					}

					int scenicScore =  visibleTreesUp * visibleTreesDown * visibleTreesLeft * visibleTreesRight;
					highestScenicScore = Math.max(highestScenicScore, scenicScore);
					
				}
			}
			
			System.out.println(visibleCount);
			System.out.println(highestScenicScore);
	}

}

