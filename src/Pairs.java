import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Pairs {

	public static void main(String[] args) throws IOException {
		Stream<String> lines = Files.lines(Paths.get("./input_pairs.txt"));
		List<String> collect = lines.collect(Collectors.toList());
		
		int sum = 0;
		
		for (String line : collect) {
			String[] split = line.split(",");
			String[] bounds1 = split[0].split("-");
			String[] bounds2 = split[1].split("-");
			int lowerbound1 = Integer.parseInt(bounds1[0]);
			int upperbound1 = Integer.parseInt(bounds1[1]);
			int lowerbound2 = Integer.parseInt(bounds2[0]);
			int upperbound2 = Integer.parseInt(bounds2[1]);
			
			Set<Integer> set1 = IntStream.rangeClosed(lowerbound1, upperbound1).mapToObj(i -> (Integer)i).collect(Collectors.toSet());
			Set<Integer> set2 = IntStream.rangeClosed(lowerbound2, upperbound2).mapToObj(i -> (Integer)i).collect(Collectors.toSet());
			
			if (set1.containsAll(set2) || set2.containsAll(set1)) {
				sum++;
			}
		}
		
		System.out.println(sum);
		
		sum = 0;
		
		for (String line : collect) {
			String[] split = line.split(",");
			String[] bounds1 = split[0].split("-");
			String[] bounds2 = split[1].split("-");
			int lowerbound1 = Integer.parseInt(bounds1[0]);
			int upperbound1 = Integer.parseInt(bounds1[1]);
			int lowerbound2 = Integer.parseInt(bounds2[0]);
			int upperbound2 = Integer.parseInt(bounds2[1]);
			
			Set<Integer> set1 = IntStream.rangeClosed(lowerbound1, upperbound1).mapToObj(i -> (Integer)i).collect(Collectors.toSet());
			Set<Integer> set2 = IntStream.rangeClosed(lowerbound2, upperbound2).mapToObj(i -> (Integer)i).collect(Collectors.toSet());
			
			set1.retainAll(set2);
			if (!set1.isEmpty()) sum++;
		}
		
		System.out.println(sum);
	}
	
}
