import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.IntBinaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Monkeys {

	public static class Monkey {

		private List<Long>items;
		private char operator;
		private int trueMonkeyTarget;
		private int testValue;
		private int falseMonkeyTarget;
		private int operandIntValue;
		private String operandVariable;
		private int processItems;

		public void setItems(List<Long> items) {
			this.items = items;
		}

		public void setOperator(char operator) {
			this.operator = operator;
		}

		public void setTestValue(int testValue) {
			this.testValue = testValue;
		}

		public void setTrueResultTargetMonkey(int monkeyTarget) {
			this.trueMonkeyTarget = monkeyTarget;
		}

		public void setFalseResultTargetMonkey(int monkeyTarget) {
			this.falseMonkeyTarget = monkeyTarget;
		}

		public void simulate(List<Monkey> monkeys, boolean divideWorry, int lcm) {
			while (!items.isEmpty()) {
				processItems++;
				long item = items.remove(0);
				long newValue = computeNewWorry(item);
				if (divideWorry)
					newValue = newValue / 3;
				else {
					newValue = newValue % lcm;
				}
				boolean result = newValue % getDivisor() == 0;
				if (result) {
					monkeys.get(trueMonkeyTarget).addItem(newValue);
				}
				else {
					monkeys.get(falseMonkeyTarget).addItem(newValue);
				}
			}
		}

		private int getDivisor() {
			return testValue;
		}

		public int getProcessItems() {
			return processItems;
		}

		private void addItem(long item) {
			items.add(item);
		}

		private long computeNewWorry(long item) {
			
			long operand = operandIntValue;
			if (operandVariable != null) {
				if (!operandVariable.equals("old")) throw new IllegalArgumentException(operandVariable);
				operand = item;
			}
			
			switch (operator) {
				case '+': return item + operand;
//				case '-': return item - operand;
				case '*': return item * operand;
//				case '/': return item.divide(operand);
				default:
					throw new RuntimeException("unknown operator");
			}
		}

		public void setOperand(int value) {
			this.operandIntValue = value;
		}

		public void setOperand(String operand) {
			this.operandVariable = operand;
		}

		public List<Long> getItems() {
			return items;
		}

	}

	static Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

	public static boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false; 
	    }
	    return pattern.matcher(strNum).matches();
	}

	
	public static void main(String[] args) throws IOException {
			Stream<String> lines = Files.lines(Paths.get("./input_monkeys.txt"));
			List<String> collect = lines.collect(Collectors.toList());

			List<Monkey> monkeys = new ArrayList<Monkey>();
			createMonkeys(collect, monkeys);
			
			for (int i=0;i<20;i++) {
				monkeys.forEach(m -> m.simulate(monkeys, true, 0));
			}
			
			List<Integer> inspections = monkeys.stream().map(Monkey::getProcessItems).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
			System.out.println(inspections.get(0) * inspections.get(1));

			monkeys.clear();
			createMonkeys(collect, monkeys);
			
			IntBinaryOperator lcmOperator = new IntBinaryOperator() {
				public int applyAsInt(int left, int right) {
					return lcm(left, right);
				}
			};
			int lcm = monkeys.stream().mapToInt(Monkey::getDivisor).reduce(lcmOperator).orElse(0);
			
			for (int i=0;i<10000;i++) {
				monkeys.forEach(m -> m.simulate(monkeys, false, lcm));
//				System.out.println(i);
				System.out.println("Round " + (i+1));
				System.out.println(monkeys.stream().map(Monkey::getProcessItems).collect(Collectors.toList()));
				System.out.println();
			}
			
			inspections = monkeys.stream().map(Monkey::getProcessItems).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
			System.out.println((long)inspections.get(0) * (long)inspections.get(1));

	}


	private static void createMonkeys(List<String> collect, List<Monkey> monkeys) {
		Monkey currentMonkey = null;
		
		
		for (String s : collect) {
			if (s.contains("Monkey")) {
				currentMonkey = new Monkey();
				monkeys.add(currentMonkey);
			}
			else {
				if (s.contains("Starting")) {
					String itemsList = s.split(":")[1];
					List<Long> items = Stream.of(itemsList.split(",")).map(String::trim).map(Long::parseLong).collect(Collectors.toList());
					currentMonkey.setItems(items);
				}
				if (s.contains("Operation")) {
					String operation = s.split("=")[1].trim();
					char operator = operation.charAt(4);
					currentMonkey.setOperator(operator);
					String operand = operation.substring(5).trim();
					if (isNumeric(operand)) {
						int value = Integer.parseInt(operation.substring(5).trim());
						currentMonkey.setOperand(value);
					}
					else {
						currentMonkey.setOperand(operand);
					}
				}
				if (s.contains("Test")) {
					int testValue = Integer.parseInt(s.split("by")[1].trim());
					currentMonkey.setTestValue(testValue);
				}
				if (s.contains("If true")) {
					int monkeyTarget = Integer.parseInt(s.split("monkey")[1].trim());
					currentMonkey.setTrueResultTargetMonkey(monkeyTarget);
				}
				if (s.contains("If false")) {
					int monkeyTarget = Integer.parseInt(s.split("monkey")[1].trim());
					currentMonkey.setFalseResultTargetMonkey(monkeyTarget);
				}
			}
		}
	}
	
	/**
	 * @param number1
	 * @param number2
	 * @return le plus grand entier multiple commun
	 */
	public static int lcm(int number1, int number2) {
	    if (number1 == 0 || number2 == 0) {
	        return 0;
	    }
	    int absNumber1 = Math.abs(number1);
	    int absNumber2 = Math.abs(number2);
	    int absHigherNumber = Math.max(absNumber1, absNumber2);
	    int absLowerNumber = Math.min(absNumber1, absNumber2);
	    int lcm = absHigherNumber;
	    while (lcm % absLowerNumber != 0) {
	        lcm += absHigherNumber;
	    }
	    return lcm;
	}

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================
}

