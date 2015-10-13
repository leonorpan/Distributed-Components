package se.kth.ict.id2203.components.multipaxos;

import se.kth.ict.id2203.ports.fpl.FplDeliver;
import se.sics.kompics.address.Address;

public class PrepareMessage extends FplDeliver{
	
	Integer pts;
	Integer al;
	Integer t;

	protected PrepareMessage(Address source,Integer pts, Integer al, Integer t) {
		super(source);
		// TODO Auto-generated constructor stub
		this.pts=pts;
		this.al=al;
		this.t=t;
	}

	public Integer getPts() {
		return pts;
	}

	public void setPts(Integer pts) {
		this.pts = pts;
	}

	public Integer getAl() {
		return al;
	}

	public void setAl(Integer al) {
		this.al = al;
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
	private static final long serialVersionUID = 1172872392613517673L;

}
