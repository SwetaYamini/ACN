package timestamp;

import java.util.ArrayList;

public class Flow {
	int id;
	int srcPort;
	int dstPort;
	int startTime;
	int duration;
	int active;
	int rate;	
	int[] timestamp;
	ArrayList<FlowLink> path;
	int currPosition;
	int removable=1;
	
	public Flow(int id, int srcPort, int dstPort, int startTime, int duration, int rate){
		this.id = id;
		this.srcPort = srcPort;
		this.dstPort = dstPort;
		this.startTime = startTime;
		this.duration = duration;
		this.rate = rate;
		timestamp = new int[Configuration.NDOMAINS];
		for(int i=0;i<timestamp.length;i++) timestamp[i]=0;
		//path
	}
	
	public void activateFlow(){
		
	}
	
	public void updateTimestamp(int[] timestamp){
		for(int i=0; i<timestamp.length; i++){
			this.timestamp[i] = Math.max(this.timestamp[i], timestamp[i]);
		}
	}
	
	public void printTimestamp(){
		String ret = "Flow["+id+"][";
		for(int i=0;i<timestamp.length;i++){
			if(i!=0) ret+=", ";
			ret+= timestamp[i];
		}
		ret += "]";
		System.out.println(ret);
	}
	
	public String toString(){
		String ret = "Flow["+id+"][port "+srcPort+"-"+dstPort+"]["+startTime+"-"+(startTime+duration)+"]";
		if(path!=null) ret+=path;
		return ret;
	}
}
