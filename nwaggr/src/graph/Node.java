package graph;

import java.util.ArrayList;
import java.util.HashMap;

public class Node {
	int id;
	int type;
	HashMap<Integer, Edge> edges;
	
	HashMap<Edge, Flow> flowtable = new HashMap<Edge, Flow>(); 
	
	public Node(int id){
		this.id=id;
		this.type=Configuration.INTERNALNODE;
	}
	
	public Node(){
		this.id = Configuration.NODEID++;
		this.type=Configuration.EXTERNALNODE;
	}
	
	public void addEdge(Edge edge){
		edges.put(edge.id, edge);
	}
	
	public void addFlow(Flow flow){
		flowtable.put(flow.edgeIn, flow);
	}
	
	public void createFlowGraph(Node node, Flow flow){
		
	}
	
	
}
