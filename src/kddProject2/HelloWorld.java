package kddProject2;
import java.util.*;

public class HelloWorld {
	
    public static void main(String[] args) {

        ArrayList<String> keys = new ArrayList<String>();
        keys.add("main1");
        keys.add("main2");
        keys.add("main5");
        keys.add("main3");
        keys.add("main4");
        keys.add("");
    	Map<String, String> list1 = new HashMap<String, String>();
    	list1.put("main1", "good");
    	list1.put("main2", "bad");
    	list1.put("main3", "not good");
    	list1.put("main4", "also good");

    	System.out.println("the keys " + keys);
    	if(keys.contains("")){
    		keys.remove("");
    	}
    	System.out.println("the keys " + keys);
//    	List<String> list2 = new ArrayList<String>(list1);
//    	System.out.println("list 2"+list2);
//    	System.out.println("contains one element "+ list2.contains(another));
//    	Map<String, Integer> map = new HashMap<String, Integer>();
//    	map.put("bji", 1);
//    	int listSize = list1.size();
//    	if(map.get("miao")!=null){
//    		System.out.println("doesn't contain it");
//    	}
//    	for(int i=0; i<listSize; i++){
//    		for(int j=i+1; j<listSize; j++){
//    			System.out.println("first: "+list1.get(i)+" Second: "+list1.get(j));
//    		}
//    	}
    }
}
