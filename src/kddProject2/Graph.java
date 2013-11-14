package kddProject2;

import java.util.List;

public class Graph {
	private final List<Vertex> vertexes;
	private final List<PathEdge> edges;

	public Graph(List<Vertex> vertexes, List<PathEdge> edges) {
		this.vertexes = vertexes;
		this.edges = edges;
	}

	public List<Vertex> getVertexes() {
		return vertexes;
	}

	public List<PathEdge> getEdges() {
		return edges;
	}
//	import java.util.Collection;
//	import java.util.HashSet;
//	import java.util.Scanner;
//	import java.util.Set;
//
//	public class Graph {
//		public float compareTo(PathEdge e) {
//			return weight - e.getWeight();
//		}
//	  private Collection<Edge> edges = new HashSet<Edge>();
//		private Set<Vertex> vertices = new HashSet<Vertex>();
//
//		/**
//		 * A class representing a graph, can be built from an edge list
//		 * 
//		 * The first int is the number of edges, following this are triples of
//		 * integers for each edge representing vertex u, vertex v, and the weight
//		 * 
//		 * @param input
//		 */
//		
//		public Graph(String input) {
//			Scanner scan = new Scanner(input);
//			int count = scan.nextInt();
//			for (int i = 0; i < count; i++) {
//				Vertex u = new Vertex(scan.nextInt());
//				Vertex v = new Vertex(scan.nextInt());
//				int weight = scan.nextInt();
//				vertices.add(u);
//				vertices.add(v);
//				edges.add(new Edge(u, v, weight));
//			}
//			scan.close();
//		}
//
//		public Collection<Edge> getEdgeList() {
//			return edges;
//		}
//
//		public Set<Vertex> getVertices() {
//			return vertices;
//		}
//		
//	}
} 