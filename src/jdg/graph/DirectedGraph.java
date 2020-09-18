package jdg.graph;

import java.util.*;

/**
 * Pointer based implementation of an Adjacency List Representation of a directed graph
 * 
 * @author Luca Castelli Aleardi (2019)
 *
 */
public class DirectedGraph {
	
	public ArrayList<Node> vertices; // list of vertices
	public HashMap<String,Node> labelMap; // map between vertices and their labels
	
	public DirectedGraph() {
		this.vertices=new ArrayList<Node>();
		this.labelMap=null; // no labels defined
	}

	public DirectedGraph(int mapCapacity) {
		this.vertices=new ArrayList<Node>();
		this.labelMap=new HashMap<String,Node>(mapCapacity); // labels are defined
	}
	
	public void addNode(Node v) {
		String label=v.label;
		if(label==null) {
			this.vertices.add(v);
			return;
		}
		
		if(this.labelMap.containsKey(label)==false) {
			this.labelMap.put(label, v);
			this.vertices.add(v);
		}
	}
	
	public Node getNode(String label) {
		if(this.labelMap!=null && this.labelMap.containsKey(label)==true) {
			return this.labelMap.get(label);
		}
		return null;
	}
	
	public Node getNode(int index) {
		if(index>=0 && index<this.vertices.size()) {
			return this.vertices.get(index);
		}
		return null;
	}
	
	public void removeNode(Node v) {
		throw new Error("To be updated/implemented");
/*		if(this.vertices.contains(v)==false)
			return;
		for(Node u: this.getNeighbors(v)) { // remove all edges between v and its neighbors
			u.removeNeighbor(v);
		}
		this.vertices.remove(v); // remove the vertex from the graph*/
	}

	/** Add the (a, b) directed from 'a' to 'b' */
    public void addDirectedEdge(Node a, Node b) {
    	if(a==null || b==null)
    		return;
    	a.addSuccessor(b); // 'b' is a successor of 'b'
    	b.addPredecessor(a);
    }

    /** Remove both edges edges (a, b) and (b, a)*/
    /*public void removeEdge(Node a, Node b){
    	if(a==null || b==null)
    		return;
    	a.removeNeighbor(b);
    	b.removeNeighbor(a);
    }*/
    
    /** Check whether one of the two edges (a, b) and (b, a) does exist */
    public boolean adjacent(Node a, Node b) {
    	if(a==null || b==null)
    		throw new Error("Graph error: vertices not defined");
    	if(a.isPredecessorsOf(b)==true)
    		return true;
    	if(a.isSuccessorOf(b)==true)
    		return true;
    	return false;
    }
    
    public int degree(Node v) {
    	return v.degree();
    }
    
    public Collection<Node> getSuccessors(Node v) {
    	return v.successorsList();
    }
    
    public Collection<Node> getPredecessors(Node v) {
    	return v.predecessorsList();
    }
        
	/**
     * Return the number of nodes (it includes isolated nodes)
     */		
    public int sizeVertices() {
    	return this.vertices.size();
    }
    
	/**
     * Return the number of directed arcs
     * 
     * Remark: arcs are not counted twice
     */		
    public int sizeEdges() {
    	int result1=0, result2=0;
    	for(Node v: this.vertices) {
    		result1=result1+getSuccessors(v).size();
    		result2=result2+getPredecessors(v).size();
    	}
    	if(result1!=result2)
    		throw new Error("Error: wrong number of edges");
    	
    	return result1;
    }

    /**
     * Return the number of non isolated nodes
     * 
     * @return  the number of non isolated nodes
     */		
    public int countNonIsolatedVertices() {
		int N=0; // count non isolated nodes
		for(Node u: this.vertices) {
			if(u.degree()>=0)
				N++;
		}
		return N;
    }
    
    /**
     * Return an array storing all vertex indices, according to the order of vertices
     */		   
    public int[] getIndices() {
    	int[] result=new int[this.vertices.size()];
    	
    	int count=0;
    	for(Node u: this.vertices) {
    		if(u!=null) {
    			result[count]=u.index;
    			count++;
    		}
    	}
    	return result;
    }
    
    /**
     * Return an array storing all vertex locations, according to the order of vertices
     */		   
    public GridPoint_2[] getPositions() {
    	GridPoint_2[] result=new GridPoint_2[this.vertices.size()];
    	
    	int count=0;
    	for(Node u: this.vertices) {
    		if(u!=null && u.getPoint()!=null) {
    			result[count]=u.getPoint();
    			count++;
    		}
    	}
    	return result;
    }
        
    /**
     * Compute the minimum vertex index of the graph (a non negative number)
     * 
     * Remark: vertices are allowed to have indices between 0..infty
     * This is required when graphs are dynamic: vertices can be removed
     */		   
    public int minVertexIndex() {
    	int result=Integer.MAX_VALUE;
    	for(Node v: this.vertices) {
    		if(v!=null)
    			result=Math.min(result, v.index); // compute max degree
    	}
    	return result;
    }

    /**
     * Compute the maximum vertex index of the graph (a non negative number)
     * 
     * Remark: vertices are allowed to have indices between 0..infty
     * This is required when graphs are dynamic: vertices can be removed
     */		   
    public int maxVertexIndex() {
    	int result=0;
    	for(Node v: this.vertices) {
    		if(v!=null)
    			result=Math.max(result, v.index); // compute max degree
    	}
    	return result;
    }
    
    /**
     * Return a string containing informations and parameters of the graph
     */		   
    public String info() {
    	String result=sizeVertices()+" vertices, "+sizeEdges()+" edges\n";
    	
    	int isolatedVertices=0;
    	int maxDegree=0;
    	for(Node v: this.vertices) {
    		if(v==null || v.degree()==0)
    			isolatedVertices++; // count isolated vertices
    		//if(v!=null && v.p!=null && v.p.distanceFrom(new Point_3()).doubleValue()>0.) // check geometric coordinates
    		//	geoCoordinates=true;
    		if(v!=null)
    			maxDegree=Math.max(maxDegree, v.degree()); // compute max degree
    	}
    	result=result+"isolated vertices: "+isolatedVertices+"\n";
    	result=result+"max vertex degree: "+maxDegree+"\n";
    	
    	result=result+"min and max vertex index: "+minVertexIndex();
    	result=result+"..."+maxVertexIndex()+"\n";
    	
    	return result;
    }
    
    public void printCoordinates() {
    	for(Node v: this.vertices) {
    		if(v!=null)
    			System.out.println(v.index+" "+v.p);
    	}
    }

}
