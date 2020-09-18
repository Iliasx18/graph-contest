import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import jdg.graph.DirectedGraph;
import jdg.graph.GridPoint_2;
import jdg.graph.GridVector_2;
import jdg.graph.GridVector_n;
import jdg.graph.Node;
import jdg.graph.Paire;

/**
 * Main class providing tools for computing an upward grid drawing of a directed graph
 * that minimizes the number of crossings
 * 
 * @author Luca Castelli Aleardi (2019)
 *
 */
public class UpwardDrawing {
    /** input graph to draw */
    public DirectedGraph g=null;
    /** height of the grid (input parameter) */
    public int height;
    /** width of the grid (input parameter) */
    public int width;
    
    /**
     * initialize the input of the program
     */
    public UpwardDrawing(DirectedGraph g, int width, int height) {
    	this.g=g;
    	this.width=width;
    	this.height=height;
    }
    
    /**
     * Return the number of edge crossings
     */
    public int computeCrossings() {
    	LinkedList<Edge> l = new LinkedList<Edge>();
    	for (Node u:g.vertices) { 
    		for (Node v:u.successors) {
    			Edge e = new Edge(u,v);
    			l.add(e);
    		}
    	}
    	int count = 0;
    	for (Edge e1:l) {
    		e1.index=-1;
    		for (Edge e2:l) {
    			if ((e1!=e2)&&(e2.index!=-1)) {
    				if (Edge.cross(e1,e2)) count++;
    			}		
    		}
    	}
    	return count;
    	
    	
    	
    }

    /**
     * Check whether the graph embedding is a valid upward drawing <br>
     * -) the drawing must be upward <br>
     * -) the integer coordinates of nodes must lie in the prescribed bounds: the drawing area is <em>[0..width] x [0..height]</em> <br>
     * -) non adjacent crossing edges must intersect at their interiors
     */
    public boolean checkValidity() {
    	LinkedList<Edge> l = new LinkedList<Edge>();
    	for (Node u:g.vertices) { 
    		for (Node v:u.successors) {
    			Edge e = new Edge(u,v);
    			if (v.p.y<=u.p.y) {
    				System.out.println("Bad edge :"+e);
    				return false;
    			}
    			for (Edge f:l) {
    				if (Edge.incorrect(e, f)) return false;
    			}
    			l.add(e);
    		}
    	}
    	return true;
    }
    
    
    public static boolean isvalid(LinkedList<Edge> edges, Node u) { //This method used in computeValidinitialLayout 
    									//checks if the current spot for the node u preserves a valid drawing
    														
    	LinkedList<Edge> nedges = new LinkedList<Edge>();
    	for (Node v : u.predecessors) {
			Edge e = new Edge(v, u);
			for (Edge f:nedges) {
				if (Edge.incorrect(e, f)) return false;
			}
			for (Edge f:edges) {
				if (Edge.incorrect(e, f)) return false;
			}
			nedges.add(e);
    	}
    	
		return true;
    
    }
    
    public static boolean islocalvalid(LinkedList<Edge> edges, Node u) {   	//Similar to the previous method 
    				//But used in local search heuristic so we need to check outgoing edges as well
    	LinkedList<Edge> nedges = new LinkedList<Edge>();
    	for (Node v : u.predecessors) {
			Edge e = new Edge(v, u);
			for (Edge f:nedges) {
				if (Edge.incorrect(e, f)) return false;
			}
			for (Edge f:edges) {
				if (Edge.incorrect(e, f)) return false;
			}
			nedges.add(e);
    	}
    	for (Node w : u.successors) {
			Edge e = new Edge(u, w);
			for (Edge f:nedges) {
				if (Edge.incorrect(e, f)) return false;
			}
			for (Edge f:edges) {
				if (Edge.incorrect(e, f)) return false;
			}
			nedges.add(e);
    	}
		return true;
    }
    
    public static List<Integer> shuffle(int w) {  //returns a random permutation of [0, w]
    	List<Integer> solution = new ArrayList<>();
    	for (int i = 0; i <= w; i++) {
    	    solution.add(i);
    	}
    	Collections.shuffle(solution);
    	return solution;
    }

