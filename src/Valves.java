import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Valves {

	public static class Agent {

		private Node node;
		private int remainingTime;
		
		private List<Action> actionsPerformed = new ArrayList<Valves.Action>();
		private List<Action> bestAction = Collections.EMPTY_LIST;
		private int m_bestPressure;
		private Environnement env;
		private Node startNode;

		public Agent(Node node, int remainingTime, Environnement env) {
			this.startNode = node;
			this.node = node;
			this.remainingTime = remainingTime;
			this.env = env;
		}
		
		public Environnement getEnvironnement() {
			return env;
		}

		public int getRemainingTime() {
			return remainingTime;
		}
		
		public void setRemainingTime(int time) {
			remainingTime = time;
		}

		public Node getNode() {
			return node;
		}

		public void setNode(Node node) {
			this.node = node;
		}

		public void addAction(Action action) { actionsPerformed.add(action); }
		public void removeLastAction() { actionsPerformed.remove(actionsPerformed.size()-1); }
		public List<Action> getActions() { return actionsPerformed; }

		public void setBestAction(ArrayList<Action> actions) {
			bestAction = actions;
		}

		public List<Action> getBestActions() {
			return bestAction;
		}
		
		public Node getLastResetValve() {
			for (int i=actionsPerformed.size()-1;i>=0;i--) {
				Action action = actionsPerformed.get(i);
				if (action instanceof OpenValve) { return ((OpenValve)action).getNode(); }
			}
			return startNode;
		}
		

		public Set<Node> getNodesSinceLastOpening() {
			Set<Node> result = new HashSet<Node>();
			List<Action> actions = new ArrayList<Valves.Action>(actionsPerformed);
			boolean stop = false;
			for (int i=actions.size()-1;i>=0&!stop;i--) {
				Action action = actions.get(i);
				if (action instanceof OpenValve) {
					result.add(((OpenValve)action).getNode());
					stop = true;
				}
				else {
					result.add(((Move)action).getTo());
					if (i==0) {
						result.add(((Move)action).getFrom());
					}
				}
			}
			return result;
		}
		
		public Set<Node> getReachableNodesSinceLastOpening() {
			Set<Node> result = new HashSet<Node>();
			List<Action> actions = new ArrayList<Valves.Action>(actionsPerformed);
			boolean stop = false;
			for (int i=actions.size()-1;i>=0&!stop;i--) {
				Action action = actions.get(i);
				if (action instanceof OpenValve) {
					stop = true;
				}
				else {
					result.add(((Move)action).getTo());
					result.addAll(((Move)action).getFrom().getAdjacentNodes());
					if (i==0) {
						result.add(((Move)action).getFrom());
					}
				}
			}
			return result;
		}

		public int getBestPressure() {
			return m_bestPressure;
		}

		public void setBestPressure(int bestPressure) {
			m_bestPressure = bestPressure;
		}

		public int getTotalPressure() {
			return env.getTotalPressure();
		}

		public int getTotalFlowRate() {
			return env.getTotalFlowRate();
		}

		public void setTotalPressure(int flowRate) {
			env.setTotalPressure(flowRate);
		}

		Set<Node> rejected = new HashSet<Node>();
		private Node target;	
		
		public void updateRejectedNode() {
			Set<Node> newRejected = new HashSet<Node>();
			for (Node node : rejected) {
				newRejected.addAll(node.getAdjacentNodes());
			}
			newRejected.add(getNode());
			rejected = newRejected;
		}

		public void resetRejected() {
			rejected.clear();
		}

		public Set<Node> getRejectedNodes() {
			// TODO Auto-generated method stub
			return rejected;
		}

		public void setTarget(Node target) {
			this.target = target;
		}

		public Node getTarget() {
			return target;
		}

		public Action createMoveActionTowardTarget() {
			Node shortestTo = getEnvironnement().getShortestTo(node.getAdjacentNodes(), target);
			return new Move(node, shortestTo);
		}
	}
	
	public static class Environnement {
		
		private Set<Node> allValves;
		private Set<Node> closedValves;
		private int totalPressure;
		private int totalFlowrate;
		
		private Map<Pair<Node, Node>, Integer> m_matrix;
		
		public Environnement(HashSet<Node> valves) {
			allValves = valves;
			closedValves = new HashSet<Node>(valves);
			computeDistanceMtrix();
		}

		public int getTotalPressure() {
			return totalPressure;
		}

		public void setTotalPressure(int totalPresure) {
			this.totalPressure = totalPresure;
		}
		
		public int getTotalFlowRate() {
			return totalFlowrate;
		}

		public void setTotalFlowRate(int flowRate) {
			this.totalFlowrate = flowRate;
		}
		
		public void valvedOpened(Node node) {
			setTotalFlowRate(getTotalFlowRate()+node.getFlowRate());
			closedValves.remove(node);
		}
		
		public void valvedClosed(Node node) {
			setTotalFlowRate(getTotalFlowRate()-node.getFlowRate());
			closedValves.add(node);
		}

		public void reset() {
			allValves.forEach(n -> n.setOpened(false));
			closedValves = new HashSet<Node>(allValves);
			totalPressure = totalFlowrate = 0;
		}

		public Set<Node> getClosedValves() {
			return closedValves;
		}
		
		public void computeDistanceMtrix() {
			m_matrix = new HashMap<Pair<Node, Node>, Integer>();
			List<Node> nodes = new ArrayList<Node>(allValves);
			for (Node key : allValves) {
				for (Node value : allValves) {
					if (key.equals(value)) { m_matrix.put(Pair.create(key, value), 0); }
					else if (key.adjacentNodes.contains(value)) { m_matrix.put(Pair.create(key, value), 1); }
					else { m_matrix.put(Pair.create(key, value), 99999); }
				}
			}
			for (int k = 0; k < nodes.size(); k++) {
	            for (int i = 0; i < nodes.size(); i++) {
	                for (int j = 0; j < nodes.size(); j++) {
	                	Node nodeK = nodes.get(k);
	                	Node nodeI = nodes.get(i);
	                	Node nodeJ = nodes.get(j);
	                    // If vertex k is on the shortest path
	                    // from i to j, then update the value of
	                    // dist[i][j]
	                	
	                	Pair<Node, Node> ik = Pair.create(nodeI, nodeK);
	                	Pair<Node, Node> kj = Pair.create(nodeK, nodeJ);
	                	Pair<Node, Node> ij = Pair.create(nodeI, nodeJ);
	                	
	                	if(m_matrix.get(ik)+m_matrix.get(kj)<m_matrix.get(ij)) {
	                		m_matrix.put(ij, m_matrix.get(ik)+m_matrix.get(kj));
	                	}
	                }
	            }
	        }
		}
		
		public Node getShortestTo(Collection<Node> candidates, Node target) {
			return candidates.stream().min((n1, n2) -> {
				return Integer.compare(m_matrix.get(Pair.create(n1, target)), m_matrix.get(Pair.create(n2, target)));
			}).get();
		}

		public int getDistance(Node from, Node to) {
			return m_matrix.get(Pair.create(from, to));
		}
	}
	
	public static class Pair<K, V> {
	    
	    private final K key;
	    private final V value;

	    public Pair(K k, V v) {
	        key = k;
	        value = v;
	    }
	    public K getKey() { return key; }
	    public V getValue() { return value; }

	    public boolean equals(Object o) {
	        if (this == o) {
	            return true;
	        }
	        if (!(o instanceof Pair)) {
	            return false;
	        } else {
	            Pair<?, ?> oP = (Pair<?, ?>) o;
	            return (key == null ?
	                    oP.key == null :
	                    key.equals(oP.key)) &&
	                (value == null ?
	                 oP.value == null :
	                 value.equals(oP.value));
	        }
	    }

	    public int hashCode() {
	        int result = key == null ? 0 : key.hashCode();

	        final int h = value == null ? 0 : value.hashCode();
	        result = 37 * result + h ^ (h >>> 16);

	        return result;
	    }

	    @Override
	    public String toString() {
	        return "[" + getKey() + ", " + getValue() + "]";
	    }

	    public static <K, V> Pair<K, V> create(K k, V v) {
	        return new Pair<K, V>(k, v);
	    }
	}

	public static void main(String[] args) throws IOException {
			Stream<String> lines = Files.lines(Paths.get("./input_valves.txt"));
			List<String> collect = lines.collect(Collectors.toList());

			final Map<String, Node> nodes = new HashMap<String, Node>();
			
			for (String s : collect) {
				String name = s.substring(6, 8);
				nodes.putIfAbsent(name, new Node(name));
				final Node node = nodes.get(name);
				int flowRate = Integer.parseInt(s.split(";")[0].split("=")[1]);
				node.setFlowRate(flowRate);
				
				List<String> neighbours = new ArrayList<String>(); 
				if (s.contains("valves")) {
					String neighboursAsString = s.split("valves")[1];
					neighbours.addAll(Stream.of(neighboursAsString.split(",", -1)).map(s2 -> s2.trim()).collect(Collectors.toList()));
				}
				else {
					String neighboursAsString = s.split("valve")[1];
					neighbours.add(neighboursAsString.trim());
				}
				
				neighbours.forEach(neighbourName -> { 
					nodes.putIfAbsent(neighbourName, new Node(neighbourName));
					Node neighbour = nodes.get(neighbourName);
					node.addNode(neighbour);
				});
				
				System.out.println(name + " " + flowRate + " " + neighbours);
			}

			
			
			// Search action tree
			Node node = nodes.get("AA");
			
//			for (int i=1;i<=30;i++) {
//				Agent agent = new Agent(node, i);
//				nodes.values().forEach(n -> n.setOpened(false));
//				int pressure = findTotalPressure(agent/*, Integer.MIN_VALUE*/);
//				
//				System.out.println("Depth " + i + " : pressure = " + pressure + " " + agent.getBestActions());			
//			}
			
			for (int i=4;i<=30;i++) {
				Environnement env = new Environnement(new HashSet<Node>(nodes.values()));
				Agent agent = new Agent(node, i, env);
				Agent elephant = new Agent(node, i, env);
				env.reset();
				int pressure = findTotalPressure(agent, elephant);
				
				System.out.println("Depth " + i + " : pressure = " + pressure);
				System.out.println("Agent moves : " + agent.getBestActions());
				System.out.println("Elephant moves : " + elephant.getBestActions());
				
				System.out.println("Pressure : ");
				for (int j=0;j<agent.getBestActions().size();j++) {
					System.out.print(agent.getTotalFlowRate()+(j<agent.getBestActions().size()-1 ? "," : ""));
					agent.getBestActions().get(j).perform(agent);
					elephant.getBestActions().get(j).perform(elephant);
				}
				System.out.println();
				System.out.println();
			}
	}
	
	private static int findTotalPressure(Agent agent) {
		
		if (agent.getRemainingTime() < 10 && agent.getTotalPressure() < agent.getBestPressure() / 2) {
			return agent.getTotalPressure();
		}
		
		if (agent.getRemainingTime() == 0) {
			if (agent.getTotalPressure() >= agent.getBestPressure()) {
				agent.setBestPressure(agent.getTotalPressure());
				agent.setBestAction(new ArrayList(agent.getActions()));
			}
			return agent.getTotalPressure();
		}
		Node node = agent.getNode();
		List<Action> actions = new ArrayList<Action>();
		if (!node.isOpened() && node.flowRate > 0) actions.add(new OpenValve(agent.getNode()));
//		for (String adjacent : node.getAdjacentNodes())
		
		Set<Node> nodesSinceLastOpening = agent.getRejectedNodes();
		Stream<Node> stream = node.getAdjacentNodes().stream();
		stream = stream.filter(n -> { return !nodesSinceLastOpening.contains(n); });
		actions.addAll(stream.map(neighbour -> new Move(node, neighbour)).collect(Collectors.toSet()));
		
		int bestPressure = agent.getTotalPressure();
		for (Action action : actions) {
			action.perform(agent);
			int pressure = findTotalPressure(agent);
			bestPressure = Math.max(bestPressure, pressure);
			action.rollback(agent);
		}
		
		return bestPressure;
	}
	
	private static int findTotalPressure(Agent agent, Agent elephant) {
		
//		int bestRemainingPressure = nodes.stream().filter(n -> !n.isOpened()).mapToInt(n -> n.getFlowRate()*agent.getRemainingTime()).sum();
//		if (bestRemainingPressure + agent.getTotalPressure() <= agent.getBestPressure()) {
//			return agent.getTotalPressure();
//		}
		
		if (agent.getRemainingTime() < 5 && agent.getTotalPressure() < agent.getBestPressure() / 2) {
////		if (agent.getTotalPressure() < 100 * agent.getBestPressure()) {
			return agent.getTotalPressure();
		}
		
//		if (agent.getRemainingTime() > 1 && agent.getClosedValves().isEmpty()) {
//			System.out.println("!!!");
//		}
		
//		int closedValvesFlowRate = agent.getEnvironnement().getClosedValves().stream().mapToInt(n -> n.getFlowRate()).sum();
//		int maxExpectedPressure = agent.getTotalPressure() + elephant.getTotalPressure();
//		maxExpectedPressure += (closedValvesFlowRate + agent.getTotalFlowRate() + elephant.getTotalFlowRate()) * agent.getRemainingTime();
//		if (maxExpectedPressure <= agent.getBestPressure()) {
//			return agent.getTotalPressure();
//		}
		
		if (agent.getRemainingTime() == 0) {
			if (agent.getTotalPressure() >= agent.getBestPressure()) {
				agent.setBestPressure(agent.getTotalPressure());
				agent.setBestAction(new ArrayList<Action>(agent.getActions()));
				elephant.setBestAction(new ArrayList<Action>(elephant.getActions()));
			}
			return agent.getTotalPressure();
		}
		
//		List<Action> agentActions = new ArrayList<Action>();
//		List<Action> elephantActions = new ArrayList<Action>();
		
		Environnement environnement = agent.getEnvironnement();
		
		int bestPressure = agent.getTotalPressure();
		
		Node node = agent.getNode();
		if (agent.getTarget() == null) {
			ArrayList<Node> sortedClosedValves = new ArrayList<Node>(environnement.getClosedValves());
			Collections.sort(sortedClosedValves, new Comparator<Node>() {
				public int compare(Node o1, Node o2) {
					return Integer.compare(o1.getFlowRate(), o2.getFlowRate());
				}
				
			});
//			List<Integer> collect = sortedClosedValves.stream().map(n -> environnement.getDistance(node, n)).collect(Collectors.toList());
			for (Node target : sortedClosedValves) {
				if (!target.equals(elephant.getTarget()) && environnement.getDistance(node, target) < agent.getRemainingTime()) {
//					if (agent.getRemainingTime() == 6) System.out.println(target);
					if (target.equals(node)) {
						OpenValve action = new OpenValve(agent.getNode());
						action.perform(agent);
						bestPressure = processElephant(agent, elephant, bestPressure);
						action.rollback(agent);
					}
					else {
						agent.setTarget(target);
						Action action = agent.createMoveActionTowardTarget();
						action.perform(agent);
						
						bestPressure = processElephant(agent, elephant, bestPressure);
						
						action.rollback(agent);
					}
				}
			}
			WaitAction action = new WaitAction();
			action.perform(agent);
			bestPressure = processElephant(agent, elephant, bestPressure);
			action.rollback(agent);
		} 
		else {
			if (agent.getTarget() == node) {
				OpenValve action = new OpenValve(agent.getNode());
				action.perform(agent);
				
				bestPressure = processElephant(agent, elephant, bestPressure);

				action.rollback(agent);
			}
			else {
				Action action = agent.createMoveActionTowardTarget();
				action.perform(agent);
				
				bestPressure = processElephant(agent, elephant, bestPressure);

				action.rollback(agent);
			}
		}
		
//		if (!node.isOpened() && node.flowRate > 0) agentActions.add(new OpenValve(agent.getNode()));
//		final Node lastOpened = agent.getLastResetValve();
//		Stream<Node> stream = node.getAdjacentNodes().stream().filter(n -> { 
//			int d1 = environnement.getDistance(n, lastOpened);
//			int d2 = environnement.getDistance(node, lastOpened);
//			return d1 > d2; });
//		Set<Move> selectedMove = stream.map(neighbour -> new Move(node, neighbour)).collect(Collectors.toSet());
//		agentActions.addAll(selectedMove);
//		
//		int bestPressure = agent.getTotalPressure();
//		for (Action action : agentActions) {
//			action.perform(agent);
//			
//			Node elephantNode = elephant.getNode();
//			if (!elephantNode.isOpened() && elephantNode.flowRate > 0) elephantActions.add(new OpenValve(elephant.getNode()));
//			
//			final Node lastOpenedValve = elephant.getLastResetValve();
//			stream = elephantNode.getAdjacentNodes().stream().filter(n -> { return environnement.getDistance(n, lastOpenedValve) > environnement.getDistance(elephantNode, lastOpenedValve); });
//			selectedMove = stream.map(neighbour -> new Move(elephantNode, neighbour)).collect(Collectors.toSet());
//			elephantActions.addAll(selectedMove);
//			
//			for (Action elephantAction : elephantActions) {
//				
//				elephantAction.perform(elephant);
//				
//				int pressure = findTotalPressure(agent, elephant);
//				bestPressure = Math.max(bestPressure, pressure);
//				elephantAction.rollback(elephant);
//			}
//
//			action.rollback(agent);
//		}
		
		return bestPressure;
	}

	private static int processElephant(Agent agent, Agent elephant, int bestPressure) {
		Environnement environnement = agent.getEnvironnement();
		Node node = elephant.getNode();
		if (elephant.getTarget() == null) {
			ArrayList<Node> sortedClosedValves = new ArrayList<Node>(environnement.getClosedValves());
			Collections.sort(sortedClosedValves, new Comparator<Node>() {
				public int compare(Node o1, Node o2) {
					return Integer.compare(o1.getFlowRate(), o2.getFlowRate());
				}
				
			});
			for (Node target : sortedClosedValves) {
				if (!target.equals(agent.getTarget()) && environnement.getDistance(node, target) < elephant.getRemainingTime()) {
					if (target.equals(node)) {
						OpenValve action = new OpenValve(elephant.getNode());
						action.perform(elephant);
						int pressure = findTotalPressure(agent, elephant);
						bestPressure = Math.max(bestPressure, pressure);
						action.rollback(elephant);
					}
					else {
						elephant.setTarget(target);
						Action action = elephant.createMoveActionTowardTarget();
						action.perform(elephant);
						int pressure = findTotalPressure(agent, elephant);
						bestPressure = Math.max(bestPressure, pressure);
						action.rollback(elephant);
					}
				}
			}
			WaitAction action = new WaitAction();
			action.perform(elephant);
			int pressure = findTotalPressure(agent, elephant);
			bestPressure = Math.max(bestPressure, pressure);
			action.rollback(elephant);
		} 
		else {
			if (elephant.getTarget() == node) {
				OpenValve action = new OpenValve(elephant.getNode());
				action.perform(elephant);
				int pressure = findTotalPressure(agent, elephant);
				bestPressure = Math.max(bestPressure, pressure);
				action.rollback(elephant);
			}
			else {
				Action action = elephant.createMoveActionTowardTarget();
				action.perform(elephant);
				int pressure = findTotalPressure(agent, elephant);
				bestPressure = Math.max(bestPressure, pressure);
				action.rollback(elephant);
			}
		}
		return bestPressure;
	}

	public static abstract class Action {
		
		public abstract void perform(Agent agent);
		public abstract void rollback(Agent agent);
	}
	
	public static class OpenValve extends Action {

		private Node node;

		public OpenValve(Node node) {
			this.node = node;
		}

		public Node getNode() {
			return node;
		}

		@Override
		public void perform(Agent agent) {
			agent.getNode().setOpened(true);
			agent.setRemainingTime(agent.getRemainingTime()-1);
			agent.setTotalPressure(agent.getTotalPressure()+agent.getRemainingTime()*agent.getNode().getFlowRate());
			agent.addAction(this);
			agent.getEnvironnement().valvedOpened(node);
			agent.resetRejected();
			agent.setTarget(null);
		}

		@Override
		public void rollback(Agent agent) {
			agent.getNode().setOpened(false);
			agent.setTotalPressure(agent.getTotalPressure()-agent.getRemainingTime()*agent.getNode().getFlowRate());
			agent.setRemainingTime(agent.getRemainingTime()+1);
			agent.removeLastAction();
			agent.getEnvironnement().valvedClosed(node);
			agent.resetRejected();
		}
		
		@Override
		public String toString() {
			return "open ["+ node.getName() + "]";
		}
	}
	
	public static class Move extends Action {

		private Node from;
		private Node to;

		public Node getTo() {
			return to;
		}

		public Node getFrom() {
			return from;
		}

		public Move(Node node, Node neighbour) {
			this.from = node;
			this.to = neighbour;
		}

		@Override
		public void perform(Agent agent) {
			agent.updateRejectedNode();
			agent.setNode(to);
			agent.setRemainingTime(agent.getRemainingTime()-1);
			agent.addAction(this);
		}

		@Override
		public void rollback(Agent agent) {
			agent.setNode(from);
			agent.setRemainingTime(agent.getRemainingTime()+1);
			agent.removeLastAction();
		}
		
		@Override
		public String toString() {
			return "move ["+ from.getName() + "]->[" + to.getName() + "]";
		}
	}
	
	public static class WaitAction extends Action {

		@Override
		public void perform(Agent agent) {
			agent.setRemainingTime(agent.getRemainingTime()-1);
			agent.addAction(this);
		}

		@Override
		public void rollback(Agent agent) {
			agent.setRemainingTime(agent.getRemainingTime()+1);
			agent.removeLastAction();
		}
		
		@Override
		public String toString() {
			return "wait";
		}
		
	}
	
	public static class Node {
	    
	    private String name;
		List<Node> adjacentNodes = new ArrayList<Node>();
		private int flowRate;
		public boolean opened = false;
		
	    public boolean isOpened() {
			return opened;
		}

		public void setOpened(boolean opened) {
			this.opened = opened;
		}

		public List<Node> getAdjacentNodes() {
			return adjacentNodes;
		}

		public int getFlowRate() {
			return flowRate;
		}

		public Node(String name) {
	        this.name = name;
	    }

		public void setFlowRate(int flowRate) {
			this.flowRate = flowRate;
		}

		public void addNode(Node neighbour) {
			adjacentNodes.add(neighbour);
		}

		public String getName() {
			return name;
		}
	}
	
}

