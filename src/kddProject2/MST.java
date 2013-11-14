package kddProject2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.HashSet;

public class MST {

  /**
     * Run Kruskal's algorithm on the given graph and return the MST, return
     * null if no MST exists for the graph
     * 
     * @param g
     *            the graph, g will never be null
     * @return the MST of the graph, null of no valid MST exists
     */
    public static ArrayList<PathEdge> kruskals(Graph g) {
        Set<Vertex> setVertexes = new HashSet<Vertex>(g.getVertexes());
		DisjointSets<Vertex> ds = new DisjointSets<Vertex>(setVertexes);

        PriorityQueue<PathEdge> pq = new PriorityQueue<PathEdge>();
        ArrayList<PathEdge> mst = new ArrayList<PathEdge>();
        for (PathEdge e : g.getEdges()) {
                    pq.add(e);
        }
		System.out.println("value of pq"+pq);

        while (!pq.isEmpty() && mst.size() < g.getVertexes().size() - 1) {
            PathEdge e = pq.poll();
            Vertex v = e.getSource();
            Vertex u = e.getDestination();
            if (!ds.sameSet(u, v)) {
                ds.merge(v, u);
                mst.add(e);
            }
            if(g.getVertexes().size()-1 == mst.size()){
                return mst;
            }
        }
        return null;
    }
    
//    public class MST {
//
//      /**
//    	 * Run Kruskal's algorithm on the given graph and return the MST, return
//    	 * null if no MST exists for the graph
//    	 * 
//    	 * @param g
//    	 *            the graph, g will never be null
//    	 * @return the MST of the graph, null of no valid MST exists
//    	 */
//    	public static Collection<Edge> kruskals(Graph g) {
//    		DisjointSets<Vertex> ds = new DisjointSets<Vertex>(g.getVertices());
//    		PriorityQueue<Edge> pq = new PriorityQueue<Edge>();
//    		ArrayList<Edge> mst = new ArrayList<Edge>();  
//    		for (Edge e : g.getEdgeList()) {
//                		pq.add(e);
//           		 }
//            while (!pq.isEmpty() && mst.size() < g.getVertices().size() - 1) {
//                Edge e = pq.poll();
//                Vertex v = e.getV();
//                Vertex u = e.getU();
//                if (!ds.sameSet(u, v)) {
//                    ds.merge(v, u);
//                    mst.add(e);  
//                }	
//                if(g.getVertices().size()-1 == mst.size()){
//            		return mst;
//                }
//            }
//    		return null;
//    	}
//    }
}
