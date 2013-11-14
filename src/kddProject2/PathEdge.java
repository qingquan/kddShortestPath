package kddProject2;

public class PathEdge implements Comparable<PathEdge>{

	private final String id; 
	private final Vertex source;
	private final Vertex destination;
	private final float weight; 

	public PathEdge(String id, Vertex source, Vertex destination, float weight) {
		this.id = id;
		this.source = source;
		this.destination = destination;
		this.weight = weight;
	}

	@Override
	public int hashCode() {
		return (source == null ? 0 : source.hashCode()) 
				^ (destination == null ? 0 : destination.hashCode()) 
				^ (int)weight;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PathEdge) {
			PathEdge e = (PathEdge) o;
			return weight == e.weight
					&& ((source.equals(e.source) && destination.equals(e.destination)) 
							|| (source.equals(e.destination) && destination.equals(e.source)));
		} else {
			return false;
		}
	}
	
	public int compareTo(PathEdge e) {
		return (int)(weight - e.getWeight());
	}
	
	public String getId() {
		return id;
	}
	public Vertex getDestination() {
		return destination;
	}

	public Vertex getSource() {
		return source;
	}
	
	public float getWeight() {
		return weight;
	}

	@Override
	public String toString() {
		return source + " " + destination+ " " + weight;
	}
} 