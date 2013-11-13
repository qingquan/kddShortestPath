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
} 