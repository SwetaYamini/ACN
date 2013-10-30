package graph;

public class Edge {
	int id;
	Node node1; 
	Node node2;
	int type;
	
	public Edge( Node node1, Node node2){
		id = Configuration.LINKID++;
		if(node1.id<=node2.id){
			this.node1 = node1;
			this.node2 = node2;
		}else{
			this.node1 = node2;
			this.node2 = node1;
		}
		//if(node1.type==)
	}
}
