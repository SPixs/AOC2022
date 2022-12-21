import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Food {

	public static void main(String[] args) throws IOException {
			Stream<String> lines = Files.lines(Paths.get("./input.txt"));
			List<String> collect = lines.collect(Collectors.toList());
			List<Integer> food = new ArrayList<Integer>();
			int value = 0;
			for (String s : collect) {
				if (s.isEmpty()) {
					food.add(value);
					value = 0;
				}
				else {
					value += Integer.parseInt(s);
				}
			}
			System.out.println(food);
			System.err.println(Collections.max(food));
			System.out.println(food.indexOf(Collections.max(food))+1);
			
			Collections.sort(food);
			Collections.reverse(food);
			System.out.println(food);
			
			System.out.println(food.get(0) + food.get(1) + food.get(2));
	}

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================
}

