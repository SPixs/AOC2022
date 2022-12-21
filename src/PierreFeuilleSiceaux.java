import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PierreFeuilleSiceaux {

	public static void main(String[] args) throws IOException {
		Stream<String> lines = Files.lines(Paths.get("./input_02122022.txt"));
//		Stream<String> lines = Files.lines(Paths.get("./input_test.txt"));
		List<String> collect = lines.collect(Collectors.toList());
		
		int totalScore = 0;
		
		for (String rule : collect) {
			char opponent = rule.charAt(0);
			char mine = rule.charAt(2);
			
			boolean win = 
					(opponent == 'A' && mine == 'Y') ||
					(opponent == 'B' && mine == 'Z') ||
					(opponent == 'C' && mine == 'X');
			boolean draw = (opponent - 'A') == (mine - 'X');
			
			int score = 1 + (mine - 'X');
			
			score += win ? 6 : (draw ? 3 : 0);
			
			totalScore += score;
		}
		
		System.out.println(totalScore);
		
		totalScore = 0;
		
		for (String rule : collect) {
			char opponent = rule.charAt(0);
			char goal = rule.charAt(2);
			boolean mustWin = goal == 'Z';
			boolean mustDraw = goal == 'Y';
			
			int mine = 0;
			if (mustWin) {
				mine = opponent == 'A' ? 1 : (opponent == 'B' ? 2 : 0);
			}
			else if (mustDraw) {
				mine = (opponent - 'A');
			}
			else {
				mine = opponent == 'A' ? 2 : (opponent == 'B' ? 0 : 1);
			}
			
			int score = 1 + mine;
			score += mustWin ? 6 : (mustDraw ? 3 : 0);
			
			totalScore += score;
		}
		
		System.out.println(totalScore);
		
		
//		System.out.println(collect);
//		List<Integer> food = new ArrayList<Integer>();
//		int value = 0;
//		for (String s : collect) {
//			if (s.isEmpty()) {
//				food.add(value);
//				value = 0;
//			}
//			else {
//				value += Integer.parseInt(s);
//			}
//		}
//		System.out.println(food);
//		System.err.println(Collections.max(food));
//		System.out.println(food.indexOf(Collections.max(food))+1);
//		
//		Collections.sort(food);
//		Collections.reverse(food);
//		System.out.println(food);
//		
//		System.out.println(food.get(0) + food.get(1) + food.get(2));
}
	
}
