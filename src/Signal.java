import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Signal {

	public static void main(String[] args) throws IOException {
			Stream<String> lines = Files.lines(Paths.get("./input_signal.txt"));
			List<String> collect = lines.collect(Collectors.toList());

			for (String s : collect) {
				for (int i=4;i<s.length()-1;i++) {
					Set<Character> start = IntStream.range(i-4, i).mapToObj(p -> s.charAt(p)).collect(Collectors.toSet());
					if (start.size() == 4) {
						System.out.println(i);
						break;
					}
				}
			}
			
			for (String s : collect) {
				for (int i=14;i<s.length()-1;i++) {
					Set<Character> start = IntStream.range(i-14, i).mapToObj(p -> s.charAt(p)).collect(Collectors.toSet());
					if (start.size() == 14) {
						System.out.println(i);
						break;
					}
				}
			}
	}
}

