package kddProject2;

import java.util.HashMap;

import java.util.Set;

public class DisjointSets<T> {
	private HashMap<T,Node> map;

	/**
	 * @param setItems
	 * the initial items (sameSet and merge will never be called with
	 * items not in this set, this set will never contain null
	 * elements)
	 */
	public DisjointSets(Set<T> setItems) {
		map = new HashMap<T,Node>();
		for(T t : setItems){
			map.put(t, new Node(t,0));
		}	
	}

	/**
	 * @param u
	 * @param v
	 * @return true if the items given are in the same set, false otherwise
	 */
	public boolean sameSet(T u, T v) {
		if(find(u).equals(find(v))){
			return true;
		}
		return false;
	}
	
	/**
	 * merges the sets u and v are in, do nothing if they are in the same set
	 *
	 * @param u
	 * @param v
	 */
	public void merge(T u, T v) {
		final T uRoot = find(u);
		final T vRoot = find(v);

		if(!uRoot.equals(vRoot)){
			final Node uNode = map.get(uRoot);
			final Node vNode = map.get(vRoot);
			if(uNode.getRank() < vNode.getRank()){
				uNode.setParent(vRoot);
			}else if(uNode.getRank() > vNode.getRank()){
				vNode.setParent(uRoot);
			}else{
				vNode.setParent(uRoot);
				uNode.rank++;
			}
		}
	}
	
	private T find(T t){
		final Node n = map.get(t);
		if(n == null){
			return null;
		}
		if(t.equals(n.getParent())){
			return t;
		}
		n.setParent(find(n.getParent()));
		return n.getParent();
	}
	
	public String toString(){
		return map.keySet().toString();
	}

	private class Node{
		private T parent;
		private int rank;
		public Node(T parent, int rank){
			this.parent = parent;
			this.rank = rank;
		}
		private T getParent(){
			return parent;
		}
		private void setParent(T parent){
			this.parent = parent;
		}
		private int getRank(){
			return rank;
		}	
	}
}