    /**
     * Compute a valid initial layout, satisfying the prescribed requirements according to the problem definition <br>
     * 
     * Remark: the vertex coordinates are stored in the class 'Node' (Point_2 'p' attribute)
     */
    public void computeValidInitialLayout() {
    	int[][] grid = new int[width+1][height+1]; //to mark occupied spots
    	Topological_Sort topo = new Topological_Sort(g);
    	LinkedList<Node> nodes = topo.sort(); //topological sorting of the nodes
    	LinkedList<Edge> edges = new LinkedList<Edge>(); //where we store the processed edges to check for validity efficiently
    	int h = 8; // this is the vertical step, it controls the size of the graph
    	for (Node u:nodes) {
    		u.p.y=0;
    	} 
    	List<Integer> shuffle;
    	for (Node u:nodes) {
    		shuffle = shuffle(width);
    		u.p.x=shuffle.get(0);
    		if (!u.predecessors.isEmpty()) {
    		for (Node v:u.predecessors) {  //setting y coordinate higher than all predecessors
    			if (v.p.y>u.p.y) u.p.y=v.p.y;
    		}
    		u.p.y=u.p.y+h;
    		}
    		int j=1;
    		int y_0=u.p.y;
			while (!isvalid(edges,u) || grid[u.p.x][u.p.y]==1) { //computing a valid spot
				if (j<width) {
					u.p.x=shuffle.get(j);
					j++;	
				}
				else {
					j=0;
					if (u.p.y<height) u.p.y++;
					else {
						u.p.y=y_0;
						shuffle=shuffle(width);
					}
				}
			}
			for (Node v: u.predecessors) { //these are the edges created by placing the new node, now we update the edges set
				Edge e = new Edge(v,u);
				edges.add(e);
			}
			grid[u.p.x][u.p.y]=1; //mark the spot
    	}
    }

    /**
     * Improve the current layout by performing a local search heuristic: nodes are moved
     * to a new location in order to reduce the number of crossings. The layout must remain valid at the end.
     */
	public void localSearchHeuristic() {
		int[][] grid = new int[width + 1][height + 1];
		LinkedList<Edge> edges = new LinkedList<Edge>();
		for (Node u : g.vertices) { 
			grid[u.p.x][u.p.y] = 1;
			for (Node v : u.successors) {
				Edge e = new Edge(u, v);
				edges.add(e);  //we store all edges in a list because we want to iterate through them later
			}
		}
		PriorityQueue<Couple> Q = new PriorityQueue<Couple>(); //Priority queue where we store edges by decreasing number of crossings
		for (Edge e1 : edges) {
			int c = 0;
			for (Edge e2 : edges) {
				if (e2 != e1 && Edge.cross(e1, e2))
					c++;
			}
			Q.add(new Couple(e1, -c));
		}
		List<Integer> shuffle;
		int i = 0;
		while (i < 1000) { //number of iterations of the heuristic
			shuffle = shuffle(width);
			Edge e = Q.poll().e;
			while (!edges.contains(e)) //there might be some outdated edges from the previous iterations that we didn't remove from the queue
				e = Q.poll().e;
			Node v = e.end;
			int j = 0;              
			grid[e.end.p.x][e.end.p.y] = 0;// we will move the node u, so we remove the edges linked to it from the lists in order to update them later
			for (Node u : v.predecessors) {
				e = new Edge(u, v);
				edges.remove(e);
			}
			for (Node w : v.successors) {	
				edges.remove(new Edge(v, w));	
			}
			v.p.x = shuffle.get(0);
			int p = 0;
			while (!islocalvalid(edges, v) || grid[v.p.x][v.p.y] == 1) { //we look for a valid x coordinate different than the initial one, otherwise we leave it as it is
				if (j < width) {
					j++;
					v.p.x = shuffle.get(j);
				} else {
					j = 0;
					v.p.x = shuffle.get(0);
					p = 1;
				}
			}
			grid[v.p.x][v.p.y] = 1;
			for (Node u : v.predecessors) { //updating the priority queue with the newly updated edges
				int c = 0;
				for (Edge e2 : edges) {            
					if (e2 != e && Edge.cross(e, e2))
						c++;
				}
				edges.add(new Edge(u, v));
				if (p == 0)          //p=0 means we couldnt move the node, so we dont put its edges back in the queue to avoid an infinite loop
					Q.add(new Couple(new Edge(u, v), -c));

			}

			for (Node w : v.successors) { //same but for outgoing edges
				int c = 0;
				for (Edge e2 : edges) {
					if (e2 != e && Edge.cross(e, e2))
						c++;
				}
				edges.add(new Edge(v, w));
				if (p == 0)
					Q.add(new Couple(new Edge(v, w), -c));
			}
			i++;
		}

	}

