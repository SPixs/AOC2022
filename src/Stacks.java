import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Stacks {

	public static class Move {

		private int count;
		private int from;
		private int to;

		public Move(int from, int to, int count) {
			this.count = count;
			this.from = from;
			this.to = to;
		}

		@Override
		public String toString() {
			return "move " + count + " from " + from + " to " + to;
		}

		public void perform(Stack[] stacks) {
			for (int i=0;i<count;i++) {
				char c = stacks[from-1].removeTop();
				stacks[to-1].addTop(c);
			}
		}

		public void performKeepOrder(Stack[] stacks) {
			List<Character> toAdd = new ArrayList<Character>();
			for (int i=0;i<count;i++) {
				char c = stacks[from-1].removeTop();
				toAdd.add(c);
			}
			stacks[to-1].addAtop(toAdd);
		}
		
	}

	public static class Stack {
		
		List<Character> pieces = new ArrayList<Character>();

		public void addUnder(char piece) {
			pieces.add(piece);
		}

		public void addAtop(List<Character> toAdd) {
			pieces.addAll(0, toAdd);
		}

		public void addTop(char c) {
			pieces.add(0, c);
		}

		public char removeTop() {
			return pieces.remove(0);
		}

		@Override
		public String toString() {
			return pieces.toString();
		}

		public char getTop() {
			return pieces.get(0);
		}
		
	}

	public static void main(String[] args) throws IOException {
		Stream<String> lines = Files.lines(Paths.get("./input_stacks.txt"));
		List<String> collect = lines.collect(Collectors.toList());
		
		int sum = 0;
		
		int stackCount = 0;
		int maxSize = 0;
		
		for (String line : collect) {
			if (line.contains("]")) {
				stackCount = Math.max(stackCount, 1 + (line.lastIndexOf("]") - 2) / 4);
				maxSize++;
			}
		}

		Stack[] stacks = new Stack[stackCount];
		for (int j=0;j<stackCount;j++) { stacks[j] = new Stack(); }
		
		
		for (int i=0;i<maxSize;i++) {
			for (int j=0;j<stackCount;j++) {
				char piece = collect.get(i).charAt(1+j*4);
				if (piece != ' ') {
					stacks[j].addUnder(piece);
				}
			}
		}	
		
		System.out.println(stackCount);
		
		for (int i=0;i<stackCount;i++) {
			System.out.println(stacks[i]);
		}
		
		List<Move> moves = new ArrayList<Move>();
		
		for (int i=maxSize+2;i<collect.size();i++) {
			String move = collect.get(i);
			String[] split = move.split(" ");
			int count = Integer.parseInt(split[1]);
			int from = Integer.parseInt(split[3]);
			int to = Integer.parseInt(split[5]);
			
			moves.add(new Move(from, to, count));
		}
		
		System.out.println(moves);
		
		for (Move move : moves) {
			move.perform(stacks);

//			System.out.println("===");
//			
//			for (int i=0;i<stackCount;i++) {
//				System.out.println(stacks[i]);
//			}

		}
		
		for (Stack stack : stacks) {
			System.out.print(stack.getTop());
		}
		System.out.println();
		
		stacks = new Stack[stackCount];
		for (int j=0;j<stackCount;j++) { stacks[j] = new Stack(); }
		for (int i=0;i<maxSize;i++) {
			for (int j=0;j<stackCount;j++) {
				char piece = collect.get(i).charAt(1+j*4);
				if (piece != ' ') {
					stacks[j].addUnder(piece);
				}
			}
		}	
		for (Move move : moves) {
			move.performKeepOrder(stacks);
		}
		for (Stack stack : stacks) {
			System.out.print(stack.getTop());
		}
		System.out.println();
	}
	
}
