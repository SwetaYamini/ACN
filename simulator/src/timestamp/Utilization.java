package timestamp;

public class Utilization {
	int link;
	int rate;
	int utilization;
	
	public Utilization(int link){
		this.link = link;
		rate=0;
		utilization=0;
	}
	
	public void update(int rate){
		this.rate += rate;
		this.utilization = 100*this.rate/Network.links.get(link).capacity;
	}
	
	public void update(Utilization update){
		if(link==update.link){
			rate = update.rate;
			utilization = update.utilization;
		}
	}
}
