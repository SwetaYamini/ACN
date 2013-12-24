package timestamp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import swiconsim.swim.JobType;




public class Test {
	
	HashMap<Controller, ArrayList<Switch>> nodes;
	HashMap<Integer, Host> hosts;
	HashMap<Integer, Integer> ports;
	HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();
	
	BufferedWriter resultWriter;
	BufferedWriter logWriter;
	BufferedWriter nodeGapWriter;
	BufferedReader nodeGapReader;
	
	int ndomains=10;
	int nswitches=20;
	int nhosts=3;
	int nports=100;
	int hostid=0;
	int switchid=1;
	int linkid=1;
	HashMap<Integer, Integer> intraNeighbors;
	int interNeighbors=2;
	
	ArrayList<Job> startjobs;
	ArrayList<Job> endjobs;
	
	public Test(String resultFile, String logFile){
		try {
			resultWriter = new BufferedWriter(new FileWriter(new File(resultFile)));
			logWriter = new BufferedWriter(new FileWriter(new File(logFile)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void createTopology() throws PortException {
		
		Network network = new Network();
		nodes = new HashMap<Controller, ArrayList<Switch>>();
		hosts = new HashMap<Integer, Host>();
		ports = new HashMap<Integer,Integer>();
		intraNeighbors = new HashMap<Integer, Integer>();
		
		for(int i=1; i<=ndomains; i++){
			
			int cid = i-1;
			Controller controller = new Controller(cid, ndomains);
			Network.controllers.put(controller.id, controller);
			int neighborCount = Math.abs(ndomains/2-i) + 1;
			if(neighborCount > 4) neighborCount=4;
			intraNeighbors.put(cid, neighborCount);
			
			ArrayList<Switch> switches = new ArrayList<Switch>();
			for(int j=1;j <=nswitches; j++){
				Switch sw = new Switch(switchid, nports, cid);
				Network.switches.put(sw.id, sw);
				for(int k=1;k<=nhosts;k++){
					Host host = new Host(hostid, cid, sw.addHost(hostid));
					Network.hosts.put(host.id,host);
					
					hosts.put(hostid, host);
					hostid++;
				}
				switches.add(sw);	
				switchid++;
			}
			nodes.put(controller, switches);
		}
		
		Iterator<Controller> it = nodes.keySet().iterator();
		ArrayList<Switch> torSwitches1 = new ArrayList<Switch>();
		ArrayList<Switch> torSwitches2 = new ArrayList<Switch>();
		
		it = nodes.keySet().iterator();

		while(it.hasNext()){
			Controller c = it.next();
			ArrayList<Switch> switches = nodes.get(c);
			torSwitches1.add(switches.get(0));
			torSwitches2.add(switches.get(10));
			int neighborCount = intraNeighbors.get(c.id);
			for(int j=0;j<switches.size();j++){
				for(int k=1;k<=neighborCount;k++){
					if(j+k < switches.size()){
						Switch sw1 = switches.get(j);
						Switch sw2 = switches.get(j+k);
						Link link = new Link(linkid, c.id, sw1.addLink(linkid), sw2.addLink(linkid), 1000);			
						Network.links.put(link.id, link);
						linkid++;
					}
				}
			}
		}
		
		Collections.sort(torSwitches1);
		for(int j=0;j<=torSwitches1.size();j++){
			for(int k=1;k<=1;k++){
				if(j+k < torSwitches1.size()){
					Switch sw1 = torSwitches1.get(j);
					Switch sw2 = torSwitches1.get(j+k);
					Link link = new Link(linkid, sw1.controller, sw1.addLink(linkid), sw2.addLink(linkid), 1000);			
					Network.links.put(link.id, link);
					linkid++;
				}
			}
		}
		
		Collections.sort(torSwitches2);
		for(int j=0;j<=torSwitches2.size();j++){
			for(int k=2;k<=2;k++){
				if(j+k < torSwitches2.size()){
					Switch sw1 = torSwitches2.get(j);
					Switch sw2 = torSwitches2.get(j+k);
					Link link = new Link(linkid, sw1.controller, sw1.addLink(linkid), sw2.addLink(linkid), 1000);			
					Network.links.put(link.id, link);
					linkid++;
				}
			}
		}
		
		it = nodes.keySet().iterator();
		while(it.hasNext()){
			Controller c = (Controller) it.next();
			c.initialize();
		}
		
		
	}
	
	public void printTopology(){
		Iterator<Integer> it = Network.controllers.keySet().iterator();
		while(it.hasNext()){
			Controller c = Network.controllers.get(it.next());
			ArrayList<Switch> switches = nodes.get(c);
			for(int j=0;j<switches.size();j++){	
				System.out.println(c.id+": "+switches.get(j).controller +": "+switches.get(j).id + ": " + Network.switches.get(switches.get(j).id).controller);
			}
		}
		
		Iterator<Integer> it2 = Network.hosts.keySet().iterator();
		while(it2.hasNext()){
			int hid = it2.next();
			Host host = Network.hosts.get(hid);
			System.out.println(host.id+ " " + host.port + " " + host.controller);
		}
		
		Iterator<Integer> it3 = Network.links.keySet().iterator();
		while(it3.hasNext()){
			int lid = it3.next();
			Link link = Network.links.get(lid);
			System.out.println(link.id+ " " + link.port1 + " " + link.port2 + " " + link.controller);
		}
	}
	
	ArrayList<Integer> readNodeGaps(){
		ArrayList<Integer> nodegaps = new ArrayList<Integer>();
		try{
			nodeGapReader = new BufferedReader(new FileReader(new File("nodeGapFile")));
			String line;
			while((line=nodeGapReader.readLine())!=null ){
				nodegaps.add(Integer.parseInt(line));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return nodegaps;
	}
	
	public void simulate(){
		int start=0; 
		int end=0;
		Simulator sim = new Simulator();
		Replicator replicator = new Replicator();
		int lastReplication=0;
		int interval=100;
		try{
			while(start<startjobs.size() && end < endjobs.size()){
				int time;
				if(startjobs.get(start).time < endjobs.get(end).time){
					Job job = startjobs.get(start);
					logWriter.write("Starting job " + job.jobid + " "+job.src +"->"+job.dst + " ");
					int cid = Network.hosts.get(job.src).controller;
					Controller controller = Network.controllers.get(cid);
					if(controller.addFlow(job.flow)){
						controller.startFlow(job.flow, 0);
					}else{
						job.flow.removable=0;
					}
					logWriter.write(job.flow.path + "\n");
					result.put(job.jobid, Replicator.getMaxUtilization());
					start++;					
					time=job.time;
				}else{
					Job job = endjobs.get(end);
					logWriter.write("Ending job " + job.jobid + "\n");
					if(job.flow.removable==1){
						sim.removeFlow(job.flow);
					}
					end++;
					time=job.time;
				}
				if(lastReplication + interval < time){
					Replicator.replicate();
					lastReplication = time;
				}
				logWriter.flush();
			}

			while(start<startjobs.size()){
				Job job = startjobs.get(start);
				logWriter.write("Starting job " + job.jobid + " "+job.src +"->"+job.dst + " ");
				int cid = Network.hosts.get(job.src).controller;
				Controller controller = Network.controllers.get(cid);
				if(controller.addFlow(job.flow)){
					controller.startFlow(job.flow, 0);
				}else{
					job.flow.removable=0;
				}
				logWriter.write(job.flow.path + "\n");
				result.put(job.jobid, Replicator.getMaxUtilization());
				start++;
				if(lastReplication + interval < job.time){
					Replicator.replicate();
					lastReplication = job.time;
				}
				logWriter.flush();
			}

			while(end<endjobs.size()){
				Job job = endjobs.get(end);
				logWriter.write("Ending job " + job.jobid + "\n");
				if(job.flow.removable==1){
					sim.removeFlow(job.flow);
				}
				end++;
				if(lastReplication + interval < job.time){
					Replicator.replicate();
					lastReplication = job.time;
				}
				logWriter.flush();
			}
			logWriter.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		printResult();
		
	}
	
	private void printResult() {
		try{
			Iterator<Integer> it = result.keySet().iterator();
			while(it.hasNext()){
				int id = it.next();
				resultWriter.write(id + ": " + result.get(id) + "\n");
			}
			resultWriter.close();
		}catch(Exception e){
			
		}
	}

	void parser(String filename, ArrayList<Integer> nodegaps){
		int src=1;
		int dst=550;
		try{			
			startjobs = new ArrayList<Job>();
			endjobs = new ArrayList<Job>();
			BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
			String line;
			long max_map_bytes=0;
			long max_reduce_bytes=0;
			long max_shuffle_bytes=0;
			//int nodegap=241;
			int id=1;
			int multiplier=600;
			while((line=reader.readLine())!=null){
				//System.out.println(line);
				int factor = multiplier/600;
				if(factor==0) factor=1;
				multiplier++;
				String[] parts = line.split("\t");
				String jobid = parts[0];
				int startTime = Integer.parseInt(parts[1]);
				long map_bytes = Long.parseLong(parts[3]);
				long shuffle_bytes = Long.parseLong(parts[4]);
				long reduce_bytes = Long.parseLong(parts[5]);
				
				if(max_map_bytes < map_bytes){
					max_map_bytes = map_bytes;
				}
				if(max_shuffle_bytes < shuffle_bytes){
					max_shuffle_bytes = shuffle_bytes;
				}
				if(max_reduce_bytes < reduce_bytes){
					max_reduce_bytes = reduce_bytes;
				}
				//int dst = (src+factor*nodegaps.get(src))%600;
				int rate = 100;
				int duration = (int) map_bytes/100000000;
				if(duration<=0) duration=1;
				if(duration>10000) duration=10000;
				int endTime = startTime+duration;
				
				Flow flow = new Flow(id, Network.hosts.get(src).port, Network.hosts.get(dst).port, startTime, duration, rate);
				
				Job job = new Job(id, map_bytes, JobType.START, startTime, src, dst, flow);
				startjobs.add(job);
				
				job = new Job(id, map_bytes, JobType.END, endTime, src, dst, flow);
				endjobs.add(job);
				
				id++;
				//src = (src+1)%600;
			}
			
			Collections.sort(startjobs);
			Collections.sort(endjobs);
			printJobs(startjobs);
			printJobs(endjobs);
			System.out.println("Max map bytes: " + max_map_bytes);
			System.out.println("Max shuffle bytes: " + max_shuffle_bytes);
			System.out.println("Max reduce bytes: " + max_reduce_bytes);
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("src" + src + " size:"+nodegaps.size());
			return;
		}
	}
	
	public static void printJobs(ArrayList<Job> jobs){
		for(int i=0;i<jobs.size();i++){
			System.out.println(jobs.get(i));
		}
	}
	
	public static void main(String[] args){
		Test test = new Test("resultFile", "logFile");
		try{
			test.createTopology();
			//test.printTopology();
			ArrayList<Integer> nodegaps = test.readNodeGaps();
			test.parser("/home/gourav/SWIM/trunk/workloadSuite/FB-2009_samples_24_times_1hr_0.tsv_sample", nodegaps);
			test.simulate();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void test(){
		Network network = new Network();
		
		Simulator sim = new Simulator();
		
		Controller c0 = new Controller(0,Configuration.NDOMAINS);
		Network.controllers.put(c0.id, c0);
		
		Controller c1 = new Controller(1,Configuration.NDOMAINS);
		Network.controllers.put(c1.id, c1);
		
		
		Switch s0 = new Switch(1,4,c0.id);
		Network.switches.put(s0.id, s0);
		Switch s1 = new Switch(2,4,c0.id);
		Network.switches.put(s1.id, s1);
		Switch s2 = new Switch(3,4,c1.id);
		Network.switches.put(s2.id, s2);
		Switch s3 = new Switch(4,4,c0.id);
		Network.switches.put(s3.id, s3);
		
		try{
			Link l0 = new Link(1, c1.id, s0.addLink(1), s1.addLink(1), 10);			
			Network.links.put(l0.id, l0);
			
			Link l1 = new Link(2, c0.id, s0.addLink(2), s2.addLink(2), 10);			
			Network.links.put(l1.id, l1);
			
			Link l2 = new Link(3, c1.id, s1.addLink(3), s3.addLink(3), 10);			
			Network.links.put(l2.id, l2);
			
			Link l3 = new Link(4, c0.id, s2.addLink(4), s3.addLink(4), 10);			
			Network.links.put(l3.id, l3);
		
			Host h0 = new Host(1, c0.id, s0.addHost(1));
			Network.hosts.put(h0.id,h0);
			
			Host h1 = new Host(2, c0.id, s3.addHost(2));
			Network.hosts.put(h1.id,h1);
			
			Replicator replicator = new Replicator();

		
			c0.initialize();
			c1.initialize();
			
			//c0.utilizations.get(1).update(5); 			
			//c0.utilizations.get(4).update(7);
			System.out.println(Replicator.getUtilizations2());
			Flow flow1 = new Flow(1, h0.port, h1.port, 0, 10, 5); 
			c0.addFlow(flow1);
			System.out.println(flow1.path);
			c0.startFlow(flow1, 0);			
			System.out.println(Replicator.getUtilizations2());
			
			//Replicator.replicate();
			
			Flow flow2 = new Flow(2, h0.port, h1.port, 0, 10, 5); 
			c0.addFlow(flow2);
			System.out.println(flow2.path);
			c0.startFlow(flow2, 0);			
			System.out.println(Replicator.getUtilizations2());
			System.out.println(Replicator.getMaxUtilization());
			
			sim.removeFlow(flow1);
			System.out.println(Replicator.getUtilizations2());

			
		}catch(PortException e){
			System.out.println("Max ports used for switch " + e.switch_id + ". Aborting.");
		}
		
		
		
	}
}
