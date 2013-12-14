package swiconsim.link;

public class Link {
	public long port1;
	public long port2;
	public int capacity;
	public double utilization;
	
	public Link(long port1, long port2, int capacity, int utilization){
		this.port1 = port1;
		this.port2 = port2;
		this.capacity = capacity;
		this.utilization = utilization;
	}
	
	public Link(Link other){
		this.port1 = other.port1;
		this.port2 = other.port2;
		this.capacity = other.capacity;
		this.utilization = other.utilization;
	}
	
	public String toString(){
		return ""+port2;
	}
}