    /**
     * Improve the current layout by performing a local search heuristic: nodes are moved
     * to a new location in order to reduce the number of crossings. The layout must remain valid at the end.
     */
	public void forceDirectedHeuristic() {
    	double step=0.1;	
    	LinkedList<Edge> edges = new LinkedList<Edge>();
    	for (Node u:g.vertices) {       //storing edges and initializing the float coordinates that we will be using
    		u.coords= new Paire(u.p.x,u.p.y);
    		for (Node v:u.successors) {
    			edges.add(new Edge(u,v));
    		}
    	}
    	int t=0;
    	while(t<1000) {  
    		t++;
    		for(Node v:g.vertices) {
    			v.force = new Paire(0,0);
    			for(Node u:v.successors) {            // attractive force with successors
    				GridVector_2 first=new GridVector_2(v.p.x,v.p.y);
    				GridVector_2 second=new GridVector_2(u.p.x,u.p.y);
    				double distance=new GridPoint_2(v.p.x,v.p.y).distanceFrom(new GridPoint_2(u.p.x,u.p.y));
    				v.force.x=v.force.x-second.difference(first).x*distance;
    				v.force.y=v.force.y-second.difference(first).y*distance;
    			}
    			for(Node u:v.predecessors) {  //attractive force with predecessors
    				GridVector_2 first=new GridVector_2(v.p.x,v.p.y);
    				GridVector_2 second=new GridVector_2(u.p.x,u.p.y);
    				double distance=new GridPoint_2(v.p.x,v.p.y).distanceFrom(new GridPoint_2(u.p.x,u.p.y));
    				v.force.x=v.force.x-second.difference(first).x*distance;
    				v.force.y=v.force.y-second.difference(first).y*distance;
    			}
    			for(Node w:g.vertices) {  // repulsive force with other nodes
    				if(w!=v) {
    					GridVector_2 first=new GridVector_2(v.p.x,v.p.y);
        				GridVector_2 second=new GridVector_2(w.p.x,w.p.y);
        				double distance=new GridPoint_2(v.p.x,v.p.y).distanceFrom(new GridPoint_2(w.p.x,w.p.y));
        				v.force.x=v.force.x+first.difference(second).x*Math.pow(distance, -2);
        				v.force.y=v.force.y+first.difference(second).y*Math.pow(distance, -2);
    				}		
    			}
    			
    		}
    		for (Edge e1:edges) {  //repulsive force between neighbor edges
    			for(Edge e2:edges) {
    				if(e1!=e2&&(e1.start==e2.start||e1.end==e2.start||e1.start==e2.end||e1.end==e2.end)) {
    					Paire bary1 = new Paire((e1.start.coords.x+e1.end.coords.x)/2,(e1.start.coords.y+e1.end.coords.y)/2);
    					Paire bary2 = new Paire((e2.start.coords.x+e2.end.coords.x)/2,(e2.start.coords.y+e2.end.coords.y)/2);
    					double distance = (float)Math.sqrt((bary1.x-bary2.x)*(bary1.x-bary2.x)+(bary1.y-bary2.y)*(bary1.y-bary2.y));
    					e1.start.force.x-=(float)(bary1.x-bary2.x)*Math.pow(distance, -2)*20;
    					e1.start.force.y-=(float)(bary1.y-bary2.y)*Math.pow(distance, -2)*20;
    					e1.end.force.x-=(float)(bary1.x-bary2.x)*Math.pow(distance, -2)*20;
    					e1.end.force.y-=(float)(bary1.y-bary2.y)*Math.pow(distance, -2)*20;
    				}
    			}
    		}
    		for (Node v:g.vertices) {  //updating coordinates with forces
    			int p = 0;
    			double fx = step*v.force.x*(Math.pow(v.force.x*v.force.x+v.force.y*v.force.y,-0.5));
    			double fy = step*v.force.y*(Math.pow(v.force.x*v.force.x+v.force.y*v.force.y,-0.5));
    			if (fy>0)
    			for (Node w:v.successors) {
    				if (w.coords.y-v.coords.y<=1) p=1;
    			}
    			if (fy<0)
    			for (Node w:v.predecessors) {
    				if (v.coords.y-w.coords.y<=1) p=1;
    			}
    			if (p!=1) {
    			v.coords.x=v.coords.x+fx;
    			v.coords.y=v.coords.y+fy;
    			}
    		}
    		step=step*0.95;	//updating step
    	}
    	for(Node u:g.vertices) {     //converting to integer coordinates
    		if(u.coords.x-(int)u.coords.x<0.5) {
    			u.p.x=(int)u.coords.x;
    		}
    		else {
    			u.p.x=(int)u.coords.x+1;
    		}
    		if(u.coords.y-(int)u.coords.y<0.5) {
    			u.p.y=(int)u.coords.y;
    		}
    		else {
    			u.p.y=(int)u.coords.y+1;
    		}	
    	}
    }
    
    

