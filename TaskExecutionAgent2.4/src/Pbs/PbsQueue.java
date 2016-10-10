package Pbs;

public class PbsQueue implements Comparable<PbsQueue> {
	private String name;
	private int priority;
	
	public PbsQueue(String n, int p) {
		this.name = n;
		this.priority = p;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public int compareTo(PbsQueue other) {
		return this.priority - other.priority;
	}

}
