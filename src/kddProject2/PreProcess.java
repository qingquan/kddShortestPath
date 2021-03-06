package kddProject2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.io.*;

import com.google.common.collect.Sets;

public class PreProcess {
	private int limitNum = 2000;
	private int initiatorLimit = 300;
	private int hop = 1;
	private ArrayList<String> userList = new ArrayList<String>();//get users 
	private ArrayList<String> repoList = new ArrayList<String>();//get repos 
	private ArrayList<String> initiatorList = new ArrayList<String>();//get users 
	private ArrayList<String> initiatorRepoList = new ArrayList<String>();//get repos 
	private Map<String, ArrayList> dictMap = new HashMap<String, ArrayList>();//{user, repos}
	private Map<String, ArrayList> itemMap = new HashMap<String, ArrayList>();//{user, items}
	private Map<String, ArrayList> friendMap = new HashMap<String, ArrayList>();//{user, users in 1- hop}
	private Map<String, ArrayList> task_item_Map = new HashMap<String, ArrayList>();//{repo, items}
	private Map<String, ArrayList> repo_user_Map = new HashMap<String, ArrayList>();//{repo, users}

    Map<String, Map> relationMap = new HashMap<String, Map>();//{user, {user, relationweight}}

	public ArrayList<String> getUserList(){
		return userList;
	}
	
	public ArrayList<String> getRepoList(){
		return repoList;
	}
	
	public Map<String, ArrayList> getUserRepoMap(){
		return dictMap;
	}
	
	public Map<String, ArrayList> getUserItemMap(){
		return itemMap;
	}
	
	public Map<String, ArrayList> getUserFriendMap(){
		return friendMap;
	}
	
	public Map<String, ArrayList> getTaskItemMap(){
		return task_item_Map;
	}

	public ArrayList<String> getInitiatorList(){
		return initiatorList;
	}
	
	public ArrayList<String> getInitiatorReposList(){
		return initiatorRepoList;
	}
	
	public Map<String, ArrayList> getRepoUserMap(){
		return repo_user_Map;
	}
	
	public void setInitiatorRepoList(Connection con) throws SQLException{
//		ArrayList<String> initiators = new ArrayList<String>();
//		ArrayList<String> initiatorRepos = new ArrayList<String>();
		
		Statement stmt = null;
		String queryUser = "SELECT user_login, repo_name " + 
				"FROM repos LIMIT " + initiatorLimit;
//		(SELECT repo_name, contributor_login "+
		stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(queryUser);
		
		while (rs.next()) {
			String userName = rs.getString(1); // or rs.getString("NAME");
			String repoName = rs.getString(2); // or rsfinalCost.getString("NAME");
			initiatorList.add(userName);
			initiatorRepoList.add(repoName);
//			System.out.println(" user name : "+userName);
		}
	}

	public void getUserRepoDict(Connection con) throws SQLException{
		Statement stmt = null;
		String queryUser = "SELECT DISTINCT contributor_login FROM " + 
							    "(SELECT repo_name, contributor_login "+
						        "FROM contributors LIMIT " + limitNum + " ) t ";
		
		stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(queryUser);
		
		//-----------------------construct user list---------------------------------
		while (rs.next()) {
			String userName = rs.getString(1); // or rs.getString("NAME");
			userList.add(userName);
//			System.out.println(" user name : "+userName);
		}
		
		//----------------construct dict <user, <repo1,repo2...>>----------------------------
		for(String s: userList){
			ArrayList<String> repoList = new ArrayList<String>();
			String query = 
					"SELECT repo_name FROM " + 
					    "(SELECT repo_name, contributor_login "+
				        "FROM contributors LIMIT " + limitNum + " ) t " + 
				    "WHERE contributor_login= '" + s + "'";
			ResultSet rsRepo= stmt.executeQuery(query);
			while(rsRepo.next()){
				String repoName = rsRepo.getString(1);
				repoList.add(repoName);
			}			
			dictMap.put(s, repoList);
			
		}
		//------------------items---------------------
		for(String s2: userList){
			ArrayList<String> langList = new ArrayList<String>();
			String queryLang = 
				"SELECT DISTINCT language " + 
		        "FROM languages " + 
		    "WHERE login_name= '" + s2 + "' "+
		    "LIMIT "+ limitNum;
			ResultSet rsLang= stmt.executeQuery(queryLang);
			while(rsLang.next()){
				String langName = rsLang.getString(1);
				langList.add(langName);
			}	
			itemMap.put(s2, langList);
		}	
	}
	
