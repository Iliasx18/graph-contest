package jdg.graph;

public class GridSegment_2{
  public GridPoint_2 p, q;

  public GridSegment_2() {}
  
  public GridSegment_2(GridPoint_2 p, GridPoint_2 q) { this.p=p; this.q=q; }

  public GridPoint_2 source() {return this.p; }
  public GridPoint_2 target() {return this.q; }
  public GridPoint_2 vertex(int i) {
  	if(i==0)return this.p; 
  	else return this.q;	
  }
  
/**
 * returns the vector s.target() - s.source()
 */
  public GridVector_2 toVector() {
  	return new GridVector_2 (this.p,this.q);
  }

/**
 * returns a segment with source and target interchanged
 */
  public GridVector_2 opposite() {
  	return new GridVector_2 (this.q,this.p);
  }

/**
 * A point is on s, 
 * iff it is equal to the source or target of s, 
 * or if it is in the interior of s
 */  
  public boolean hasOn(GridPoint_2 p) {
  	return (p.x - this.p.x) * (this.q.y - this.p.y) == (this.q.x - this.p.x) * (p.y - this.p.y) && p.y>this.p.y && p.y < this.q.y;
  }

  public String toString() {return "["+p+","+q+"]"; }
  public int dimension() { return 2;}
  
}




