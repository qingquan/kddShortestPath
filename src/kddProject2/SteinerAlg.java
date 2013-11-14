package kddProject2;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class SteinerAlg {

	public void steinerTeam(User initiator, Graph graph, int k){
		ArrayList<User> formedTeam = new ArrayList<User>();
		List<Vertex> vertexs = graph.getVertexes();
		formedTeam.add(initiator);
		float minCost = 1000;
		
		while(MaxItem(formedTeam) != k){
			for(Vertex v: vertexs){//don't know whether v include initiator
				int minCostIndex = 
				int oldMaxItem = MaxItem(formedTeam);
				int newMaxItem = MaxItem(formedTeam.add(v));
				float shortestDis = shortestDis(initiator, v);
				
				float increCost = ((float)(newMaxItem-oldMaxItem))/shortestDis;
				if(increCost < minCost){
					minCost = increCost;
					
				}
			}			
		}

		//getUsers within hop
//		ArrayList<User> usersWithinHop = new 
		
		//getUsers weigh for users within this hop
		
	}
	
	public Graph constructGraph(String userInitiator, 
			ArrayList<String> userWithinHop, Map<String, Map> relationMap){
		
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
		//compute shortest paths from initiator to every user
		Graph graph = new Graph(nodes, edges);
		DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
		dijkstra.execute(nodes.get(0));
		LinkedList<Vertex> path = dijkstra.getPath(nodes.get(10));
		System.out.println("distance is "+ dijkstra.getDistance());
		
		for (Vertex vertex : path) {
			System.out.println(vertex);
		}
		return graph;
	}
	
	//minimum steiner tree cost of this graph
	public float finalTeam(Graph graph){
		//minimum spanning tree
		ArrayList<PathEdge> minTree = MST.kruskals(graph);
		if(minTree == null){
			System.out.println("no spanning tree for this group");
		}
		
		float teamCost = 0;
		return teamCost;
	}
	
	public static void main(String[] args) {
		TestCon con =new TestCon();
		Connection dbCon = con.doConnection();
		if(dbCon != null)
			System.out.println("successfully connect to db");

		PreProcess myPreProcess = new PreProcess();

		try{
			//setUserList, setDictMap{user,<repo1...>}, setItemMap{user,<item1...>}
			myPreProcess.getUserRepoDict(dbCon);
		}catch(SQLException ex){System.out.println(ex.getMessage());}
		try{
			//setUserList, setDictMap{user,<repo1...>}, setItemMap{user,<item1...>}
			myPreProcess.setInitiatorRepoList(dbCon);
		}catch(SQLException ex){System.out.println(ex.getMessage());}
		try{
			myPreProcess.getTaskItemMapping(dbCon);//set something
		}catch(SQLException ex){System.out.println(ex.getMessage());}

		ArrayList<String> users = myPreProcess.getUserList();//whole database
		Map<String, ArrayList> dictMap = myPreProcess.getUserRepoMap();//whole database
		Map<String, ArrayList> itemMap = myPreProcess.getUserItemMap();//whole database,<user, items>
		Map<String, ArrayList> taskItemMap = myPreProcess.getTaskItemMap();//whole database,<repo, items>

//		myPreProcess.getRelationWeight(users, dictMap);
		
		Map<String, Map> relationMap = new HashMap<String, Map>();//whole database, name and weight
		Map<String, ArrayList> friendMap = new HashMap<String, ArrayList>();//whole database, only name
		relationMap = myPreProcess.getRelationWeight(users, dictMap).get(1);//1:relationMap
		friendMap = myPreProcess.getRelationWeight(users, dictMap).get(0);//0:friendMap
		
		SteinerAlg steinerAlg = new SteinerAlg();
		ArrayList<String> initiators = myPreProcess.getInitiatorList();
		ArrayList<String> initiatorRepos = myPreProcess.getInitiatorReposList();
		int initSize = initiators.size();
		
		for(int i=0; i<initSize; i++){
			String initiator = initiators.get(i);
			String initiatorRepo = initiatorRepos.get(i);
			if(relationMap.get(initiator)!=null && taskItemMap.get(initiatorRepo)!=null){
				int repoItemNum = taskItemMap.get(initiatorRepo).size();
				ArrayList<String> userWithinHop = myPreProcess.getUserWithinHop(initiator, friendMap);
				Graph graph = steinerAlg.constructGraph(initiator, userWithinHop, relationMap);
				
				steinerAlg.steinerTeam(initiator, graph, repoItemNum);
			}
			System.out.println("initiator name "+initiators.get(i)+ " repos name "+initiatorRepos.get(i));
		}
		//iterate over all users
//		for(String initiator: users){
////			String testInitiator = "crodjer";
//
//			// Lets check from location Loc_1 to Loc_10

//		}
	
		
	}
	
}
