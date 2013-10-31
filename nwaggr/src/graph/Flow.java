package graph;

import java.util.ArrayList;
import java.util.Comparator;

public class Flow implements Comparable<Flow> {
	int[] ips = new int[32];
	int empty;
	int len;
	Edge edgeIn;
	Edge edgeOut;
	int priority;
	
	public Flow(){
		empty=1;
		len=0;
	}
	
	public Flow overlap(Flow f){
		Flow overlap = new Flow();
		if(f.empty==1 || this.empty==1){
			return overlap;
		}
		if(this.len >= f.len){
			if(compareHelper(this, f, f.len)==0){
				overlap.ips = this.ips;
				overlap.len = this.len;
				overlap.empty=0;
			}
		}else{
			if(compareHelper(this, f, this.len)==0){
				overlap.ips = f.ips;
				overlap.len = f.len;
				overlap.empty=0;
			}
		}
		return overlap;
	}
	
	public int compareHelper(Flow f1, Flow f2, int len){
		//if(len==0) return 
		for(int i=0;i<len;i++){
			if(f1.ips[i]!=f2.ips[i]) return 1;
		}
		return 0;
	}
	
	public int compare(Flow flow){
		if(flow.len==this.len && compareHelper(this,flow,this.len)==0){
			return 0;
		}
		return 1;
	}
	
	public void subtract(Flow flow){
		if(compareHelper(this, flow, Math.min(this.len, flow.len))==1){
			return;
		}
		if(this.len>=flow.len){
			this.len=0;
			this.empty=1;
			return;
		}
		for(int i=this.len; i<flow.len; i++){
			this.ips[i] = 1 - flow.ips[i];
		}
		this.len = flow.len;
		return;
	}
	
	public static Flow getCompleteFlow(){
		Flow flow = new Flow();
		flow.empty=0;
		flow.len=0;
		return flow;
	}
	
	


	@Override
	public int compareTo(Flow f) {
		if(this.len==f.len && compareHelper(this, f, this.len)==0 && this.empty==f.empty && this.priority==f.priority){
			return 0;
		}
		if(this.priority > f.priority) return 1;
		return -1;
	}
	
	public String toString(){
		String ret="empty:" + empty + " len: "+len +" ";
		for(int i=0;i<this.len;i++){
			ret+=ips[i];
		}
		return ret;
	}

	public static void main(String[] args){
		Flow f1 = new Flow();
		f1.empty=0;
		Flow f2 = new Flow();
		f2.empty=0;
		Flow f = f1.overlap(f2);
		System.out.println(f);
	}
	
}
