import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Robots {

	public static class State {

		private Map<Integer, Integer> cache = new HashMap<Integer, Integer>();

//		private int timeForFirstClay;
//		private int timeForFirstObsdian;
		private int[] bestGeodeFound = new int[100];

		private int geodes;
		private int oreRobots;
		private int ores;
		private int clayRobots;
		private int clays;
		private int obsidianRobots;
		private int geodeRobots;
		private int obsidians;
		private int timeInMinute;
		private int totalCreatedOres;
		private int totalCreateObsidians;
		private int totalCreateClays;
		private int totalCreatedGeodes;

		private int timeout;
		
		public State() {
			oreRobots = 1;
			timeInMinute = 1;
		}

		public void setTimeInMinute(int timeInMinute) {
			this.timeInMinute = timeInMinute;
		}
		
		public int getOres() {
			return ores;
		}

		public int getClays() {
			return clays;
		}

		public int getObsidians() {
			return obsidians;
		}

		public int getGeodes() {
			return geodes;
		}

		// =========
		
		public void increaseOreRobots() {
			oreRobots++;
		}


		public void increaseClayRobots() {
			clayRobots++;
		}

		public void increaseObsidianRobots() {
			obsidianRobots++;
		}

		public void increaseGeodeRobots() {
			geodeRobots++;
		}
		
		public void decreaseOreRobots() {
			oreRobots--;	
		}

		public void decreaseClayRobots() {
			clayRobots--;
		}

		public void decreaseObsidianRobots() {
			obsidianRobots--;
		}

		public void decreaseGeodeRobots() {
			geodeRobots--;
		}
		
		// =========

		public void increaseOres(int oreForOreRobot) {
			ores += oreForOreRobot;
		}

		public void increaseClays(int clayForObsidianRobot) {
			clays += clayForObsidianRobot;
		}


		public void increaseObsidians(int obsidianForGeodeRobot) {
			obsidians += obsidianForGeodeRobot;
		}

		public void decreaseOres(int oreForOreRobot) {
			ores-= oreForOreRobot;
		}

		public void decreaseClays(int clayForObsidianRobot) {
			clays -= clayForObsidianRobot;
		}

		public void decreaseObsidians(int obsidianForGeodeRobot) {
			obsidians -= obsidianForGeodeRobot;
		}

		// =========
		
		public void processRobots() {
			ores += oreRobots;
			clays += clayRobots;
			obsidians += obsidianRobots;
			geodes += geodeRobots;
			
			totalCreatedOres += oreRobots;
			totalCreateClays += clayRobots;
			totalCreateObsidians += obsidianRobots;
			totalCreatedGeodes += geodeRobots;
			
//			if (clays > 0 && timeForFirstClay < remainingTime) timeForFirstClay = remainingTime;
//			if (obsidians > 0 && timeForFirstObsdian < remainingTime) timeForFirstObsdian = remainingTime;
			bestGeodeFound[timeInMinute] = Math.max(bestGeodeFound[timeInMinute], geodes);
		}

		public void unprocessRobots() {
			ores -= oreRobots;
			clays -= clayRobots;
			obsidians -= obsidianRobots;
			geodes -= geodeRobots;

			totalCreatedOres -= oreRobots;
			totalCreateClays -= clayRobots;
			totalCreateObsidians -= obsidianRobots;
			totalCreatedGeodes -= geodeRobots;
		}
		
		// =========

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + clayRobots;
			result = prime * result + clays;
			result = prime * result + geodeRobots;
			result = prime * result + geodes;
			result = prime * result + obsidianRobots;
			result = prime * result + obsidians;
			result = prime * result + oreRobots;
			result = prime * result + ores;
			result = prime * result + timeInMinute;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			State other = (State) obj;
			if (clayRobots != other.clayRobots)
				return false;
			if (clays != other.clays)
				return false;
			if (geodeRobots != other.geodeRobots)
				return false;
			if (geodes != other.geodes)
				return false;
			if (obsidianRobots != other.obsidianRobots)
				return false;
			if (obsidians != other.obsidians)
				return false;
			if (oreRobots != other.oreRobots)
				return false;
			if (ores != other.ores)
				return false;
			if (timeInMinute != other.timeInMinute)
				return false;
			return true;
		}

		public void setTimeout(int timeout) {
			this.timeout = timeout;
		}
		
		public int getTimeout() {
			return timeout;
		}
	}

	public static class Factory {

		private int index;
		private int oreForOreRobot;
		private int oreForClayRobot;
		private int oreForObsidianRoboto;
		private int clayForObsidianRobot;
		private int oreForGeodeRobot;
		private int obsidianForGeodeRobot;

		public Factory(int index, int oreForOreRobot, int oreForClayRobot, int oreForObsidianRoboto, int clayForObsidianRobot, 
				int oreForGeodeRobot, int obsidianForGeodeRobot) {

			this.index = index;
			this.oreForOreRobot = oreForOreRobot;
			this.oreForClayRobot = oreForClayRobot;
			this.oreForObsidianRoboto = oreForObsidianRoboto;
			this.clayForObsidianRobot = clayForObsidianRobot;
			this.oreForGeodeRobot = oreForGeodeRobot;
			this.obsidianForGeodeRobot = obsidianForGeodeRobot;
		}

		public int getIndex() {
			return index;
		}

		public int getOreForOreRobot() {
			return oreForOreRobot;
		}

		public int getOreForClayRobot() {
			return oreForClayRobot;
		}

		public int getOreForObsidianRoboto() {
			return oreForObsidianRoboto;
		}

		public int getClayForObsidianRobot() {
			return clayForObsidianRobot;
		}

		public int getOreForGeodeRobot() {
			return oreForGeodeRobot;
		}

		public int getObsidianForGeodeRobot() {
			return obsidianForGeodeRobot;
		}

		
	}

	public static void main(String[] args) throws IOException {
		Stream<String> lines = Files.lines(Paths.get("./input_robots.txt"));
		List<String> collect = lines.collect(Collectors.toList());
		
//		collect = Arrays.asList(
//				"Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.",
//				"Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.");
				
		Pattern pat = Pattern.compile("Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.");
		
		List<Factory> factories = new ArrayList<Factory>();
		for (String s : collect) {
			Matcher m = pat.matcher(s);
            boolean find = m.find();
            Factory factory = new Factory(
            		Integer.parseInt(m.group(1)), 
            		Integer.parseInt(m.group(2)),
            		Integer.parseInt(m.group(3)), 
            		Integer.parseInt(m.group(4)), 
            		Integer.parseInt(m.group(5)), 
            		Integer.parseInt(m.group(6)), 
            		Integer.parseInt(m.group(7)));
            factories.add(factory);
		}			
		
		int sum = factories.parallelStream().mapToInt(f -> computeGeodes(f) * f.getIndex()).sum();
		System.out.println("sum : " + sum);
		
		System.out.println();
		
		int dot = factories.stream().limit(3).parallel().mapToInt(f -> computeGeodes(f, 32)).reduce((s1, s2) -> { return s1*s2; }).getAsInt();
		System.out.println("dot : " + dot);

		
		
		//		int sum = 0;
//		for (Factory factory : factories) {
//			int geodes = computeGeodes(factory);
//			int qualityLevel = factory.getIndex() * geodes;
//			sum += qualityLevel;
//		}
//		System.out.println("sum : " + sum);
	}

	private static int computeGeodes(Factory factory, int timeout) {
		State state = new State();
		state.setTimeout(timeout);
		int geodes = computeGeodes(factory, state, 1, new HashSet<Action>());
		int qualityLevel = factory.getIndex() * geodes;
		System.out.println(factory.getIndex() + " " + geodes + " quality level = " + qualityLevel);
		state.cache.clear();
		return geodes;
	}

	private static int computeGeodes(Factory factory) {
		State state = new State();
		int geodes = computeGeodes(factory, state, 1, new HashSet<Action>());
		int qualityLevel = factory.getIndex() * geodes;
		System.out.println(factory.getIndex() + " " + geodes + " quality level = " + qualityLevel);
		state.cache.clear();
		return geodes;
	}
	
