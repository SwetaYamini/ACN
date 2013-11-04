package timestamp;

import java.util.ArrayList;
import java.util.HashMap;


import java.util.Iterator;

public class Controller extends Thread {
	int id;
	int[] timestamp;
	HashMap<Integer, Utilization> utilizations;
	
	int globalBest;
	ArrayList<FlowLink> bestPath = new ArrayList<FlowLink>();
	HashMap<Integer, Integer> visited = new HashMap<Integer, Integer>(); 
	
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
		getBestPath(flow.srcSwitch, flow.dstSwitch);
		if(globalBest==-1){
			Simulator.badFlows++;
			System.out.println("WARN: Flow " + flow + " is not possible");
			return false;
		}
		//System.out.println("Controller " + id + " is done adding flow");
		int end = getMyPart(bestPath, 0);
		//System.out.println("end: " + end);
		updateUtilizations(flow, bestPath, 0 , end);
		flow.updateTimestamp(timestamp);
		flow.path = new ArrayList<FlowLink>(bestPath);
		if(end<bestPath.size()-1){
			int nextLink = bestPath.get(end+1).link;
			int nextController = Network.links.get(nextLink).controller;
			Network.controllers.get(nextController).addFlow(flow, end+1);
		}
		return true;
	}
	
	public void addFlow(Flow flow, int start){
		
		int end = getMyPart(flow.path, start);
		
		//Algorithm
		//DO nothing for now.
		//System.out.println("Controller "+id+" adding flow");
		/*if(flow.timestamp[id]<timestamp[id]){
			//System.out.println("Controller "+id+" trying to change flow");
			int sw1 = Network.ports.get(flow.path.get(start).srcPort).parent;
			int sw2 = Network.ports.get(flow.path.get(start).dstPort).parent;
			getBestIntraPath(sw1,sw2);
			if(globalBest!=-1){
				ArrayList<FlowLink> newPath = new ArrayList<FlowLink>();
				for(int i=0;i<=start-1;i++){
					newPath.add(flow.path.get(i));
				}
				for(int i=0;i<bestPath.size();i++){
					newPath.add(bestPath.get(i));
				}
				int new_end=newPath.size()-1;
				for(int i=end+1;i<flow.path.size();i++){
					newPath.add(flow.path.get(i));
				}
				//System.out.println("Controller "+id+" is changing path from : "+ flow.path +" to "+ newPath);
				flow.path = new ArrayList<FlowLink>(newPath);
				end = new_end;					
			}
		}*/
		
		updateUtilizations(flow, flow.path, start, end);
		flow.updateTimestamp(timestamp);
		if(end<flow.path.size()-1){
			int nextLink = flow.path.get(end+1).link;
			int nextController = Network.links.get(nextLink).controller;
			Network.controllers.get(nextController).addFlow(flow, end+1);
		}
	}
	
	public void getBestPath(int sw1, int sw2){		
		globalBest = -1;
		bestPath.clear();
		visited.clear();
		DFS(sw1,sw2,0, new ArrayList<FlowLink>());
		//System.out.println("Best path cost (" + globalBest +") is " + bestPath);
	}
	
	public void DFS(int sw, int dst, int maxUtil, ArrayList<FlowLink> path){
		//System.out.println("DFS Recursion: " + sw + " " + visited);
		if(sw==dst){
			if(globalBest==-1 || maxUtil < globalBest || (maxUtil==globalBest && path.size() < bestPath.size())){
				globalBest=maxUtil;
				//System.out.println("updating bestPath");
				bestPath = new ArrayList<FlowLink>(path);
			}
			return;
		}
		if(visited.containsKey(sw)) return;
		visited.put(sw, 1);
		Iterator<Port> it = Network.switches.get(sw).ports.values().iterator();
		while(it.hasNext()){
			Port port = it.next();
			int new_maxUtil=maxUtil;
			if(port.link!=-1){
				Link link = Network.links.get(port.link);
				//System.out.println("DFS: link: " + link.id + " port: " + port.id);
				//if(utilizations.containsKey(link.id)) System.out.println("Utilization does contain link");
				if(utilizations.get(link.id).utilization > new_maxUtil) new_maxUtil = utilizations.get(link.id).utilization; 
				path.add(new FlowLink(link.id, port.id, link.getOtherPort(port.id)));
				DFS(Network.ports.get(link.getOtherPort(port.id)).parent, dst, new_maxUtil, path);
				path.remove(path.size()-1);
			}
		}
		visited.remove(sw);
	}
	
	public void getBestIntraPath(int sw1, int sw2){		
		globalBest = -1;
		bestPath.clear();
		visited.clear();
		intraDFS(sw1,sw2,0, new ArrayList<FlowLink>());
		//System.out.println("Best path cost (" + globalBest +") is " + bestPath);
	}
	
	public void intraDFS(int sw, int dst, int maxUtil, ArrayList<FlowLink> path){
		if(sw==dst){
			if(globalBest==-1 || maxUtil < globalBest || (maxUtil==globalBest && path.size() < bestPath.size())){
				globalBest=maxUtil;
				//System.out.println("updating bestPath");
				bestPath = new ArrayList<FlowLink>(path);
			}
			return;
		}
		//pruning
		//if(maxUtil > globalBest) return;
		if(visited.containsKey(sw)) return;
		visited.put(sw, 1);
		Iterator<Port> it = Network.switches.get(sw).ports.values().iterator();
		while(it.hasNext()){
			Port port = it.next();
			int new_maxUtil=maxUtil;
			if(port.link!=-1 && Network.links.get(port.link).controller==id){
				Link link = Network.links.get(port.link);
				//System.out.println("DFS: link: " + link.id + " port: " + port.id);
				//if(utilizations.containsKey(link.id)) System.out.println("Utilization does contain link");
				if(utilizations.get(link.id).utilization > new_maxUtil) new_maxUtil = utilizations.get(link.id).utilization; 
				path.add(new FlowLink(link.id, port.id, link.getOtherPort(port.id)));
				DFS(Network.ports.get(link.getOtherPort(port.id)).parent, dst, new_maxUtil, path);
				path.remove(path.size()-1);
			}
		}
		visited.remove(sw);
	}
	
	int updateUtilizations(Flow flow, ArrayList<FlowLink> path, int start, int end){
		for(int i=start; i<=end; i++){
			utilizations.get(path.get(i).link).update(flow.rate);
			Replicator.utilizations.get(path.get(i).link).update(flow.rate);
		}
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
	
}
