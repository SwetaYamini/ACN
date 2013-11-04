package timestamp;

import java.util.HashMap;
import java.util.Iterator;

public class Replicator extends Thread {

		public static HashMap<Integer, Utilization> utilizations;
		public static int[] timestamp;
		
		public Replicator(){
			timestamp = new int[Configuration.NDOMAINS];
			
			utilizations = new HashMap<Integer, Utilization>();
			Iterator<Integer> it = Network.links.keySet().iterator();
			//System.out.println("Initializing utilizations");
			while(it.hasNext()){
				int linkid = it.next();
				//System.out.println("putting " + linkid);
				utilizations.put(linkid, new Utilization(linkid));
			}
		}
		
		public static String getUtilizations(){
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
		
		public static String getUtilizations2(){
			String ret="";
			Iterator<Integer> it = utilizations.keySet().iterator();
			//System.out.print("Utilizations: time="+time+", ");
			int first=1;
			while(it.hasNext()){
				if(first==0) ret += ", ";
				Utilization util = utilizations.get(it.next());
				ret+=util.utilization;
				first=0;
			}
			return ret;
		}
		
		public static String getTimestamp(){
			String ret = "[";
			int first=1;
			for(int i=0; i< timestamp.length;i++){
				if(first==0) ret += ", ";
				ret+= timestamp[i];
				first=0;
			}
			return ret+"]";
		}
		
		public static void replicate(){
			Iterator<Controller> it = Network.controllers.values().iterator();
			while(it.hasNext()){
				it.next().replicateUtilizations();
			}
		}
}
