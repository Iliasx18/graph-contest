
public class Couple implements Comparable<Couple> {  //This class is used in the priority queue to sort by crossings
	Edge e;
	int count;
	
	public Couple(Edge e, int count) {
		this.e=e;
		this.count = count;
	}

	@Override
	public int compareTo(Couple o) {
		if (this.count<o.count) return -1;
		if (this.count>o.count) return 1;
		return 0;
	}

	
	
	
	
}
