package se.kth.ict.id2203.components.multipaxos;

import java.util.ArrayList;
import java.util.List;

import se.kth.ict.id2203.ports.fpl.FplDeliver;
import se.sics.kompics.address.Address;

public class PrepareAckMessage extends FplDeliver{
	
	Integer ts;
	Integer ats;
	List<Object> suffixed;
	Integer al;
	Integer t;
	

	protected PrepareAckMessage(Address source,Integer ts,Integer ats,List<Object> suffixed,Integer al, Integer t) {
		super(source);
		// TODO Auto-generated constructor stub
		this.ts=ts;
		this.ats=ats;
		this.suffixed=suffixed;
		this.al=al;
		this.t=t;
	}

	public Integer getTs() {
		return ts;
	}

	public void setTs(Integer ts) {
		this.ts = ts;
	}

	public Integer getAts() {
		return ats;
	}

	public void setAts(Integer ats) {
		this.ats = ats;
	}

	public List<Object> getSuffixed() {
		return suffixed;
	}

	public void setSuffixed(List<Object> suffixed) {
		this.suffixed = suffixed;
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
	private static final long serialVersionUID = -374423557948437340L;

}
