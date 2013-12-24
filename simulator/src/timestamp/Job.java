package timestamp;

import swiconsim.swim.JobType;

public class Job implements Comparable{
	int jobid;
	long bytes;
	JobType type;
	int time;
	int src;
	int dst;
	public Flow flow;
	
	public Job(int jobid, long bytes, JobType type, int time, int src, int dst, Flow flow){
		this.jobid = jobid;
		this.bytes = bytes;
		this.type = type;
		this.time = time;
		this.src = src;
		this.dst = dst;
		this.flow = flow;
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