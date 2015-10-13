package se.kth.ict.id2203.components.multipaxos;

import se.kth.ict.id2203.ports.fpl.FplDeliver;
import se.sics.kompics.address.Address;

public class AcceptAckMessage extends FplDeliver{
	
	private Integer ts;
	private Integer av;
	private Integer t;

	protected AcceptAckMessage(Address source,Integer ts,Integer av, Integer t) {
		super(source);
		// TODO Auto-generated constructor stub
		this.ts=ts;
		this.av=av;
		this.t=t;
	}

	public Integer getTs() {
		return ts;
	}

	public void setTs(Integer ts) {
		this.ts = ts;
	}

	public Integer getAv() {
		return av;
	}

	public void setAv(Integer av) {
		this.av = av;
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
	private static final long serialVersionUID = 2127201910987197066L;

}
