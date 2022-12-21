import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MonkeysTalk {

	public static class Monkey {

		private char operator;
		private String m_operand1Name;
		private String m_operand2Name;
		private BigInteger m_number = null;
		private String name;

		public Monkey(String name) {
			this.name = name;
		}

		public void setOperator(char operator) {
			this.operator = operator;
		}

		public void setYelledNumber(int number) {
			m_number = BigInteger.valueOf(number);
		}

		public void setMonkeyOperand1(String name) {
			m_operand1Name = name;
		}

		public void setMonkeyOperand2(String name) {
			m_operand2Name = name;
		}

		public void resolve(Map<String, Monkey> monkeysMap, Set<Monkey> unresolvedMonkeys) {
			if (m_number != null) {
				unresolvedMonkeys.remove(this);
			}
			else {
				Monkey monkey1 = monkeysMap.get(m_operand1Name);
				Monkey monkey2 = monkeysMap.get(m_operand2Name);
				if (!unresolvedMonkeys.contains(monkey1) && !unresolvedMonkeys.contains(monkey2)) {
					BigInteger operand1 = monkey1.m_number;
					BigInteger operand2 = monkey2.m_number;
					switch (operator) {
						case '+':
							m_number = operand1.add(operand2);
							break;
						case '-':
							m_number = operand1.subtract(operand2);
							break;
						case '*':
							m_number = operand1.multiply(operand2);
							break;
						case '/':
							m_number = operand1.divide(operand2);
							break;
						default:
							throw new RuntimeException();
					}
					unresolvedMonkeys.remove(this);
				}
			}
		}

		public void collect(Map<String, Monkey> monkeysMap, Map<String, Monkey> dependantMonkys) {
			dependantMonkys.put(name, this);
			if (m_operand1Name != null) monkeysMap.get(m_operand1Name).collect(monkeysMap, dependantMonkys);
			if (m_operand2Name != null) monkeysMap.get(m_operand2Name).collect(monkeysMap, dependantMonkys);
		}

		public void forward(Map<String, Monkey> monkeysMap, BigInteger rootValue) {
			if (m_number != null) throw new IllegalStateException(m_number + " " + rootValue);
			m_number = rootValue;
			if (m_operand1Name == null) return;
			Monkey monkey1 = monkeysMap.get(m_operand1Name);
			Monkey monkey2 = monkeysMap.get(m_operand2Name);
			BigInteger operand1 = monkey1.resolve(monkeysMap);
			BigInteger operand2 = monkey2.resolve(monkeysMap);
			
			if (operand1 == null && operand2 == null) throw new IllegalStateException();
			if (operand1 != null) {
				switch (operator) {
					case '+':
						monkey2.forward(monkeysMap, rootValue.subtract(operand1));
						break;
					case '-':
						monkey2.forward(monkeysMap, rootValue.subtract(operand1).negate());
						break;
					case '*':
						monkey2.forward(monkeysMap, rootValue.divide(operand1));
						break;
					case '/':
						monkey2.forward(monkeysMap, operand1.divide(rootValue));
						break;
					default:
						throw new RuntimeException();
				}
			}
			else {
				switch (operator) {
				case '+':
					monkey1.forward(monkeysMap, rootValue.subtract(operand2));
					break;
				case '-':
					monkey1.forward(monkeysMap, rootValue.add(operand2));
					break;
				case '*':
					monkey1.forward(monkeysMap, rootValue.divide(operand2));
					break;
				case '/':
					monkey1.forward(monkeysMap, operand2.multiply(rootValue));
					break;
				default:
					throw new RuntimeException();
			}
				
			}
		}

		private BigInteger resolve(Map<String, Monkey> monkeysMap) {
			if (m_number != null) return m_number;
			if (m_operand1Name == null) return null;
			Monkey monkey1 = monkeysMap.get(m_operand1Name);
			Monkey monkey2 = monkeysMap.get(m_operand2Name);
			BigInteger operand1 = monkey1.resolve(monkeysMap);
			BigInteger operand2 = monkey2.resolve(monkeysMap);		
			if (operand1 == null || operand2 == null) return null;
			switch (operator) {
				case '+':
					return operand1.add(operand2);
				case '-':
					return operand1.subtract(operand2);
				case '*':
					return operand1.multiply(operand2);
				case '/':
					return operand1.divide(operand2);
				default:
					throw new RuntimeException();
				}
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
			Stream<String> lines = Files.lines(Paths.get("./input_monkeysTalk.txt"));
			List<String> collect = lines.collect(Collectors.toList());

			List<Monkey> monkeys = new ArrayList<Monkey>();
			
			long currentTime = System.nanoTime();
			createMonkeys(collect, monkeys);
			System.out.println((System.nanoTime()-currentTime) / 0.000001d +"ms");
	}


	private static void createMonkeys(List<String> collect, List<Monkey> monkeys) {
		
		Map<String, Monkey> monkeysMap = new HashMap<String, Monkey>();
//		Set<Monkey> resolvedMonkeys = new HashSet<Monkey>();
		Set<Monkey> unresolvedMonkeys = new HashSet<Monkey>();
		
		initData(collect, monkeysMap, unresolvedMonkeys);
		
		while (!unresolvedMonkeys.isEmpty()) {
			for (Monkey monkey : new HashSet<Monkey>(unresolvedMonkeys)) {
				monkey.resolve(monkeysMap, unresolvedMonkeys);
			}
		}
		
		System.out.println(monkeysMap.get("root").m_number);
		
		initData(collect, monkeysMap, unresolvedMonkeys);
		Monkey rootMonkey = monkeysMap.get("root");
		Monkey monkeyToReduce = null;
		Monkey monkeyToForward = null;
		
		Map<String,  Monkey> dependantMonkys = new HashMap<String, Monkey>();
		monkeysMap.get(rootMonkey.m_operand1Name).collect(monkeysMap, dependantMonkys);
		if (dependantMonkys.containsKey("humn")) {
			dependantMonkys = new HashMap<String, Monkey>();
			monkeysMap.get(rootMonkey.m_operand2Name).collect(monkeysMap, dependantMonkys);
			monkeyToForward = monkeysMap.get(rootMonkey.m_operand1Name);
			monkeyToReduce = monkeysMap.get(rootMonkey.m_operand2Name);
			unresolvedMonkeys = new HashSet<Monkey>(dependantMonkys.values());
		}
		else {
			monkeyToForward = monkeysMap.get(rootMonkey.m_operand2Name);
			monkeyToReduce = monkeysMap.get(rootMonkey.m_operand1Name);
			unresolvedMonkeys = new HashSet<Monkey>(dependantMonkys.values());
		}
		while (!unresolvedMonkeys.isEmpty()) {
			for (Monkey monkey : new HashSet<Monkey>(unresolvedMonkeys)) {
				monkey.resolve(monkeysMap, unresolvedMonkeys);
			}
		}
		BigInteger rootValue = monkeyToReduce.m_number;
		System.out.println("Root equality value is " + rootValue);
		monkeysMap.get("humn").m_number = null;
		monkeyToForward.forward(monkeysMap, rootValue);
		System.out.println(monkeysMap.get("humn").m_number);
		
		// 5821816291773 false...
	}


	private static void initData(List<String> collect, Map<String, Monkey> monkeysMap, Set<Monkey> unresolvedMonkeys) {
		monkeysMap.clear();
		unresolvedMonkeys.clear();
		for (String s : collect) {
			String[] split = s.split(":");
			String name = split[0];
			Monkey monkey = new Monkey(name);
			if (isNumeric(split[1].trim())) {
				monkey.setYelledNumber(Integer.parseInt(split[1].trim()));
			}
			else {
				String operation[] = split[1].trim().split(" ");
				monkey.setMonkeyOperand1(operation[0]);
				monkey.setMonkeyOperand2(operation[2]);
				monkey.setOperator(operation[1].charAt(0));
			}
			monkeysMap.put(name,  monkey);
			unresolvedMonkeys.add(monkey);
		}
	}
	
	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================
}

