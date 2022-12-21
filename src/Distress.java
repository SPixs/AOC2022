import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Distress {

	public static abstract class Node implements Comparable<Node> {
		protected Node parent = null;
		protected List<Node> children = new ArrayList<Distress.Node>();
		public Node getParent() {
			return parent;
		}
		public void add(Node newNode) {
			children.add(newNode);
		}
		
		public int compareTo(Node rightTree) {
//			int index = 0;
//			Node leftChild = getChildAt(index);
//			Node rightChild = rightTree.getChildAt(index);
//			if (leftChild == null && rightChild != null) return true;
//			if (leftChild != null && rightChild == null) return false;
			return compareToImpl(rightTree);
		}
		
		protected abstract int compareToImpl(Node rightTree);
		
		protected Node getChildAt(int index) {
			return index >= children.size() ? null : children.get(index);
		}
//		protected abstract boolean isRightOrderTyped(IntegerNode integerNode);
//		protected abstract boolean isRightOrderTyped(ListNode listNode);
		protected abstract String asText();
		@Override
		public String toString() {
			return asText();
		}
		
	}
	
	public static class IntegerNode extends Node {

		private int number;

		public IntegerNode(int number) {
			this.number = number;
		}

		@Override
		protected int compareToImpl(Node rightTree) {
			if (rightTree instanceof IntegerNode) {
				return compareToTyped((IntegerNode)rightTree);
			}
			else {
				return compareToTyped((ListNode)rightTree);
			}
		}

		protected int compareToTyped(IntegerNode integerNode) {
			return Integer.compare(number, integerNode.number);
		}

		protected int compareToTyped(ListNode listNode) {
			ListNode promotedList = new ListNode(parent);
			promotedList.add(new IntegerNode(number));
			int result = promotedList.compareToTyped(listNode);
			return result;
		}
		
		public String asText() {
			return String.valueOf(number);
		}
		
	}

	public static class ListNode extends Node {

		public ListNode(Node parent) {
			this.parent = parent;
		}

		@Override
		protected int compareToImpl(Node rightTree) {
			if (rightTree instanceof IntegerNode) {
				return compareToTyped((IntegerNode)rightTree);
			}
			else {
				return compareToTyped((ListNode)rightTree);
			}
		}

		protected int compareToTyped(IntegerNode integerNode) {
			ListNode promotedList = new ListNode(integerNode.parent);
			promotedList.add(new IntegerNode(integerNode.number));
			int result = compareTo(promotedList);
			return result;
		}

		protected int compareToTyped(ListNode listNode) {
			for (int i=0;i<Math.max(children.size(), listNode.children.size());i++) {
				Node leftChild = getChildAt(i);
				Node rightChild = listNode.getChildAt(i);
				if (leftChild == null && rightChild != null) return -1;
				else if (leftChild != null && rightChild == null) return 1;
				else {
					int compare = leftChild.compareTo(rightChild);
					if (compare != 0) return compare;
				}
			}
			return 0;
		}
		
		public String asText() {
			StringBuffer result = new StringBuffer();
			result.append('[');
			for (int i=0;i<children.size();i++) {
				result.append(children.get(i).asText());
				if (i<children.size()-1) result.append(",");
			}
			result.append(']');
			return result.toString();
		}
	}

	public static void main(String[] args) throws IOException {
		Stream<String> lines = Files.lines(Paths.get("./input_distress.txt"));
		List<String> collect = lines.collect(Collectors.toList());

		Node leftTree = null;
		Node rightTree = null;
		int pairIndex = 1;
		int sum = 0;
		
		for (String s : collect) {
			if (s.isEmpty()) {
				if (pairIndex == 2) {
					Thread.yield();
				}
				if (isRightOrder(leftTree, rightTree)) {
					System.out.println(">> " + pairIndex);
					sum += pairIndex;
				}
				pairIndex++;
				leftTree = null;
				rightTree = null;
			}
			else {
				if (pairIndex == 8) {
					Thread.yield();
				}
				if (leftTree == null) leftTree = parse(s);
				else {
					if (rightTree == null) rightTree = parse(s);
				}
			}
		}
		
		System.out.println(sum);
		
		List<Node> allNodes = new ArrayList<Distress.Node>();
		
		for (String s : collect) {
			if (!s.isEmpty()) {
				allNodes.add(parse(s));
			}
		}

		Node divider1 = new ListNode(null);
		divider1.add(new IntegerNode(2));
		allNodes.add(divider1);
		
		Node divider2 = new ListNode(null);
		divider2.add(new IntegerNode(6));
		allNodes.add(divider2);
		
		Collections.sort(allNodes);
//		allNodes.forEach(n -> System.out.println(n));
		int result = (allNodes.indexOf(divider1) + 1) * (allNodes.indexOf(divider2) + 1);
		System.out.println(result);
	}

	private static boolean isRightOrder(Node leftTree, Node rightTree) {
		return leftTree.compareTo(rightTree) < 0;
	}

	private static Node parse(String s) {
		char[] charArray = s.toCharArray(); 
		int index = 0;
		Integer currentNumber = null;
		Node rootNode = null;
		Node currentNode = null;
		while (index < charArray.length) {
			char c = charArray[index];
			if (c == '[') {
				Node newNode = new ListNode(currentNode);
				if (currentNode == null) { 
					rootNode = newNode;
				}
				else {
					currentNode.add(newNode);
				}
				currentNode = newNode;
				currentNumber = null;
			}
			else if (c == ']') {
				if (currentNumber != null) {
					currentNode.add(new IntegerNode(currentNumber));
					currentNumber = null;
				}
				currentNode = currentNode.getParent();
			}
			else if (c == ',') {
				if (currentNumber != null) {
					currentNode.add(new IntegerNode(currentNumber));
				}
				currentNumber = null;
			}
			else {
				int digit = c - '0';
				currentNumber = currentNumber == null ? digit : (currentNumber * 10 + digit);
			}
			index++;
		}
		return rootNode;
	}

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================
}

