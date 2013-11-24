package kddProject2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class SteinerAlg {

	public ArrayList<String> steinerTeam(String initiator, Graph graph, int k,
			ArrayList<String> itemsRequired, Map<String, ArrayList> itemMap, 
			Map<String, Double> capacityMap){
		
		ArrayList<String> formedTeam = new ArrayList<String>();
		List<Vertex> vertexs = graph.getVertexes();
		
		formedTeam.add(initiator);
		float minCost = 1000;
		
		Map<String, Map> shortestWeight = SPToInitiator(graph);
//		System.out.println("the shortest path "+ shortestWeight);
		
//		int maxflow = maxItem(initiator, graph, itemsRequired, itemMap, capacityMap);
//		while(maxItem(formedTeam) != k){
//			for(Vertex v: vertexs){//don't know whether v include initiator
//				int minCostIndex = 
//				int oldMaxItem = MaxItem(formedTeam);
//				int newMaxItem = MaxItem(formedTeam.add(v));
//				float shortestDis = shortestDis(initiator, v);
//				
//				float increCost = ((float)(newMaxItem-oldMaxItem))/shortestDis;
//				if(increCost < minCost){
//					minCost = increCost;
//					
//				}
//			}			
//		}
		
		String v = initiator;
		//边集合。首先添加与v相关的边。
		ArrayList<Map> EdgeInMid = new ArrayList<Map>();
		ArrayList<String> itemsOfv = itemMap.get(v);
		for (String item_r : itemsRequired){
			for(String item_v: itemsOfv){
				if (item_r.equals(item_v)){
					Map<String,String> tmp = new HashMap<String, String>();
					tmp.put(item_r, v);
					EdgeInMid.add(tmp);
				}
			}
		}
		//System.out.println(EdgeInMid);
		
		//按顺序一个个的把user加进来
		ArrayList<String> finalGroupMem = new ArrayList();
		finalGroupMem.add(v);
		String member = v;
		int maxflow_old = 0;

		float maxFlow = (float)0;
		MinDiamSol minDiaAlg = new MinDiamSol();
		minDiaAlg.writeToMFFile(finalGroupMem, itemsRequired, itemMap, EdgeInMid, capacityMap);//initial maxflow
		Maxflow mf = new Maxflow();
		mf.runMF();
		maxFlow = mf.maxFlow;
		
		while(maxFlow != k){
			float tempCost = (float)1000;
			ArrayList<String> tempMember = new ArrayList<String>();
			String tempMemberStr = "";
			ArrayList<Map> tempEdgeInMid = new ArrayList<Map>();//find current min cost
			
			for(int i=0; i<graph.getVertexes().size();i++){
				////写个txt让Maxflow读一下	
				String u = graph.getVertexes().get(i).getName();
				ArrayList<String> tempFinalGroup = new ArrayList<String>(finalGroupMem);
				if(! finalGroupMem.contains(u)){
					tempFinalGroup.add(u);		
					
					//此时maxflow中，Users和Task之间的边的集合
					ArrayList<String> itemsOfu = itemMap.get(member);
					for (String item_r: itemsRequired){
						for(String item_u: itemsOfu){
							if (item_r.equals(item_u)){
								Map<String,String> tmp = new HashMap<String, String>();
								tmp.put(item_r, member);
								EdgeInMid.add(tmp);
							}
						}
					}
					
					minDiaAlg.writeToMFFile(tempFinalGroup, itemsRequired, itemMap, EdgeInMid, capacityMap);
					mf.runMF();
					int newMaxflow = mf.maxFlow;//just use this object and run it? will the file realted?

					if(shortestWeight.get(v).get(u) == null){
						break;
					}
					float shortestPathVU = (Float)shortestWeight.get(v).get(u);
					if(shortestPathVU == (float)0){
						break;
					}
					
					float marginCost = (newMaxflow - maxFlow)/shortestPathVU;
					if(marginCost < tempCost){
						tempCost = marginCost;
						tempMemberStr = u;

						ArrayList<Map> tempEdge = new ArrayList<Map>();//record the temp min maxflow structure
						for(int m=0; i<EdgeInMid.size(); i++){
							Map<String, String> tempCopy = new HashMap<String, String>();
							Iterator it = EdgeInMid.get(m).entrySet().iterator();
							while (it.hasNext()) {
								Map.Entry pairs = (Map.Entry)it.next();
								tempCopy.put((String)pairs.getKey(), (String)pairs.getValue());
							}
							tempEdge.add(tempCopy);
						}
					}
				}
			}
			if(tempMemberStr.equals("")){
				break;
			}
			finalGroupMem.add(tempMemberStr);
			EdgeInMid = tempEdgeInMid;
//			maxFlow = tempCost;
		}
		
		return finalGroupMem;
	}
	
//	public static int maxItem(String initiator, Graph graph, ArrayList<String> itemsRequired, Map<String, ArrayList> itemMap, 
//			Map<String, Double> capacityMap){
//		//按顺序一个个的把user加进来
//		ArrayList<String> finalGcapacityMaproupMem = new ArrayList();
//		finalGroupMem.add(v);
//		String member = v;
//		int maxflow_old = 0;
//				
//		ArrayList<Map> EdgeInMid = new ArrayList<Map>();
//		ArrayList<String> itemsOfv = itemMap.get(v);
//		for (String item_r : itemsRequired){
//			for(String item_v: itemsOfv){
//				if (item_r.equals(item_v)){
//					Map<String,String> tmp = new HashMap<String, String>();
//					tmp.put(item_r, v);
//					EdgeInMid.add(tmp);
//				}
//			}
//		}
//		//System.out.println(EdgeInMid);
//		MinDiamSol minDiaAlg = new MinDiamSol();
//		minDiaAlg.writeToMFFile(finalGroupMem, itemsRequired, itemMap, EdgeInMid, capacityMap);
//		
//		Maxflow mf = new Maxflow();
//        mf.runMF();
//        return mf.maxFlow;
////        System.out.println("maxflow=" + mf.maxFlow);
//	}
	
	
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
//				System.out.println("user name: "+ v1Name+ " "+v2Name);
				if(relationMap.get(v1Name).get(v2Name) != null){
					weight = (Float)relationMap.get(v1Name).get(v2Name);//weight(u,v)
				}
				if(weight != 0){
					edges.add(new PathEdge("Edge_"+edgeId, nodes.get(i), nodes.get(j), weight));
//					System.out.println("value of source, dest, weight are "+v1Name+" "+v2Name+" " + weight);
				}
			}
		}
		Graph graph = new Graph(nodes, edges);
		System.out.println("sucessfully construct the graph");

		return graph;
	}
	
	public Map<String, Map> SPToInitiator(Graph graph){
		//compute shortest paths from initiator to every user

		DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
		dijkstra.execute(graph.getVertexes().get(0));//node 1 is initiator
//		System.out.println("distance is "+ dijkstra.getDistance());
		
		Map<String, Map> shortestWeight = new HashMap<String, Map>();
		shortestWeight.put(graph.getVertexes().get(0).getName(), dijkstra.getDistance());
//		LinkedList<Vertex> path = dijkstra.getPath(nodes.get(10));
//		for (Vertex vertex : path) {
//			System.out.println(vertex);
//		}
		return shortestWeight;
	}
	
	//minimum steiner tree cost of this graph
	public float finalSteinerCost(Graph graph){
		//minimum spanning tree
		ArrayList<PathEdge> minTree = MST.kruskals(graph);
		if(minTree == null){
			System.out.println("no spanning tree for this group");
			return 0;
		}else{
			float teamCost = 0;
			for(int i=0; i<minTree.size(); i++){
				teamCost += minTree.get(i).getWeight();
			}
			
			return teamCost;
		}
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
		Map<String, ArrayList> task_item_Map = myPreProcess.getTaskItemMap();//whole database,<repo, items>
		Map<String, ArrayList> repo_user_Map = myPreProcess.getRepoUserMap();

//		myPreProcess.getRelationWeight(users, dictMap);
		
		Map<String, Map> relationMap = new HashMap<String, Map>();//whole database, name and weight
		Map<String, ArrayList> friendMap = new HashMap<String, ArrayList>();//whole database, only name
		relationMap = myPreProcess.getRelationWeight(users, dictMap).get(1);//1:relationMap
		friendMap = myPreProcess.getRelationWeight(users, dictMap).get(0);//0:friendMap
		
		SteinerAlg steinerAlg = new SteinerAlg();
		ArrayList<String> initiators = myPreProcess.getInitiatorList();
		ArrayList<String> initiatorRepos = myPreProcess.getInitiatorReposList();
		
		//{user, capacity}
        Map<String, Double> capacityMap = new HashMap<String, Double>();
        for(String u: users){
            int numOfRepo =((dictMap.get(u)).size());
            double cap = Math.log(1+numOfRepo);
            capacityMap.put(u, cap);
        }
//        System.out.println("user map " + users);
//        System.out.println("capacity map " + capacityMap);
       
		
		int initSize = initiators.size();
//		int initSize = 20;
//		System.out.println("initiators and repos "+ initiators);
		ArrayList<String> resultStr = new ArrayList<String>();
		
		//for real use
		for(int i=0; i<initSize; i++){
			System.out.println("in the round " + i);
//			String initiatorRepo = initiatorRepos.get(i);
			String oneResult = "";
			String initiator = initiators.get(i);
			String task = initiatorRepos.get(i);
			if(relationMap.get(initiator)!=null && task_item_Map.get(task)!=null && capacityMap.get(initiator) != null){
				long startTime = System.currentTimeMillis();

				ArrayList<String> itemsRequired = new ArrayList();
				itemsRequired.addAll(task_item_Map.get(task));//这个Task需要的items
				int repoItemNum = task_item_Map.get(task).size();
//				System.out.println("we are to construct the map");
				
				ArrayList<String> userWithinHop = myPreProcess.getUserWithinHop(initiator, friendMap);
				Graph graph = steinerAlg.constructGraph(initiator, userWithinHop, relationMap);
				
				float originalCost = steinerAlg.finalSteinerCost(graph);
				ArrayList<String> finalTeam = steinerAlg.steinerTeam(initiator, graph, repoItemNum, itemsRequired, itemMap, capacityMap);
				if(finalTeam.contains(initiator)){
					finalTeam.remove(initiator);
				}
				
				Graph finalGraph = steinerAlg.constructGraph(initiator, finalTeam, relationMap);
				float finalCost = steinerAlg.finalSteinerCost(finalGraph);
				
				long endTime = System.currentTimeMillis();

				oneResult += initiator + " ";
				oneResult += task + " ";
				oneResult += repoItemNum + " ";
				oneResult += finalTeam.size() + " ";
				oneResult += finalCost + " ";
				oneResult += repo_user_Map.get(task).size() + " ";
				oneResult += originalCost + " ";
				oneResult += (endTime-startTime)+"ms";
				oneResult += "\n";
				
				resultStr.add(oneResult);
				System.out.println("this result is "+oneResult);
			}
//			System.out.println("initiator name "+initiators.get(i)+ " repos name "+initiatorRepos.get(i));
		}
//		System.out.println("finish computation");
		//write result to file
        try {
			File file = new File("files/results_of_MinMaxSol.txt");
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
		    for(String s: resultStr){
		    	bw.write(s);
		    }

		    bw.close();
		    fw.close();
			System.out.println("Done");
		} catch (Exception e)
		{
		    e.printStackTrace();
		    System.out.println("No such file exists.");
		}
        
	}
	
}
