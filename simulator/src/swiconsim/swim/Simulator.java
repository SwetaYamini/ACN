package swiconsim.swim;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import swiconsim.controller.*;
import swiconsim.network.DataNetwork;
import swiconsim.network.ManagementNetwork;
import swiconsim.node.ManagementNode;
import swiconsim.nwswitch.*;
import swiconsim.nwswitch.port.Port;
import swiconsim.packet.Packet;

import java.util.ArrayList;
import swiconsim.host.*;
import java.util.HashMap;
import swiconsim.util.*;

public class Simulator {
	
	HashMap<ManagementNode, ArrayList<Switch>> nodes;
	HashMap<Long, Host> hosts;
	HashMap<Long, Short> ports;
	Controller parent;
	
	BufferedWriter resultWriter;
	BufferedWriter logWriter;
	BufferedWriter nodeGapWriter;
	BufferedReader nodeGapReader;
	
	int ndomains=10;
	int nswitches=20;
	int nhosts=3;
	int nports=100;
	int hostid=0;
	HashMap<Long, Integer> intraNeighbors;
	int interNeighbors=2;
	
	ArrayList<Job> startjobs;
	ArrayList<Job> endjobs;
	
	public Simulator(String resultFile, String logFile){
		try {
			resultWriter = new BufferedWriter(new FileWriter(new File(resultFile)));
			logWriter = new BufferedWriter(new FileWriter(new File(logFile)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	void createTopology(int flag){
		
		if(flag==1){
			parent = new Controller(1000);
		}
		
		nodes = new HashMap<ManagementNode, ArrayList<Switch>>();
		hosts = new HashMap<Long, Host>();
		ports = new HashMap<Long,Short>();
		intraNeighbors = new HashMap<Long, Integer>();
		
		for(int i=1; i<=ndomains; i++){
			long cid = 10000 +i;
			ManagementNode controller;
			if(flag==1){
				controller = new Controller(cid, parent.getId());
			}else{
				controller = new Controller2(cid);
			}
			int neighborCount = Math.abs(ndomains/2-i) + 1;
			if(neighborCount > 3) neighborCount=3;
			intraNeighbors.put(cid, neighborCount);
			ArrayList<Switch> switches = new ArrayList<Switch>();
			for(int j=1;j <=nswitches; j++){
				Switch sw = new Switch(i*100 + j, nports, cid);
				ports.put(sw.getId(),(short) 1);
				for(int k=1;k<=nhosts;k++){
					Host host = new Host(hostid, IPUtil.toString(IPUtil.stringToIP("1.1.1.1") + hostid));
					sw.addHost(host, PortUtil.calculatePortId(sw.getId(), ports.get(sw.getId())));
					ports.put(sw.getId(), (short) (ports.get(sw.getId())+1));
					hosts.put((long)hostid, host);
					hostid++;
				}
				switches.add(sw);				
			}
			nodes.put(controller, switches);
		}
		Iterator<ManagementNode> it = nodes.keySet().iterator();
		ArrayList<Switch> torSwitches1 = new ArrayList<Switch>();
		ArrayList<Switch> torSwitches2 = new ArrayList<Switch>();
		
		it = nodes.keySet().iterator();

		while(it.hasNext()){
			ManagementNode c = it.next();
			ArrayList<Switch> switches = nodes.get(c);
			torSwitches1.add(switches.get(0));
			torSwitches2.add(switches.get(10));
			int neighborCount = intraNeighbors.get(c.getId());
			for(int j=0;j<switches.size();j++){
				for(int k=1;k<=neighborCount;k++){
					if(j+k < switches.size()){
						Switch sw1 = switches.get(j);
						Switch sw2 = switches.get(j+k);
						long port1 = PortUtil.calculatePortId(sw1.getId(),ports.get(sw1.getId()));
						ports.put(sw1.getId(), (short) (ports.get(sw1.getId())+1));
						long port2 = PortUtil.calculatePortId(sw2.getId(),ports.get(sw2.getId()));
						ports.put(sw2.getId(), (short) (ports.get(sw2.getId())+1));
						DataNetwork.getInstance().addEdge(port1, port2, 10000);
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
					long port1 = PortUtil.calculatePortId(sw1.getId(),ports.get(sw1.getId()));
					ports.put(sw1.getId(), (short) (ports.get(sw1.getId())+1));
					long port2 = PortUtil.calculatePortId(sw2.getId(),ports.get(sw2.getId()));
					ports.put(sw2.getId(), (short) (ports.get(sw2.getId())+1));
					DataNetwork.getInstance().addEdge(port1, port2, 10000);
				}
			}
		}
		
		Collections.sort(torSwitches2);
		for(int j=0;j<=torSwitches2.size();j++){
			for(int k=2;k<=2;k++){
				if(j+k < torSwitches2.size()){
					Switch sw1 = torSwitches2.get(j);
					Switch sw2 = torSwitches2.get(j+k);
					long port1 = PortUtil.calculatePortId(sw1.getId(),ports.get(sw1.getId()));
					ports.put(sw1.getId(), (short) (ports.get(sw1.getId())+1));
					long port2 = PortUtil.calculatePortId(sw2.getId(),ports.get(sw2.getId()));
					ports.put(sw2.getId(), (short) (ports.get(sw2.getId())+1));
					DataNetwork.getInstance().addEdge(port1, port2, 10000);
				}
			}
		}
		if(flag==1){
			it = nodes.keySet().iterator();
			while(it.hasNext()){
				Controller c = (Controller) it.next();
				c.populatePorts();
				c.populateExternalPorts();
			}
			parent.populatePorts();
			parent.populateExternalPorts();
		}
		
	}
	
	public void simulate(){
		int start=0; 
		int end=0;
		try{
			while(start<startjobs.size() && end < endjobs.size()){
				if(startjobs.get(start).time < endjobs.get(end).time){
					Job job = startjobs.get(start);
					logWriter.write("Starting job " + job.jobid + " "+job.src +"->"+job.dst + "\n");
					hosts.get((long)job.src).sendPkt(job.packet);
					hosts.get((long)job.src).startFlow(job.packet);				
					start++;
				}else{
					Job job = endjobs.get(end);
					logWriter.write("Ending job " + job.jobid + "\n");
					job.packet.last=1;
					hosts.get((long)job.src).endFlow(job.packet);
					end++;
				}
				logWriter.flush();
			}

			while(start<startjobs.size()){
				Job job = startjobs.get(start);
				logWriter.write("Starting job " + job.jobid + "\n");
				hosts.get((long)job.src).sendPkt(job.packet);
				hosts.get((long)job.src).startFlow(job.packet);				
				start++;
				logWriter.flush();
			}

			while(end<endjobs.size()){
				Job job = endjobs.get(end);
				logWriter.write("Ending job " + job.jobid + "\n");
				job.packet.last=1;
				hosts.get((long)job.src).endFlow(job.packet);
				end++;
				logWriter.flush();
			}
			logWriter.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		printResult();
		
	}
	
	
	void parser(String filename, ArrayList<Integer> nodegaps){
		int src=0;
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
				int dst = (src+factor*nodegaps.get(src))%600;
				Packet packet = new Packet((long) 0, IPUtil.stringToIP("1.1.1.1")+src, IPUtil.stringToIP("1.1.1.1")+dst, 10, id);
				
				Job job = new Job(id, map_bytes, JobType.START, startTime, src, dst, packet);
				startjobs.add(job);
				
				long duration = map_bytes/100000000;
				if(duration==0) duration=1;
				if(duration>10000) duration=10000;
				long endTime = startTime+duration;
				job = new Job(id, map_bytes, JobType.END, endTime, src, dst, packet);
				endjobs.add(job);
				
				id++;
				src = (src+1)%600;
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
	
	public void sendTestPacket(){
		Packet pkt12 = new Packet((long) 0, IPUtil.stringToIP("1.1.1.1"),
				IPUtil.stringToIP("1.1.1.1")+241, 10, 1);
		hosts.get((long)0).sendPkt(pkt12);
		hosts.get((long)0).startFlow(pkt12);
		pkt12.last=1;
		hosts.get((long)0).endFlow(pkt12);
		printResult();
	}
	
	public void printResult(){
		try{
			List<Packet> packets = new ArrayList<Packet>(DataNetwork.getInstance().HopCount.keySet());
			Collections.sort(packets);
			for(int i=0; i<packets.size(); i++){
				Packet pkt = packets.get(i);
				resultWriter.write(pkt.id + ": " + pkt.nhops + "\n");
			}
	
			resultWriter.write("ControllerToSwitchCount: " + ManagementNetwork.getInstance().ControllerToSwitchCount + "\n");
			resultWriter.write("SwitchToControllerCount: " + ManagementNetwork.getInstance().SwitchToControllerCount + "\n");
			resultWriter.write("ControllerToControllerCount: " + ManagementNetwork.getInstance().ControllerToControllerCount + "\n");
			//System.out.println("MessageCount size: " + ManagementNetwork.getInstance().MessageCount.size());

			packets = new ArrayList<Packet>(ManagementNetwork.getInstance().MessageCount.keySet());
			Collections.sort(packets);
			for(int i=0; i<packets.size(); i++){
				Packet pkt = packets.get(i);
				resultWriter.write(pkt.id + ": " + ManagementNetwork.getInstance().MessageCount.get(pkt) + "\n");
			}
			
			resultWriter.write("Utilizations: \n");
			packets = new ArrayList<Packet>(DataNetwork.getInstance().UtilizationTracker.keySet());
			Collections.sort(packets);
			for(int i=0; i<packets.size(); i++){
				Packet pkt = packets.get(i);
				resultWriter.write(pkt.id + ": " + pkt.maxutil + "\n");
			}
			
			resultWriter.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void printTopology(){
		Iterator<ManagementNode> it = nodes.keySet().iterator();
		while(it.hasNext()){
			ManagementNode c = it.next();
			ArrayList<Switch> switches = nodes.get(c);
			for(int j=0;j<switches.size();j++){	
				System.out.println(c.getId()+": "+switches.get(j).parent +": "+switches.get(j).getId());
			}
		}
		
		Iterator<Long> it2 = hosts.keySet().iterator();
		while(it2.hasNext()){
			long swid = it2.next();
			Host host = hosts.get(swid);
			System.out.println(host.getIp()+ " " + host.getPort().getId() + " " + swid);
		}
	}
	
	public void generateNodegaps(){
		Random random = new Random();
		try {
			nodeGapWriter = new BufferedWriter(new FileWriter(new File("nodeGapFile")));
			for(int i=0;i<600;i++){
				nodeGapWriter.write(random.nextInt(600)+"\n");
				//System.out.println("Writing " + 241);
				//nodeGapWriter.write(241+"\n");
			}
			nodeGapWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
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
	
	
	public static void main(String[] args){
		
		long start = System.currentTimeMillis();
		Simulator simulator = new Simulator("resultFile", "logFile");		
		//simulator.generateNodegaps();
		
		ArrayList<Integer> nodegaps = simulator.readNodeGaps();
		System.out.println(nodegaps);
		simulator.parser("/home/gourav/SWIM/trunk/workloadSuite/FB-2009_samples_24_times_1hr_0.tsv", nodegaps);
		simulator.createTopology(2);
		//simulator.sendTestPacket();
		simulator.simulate();
		
		long end = System.currentTimeMillis();
		long time = (end-start);///1000;
		System.out.println("Time taken: " + time);
	}
}
