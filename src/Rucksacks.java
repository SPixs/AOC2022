import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Rucksacks {

	public static void main(String[] args) throws IOException {
		Stream<String> lines = Files.lines(Paths.get("./imput_rucksacks.txt"));
		List<String> collect = lines.collect(Collectors.toList());
		
		int sum = 0;
		
		for (String line : collect) {
			String compartment1  = line.substring(0, line.length()/2);
			String compartment2  = line.substring(line.length()/2, line.length());
			
			Set<Character> set1 = compartment1.chars().mapToObj(c -> (char)c).collect(Collectors.toSet());
			Set<Character> set2 = compartment2.chars().mapToObj(c -> (char)c).collect(Collectors.toSet());
			
			set1.retainAll(set2);
			
			char commonItem = set1.iterator().next();
			int priority = 0;
			if (commonItem >= 'a' && commonItem <= 'z') priority = 1 + commonItem - 'a';
			if (commonItem >= 'A' && commonItem <= 'Z') priority = 27 + commonItem - 'A';
			sum += priority;
			
//			System.out.println(set1 + " " + priority);
		}
		System.out.println(sum);

		sum = 0;
		
		for (int i=0;i<collect.size();i+=3) {
			String rucksack1 = collect.get(i);
			String rucksack2 = collect.get(i+1);
			String rucksack3 = collect.get(i+2);
			
			Set<Character> set1 = rucksack1.chars().mapToObj(c -> (char)c).collect(Collectors.toSet());
			Set<Character> set2 = rucksack2.chars().mapToObj(c -> (char)c).collect(Collectors.toSet());
			Set<Character> set3 = rucksack3.chars().mapToObj(c -> (char)c).collect(Collectors.toSet());
			
			set1.retainAll(set2);
			set1.retainAll(set3);
//			System.out.println(set1);
			
			char badge = set1.iterator().next();
			int priority = 0;
			if (badge >= 'a' && badge <= 'z') priority = 1 + badge - 'a';
			if (badge >= 'A' && badge <= 'Z') priority = 27 + badge - 'A';
			sum += priority;
		}
		System.out.println(sum);
	}
}
