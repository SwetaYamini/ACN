package timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

import swiconsim.network.DataNetwork;
import swiconsim.network.ManagementNetwork;

public class Controller extends Thread {
	int id;
	int[] timestamp;
	HashMap<Integer, Utilization> utilizations;
	 
	
	public Controller(int id, int domainsize){
		this.id = id;
		timestamp = new int[domainsize];
		for(int i=0;i<domainsize;i++){
			timestamp[i]=0;
		}
	}
	
	public void initialize(){
		utilizations = new HashMap<Integer, Utilization>();
		Iterator<Integer> it = Network.links.keySet().iterator();
		//System.out.println("Initializing utilizations");
		while(it.hasNext()){
			int linkid = it.next();
			//System.out.println("putting " + linkid);
			utilizations.put(linkid, new Utilization(linkid));
		}
	}

	public boolean addFlow(Flow flow){
		flow.path = BFS(flow.srcPort, flow.dstPort, 0);
		if(flow.path==null){
			Simulator.badFlows++;
			System.out.println("WARN: Flow " + flow + " is not possible");
			return false;
		}
		flow.updateTimestamp(timestamp);
		//if(flow.id==125) System.out.println("125: " + flow.path);
		//System.out.println("Controller " + id + " added flow " + flow);
		return true;
	}
	
