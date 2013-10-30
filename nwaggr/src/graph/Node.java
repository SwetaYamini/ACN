package graph;

public class Node {
	int id;
	int type;
	
	public Node(int type){
		this.id=Configuration.NODEID++;
		this.type=type;
	}
}
