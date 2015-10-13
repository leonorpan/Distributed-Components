package se.kth.ict.id2203.components.multipaxos;

import java.util.ArrayList;
import java.util.List;

import se.kth.ict.id2203.ports.fpl.FplDeliver;
import se.sics.kompics.address.Address;

public class AcceptMessage extends FplDeliver{
	
	Integer pts;
	List<Object> v;
	Integer pvm;
	Integer t;

	protected AcceptMessage(Address source,Integer pts,List<Object> v,Integer pvm,Integer t) {
		super(source);
		// TODO Auto-generated constructor stub
		this.pts=pts;
		this.v=v;
		this.pvm=pvm;
		this.t=t;
	}

	public Integer getPts() {
		return pts;
	}

	public void setPts(Integer pts) {
		this.pts = pts;
	}

	public List<Object> getV() {
		return v;
	}

	public void setV(List<Object> v) {
		this.v = v;
	}

	public Integer getPvm() {
		return pvm;
	}

	public void setPvm(Integer pvm) {
		this.pvm = pvm;
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
	private static final long serialVersionUID = -8073417488609069835L;

}