//	private static LinkedHashSet<Integer> keys = new LinkedHashSet<Integer>();
	
	private static int computeGeodes(Factory factory, State state, int timeInMinute, Set<Action> actionsToSkip) {
		
		final int stateHash = state.hashCode();
		Integer score = state.cache.get(stateHash);
		if (score != null) {
			return score;
		}
		if (state.cache.size() > 25000) {
			state.cache.clear();
		}
		
//		if (state.totalCreateClays == 0 && state.timeForFirstClay > remainingTime+1) { return 0; }
//		if (state.totalCreateObsidians == 0 && state.timeForFirstObsdian > remainingTime+1) { return 0; }
//		if (state.totalCreatedGeodes == 0 && state.timeForFirstGeode > remainingTime+1) { return 0; }

//		if (timeInMinute > 14 && state.totalCreateClays == 0) {
//			return 0;
//		}
//		if (timeInMinute > 17 && state.totalCreateObsidians == 0) {
//			return 0;
//		}
//		if (timeInMinute > 22 && state.totalCreatedGeodes == 0) {
//			return 0;
//		}
		
		if (state.bestGeodeFound[timeInMinute] > state.geodes + 10) {
			return 0;
		}
		
		int timeout = 24;
		if (state.getTimeout() > 0) timeout = state.getTimeout();
		if (timeInMinute == timeout+1) {
			return state.getGeodes();
		}
		
		int result = 0;
		boolean actionFound = false;
		for (Action action : Action.values()) {
//			if (timeInMinute == 24) System.out.println(action);
			if (!actionFound && action.canPerform(factory, state) && !actionsToSkip.contains(action)) {
				if (action == Action.BUILD_GEODE_ROBOT) {
					actionFound = true;
				}
				if (action == Action.WAIT) {
					if (Action.BUILD_CLAY_ROBOT.canPerform(factory, state)) actionsToSkip.add(Action.BUILD_CLAY_ROBOT);
					if (Action.BUILD_ORE_ROBOT.canPerform(factory, state)) actionsToSkip.add(Action.BUILD_ORE_ROBOT);
					if (Action.BUILD_OBSIDIAN_ROBOT.canPerform(factory, state)) actionsToSkip.add(Action.BUILD_OBSIDIAN_ROBOT);
				}
				else {
					actionsToSkip.clear();
				}
				state.processRobots();
				action.perform(factory, state);
				state.setTimeInMinute(timeInMinute+1);
				result = Math.max(result, computeGeodes(factory, state, timeInMinute+1, new HashSet<Action>(actionsToSkip)));
				state.setTimeInMinute(timeInMinute);
				action.rollback(factory, state);
				state.unprocessRobots();
			}
		}
		
		state.cache.put(stateHash, result);
//		if (score != null && score > 0) System.out.println(result + " cached(" + score + ")");
//		if (hashBefore != state.hashCode()) System.out.println(hashBefore + " -> " + state.hashCode());

		return result;
	}

	private enum Action {
		
		BUILD_GEODE_ROBOT {
			public final boolean canPerform(Factory factory, State state) { 
				return state.ores >= factory.oreForGeodeRobot && state.obsidians >= factory.obsidianForGeodeRobot;
			}
			public final void perform(Factory factory, State state) {
				state.geodeRobots++;
				state.ores -= factory.oreForGeodeRobot;
				state.obsidians -= factory.obsidianForGeodeRobot;
			}
			public final void rollback(Factory factory, State state) {
				state.geodeRobots--;
				state.ores += factory.oreForGeodeRobot;
				state.obsidians += factory.obsidianForGeodeRobot;
			}
		},
		BUILD_OBSIDIAN_ROBOT {
			public final boolean canPerform(Factory factory, State state) { 
				return state.ores >= factory.oreForObsidianRoboto && state.clays >= factory.clayForObsidianRobot;
			}
			public final void perform(Factory factory, State state) {
				state.obsidianRobots++;
				state.ores -= factory.oreForObsidianRoboto;
				state.clays -= factory.clayForObsidianRobot;
			}
			public final void rollback(Factory factory, State state) {
				state.obsidianRobots--;
				state.ores += factory.oreForObsidianRoboto;
				state.clays += factory.clayForObsidianRobot;
			}
		},
		BUILD_CLAY_ROBOT {
			public final boolean canPerform(Factory factory, State state) { 
				return state.ores >= factory.oreForClayRobot;
			}
			public final void perform(Factory factory, State state) {
				state.clayRobots++;
				state.ores -= factory.oreForClayRobot;
			}
			public final void rollback(Factory factory, State state) {
				state.clayRobots--;
				state.ores += factory.oreForClayRobot;
			}
		},
		BUILD_ORE_ROBOT {
			public final boolean canPerform(Factory factory, State state) { 
				if (factory.index == 2) {
					Thread.yield();
				}
				return state.ores >= factory.oreForOreRobot;
			}
			public final void perform(Factory factory, State state) {
				state.oreRobots++;
				state.ores -= factory.oreForOreRobot;
			}
			public final void rollback(Factory factory, State state) {
				state.oreRobots--;
				state.ores += factory.oreForOreRobot;
			}
		},
		WAIT {
			public final boolean canPerform(Factory factory, State state) { 
//				if (BUILD_ORE_ROBOT.canPerform(factory, state)) return false;
//				if (BUILD_CLAY_ROBOT.canBuild(factory, state)) return false;
//				if (BUILD_OBSIDIAN_ROBOT.canPerform(factory, state)) return false;
//				if (BUILD_GEODE_ROBOT.canPerform(factory, state)) return false;
				return true;
//				return !(BUILD_ORE_ROBOT.canBuild(factory, state) || 
//						BUILD_CLAY_ROBOT.canBuild(factory, state) || 
//						BUILD_OBSIDIAN_ROBOT.canBuild(factory, state) || 
//						BUILD_GEODE_ROBOT.canBuild(factory, state));
//				return true;
			}
			public final void perform(Factory factory, State state) {}
			public final void rollback(Factory factory, State state) {}
		};
		
		public abstract boolean canPerform(Factory factory, State state);
		public abstract void rollback(Factory factory, State state);
		public abstract void perform(Factory factory, State state);
	}

}

