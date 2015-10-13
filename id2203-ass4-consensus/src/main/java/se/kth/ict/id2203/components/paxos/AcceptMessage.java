package se.kth.ict.id2203.components.paxos;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class AcceptMessage extends BebDeliver{
	
	private Integer pts;
	private Integer pv;
	private Integer t;
 
	public AcceptMessage(Address source,Integer pts, Integer pv, Integer t) {
		super(source);
		// TODO Auto-generated constructor stub
		this.pts=pts;
		this.pv=pv;
		this.t=t;
	}
	

	public Integer getPts() {
		return pts;
	}


	public void setPts(Integer pts) {
		this.pts = pts;
	}


	public Integer getPv() {
		return pv;
	}


	public void setPv(Integer pv) {
		this.pv = pv;
	}


	public Integer getT() {
		return t;
	}


	public void setT(Integer t) {
		this.t = t;
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 4077684345730752971L;

}
