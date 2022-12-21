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

public class Valves2 {

	public static class Environnement {
		
		private Set<Node> allValves;
		private Map<Pair<Node, Node>, Integer> m_matrix;
		
		public Environnement(HashSet<Node> valves) {
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

			Environnement env = new Environnement(new HashSet<Node>(nodes.values()));
			
//			List<Node> path = new ArrayList<Node>(nodes.values());
//			Collections.shuffle(path);
//			int totalPressure = computePressure(env, path, nodes);
//			
//			double temperature = 50;
//			while(temperature > 0.0001) {
//				List<Node> oldPath = new ArrayList<Node>(path);
//				shuffleSoft(nodes, path);
//				int newTotalPressure = computePressure(env, path, nodes);
//				
//				double delta = totalPressure - newTotalPressure;
//				if (Math.random() < Math.exp(-delta/temperature)) {
//					if (delta < 0) {
//						System.out.println(newTotalPressure + " " + path);
//					}
//					totalPressure = newTotalPressure;
//				}
//				else {
//					path = oldPath;
//				}
//				temperature *= 0.99999999;
//			}
			
			List<Node> path = new ArrayList<Node>(nodes.values());
			Collections.shuffle(path);
			List<Node> elephantPath = new ArrayList<Node>();
			int totalPressure = computePressure(26, env, path, nodes);
			
			double temperature = 10;
			while(temperature > 0.0001) {
				List<Node> oldPath = new ArrayList<Node>(path);
				List<Node> oldElephantPath = new ArrayList<Node>(elephantPath);
				shuffleSoft(nodes, path, elephantPath);
				int newTotalPressure = computePressure(26, env, path, nodes) + computePressure(26, env, elephantPath, nodes);
				
				double delta = totalPressure - newTotalPressure;
				if (Math.random() < Math.exp(-delta/temperature)) {
					if (delta < 0) {
						System.out.println(newTotalPressure + " " + path + elephantPath);
					}
					totalPressure = newTotalPressure;
				}
				else {
					path = oldPath;
					elephantPath = oldElephantPath;
				}
				temperature *= 0.99999999;
			}
	}
	
	private static void shuffleSoft(Map<String, Node> nodes, List<Node> path, List<Node> elephantPath) {
		if (Math.random() < 0.5 && path.size() > 1) {
			int i1 = (int)(Math.random() * (path.size()));
			int i2 = (int)(Math.random() * (path.size()));
			Node n1 = path.get(i1);
			Node n2 = path.get(i2);
			path.set(i1, n2);
			path.set(i2, n1);
		}
		if (Math.random() < 0.5&& elephantPath.size() > 1) {
			int i1 = (int)(Math.random() * (elephantPath.size()));
			int i2 = (int)(Math.random() * (elephantPath.size()));
			Node n1 = elephantPath.get(i1);
			Node n2 = elephantPath.get(i2);
			elephantPath.set(i1, n2);
			elephantPath.set(i2, n1);
		}
		if (Math.random() < 0.5 && !path.isEmpty()) {
			int i1 = (int)(Math.random() * (path.size()));
			int i2 = (int)(Math.random() * (elephantPath.size()));
			Node n = path.get(i1);
			path.remove(i1);
			elephantPath.add(i2, n);
		}
		if (Math.random() < 0.5 && !elephantPath.isEmpty()) {
			int i1 = (int)(Math.random() * (path.size()));
			int i2 = (int)(Math.random() * (elephantPath.size()));
			Node n = elephantPath.get(i2);
			elephantPath.remove(i2);
			path.add(i1, n);
		}
	}

	private static void shuffleSoft(Map<String, Node> nodes, List<Node> path) {
		int i1 = (int)(Math.random() * (path.size()));
		int i2 = (int)(Math.random() * (path.size()));
		Node n1 = path.get(i1);
		Node n2 = path.get(i2);
		path.set(i1, n2);
		path.set(i2, n1);
	}

//	private static void shuffle(Map<String, Node> nodes, List<Node> path) {
//		Node start = nodes.get("AA");
//		path.remove(start);
//		Collections.shuffle(path);
//		path.add(0, start);
//	}

	private static int computePressure(int totalTime, Environnement env, List<Node> path, Map<String, Node> nodes) {
		int currentFlow = 0;
		int pressure = 0;
		Node currentNode = nodes.get("AA");
		for (Node node : path) {
			int distance = env.getDistance(currentNode, node);
			if (distance + 1 > totalTime) {
				pressure += currentFlow * totalTime;
				break;
			}
			pressure += currentFlow * (distance + 1);
			totalTime -= distance + 1;
			currentNode = node;
			currentFlow += node.getFlowRate();
		}
				
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

