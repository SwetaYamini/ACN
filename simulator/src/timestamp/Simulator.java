package timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class Simulator {
	int time;
	int simulationLife;
	HashMap<Integer, ArrayList<Flow>> flows;  
	ArrayList<Flow> activeFlows;
	public static int badFlows=0;
	
	public void generateFlows(){
		flows = new HashMap<Integer, ArrayList<Flow>>();
		ArrayList<Flow> flowsAtInstant = new ArrayList<Flow>();
		
		
		flowsAtInstant.clear();
		flowsAtInstant.add(new Flow(0, 4, 5, 1, 8, 5));
		flows.put(1, new ArrayList<Flow>(flowsAtInstant));

		flowsAtInstant.clear();
		flowsAtInstant.add(new Flow(1, 4, 5, 2, 7, 5));
		flows.put(2, new ArrayList<Flow>(flowsAtInstant));
		
		flowsAtInstant.clear();
		flowsAtInstant.add(new Flow(2, 4, 5, 3, 6, 5));
		flows.put(3, new ArrayList<Flow>(flowsAtInstant));
		
		/*flowsAtInstant.clear();
		flowsAtInstant.add(new Flow(3, 0, 3, 6, 3, 5));
		flows.put(6, new ArrayList<Flow>(flowsAtInstant));*/
		
	}
	
	public void generateFlows2(){
		Random rand = new Random();
		flows = new HashMap<Integer, ArrayList<Flow>>();
		ArrayList<Flow> flowsAtInstant = new ArrayList<Flow>();
		
		int flowid=0;
		for(int i=1;i<simulationLife; i++){
			flowsAtInstant.clear();
			if(rand.nextBoolean()) flowsAtInstant.add(new Flow(flowid, 10007, 11007, i, 10, 5));
			else flowsAtInstant.add(new Flow(flowid, 11007, 10007, i, 10, 5));
			flows.put(i, new ArrayList<Flow>(flowsAtInstant));
			flowid++;
		}
	}
	
	public void generateRandomFlows(){
		flows = new HashMap<Integer, ArrayList<Flow>>();
		ArrayList<Flow> flowsAtInstant = new ArrayList<Flow>();
		int nflows = simulationLife-2;
		Random rand = new Random();
		int rate = 5;
		
		for(int i=0;i<simulationLife;i++){
			flows.put(i,new ArrayList<Flow>());
		}
		
		for(int i=0;i<nflows;i++){
			int start = rand.nextInt(simulationLife);
			int duration = rand.nextInt(simulationLife - start);
			int sw1 = rand.nextInt(Network.switchPerDomain);
			int sw2 = 10 + rand.nextInt(Network.switchPerDomain);
			flows.get(start).add(new Flow(i,sw1,sw2,start,duration,rate));
		}
		
		
		
	}
	
	public void simulate(){
		simulationLife = 100;
		Network.createNetwork();
		//Network.printNetwork();
		//System.out.println();
		Iterator<Controller> it = Network.controllers.values().iterator();
		while(it.hasNext()){
			it.next().initialize();
		}
		new Replicator();
		time=0;
		activeFlows = new ArrayList<Flow>();
		generateFlows2();
		
		while(time<=simulationLife){
			
			//Remove expired flows
			for(int i=0;i<activeFlows.size();i++){
				if(activeFlows.get(i).startTime + activeFlows.get(i).duration <= time){
					//System.out.println("Removing flow " + activeFlows.get(i));
					removeFlow(activeFlows.get(i));
					activeFlows.remove(i);
				}
			}
			
			//Add new flows
			if(flows.containsKey(time)){
				ArrayList<Flow> todaysFlow = flows.get(time);
				for(int i=0;i<todaysFlow.size();i++){
					//System.out.println("Adding flow : " + todaysFlow.get(i));
					//System.out.println(todaysFlow.get(i).srcPort);
					//System.out.println(Network.ports.keySet());
					int sw = Network.ports.get(todaysFlow.get(i).srcPort).parent;
					Controller controller = Network.controllers.get(Network.switches.get(sw).controller);
					if(controller.addFlow(todaysFlow.get(i))){
						//todaysFlow.get(i).printTimestamp();
						controller.startFlow(todaysFlow.get(i), 0);
						activeFlows.add(todaysFlow.get(i));
					}
					
					//controllers.get(switches.get(todaysFlow.get(i).srcSwitch).co)
				}
			}
			
			
		
			
			
			//Replicator.printUtilizations(time);
			if(time%Configuration.REPLICATIONTIME==0){
				//System.out.println("Replicated utilizations");
				Replicator.replicate();
			}
			
			//Logging
			/*System.out.println();
			System.out.println("Time: "+time);
			System.out.println("Number of active flows: " + activeFlows.size());
			System.out.println("System utilizations: " + Replicator.getUtilizations());
			System.out.println("System timestamp: " + Replicator.getTimestamp());
			Iterator<Controller> it2 = Network.controllers.values().iterator();
			while(it2.hasNext()){
				Controller controller = it2.next();
				System.out.println("Controller " + controller.id + " utilizations: " + controller.getUtilizations());
				System.out.println("Controller " + controller.id + " timestamp: " + controller.getTimestamp());
			}*/
			System.out.println(Replicator.getUtilizations2());
			//System.out.println("\n\n");
			
			
		
			time++;
			
		
		}
		
	}
	
	public void removeFlow(Flow flow){
		HashMap<Integer, Integer> updatedControllers = new HashMap<Integer, Integer>(); 
		//System.out.println("Removing flow " + flow.id + " " + flow);
		for(int i=0;i<flow.path.size();i++){
			int link = flow.path.get(i).link;
			int controller = Network.links.get(link).controller;
			if(!updatedControllers.containsKey(controller)) updatedControllers.put(controller,1);
			Replicator.utilizations.get(link).update(-1*flow.rate);
			Network.controllers.get(controller).utilizations.get(link).update(-1*flow.rate);
		}
		Iterator<Integer> it = updatedControllers.keySet().iterator();
		while(it.hasNext()){
			int controller = it.next();
			Network.controllers.get(controller).timestamp[controller]++;
			Replicator.timestamp[controller] = Network.controllers.get(controller).timestamp[controller];
		}
	}
	
	public static void main(String[] args){
		Simulator sim = new Simulator();
		sim.simulate();
	}
}
