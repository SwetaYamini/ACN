package graph;

import java.util.ArrayList;

public class Flow {
	int[] ips = new int[32];
	int empty;
	Edge edgeIn;
	Edge edgeOut;
	int priority;
	
	public Flow overlap(Flow f){
		Flow overlap = new Flow();
		for(int i=0; i<ips.length;i++){
			if(ips[i]==2){
				overlap.ips = f.ips;
				return overlap;
			}else if(f.ips[i]==2){
				overlap.ips = this.ips;
				return overlap;
			}else if(ips[i]==f.ips[i]){
				overlap.ips[i] = ips[i];
			}else{
				break;
			}
		}
		return null;
	}
	
	/*public int compare(Flow flow){
		for(int i=0; i<ips.length;i++){
			if(ips[i]==flow.ips[i]) continue;
		}
	}
	
	public int subtract(Flow flow){
		if(flow.ips)
	}*/
	
}
