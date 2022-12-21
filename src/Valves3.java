import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Valves3 {

	public static class Environnement {
		
		private Set<Node> allValves;
		private Map<Pair<Node, Node>, Integer> m_matrix;
		private Map<String, Node> valvesMap;
		private int time;
		
		public int getTime() {
			return time;
		}

		public Environnement(int time, Map<String, Node> nodes, Set<Node> valves) {
			this.time = time;
			valvesMap = nodes;
			allValves = valves;
			computeDistanceMtrix();
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
		
		public Node getValve(String name) {
			return valvesMap.get(name);
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

			Set<Node> nodesSet = nodes.values().stream().collect(Collectors.toSet());
			Environnement env = new Environnement(26, nodes, nodesSet);
			
			nodesSet = nodesSet.stream().filter(n -> n.getFlowRate() > 0).collect(Collectors.toSet());
			
			List<Node> path = new ArrayList<Node>();
			int totalPressure = recurse(env, nodesSet, path, 0);
			System.out.println(totalPressure);

	}
	
	private static int recurse(Environnement env, Collection<Node> nodesCollection, List<Node> path, int elapsed) {
		if (nodesCollection.isEmpty()) return computePressure(env, path);
		int pressure = 0;
		List<Node> nodes = new ArrayList<Node>(nodesCollection);
		for (int i=0;i<nodes.size();i++) {
			if (path.isEmpty()) System.out.println(i + "/" + nodes.size());
			Node remove = nodes.remove(i);
			int savedElapsed = elapsed;
			elapsed += env.getDistance(path.isEmpty() ? env.getValve("AA") : path.get(path.size()-1), remove) + 1;
			path.add(remove);
			if (elapsed > env.getTime()) {
				pressure = Math.max(pressure, computePressure(env, path));
			}
			else {
				pressure = Math.max(pressure, recurse(env, nodes, path, elapsed));
			}
			path.remove(path.size()-1);
			elapsed = savedElapsed;
			nodes.add(i, remove);
		}
		return pressure;
	}

	private static int computePressure(Environnement env, List<Node> path) {
		int totalTime = env.getTime();
		int currentFlow = 0;
		int pressure = 0;
		Node currentNode = env.getValve("AA");
		for (Node node : path) {
			int distance = env.getDistance(currentNode, node);
			if (distance + 1 <= totalTime) {
				pressure += currentFlow * (distance + 1);
				totalTime -= distance + 1;
				currentNode = node;
				currentFlow += node.getFlowRate();
			}
		}
		
		if (totalTime > 0) {
			pressure += currentFlow * totalTime;
		}
		
//		System.out.println(pressure + " " + path);
		return pressure;
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
		
		@Override
		public String toString() {
			return getName();
		}
	}
	
}

