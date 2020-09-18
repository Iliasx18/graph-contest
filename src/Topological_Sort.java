import java.util.LinkedList;
import java.util.Stack;

import jdg.graph.DirectedGraph;
import jdg.graph.Node;

public class Topological_Sort {
	int[] visited;
	DirectedGraph g;
	LinkedList<Node> S;
	
	public Topological_Sort(DirectedGraph g) {
		this.g = g;
		int i = 0;
		for (Node u:this.g.vertices) i++;
		this.visited = new int[i];
		this.S = new LinkedList<Node>();
	}
	
	public void topoSort(Node u) {
		this.visited[u.index]=1;
		for (Node v:u.successors) {
			if (this.visited[v.index]==0) topoSort(v);
		}
		S.push(u);
	}
	
	public LinkedList<Node> sort() {
		for (Node u:g.vertices) {
			if (visited[u.index]==0)  topoSort(u);
		}
		return S;
	}

}
