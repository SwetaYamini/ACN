package graph;

import java.util.HashMap;

public class FlowGraph {
	Node root;
	HashMap<Flow, FlowPath> flowpaths = new HashMap<Flow, FlowPath>();
}