	public void getTaskItemMapping(Connection con) throws SQLException{
		Statement stmt = null;
		String queryRepo = "SELECT DISTINCT repo_name FROM " + 
	    					"repos LIMIT "+ initiatorLimit;
		
		stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(queryRepo);
		//-----------------------construct repo list---------------------------------
		while (rs.next()) {
			String repoName = rs.getString(1); // or rs.getString("NAME");
			repoList.add(repoName);
		}
		//------------------items---------------------
		for(String s2: repoList){
			ArrayList<String> langList = new ArrayList<String>();
			String queryLang = 
				"SELECT DISTINCT language " + 
		        "FROM languages " + 
		    "WHERE repo_name= '" + s2 + "' "+
		    "LIMIT "+ limitNum;
			ResultSet rsLang= stmt.executeQuery(queryLang);
			while(rsLang.next()){
				String langName = rsLang.getString(1);
				langList.add(langName);
			}	
			task_item_Map.put(s2, langList);
		}
		//------------------users in this repo---------------------
		for(String s2: repoList){
			ArrayList<String> userList = new ArrayList<String>();
			String queryUsers = 
					"SELECT DISTINCT contributor_login " + 
							"FROM contributors " + 
							"WHERE repo_name= '" + s2 + "' "+
							"LIMIT "+ limitNum;
			ResultSet rsUsers= stmt.executeQuery(queryUsers);
			while(rsUsers.next()){
				String userName = rsUsers.getString(1);
				userList.add(userName);
			}	
			repo_user_Map.put(s2, userList);
		}	
	}
	
	public ArrayList<Map> getRelationWeight(ArrayList<String> users, Map<String, ArrayList> dictMap){
		ArrayList<Map> relationAndFriendMap = new ArrayList<Map>();
		
//	    Map<String, Map> relationMap = new HashMap<String, Map>();
	    for(String s: users){
	    	ArrayList repoList1 = dictMap.get(s);
	    	Set<String> repoSet1 = new HashSet<String>(repoList1);
	    	int numRepo1 = repoList1.size();
	    	
			Map<String, Float> relation = new HashMap<String, Float>();
			ArrayList<String> friends = new ArrayList();

	    	for(String os: users){
	    		if(!os.equals(s)){
	    			ArrayList repoList2 = dictMap.get(os);
	    			int numRepo2 = repoList2.size();
	    			
	    			Set<String> repoSet2 = new HashSet<String>(repoList2);
	    			Set<String> intersect = Sets.intersection(repoSet1, repoSet2);
	    			int numIntersect = intersect.size();
	    			float weight = (float)numIntersect/(float)(numRepo1+numRepo2);
	    			if(weight>0.00){
	    				friends.add(os);
	    			}
	    			relation.put(os, weight);
	    		}
	    	}
	    	friendMap.put(s, friends);
	    	relationMap.put(s, relation);
	    }
	    relationAndFriendMap.add(friendMap);
	    relationAndFriendMap.add(relationMap);
	    return relationAndFriendMap;
	}
	
