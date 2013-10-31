package graph;

public class Main {
	public static void main(String[] args){
		Graph graph = new Graph();
		Node node1 = graph.addNode(1);
		Node node2 = graph.addNode(2);
		Edge edge1 = graph.addEdge(1, 1);
		Edge edge2 =graph.addEdge(2, 2);
		Edge edge3 =graph.addEdge(1, 3);
		graph.addEdge(2, 3);
		
		Flow flow = new Flow();
		flow.empty=0;
		flow.edgeIn=edge1;
		flow.edgeOut = edge3;
		flow.priority=1;
		node1.addFlow(flow);
		
		flow = new Flow();
		flow.empty=0;
		flow.edgeIn=edge3;
		flow.edgeOut = edge2;
		flow.priority=1;
		node2.addFlow(flow);
		
		graph.Initialize();

	}
	
	
}
