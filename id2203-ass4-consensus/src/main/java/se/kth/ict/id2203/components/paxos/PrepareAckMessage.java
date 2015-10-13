package se.kth.ict.id2203.components.paxos;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class PrepareAckMessage extends Pp2pDeliver{
	
	private Integer ats;
	private Integer av;
	private Integer ts;
	private Integer t;
	

	protected PrepareAckMessage(Address source,Integer ats,Integer av, Integer ts, Integer t) {
		super(source);
		// TODO Auto-generated constructor stub
		this.ats=ats;
		this.av=av;
		this.ts=ts;
		this.t=t;
	}

	public Integer getAts() {
		return ats;
	}

	public void setAts(Integer ats) {
		this.ats = ats;
	}

	public Integer getAv() {
		return av;
	}

	public void setAv(Integer av) {
		this.av = av;
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
	private static final long serialVersionUID = -3101569120524809550L;

}
