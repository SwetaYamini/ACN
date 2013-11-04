package timestamp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Random;

public class Network {
	public static HashMap<Integer, Switch > switches;
	public static HashMap<Integer, Link> links;
	public static HashMap<Integer, Port> ports;
	public static HashMap<Integer, Controller> controllers;
	
	public static int NSWITCHES;
	public static int NLINKS; 
	public static int switchPerDomain=5;
	
	public Network(){
		switches = new HashMap<Integer, Switch>();
		links = new HashMap<Integer, Link>();
		ports = new HashMap<Integer, Port>();
		controllers = new HashMap<Integer, Controller>();
	}
	
	public static void createRealRandomNetwork(){
		Network network = new Network();
		Random rand = new Random(50);
		
		Configuration.NDOMAINS = 3;
		int nports = 100;
		int switchid=0;
		int linkid=0;
		int capacity=100;
		switchPerDomain=5;
		
		for(int i=0;i<Configuration.NDOMAINS; i++){
			Controller c = new Controller(i,Configuration.NDOMAINS);
			controllers.put(c.id, c);
			for(int j=0; j<switchPerDomain;j++){
				Switch s = new Switch(switchid,nports,c.id);
				switches.put(s.id, s);
				switchid++;
			}
		}
		
		for(int i=0;i<switchid;i++){
			for(int j=i+1;j<switchid;j++){
				Switch s1 = switches.get(i);
				Switch s2 = switches.get(j);
				int random = rand.nextInt(100);
				
				if(( s1.controller==s2.controller && random<70 ) || (s1.controller!=s2.controller && random < 50 )){	
					int controller = s1.controller;
					if(rand.nextBoolean()) controller = s2.controller;					
			
					try{
						Link l = new Link(linkid, controller, s1.addLink(linkid), s2.addLink(linkid), capacity);	
						//System.out.println("Creating link");
						links.put(l.id, l);
						linkid++;
					}catch(PortException e){
						System.out.println("Max ports used for switch " + e.switch_id + ". Aborting.");
					}
				}else{
					//System.out.println("Not creating link: " + s1.controller + " " + s2.controller + " " + random);
				}
			}
		}
		
	}
	
	public static void createNetwork(){
		Network network = new Network();
		
		Controller c0 = new Controller(0,Configuration.NDOMAINS);
		controllers.put(c0.id, c0);
		Controller c1 = new Controller(1,Configuration.NDOMAINS);
		controllers.put(c1.id, c1);
		Controller c2 = new Controller(2,Configuration.NDOMAINS);
		controllers.put(c2.id, c2);
		
		for(int j=0; j<10;j++){
			Switch s = new Switch(j,10,c0.id);
			switches.put(s.id, s);
		}

		int linkid=0;
		for(int i=0;i<10;i++){
			for(int j=i+1;j<10;j++){
				Switch s1 = switches.get(i);
				Switch s2 = switches.get(j);
				try{
					Link l = new Link(linkid, c0.id, s1.addLink(linkid), s2.addLink(linkid), 50);	
					//System.out.println("Creating link");
					links.put(l.id, l);
					linkid++;
				}catch(PortException e){
					System.out.println("Max ports used for switch " + e.switch_id + ". Aborting.");
				}
			}
		}
		
		Switch s10 = new Switch(10,10,c1.id);
		switches.put(s10.id, s10);
		
		Switch s11 = new Switch(11,10,c2.id);
		switches.put(s11.id, s11);
		
		Switch s0 = switches.get(0);
		Switch s9 = switches.get(9);
		
		try{			
			
			Link l4 = new Link(linkid, c1.id, s0.addLink(linkid), s10.addLink(linkid), 10000);			
			links.put(l4.id, l4);
			linkid++;
			
			Link l5 = new Link(linkid, c2.id, s9.addLink(linkid), s11.addLink(linkid), 10000);			
			links.put(l5.id, l5);
			linkid++;
			
			
		}catch(PortException e){
			System.out.println("Max ports used for switch " + e.switch_id + ". Aborting.");
		}
	}
	
	public static void createRandomNetwork(){
		Network network = new Network();
		
		Controller c0 = new Controller(0,Configuration.NDOMAINS);
		controllers.put(c0.id, c0);
		Controller c1 = new Controller(1,Configuration.NDOMAINS);
		controllers.put(c1.id, c1);
		Controller c2 = new Controller(2,Configuration.NDOMAINS);
		controllers.put(c2.id, c2);
		
		Switch s0 = new Switch(0,4,c0.id);
		switches.put(s0.id, s0);
		Switch s1 = new Switch(1,4,c0.id);
		switches.put(s1.id, s1);
		Switch s2 = new Switch(2,4,c0.id);
		switches.put(s2.id, s2);
		Switch s3 = new Switch(3,4,c0.id);
		switches.put(s3.id, s3);	
		
		Switch s4 = new Switch(4,4,c1.id);
		switches.put(s4.id, s4);
		
		Switch s5 = new Switch(5,4,c2.id);
		switches.put(s5.id, s5);
		
		
		try{
			Link l0 = new Link(0, c0.id, s0.addLink(0), s1.addLink(0), 10);			
			links.put(l0.id, l0);
			
			Link l1 = new Link(1, c0.id, s0.addLink(1), s2.addLink(1), 10);			
			links.put(l1.id, l1);
			
			Link l2 = new Link(2, c0.id, s1.addLink(2), s3.addLink(2), 10);			
			links.put(l2.id, l2);
			
			Link l3 = new Link(3, c0.id, s2.addLink(3), s3.addLink(3), 10);			
			links.put(l3.id, l3);
			
			Link l4 = new Link(4, c1.id, s1.addLink(4), s4.addLink(4), 100);			
			links.put(l4.id, l4);
			
			Link l5 = new Link(5, c2.id, s3.addLink(5), s5.addLink(5), 100);			
			links.put(l5.id, l5);
			
			
		}catch(PortException e){
			System.out.println("Max ports used for switch " + e.switch_id + ". Aborting.");
		}
		
	}
	
	public static void printNetwork(){
		Iterator<Switch> it = switches.values().iterator();
		while(it.hasNext()){
			System.out.println(it.next());
		}
		Iterator<Link> it2 = links.values().iterator();
		while(it2.hasNext()){
			System.out.println(it2.next());
		}
	}
}
