package swiconsim.swim;
import swiconsim.packet.*;

public class Job implements Comparable{
	int jobid;
	long bytes;
	JobType type;
	long time;
	int src;
	int dst;
	public Packet packet;
	
	public Job(int jobid, long bytes, JobType type, long time, int src, int dst, Packet packet){
		this.jobid = jobid;
		this.bytes = bytes;
		this.type = type;
		this.time = time;
		this.src = src;
		this.dst = dst;
		this.packet = packet;
	}


	@Override
	public int compareTo(Object other) {
		Job job = (Job) other;
		if(job.time == this.time) return 0;
		if(job.time < this.time) return 1;
		return -1;
	}
	
	public String toString(){
		return "["+jobid +": "+bytes+": "+time+ ": " + src + "-" + dst +"]";
	}
}