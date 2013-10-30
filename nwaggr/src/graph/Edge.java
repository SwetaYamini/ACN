package graph;

public class Edge {
	int id;
	Node node1; 
	Node node2;
	int type;
	
	
	public Edge(int id){
		this.id=id;
		this.type=Configuration.INTERNALLINK;
	}
}
