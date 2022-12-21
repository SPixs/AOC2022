import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Cycles {

	public static void main(String[] args) throws IOException {
			Stream<String> lines = Files.lines(Paths.get("./input_cycles.txt"));
			List<String> collect = lines.collect(Collectors.toList());

			int value = 0;
			List<Integer> xValuesAtCycle = new ArrayList<Integer>();
			int x = 1;
			
			for (String s : collect) {
				if (s.equals("noop")) {
					xValuesAtCycle.add(x);
				}
				else {
					xValuesAtCycle.add(x);
					x += Integer.parseInt(s.split(" ")[1]);
					xValuesAtCycle.add(x);
				}
//				System.out.println(xValuesAtCycle.size() + " " + x);
			}
			
			for (int i=20;i<=220;i+=40) {
				value += i * xValuesAtCycle.get(i-2);
			}
			System.out.println(value);

			for (int clock=0;clock<40*6;clock++) {
				if (clock > 0 && clock % 40 == 0) {
					System.out.println();
				}
				int spriteX = clock > 1 ? xValuesAtCycle.get(clock-1) : 1;
				int beamX = clock % 40;
				if (Math.abs(beamX-spriteX) <= 1) {
					System.out.print('#');
				}
				else {
					System.out.print('.');
				}
			}
			
	}

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================
}