    /**
     * Main function that computes a valid upward drawing that minimizes edge crossings. <br>
     * 
     * You are free to use and combine any algorithm/heuristic to obtain a good upward drawing.
     */
    public void computeUpwardDrawing() {
    	System.out.print("Compute a valid drawing with few crossings: ");
    	System.out.println("Computing valid initial layout :");
		long startTime=System.nanoTime(), endTime;
		computeValidInitialLayout(); // first phase: compute a valid drawing (if necessary)
		endTime=System.nanoTime();
        double duration=(double)(endTime-startTime)/1000000000.;
    	System.out.println("Initial layout computed in "+duration+" seconds");
    	boolean isValid=checkValidity();// check whether the result is a valid drawing
    	String s;
		if (isValid==true) s = "valid";
		else s = "invalid";
		System.out.println("Initial graph is "+s);
		int crossings=computeCrossings(); // count the number of crossings
		System.out.println("Initial number of crossings : "+crossings);
		System.out.println("Launching local search heuristic :");
		startTime=System.nanoTime();
		localSearchHeuristic();
		endTime=System.nanoTime();
		duration=(double)(endTime-startTime)/1000000000.;
    	System.out.println("Local search time : "+duration+" seconds");
    	isValid=checkValidity();// check whether the result is a valid drawing
    	if (isValid==true) s = "valid";
		else s = "invalid";
		System.out.println("Graph after local search is : "+s);
		crossings=computeCrossings(); // count the number of crossings
		System.out.println("Crossings after local search :"+computeCrossings());
//		System.out.println("Now launching force directed heuristics :");
//		startTime=System.nanoTime();
//		forceDirectedHeuristic();
//		endTime=System.nanoTime();
//		duration=(double)(endTime-startTime)/1000000000.;
//    	System.out.println("Force directed heuristic time "+duration+" seconds");
//		isValid=checkValidity();// check whether the result is a valid drawing
//    	if (isValid==true) s = "valid";
//		else s = "invalid";
//		System.out.println("Graph after force directed heuristics is : "+s);
//		crossings=computeCrossings(); // count the number of crossings
//		System.out.println("Final number of crossings : "+crossings);
    }

    /**
     * Check whether the current graph is provided with an embedding <br>
     * -) if all nodes are set to (0, 0): the graph has no embedding by definition <br>
     * -) otherwise, the graph has an embedding
     */
    public static boolean hasInitialLayout(DirectedGraph graph) {
    	for(Node v: graph.vertices) {
    		if(v.getPoint().getX()!=0 || v.getPoint().getY()!=0)
    			return true;
    	}
    	return false;
    }

}
