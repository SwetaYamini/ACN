package graph;

public class Edge extends PathElement{
	int id;
	Node node1; 
	Node node2;
	int type;
	
	
	public Edge(int id){
		this.id=id;
		this.type=Configuration.INTERNALLINK;
	}
	
	public Node getOtherNode(Node node){
		if(node.id==node1.id) return node2;
		return node1;
	}
	
	public String toString(){
		return "edge-"+id;
	}
}
