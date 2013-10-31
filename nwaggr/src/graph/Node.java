package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import java.util.Iterator;

public class Node extends PathElement {
	int id;
	int type;
	HashMap<Integer, Edge> edges = new HashMap<Integer, Edge>();
	
	HashMap<Edge, ArrayList<Flow>> flowtable = new HashMap<Edge, ArrayList<Flow>>(); 
	
	HashMap<Flow, ArrayList<PathElement>> flowpaths = new HashMap<Flow, ArrayList<PathElement>>();
	
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
		if(!flowtable.containsKey(flow.edgeIn)){
			flowtable.put(flow.edgeIn, new ArrayList<Flow>());
		}
		flowtable.get(flow.edgeIn).add(flow);
		return;
	}
	
	//An external node has only one edge!
	public void createFlowGraph(){
		//System.out.println("Node: "+ this);
		//System.out.println("Edges: "+edges);
		if(this.type!=Configuration.EXTERNALNODE) {
			System.out.println("INFO: Not creating flow graph for internal node");
			return;
		}
		if(this.edges.size()!=1){
			System.out.println("ERROR: The external node " + id + " do not have exactly one edge");
			return;
		}
		Flow completeflow = Flow.getCompleteFlow();
		ArrayList<PathElement> path = new ArrayList<PathElement>();
		path.add(this);		
		Edge edgeOut = this.edges.values().iterator().next();		
		if(edgeOut!=null){
			System.out.println(edgeOut);
		}
		path.add(edgeOut);
		createFlowGraph(edgeOut.node1, edgeOut, completeflow, path);
	}
	
	public void createFlowGraph(Node node, Edge edgeIn, Flow flow, ArrayList<PathElement> path){
		System.out.println("CreateGraphFlow: " +node + " " + edgeIn);
		if(node.type==Configuration.EXTERNALNODE){
			path.add(node);
			flowpaths.put(flow, path);
			path.remove(path.size()-1);
			return;
		}
		ArrayList<Flow> flows = node.flowtable.get(edgeIn);
		Collections.sort(flows);
		path.add(node);
		for(int i=flows.size()-1;i>=0;i--){
			//System.out.println("");
			Flow overlap = flow.overlap(flows.get(i));
			if(overlap.empty==1) continue;
			Edge edgeOut = flows.get(i).edgeOut;
			path.add(edgeOut);
			createFlowGraph(edgeOut.getOtherNode(node), edgeOut, overlap, path);
			flow.subtract(overlap);
			path.remove(path.size()-1);
			//if(flow.empty==1) break;
		}
		if(flow.empty!=1){
			flowpaths.put(flow, path);
		}
		path.remove(path.size()-1);
		return;
	}

	public void printFlowGraph() {
		System.out.println("Flow paths for Node " + id);
		Iterator<Flow> it = flowpaths.keySet().iterator();
		while(it.hasNext()){
			Flow flow = it.next();
			ArrayList<PathElement> path = flowpaths.get(flow);
			System.out.println(flow + ": "+path);
		}
		System.out.println();
	}
	
	public String toString(){
		return "node-"+id;
	}
	
}
