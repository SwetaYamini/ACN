package timestamp;

public class Port extends Element {
	int id;
	int local_id;
	int parent;
	int link;
	
	public Port(int id, int local_id, int parent){
		this.parent = parent;
		this.id = id;
		this.local_id = local_id;
		link=-1;
	}
}
