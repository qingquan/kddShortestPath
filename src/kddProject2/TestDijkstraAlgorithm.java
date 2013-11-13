package kddProject2;

//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.*;

//import org.junit.Test;
//import de.vogella.algorithms.dijkstra.engine.DijkstraAlgorithm;
//import de.vogella.algorithms.dijkstra.model.Edge;
//import de.vogella.algorithms.dijkstra.model.Graph;
//import de.vogella.algorithms.dijkstra.model.Vertex;

//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;

public class TestDijkstraAlgorithm {

//	private nodes;
//	private edges;

//	public void addLane(String laneId, int sourceLocNo, int destLocNo, int duration) {
//		PathEdge lane = new PathEdge(laneId,nodes.get(sourceLocNo), nodes.get(destLocNo), duration);
//		edges.add(lane);
//	}
	
//	@Test
	public static void main(String[] args) {
		String userInitiator = "crodjer";		
//		public static void main(String arg[]){
		TestCon con =new TestCon();
		Connection dbCon = con.doConnection();
		if(dbCon != null)
			System.out.println("successfully connect to db");
		
		PreProcess myPreProcess = new PreProcess();
		
		try{
			myPreProcess.getUserRepoDict(dbCon);
		}catch(SQLException ex){System.out.println(ex.getMessage());}
		try{
			myPreProcess.getTaskItemMapping(dbCon);
		}catch(SQLException ex){System.out.println(ex.getMessage());}
//		
		ArrayList<String> users = myPreProcess.getUserList();
		ArrayList<String> repos = myPreProcess.getRepoList();
		Map<String, ArrayList> dictMap = myPreProcess.getUserRepoMap();
		Map<String, ArrayList> itemMap = myPreProcess.getUserItemMap();
		
//		Map<String, ArrayList> task_item_Map = myPreProcess.getTaskItemMap();
//		
		myPreProcess.getRelationWeight(users, dictMap);
//
		Map<String, Map> relationMap = myPreProcess.getRelationWeight(users, dictMap).get(1);//1:relationMap
		Map<String, ArrayList> friendMap = myPreProcess.getRelationWeight(users, dictMap).get(0);//0:friendMap
//		myPreProcess.writeRelationFile(relationMap);//write relation map to file
////		myPreProcess.writeReposFile(dictMap);//write repos map to file
//		Map<String, ArrayList> friendMap = myPreProcess.getUserFriendMap();
//		System.out.print("friend map is " + relationMap);
//		System.out.print(friendMap.get("crodjer").size());
//	}
		
//		System.out.println("structure of relationmap "+relationMap.get(userInitiator));
//		System.out.println("relation "+ (Float)relationMap.get(userInitiator).get("victorcoder"));
//		String testUserName = preProcess.getUserList().get(0); //use 1 user to test
		ArrayList<String> userWithinHop = myPreProcess.getUserWithinHop(userInitiator, friendMap);
//		for(String s: users){
//			System.out.println("users within two hops " + s + " "+ relationMap.get(s));
//		}
		int userSize = userWithinHop.size();
		
		List<Vertex> nodes = new ArrayList<Vertex>();
		List<PathEdge> edges = new ArrayList<PathEdge>();
		//construct user node, <id, userName>
		Vertex testUser = new Vertex("Node_" + 0, userInitiator);//user id, name
		nodes.add(testUser);
		int tempId = 1;
		for (String userName: userWithinHop) {
			Vertex user = new Vertex("Node_" + tempId, userName);
			nodes.add(user);
			tempId ++;
		}
//		for (int i = 0; i < 11; i++) {
//			Vertex location = new Vertex("Node_" + i, "NodeName_" + i);
//			nodes.add(location);
//		}
//		construct edge <id, source, destination, weight>
		int edgeId = 0;
		for(int i=0; i<userSize; i++){
			for(int j=i+1; j<userSize; j++){
				String v1Name = nodes.get(i).getName();
				String v2Name = nodes.get(j).getName();
				float weight = (float)0;
				System.out.println("user name: "+ v1Name+ " "+v2Name);
				if(relationMap.get(v1Name).get(v2Name) != null){
					weight = (Float)relationMap.get(v1Name).get(v2Name);//weight(u,v)
				}
				if(weight != 0){
					edges.add(new PathEdge("Edge_"+edgeId, nodes.get(i), nodes.get(j), weight));
					System.out.println("value of source, dest, weight are "+v1Name+" "+v2Name+" " + weight);
				}
			}
		}
//		System.out.println("the conent of node "+nodes.get(0)+" "+nodes.get(1));
//		edges.add(new PathEdge("Edge_0", nodes.get(0), nodes.get(1), (float)850.2));
//		edges.add(new PathEdge("Edge_0", nodes.get(0), nodes.get(2), (float)400.2));
//		edges.add(new PathEdge("Edge_0", nodes.get(0), nodes.get(4), (float)173.4));
//		edges.add(new PathEdge("Edge_0", nodes.get(2), nodes.get(6), 400));
//		edges.add(new PathEdge("Edge_0", nodes.get(2), nodes.get(7), 103));
//		edges.add(new PathEdge("Edge_0", nodes.get(3), nodes.get(7), 183));
//		edges.add(new PathEdge("Edge_0", nodes.get(5), nodes.get(8), 250));
//		edges.add(new PathEdge("Edge_0", nodes.get(8), nodes.get(9), 84));
//		edges.add(new PathEdge("Edge_0", nodes.get(7), nodes.get(9), 167));
//		edges.add(new PathEdge("Edge_0", nodes.get(4), nodes.get(9), 502));
//		edges.add(new PathEdge("Edge_0", nodes.get(9), nodes.get(10), 40));
//		edges.add(new PathEdge("Edge_0", nodes.get(1), nodes.get(10), 600));
//		addLane("Edge_0", 0, 1, 85);
//		addLane("Edge_1", 0, 2, 217);
//		addLane("Edge_2", 0, 4, 173);
//		addLane("Edge_3", 2, 6, 186);
//		addLane("Edge_4", 2, 7, 103);
//		addLane("Edge_5", 3, 7, 183);
//		addLane("Edge_6", 5, 8, 250);
//		addLane("Edge_7", 8, 9, 84);
//		addLane("Edge_8", 7, 9, 167);
//		addLane("Edge_9", 4, 9, 502);
//		addLane("Edge_10", 9, 10, 40);
//		addLane("Edge_11", 1, 10, 600);

		// Lets check from location Loc_1 to Loc_10
		Graph graph = new Graph(nodes, edges);
		DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
		dijkstra.execute(nodes.get(0));
		LinkedList<Vertex> path = dijkstra.getPath(nodes.get(10));
		System.out.println("distance is "+ dijkstra.getDistance());

//		assertNotNull(path);
//		assertTrue(path.size() > 0);

		for (Vertex vertex : path) {
			System.out.println(vertex);
		}
	}
} 