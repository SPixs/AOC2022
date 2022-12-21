import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Set;

public class HeightMap {

	public static void main(String[] args) throws IOException {
		
		Stream<String> lines = Files.lines(Paths.get("./input_heightMap.txt"));
		List<String> collect = lines.collect(Collectors.toList());
		
		Node[][] nodes = null;

		int rowIndex = 0;
		for (String s : collect) {
			int width = s.length();
			if (nodes == null) {
				nodes = new Node[width][collect.size()];
			}
			for (int x=0;x<width;x++) {
				nodes[x][rowIndex] = new Node(s.charAt(x));
			}
			rowIndex++;
		}			

		// Part 1:
		// Use Dijkstra 
		Graph graph = new Graph();
		int width = nodes.length;
		int height = nodes[0].length;
		
		Node startNode = null;
		Node endNode = null;
		
		List<Node> lowestNodes = new ArrayList<HeightMap.Node>();
		
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				Node node = nodes[x][y];
				graph.addNode(node);
				if (node.getName() == 'S') { startNode = node; }
				if (node.getName() == 'E') { endNode = node; }
				if (node.getHeight() == 0) { lowestNodes.add(node); }

				if (x < width - 1) addIfReachable(node, nodes[x+1][y]);
				if (x > 0) addIfReachable(node, nodes[x-1][y]);
				if (y < height - 1) addIfReachable(node, nodes[x][y+1]);
				if (y > 0) addIfReachable(node, nodes[x][y-1]);
			}
		}
		
		graph = calculateShortestPathFromSource(graph, startNode);
		System.out.println(endNode.getDistance());

		// Part 2:
		// brute force on all lowest candidates
		int min = Integer.MAX_VALUE;
		for (Node node : lowestNodes) {
			Stream.of(nodes).flatMap(Stream::of).forEach(Node::reset);
			graph = calculateShortestPathFromSource(graph, node);
			min = Math.min(min, endNode.getDistance());
		};
		
		System.out.println(min);
	}
	
	private final static void addIfReachable(Node source, Node foreignNode) {
		if (foreignNode.isReachableFrom(source)) {
			source.addForeignNode(foreignNode, 1);
		}
	}

	public static class Graph {
	
	    private Set<Node> nodes = new HashSet<>();
	    
	    public void addNode(Node nodeA) {
	        nodes.add(nodeA);
	    }
	}
	
	public static class Node {
	    
	    private char name;
		private Integer distance = Integer.MAX_VALUE;
	    private List<Node> shortestPath = new LinkedList<>();
		Map<Node, Integer> adjacentNodes = new HashMap<>();
		
	    public Node(char c) {
	        this.name = c;
	    }

		public char getName() {
			return name;
		}

	    public List<Node> getShortestPath() {
			return shortestPath;
		}

		public void reset() {
			distance = Integer.MAX_VALUE;
			Set<Node> keySet = adjacentNodes.keySet();
			for (Node node : keySet) {
				adjacentNodes.put(node, 1);
			}
		}

		public boolean isReachableFrom(Node node) {
			return getHeight() <= node.getHeight() + 1;
		}

		private int getHeight() {
			if (name == 'S') return 0;
			if (name == 'E') return 'z'-'a';
			return name - 'a';
		}

		public void setShortestPath(List<Node> shortestPath) {
			this.shortestPath = shortestPath;
		}

	    public void setDistance(Integer distance) {
			this.distance = distance;
		}

	    public void addForeignNode(Node destination, int distance) {
	        adjacentNodes.put(destination, distance);
	    }
	 
		public Map<Node, Integer> getAdjacentNodes() {
			return adjacentNodes;
		}

		public int getDistance() {
			return distance;
		}
	}
	
	public static Graph calculateShortestPathFromSource(Graph graph, Node source) {
	    
		source.setDistance(0);
	
	    Set<Node> settledNodes = new HashSet<>();
	    Set<Node> unsettledNodes = new HashSet<>();
	
	    unsettledNodes.add(source);
	
	    while (unsettledNodes.size() != 0) {
	        Node currentNode = getLowestDistanceNode(unsettledNodes);
	        unsettledNodes.remove(currentNode);
	        for (Entry<Node, Integer> adjacencyPair : currentNode.getAdjacentNodes().entrySet()) {
	            Node adjacentNode = adjacencyPair.getKey();
	            Integer edgeWeight = adjacencyPair.getValue();
	            if (!settledNodes.contains(adjacentNode)) {
	                calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
	                unsettledNodes.add(adjacentNode);
	            }
	        }
	        settledNodes.add(currentNode);
	    }
	    return graph;
	}
	
	private static Node getLowestDistanceNode(Set<Node> unsettledNodes) {
		return unsettledNodes.stream().min((n1, n2) -> {
			return Integer.compare(n1.getDistance(), n2.getDistance());
		}).orElse(null);
	}
	
	public static void calculateMinimumDistance(Node evaluationNode,  Integer edgeWeigh, Node sourceNode) {
	    Integer sourceDistance = sourceNode.getDistance();
	    if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
	        evaluationNode.setDistance(sourceDistance + edgeWeigh);
	        LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
	        shortestPath.add(sourceNode);
	        evaluationNode.setShortestPath(shortestPath);
	    }
	}
}