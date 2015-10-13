package se.kth.ict.id2203.components.multipaxos;

import java.util.ArrayList;
import java.util.List;

public class ProposeObj {

	private Integer ts;
	private List<Object> suf;
	
	public ProposeObj(Integer ts, List<Object> suf){
		suf = new ArrayList<Object>();
		this.ts=ts;
		this.suf=suf;
	}

	public Integer getTs() {
		return ts;
	}

	public void setTs(Integer ts) {
		this.ts = ts;
	}

	public List<Object> getSuf() {
		return suf;
	}

	public void setSuf(List<Object> suf) {
		this.suf = suf;
	}
	
	
}