	public void startFlow(Flow flow, int start){
		
		int end = getMyPart(flow.path, start);
		int optimization=0;
		//Algorithm
		//DO nothing for now.
		//System.out.println("Controller "+id+" adding flow");
		if(flow.timestamp[id]<timestamp[id]  && optimization==1){
			//System.out.println("Controller "+id+" trying to change flow " + flow + " from " + start + " " + end);
			int port1 = flow.path.get(start-1).dstPort;
			int port2;
			if(end < flow.path.size()-1){
				port2 = flow.path.get(end+1).srcPort;
			}else{
				port2 = flow.dstPort;
			}
			ArrayList<FlowLink> alternatePath = BFS(port1,port2,1);
			if(alternatePath != null){
				ArrayList<FlowLink> newPath = new ArrayList<FlowLink>();
				for(int i=0;i<=start-1;i++){
					newPath.add(flow.path.get(i));
				}
				for(int i=0;i<alternatePath.size();i++){
					newPath.add(alternatePath.get(i));
				}
				int new_end=newPath.size()-1;
				for(int i=end+1;i<flow.path.size();i++){
					newPath.add(flow.path.get(i));
				}
				System.out.println("Controller "+id+" is changing path from : "+ flow.path +" to "+ newPath);
				flow.path = new ArrayList<FlowLink>(newPath);
				end = new_end;					
			}
		}
		
		updateUtilizations(flow, flow.path, start, end);
		flow.updateTimestamp(timestamp);
		if(end<flow.path.size()-1){
			int nextLink = flow.path.get(end+1).link;
			int nextController = Network.links.get(nextLink).controller;
			Network.controllers.get(nextController).startFlow(flow, end+1);
		}
	}
	
	
	ArrayList<FlowLink> BFS(int in_port, int out_port, int intra){
		int start = Network.ports.get(in_port).parent;
		int end = Network.ports.get(out_port).parent;
		//System.out.println("BFS on " + start + "->" + end);
		//if(start==0 || end==0) return new ArrayList<FlowLink>();
		if(start==end) return null;
		if(intra==1 && Network.switches.get(start).controller!=id ){
			System.out.println("WARNING: Intra BFS call for alien switches");
			return null;
		}
		
		//System.out.println("BFS on " + in_port + "->" + out_port);
		ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(Network.switches.size());
		ArrayList<Integer> closed = new ArrayList<Integer>(); 
		HashMap<Integer, Integer> open = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> parent = new HashMap<Integer, Integer>();
		ArrayList<FlowLink> path = new ArrayList<FlowLink>();
		queue.add(in_port);
		double globalBest=100000;
		boolean found=false;
		
		while(!queue.isEmpty()){
			int port = queue.peek();
			queue.poll();
			int sw = Network.ports.get(port).parent;
			closed.add(sw);
			open.remove(sw);
			//System.out.println("BFS: port " + port);
			//path.add(sw);
			
			
			Iterator<Port> it = Network.switches.get(sw).ports.values().iterator();
		
			while(it.hasNext()){
				Port candidate = it.next();
				//System.out.println("Analyzing port " + port2);
				if(candidate.id==port) continue;
				if(candidate.link==-1) continue;
				
				Link link = Network.links.get(candidate.link);
				int next = link.getOtherPort(candidate.id);
				int nextSw = Network.ports.get(next).parent;
				
				if(end==nextSw){
				//	System.out.println("Found port " + next);
					found=true;
					parent.put(candidate.id, port);
					parent.put(next, candidate.id);
					parent.put(out_port, next);
					int curr = out_port;
					ArrayList<FlowLink> currPath = new ArrayList<FlowLink>();
					int max_util=0;
					while(Network.ports.get(curr).parent != Network.ports.get(in_port).parent){
						int port2 = parent.get(curr);
						int port1 = parent.get(port2);
						//System.out.println(curr + ": " + port2 + ": " + port1);
						if(Network.ports.get(port1).link != Network.ports.get(port2).link){
							System.out.println("BFS Error: 1");
						}
						int link12 = Network.ports.get(port1).link;
						if(utilizations.get(link12).utilization > max_util){
							max_util = utilizations.get(link12).utilization;
						}
						FlowLink flowlink = new FlowLink(link12, port1, port2);
						currPath.add(0,flowlink);
						curr = port1;
					}
					
					//System.out.println(max_util +" and " + currPath.size());
					if(globalBest > max_util){ // || (globalBest==max_util && currPath.size() < path.size())){
						path.clear();
						path.addAll(currPath);
						globalBest = max_util;
					}
					parent.remove(candidate.id);
					parent.remove(next);
					parent.remove(out_port);
			
				}else{

					//TODO: add condition for intraBFS
					if(intra==1 && Network.switches.get(nextSw).controller!=id){
					//	System.out.println("Not putting controller " +Network.switches.get(nextSw).controller);
						continue;
					}

					if(closed.contains(nextSw) || open.containsKey(nextSw)) continue;
					parent.put(candidate.id, port);
					parent.put(next, candidate.id);
					try {
					//	System.out.println("Adding port " + next);
						queue.put(next);
						open.put(nextSw, nextSw);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			//System.out.println("State: "+port+" "+queue+" "+closed);
			
		}
		
		return path;
		
	}	
	
	int updateUtilizations(Flow flow, ArrayList<FlowLink> path, int start, int end){
		for(int i=start; i<=end; i++){
			utilizations.get(path.get(i).link).update(flow.rate);
			Replicator.utilizations.get(path.get(i).link).update(flow.rate);
		}
		//System.out.println("id: " + id + " size: " + timestamp.length);
		timestamp[id]++;
		Replicator.timestamp[id]=timestamp[id];
		return 0;
	}
	
	int getMyPart(ArrayList<FlowLink> path, int start){
		int i=start;
		for(; i<path.size();i++){
			//System.out.println("link's parent: "+Network.links.get(path.get(i).link).controller + " , my id: "+id);
			if(Network.links.get(path.get(i).link).controller!=id){
				break;
			}
		}
		return i-1;
	}
	
	
	public void replicateUtilizations(){
		Iterator<Integer> it = utilizations.keySet().iterator();
		while(it.hasNext()){
			int linkid = it.next();
			utilizations.get(linkid).update(Replicator.utilizations.get(linkid));
		}
		for(int i=0; i<timestamp.length; i++){
			this.timestamp[i] = Math.max(this.timestamp[i], Replicator.timestamp[i]);
		}		
	}
	
	public String getUtilizations(){
		String ret="[";
		Iterator<Integer> it = utilizations.keySet().iterator();
		//System.out.print("Utilizations: time="+time+", ");
		int first=1;
		while(it.hasNext()){
			if(first==0) ret += ", ";
			Utilization util = utilizations.get(it.next());
			ret+=util.link+":"+util.utilization;
			first=0;
		}
		return ret+"]";
	}
	
	public String getTimestamp(){
		String ret = "[";
		int first=1;
		for(int i=0; i< timestamp.length;i++){
			if(first==0) ret += ", ";
			ret+= timestamp[i];
			first=0;
		}
		return ret+"]";
	}
	
	public static void main(String[] args){
		Network network = new Network();
		Controller c0 = new Controller(1,Configuration.NDOMAINS);
		Network.controllers.put(c0.id, c0);
		
		Switch s0 = new Switch(1,4,c0.id);
		Network.switches.put(s0.id, s0);
		Switch s1 = new Switch(2,4,c0.id);
		Network.switches.put(s1.id, s1);
		Switch s2 = new Switch(3,4,c0.id);
		Network.switches.put(s2.id, s2);
		Switch s3 = new Switch(4,4,c0.id);
		Network.switches.put(s3.id, s3);
		
		try{
			Link l0 = new Link(1, c0.id, s0.addLink(1), s1.addLink(1), 10);			
			Network.links.put(l0.id, l0);
			
			Link l1 = new Link(2, c0.id, s0.addLink(2), s2.addLink(2), 10);			
			Network.links.put(l1.id, l1);
			
			Link l2 = new Link(3, c0.id, s1.addLink(3), s3.addLink(3), 10);			
			Network.links.put(l2.id, l2);
			
			Link l3 = new Link(4, c0.id, s2.addLink(4), s3.addLink(4), 10);			
			Network.links.put(l3.id, l3);
		
			Host h0 = new Host(1, c0.id, s0.addHost(1));
			Network.hosts.put(h0.id,h0);
			
			Host h1 = new Host(2, c0.id, s3.addHost(2));
			Network.hosts.put(h1.id,h1);
		
			c0.initialize();
			
			System.out.println(c0.BFS(h0.port, h1.port, 0));
			
		}catch(PortException e){
			System.out.println("Max ports used for switch " + e.switch_id + ". Aborting.");
		}
		
		
		
	}
	
}
