package se.kth.ict.id2203.components.multipaxos;

import se.kth.ict.id2203.ports.fpl.FplDeliver;
import se.sics.kompics.address.Address;

public class NackMessage extends FplDeliver{
	Integer ts;
	Integer t;

	protected NackMessage(Address source,Integer ts,Integer t) {
		super(source);
		// TODO Auto-generated constructor stub
		this.ts=ts;
		this.t=t;
	}

	public Integer getTs() {
		return ts;
	}

	public void setTs(Integer ts) {
		this.ts = ts;
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
	private static final long serialVersionUID = -2908411933488841251L;

}
