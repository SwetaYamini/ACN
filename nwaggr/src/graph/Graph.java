package graph;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Iterator;


public class Graph {

	HashMap<Integer, Node> nodes;
	HashMap<Integer, Edge> edges;

	public Graph(){
		nodes = new HashMap<Integer, Node>();
		edges = new HashMap<Integer, Edge>();
	}

	public void addNode(int id){
		if(nodes.containsKey(id)) return;
		Node node = new Node(id);
		nodes.put(id, node);
		Configuration.NODEID = id+1;
	}

	public boolean addEdge(int nodeId, int id){
		if(!nodes.containsKey(nodeId)) return false;
		Node node = nodes.get(nodeId);
		Edge edge;
		if(!edges.containsKey(id)){
			edge = new Edge(id);
			edge.node1 = node;
			edges.put(id, edge);
		}else{
			edge = edges.get(id);
			if(edge.node2!=null) return false;
			edge.node2 = node;
		}
		node.addEdge(edge);
		return true;
	}

	public void addExternalNodes(){
		Iterator<Edge> it = edges.values().iterator();
		while(it.hasNext()){
			Edge edge = it.next();
			if(edge.node2==null){
				edge.type = Configuration.EXTERNALLINK;
				Node node = new Node();
				edge.node2 = node;
				node.addEdge(edge);
				nodes.put(node.id, node);
			}
		}
	}
	
	public void createFlowGraphs(){
		Iterator<Node> it = nodes.values().iterator();
		while(it.hasNext()){
			Node node = it.next();
			if(node.type==Configuration.EXTERNALNODE){
				node.createFlowGraph();
				node.printFlowGraph();
			}
		}
	}
	
	public void Initialize(){
		addExternalNodes();
		printGraph();
		createFlowGraphs();
	}
	
	public void printGraph(){
		System.out.println("Graph");
		Iterator<Node> it = nodes.values().iterator();
		while(it.hasNext()){
			Node node = it.next();
			System.out.print("Node "+node.id+": ");
			Iterator<Edge> it2 = node.edges.values().iterator();
			while(it2.hasNext()){
				Edge edge = it2.next();
				System.out.print("("+edge.id+","+edge.getOtherNode(node)+")");
			}
			System.out.println();
		}
		System.out.println();
	}
	

	public boolean validate(){
		return true;
	}
	
	
}
