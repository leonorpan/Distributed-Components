package se.kth.ict.id2203.components.paxos;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class PrepareMessage extends BebDeliver{
	
	private Integer pts;
	private Integer t;

	public PrepareMessage(Address source,Integer proposerTimestamp, Integer logicalClock) {
		super(source);
		// TODO Auto-generated constructor stub
		this.pts=proposerTimestamp;
		this.t = logicalClock;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1551419783218735716L;

	public Integer getPts() {
		return pts;
	}

	public void setPts(Integer pts) {
		this.pts = pts;
	}

	public Integer getT() {
		return t;
	}

	public void setT(Integer t) {
		this.t = t;
	}

}
