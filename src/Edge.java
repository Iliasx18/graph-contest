import jdg.graph.Node;
import jdg.graph.GridSegment_2;

public class Edge  {         
	public Node start;
	public Node end;
	public double m;  //slope
	public double p;  //ordinate at origin
	public int index;  
	public int lo;  //y_0
	public int hi;   // y_1  
	public int tag = 0;
	
	public Edge(Node start, Node end) {
		this.start = start;
		this.end = end;
		if (this.start.p.x != this.end.p.x) {
			this.m = (start.p.y - end.p.y) / (double) (start.p.x - end.p.x);
			this.p = (double) this.start.p.y - this.start.p.x * this.m;
		} else {
			this.m = Float.POSITIVE_INFINITY;  //a vertical edge has an infinite slope, we agree that p is the x coordinate in this case
			this.p = (double) this.start.p.x;
		}
		this.lo = this.start.p.y;
		this.hi = this.end.p.y;
	}

	@Override
	public boolean equals(Object o) {    //defining an order on the set of edges, two edges are equal if they overlap
		Edge other = (Edge)o;
		return (this.start==other.start&&this.end==other.end);

	}

	public static boolean ccw(Node A, Node B, Node C) {
		return (C.p.y - A.p.y) * (B.p.x - A.p.x) > (B.p.y - A.p.y) * (C.p.x - A.p.x);
	}
	public static boolean colinear(Node A, Node B, Node C) {
		return (B.p.x-A.p.x)*(C.p.y-B.p.y)-(B.p.y-A.p.y)*(C.p.x-B.p.x)==0;
	}
	public static boolean parallel(Edge e1, Edge e2) {
		return ((e1.start.p.x-e1.end.p.x)*(e2.start.p.y-e2.end.p.y)-
				(e1.start.p.y-e1.end.p.y)*(e2.start.p.x-e2.end.p.x)==0);
	}

	public static boolean cross(Edge e1, Edge e2) {    //an efficient way to test for crossings
		if(parallel(e1,e2)) return false;
		if (e1.hi==e2.lo||e2.hi==e1.lo) return false;
		return (ccw(e1.start,e2.start,e2.end) != ccw(e1.end,e2.start,e2.end)) &&
				(ccw(e1.start,e1.end,e2.start) != ccw(e1.start,e1.end,e2.end));
	}
	public String toString() {
		return "("+this.start.index+","+this.end.index+")";
	}
	public static boolean incorrect(Edge e1, Edge e2) { //testing if two edges form an invalid intersection
		GridSegment_2 s1 = new GridSegment_2(e1.start.p,e1.end.p);
		GridSegment_2 s2 = new GridSegment_2(e2.start.p,e2.end.p);
		if (s1.hasOn(e2.start.p)||s1.hasOn(e2.end.p)||s2.hasOn(e1.start.p)||s2.hasOn(e1.end.p)) return true;
		if (e1.m==e2.m && e1.p==e2.p) {
			if (e1.hi>e2.lo&&e1.lo<e2.lo || e2.hi>e1.lo&&e2.lo<e1.hi) return true;
		}
		return false;
	}
}