	public ArrayList<String> getUserWithinHop(String name, Map<String, ArrayList> friendMap){
//		Map<String, ArrayList> friendMap = myPreProcess.getUserFriendMap();

		//search all users within hop
		String v = name;
//		System.out.println("user within it " + v);
		
		ArrayList<String> groupMem = new ArrayList<String>();
		ArrayList<String> groupMem2 = new ArrayList<String>();
//		Map<String, ArrayList> friendMap = getUserFriendMap();

		//hop = 2 先找出hop=2以內的所有用戶，放进groupMem里面
		groupMem = friendMap.get(v);
		for (String gm1: groupMem){
			ArrayList groupMemTmp = friendMap.get(gm1);
			groupMem2.removeAll(groupMemTmp);//去掉重复元素
			groupMem2.addAll(groupMemTmp);
		}
		groupMem.removeAll(groupMem2);
		groupMem.addAll(groupMem2);
		groupMem.remove(v);
		System.out.println(groupMem);
		//System.out.println(groupMem.size());
				
		return groupMem;
	}
	
	public void writeReposFile(Map<String, ArrayList> dictMap){
		try {
			File file = new File("files/userRepos.txt");
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
		    Iterator it = dictMap.entrySet().iterator();
		    while (it.hasNext()) {
				String repoString = "";

		        Map.Entry pairs = (Map.Entry)it.next();
		        
		        String userName = (String)pairs.getKey();
		        repoString += userName; //add userName to string
		        
		        List<String> repoList = (ArrayList<String>)pairs.getValue();
		        String numRepo = String.valueOf(repoList.size());
		        repoString += " " + numRepo;
			    repoString += "\n\n";
				bw.write(repoString);
		        System.out.println(pairs.getKey() + " = " + pairs.getValue());
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
	
	public void writeRelationFile(Map<String, Map> relationMap){
		try {
			File file = new File("files/relationWeight.txt");
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
//		    PrintWriter pr = new PrintWriter(file);
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
		    Iterator it = relationMap.entrySet().iterator();
		    while (it.hasNext()) {
				String relationString = "";

		        Map.Entry pairs = (Map.Entry)it.next();
		        
		        String userName = (String)pairs.getKey();
		        relationString += userName; //add userName to string
		        
		        Map<String, Float> mapOthers = (Map<String, Float>)pairs.getValue();
			    Iterator itOthers = mapOthers.entrySet().iterator();
			    while (itOthers.hasNext()) {
			        Map.Entry pairsOthers = (Map.Entry)itOthers.next();
			        relationString += " " + (String)pairsOthers.getKey(); //add other users and weight to str
			        relationString += " " + pairsOthers.getValue().toString();
//			        System.out.println(pairsOthers.getKey() + " = " + pairsOthers.getValue());
			    }
			    relationString += "\n\n";
				bw.write(relationString);
//		        System.out.println(pairs.getKey() + " = " + pairs.getValue());
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
	
//	public static void main(String arg[]){
//		TestCon con =new TestCon();
//		Connection dbCon = con.doConnection();
//		if(dbCon != null)
//			System.out.println("successfully connect to db");
//		
//		PreProcess myPreProcess = new PreProcess();
//		
//		try{
//			myPreProcess.getUserRepoDict(dbCon);
//		}catch(SQLException ex){System.out.println(ex.getMessage());}
//		try{
//			myPreProcess.getTaskItemMapping(dbCon);
//		}catch(SQLException ex){System.out.println(ex.getMessage());}
//		
//		ArrayList<String> users = myPreProcess.getUserList();
//		ArrayList<String> repos = myPreProcess.getRepoList();
//		Map<String, ArrayList> dictMap = myPreProcess.getUserRepoMap();
//		Map<String, ArrayList> itemMap = myPreProcess.getUserItemMap();
//		
//		Map<String, ArrayList> task_item_Map = myPreProcess.getTaskItemMap();
//		
//		myPreProcess.getRelationWeight(users, dictMap);
//
//		Map<String, Map> relationMap = myPreProcess.getRelationWeight(users, dictMap);
//		myPreProcess.writeRelationFile(relationMap);//write relation map to file
////		myPreProcess.writeReposFile(dictMap);//write repos map to file
//		Map<String, ArrayList> friendMap = myPreProcess.getUserFriendMap();
//		System.out.print(friendMap);
//		System.out.print(friendMap.get("crodjer").size());
//	}

}
