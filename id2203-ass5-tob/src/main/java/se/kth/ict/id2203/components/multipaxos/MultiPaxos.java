package se.kth.ict.id2203.components.multipaxos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.ports.asc.AbortableSequenceConsensus;
import se.kth.ict.id2203.ports.asc.AscAbort;
import se.kth.ict.id2203.ports.asc.AscDecide;
import se.kth.ict.id2203.ports.asc.AscPropose;
import se.kth.ict.id2203.ports.fpl.FIFOPerfectPointToPointLink;
import se.kth.ict.id2203.ports.fpl.FplSend;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.address.Address;

public class MultiPaxos extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(MultiPaxos.class);

	private Negative<AbortableSequenceConsensus> asc = provides(AbortableSequenceConsensus.class);
	private Positive<FIFOPerfectPointToPointLink> fpl = requires(FIFOPerfectPointToPointLink.class);
	
	private MultiPaxosInit init;
	
	Set<Address> allAddresses;
	Address self;
	//logical clock
	Integer t;
	
	//prepared timestamp/acceptor
	Integer prepts;
	
	//acceptor: timestamp/accpted seq/length of decided seq
	Integer ats; //ats,av,al
	List<Object> av;
	Integer al;
	
	//proposer timestamp. proposed seq, length of learned seq
	//Integer[] pv; //pts,pv,pl
	Integer pts;
	List<Object> pv;
	Integer pl;
	
	//values proposed while preparing
	List<Object> proposedValues;
	
	//Map<Integer,ArrayList<Integer>> readList;
	Map<Address,ProposeObj> readList;
	
	Map<Address,Integer> accepted;
	
	Map<Address,Integer> decided;
	

	public MultiPaxos(MultiPaxosInit event) {
		this.init=event;
		logger.info("Constructing MultiPaxos component.");
		subscribe(handleStart,control);
		subscribe(handleAscPropose,asc);
		subscribe(handlePrepareMessage,fpl);
		subscribe(handleNackMessage, fpl);
		subscribe(handlePrepareAckMessage,fpl);
		subscribe(handleAcceptMessage,fpl);
		subscribe(handleAcceptAckMessage,fpl);
		subscribe(handleDecideMessage,fpl);
		
	}
	
	Handler<Start> handleStart = new Handler<Start>(){

		@Override
		public void handle(Start event) {
			// TODO Auto-generated method stub
			logger.info("Initializing multipaxos.... Start handler");
			allAddresses = new HashSet<Address>();
			//allAddresses.addAll(init.getAllAddresses());
			allAddresses=init.getAllAddresses();
			logger.info("Number of nodes is {}",allAddresses.size());
			self=init.getSelfAddress();
			
			t=0;
			prepts=0;
			ats =0;
			av = new ArrayList<Object>();
			al =0;
			pts=0;
			pv = new ArrayList<Object>();
			pl=0;
			proposedValues=new ArrayList<Object>();
			readList = new HashMap<Address,ProposeObj>();
			accepted = new HashMap<Address,Integer>();
			decided = new HashMap<Address,Integer>();
			logger.info("readlist size is {}",readList.size());
			logger.info("accepted size is {}",accepted.size());

		}
		
	};
	
	private List<Object> addToExistingList(List<Object> list,List<Object> globalList){
		List<Object> newList = new ArrayList<Object>();
		for (int i=0; i<globalList.size(); i++){
			newList.add(globalList.get(i));
		}
		for (int i=0; i<list.size(); i++){
			newList.add(list.get(i));
		}
		return  newList;
	}
	
	//upon event propose v
	Handler<AscPropose> handleAscPropose = new Handler<AscPropose>(){

		@Override
		public void handle(AscPropose event) {
			// TODO Auto-generated method stub
			logger.info("Handling asc propose event....");
			Object va = (Object) event.getValue();
			List<Object> value = new ArrayList<Object>();
			value.add((Object) va);
			logger.info("Proposal is {}",va);
			t++;
			if (pts.equals(0)){
				pts= (t*(allAddresses.size()))+self.getId();
				pv= prefix(av,al);
				pl=0;
				
				proposedValues.clear();
				proposedValues.add(event.getValue());
				readList.clear();
				accepted.clear();
				decided.clear();
				for (Address a:allAddresses){
					logger.info("Sending prepare message...");
					trigger(new FplSend(a,new PrepareMessage(self,pts,al,t)), fpl);
				}
			}else  if (readList.size()<=(allAddresses.size()/2)){
				proposedValues = addToExistingList(value,proposedValues);
			}else if (!pv.contains(va)){
				pv=addToExistingList(value, pv);
						//.addAll(value);
				for (Address a: allAddresses){
					if (readList.get(a)!=null){
						trigger(new FplSend(a, new AcceptMessage(self, pts, value, pv.size()-1,t)), fpl);
					}
				}
			}
		}
		
	};
	
	
	private List<Object> prefix(List<Object> l, Integer p){
		List<Object> temporary = new ArrayList<Object>();
		temporary=l;
		if (l.size()>p){
			temporary.subList(p, temporary.size()).clear();;
		}
		return temporary;
		
		
	}
	
	Handler<PrepareMessage> handlePrepareMessage = new Handler<PrepareMessage>(){

		@Override
		public void handle(PrepareMessage event) {
			logger.info("Handling prepare message");
			// TODO Auto-generated method stub
			t = getMax(t,event.getT())+1;
			if (event.getPts()<prepts){
				trigger(new FplSend(event.getSource(), new NackMessage(self, event.getPts(), t)), fpl);
			}else {
				prepts = event.getPts();
				trigger(new FplSend(event.getSource(), new PrepareAckMessage(self, event.getPts(), ats, suffix(av,event.getAl()), al, t)), fpl);
			}
		}		
	};
	
	private List<Object> suffix(List<Object> l, Integer k){
		List<Object> tem = new ArrayList<Object>();
		if (l.size()>k){
			for (int i = k; i < l.size(); i++) {
				tem.add(l.get(i));

			}
		}
		return tem;
	}
	
	private Integer getMax(Integer i, Integer j){
		if (i>=j){
			return i;
		}else {
			return j;
		}
	}
	
	Handler<NackMessage> handleNackMessage = new Handler<NackMessage>(){

		@Override
		public void handle(NackMessage event) {
			logger.info("Handling NACK message...");
			// TODO Auto-generated method stub
			t = getMax(t,event.getT())+1;
			if (pts.equals(event.getTs())){
				pts = 0;
				logger.info("Will ABORT NOW...");
				trigger(new AscAbort(), asc);
			}
		}
		
	};
	
	Handler<PrepareAckMessage> handlePrepareAckMessage = new Handler<PrepareAckMessage>(){

		@Override
		public void handle(PrepareAckMessage event) {
			// TODO Auto-generated method stub
			logger.info("EVERYTHING SHOULD BE OK UNTIL NOW!!!!");
			logger.info("Handling PrepareAckMessage....");
			t = getMax(t,event.getT())+1;
			if (pts.equals(event.getTs())){
				readList.put(event.getSource(), new ProposeObj(event.getAts(),event.getSuffixed()));
				decided.put(event.getSource(), event.getAl());
				//possible mistake with ==
				if (readList.size()==((allAddresses.size()/2)+1)){
					//ArrayList<ProposeObj> usuf = new ArrayList<ProposeObj>();
					ProposeObj o = new ProposeObj(0, new ArrayList<Object>());
					//usuf.add(o);
					//Iterator<?> it = readList.entrySet().iterator();
					for(Map.Entry<Address, ProposeObj> entry : readList.entrySet()){
					//while (it.hasNext()){
						//possible nullpointerexception
						logger.info("Iterating readList map size is {}",readList.size());
						//Map.Entry<Integer,ProposeObj> pair = (Map.Entry<Integer,ProposeObj>)it.next();
						Address key =(Address) entry.getKey();
						ProposeObj prop = (ProposeObj) entry.getValue();
						if ((o.getTs()<prop.getTs())||((o.getTs().equals(prop.getTs()))&&((o.getSuf().size())<(prop.getSuf().size())))){
							o.setTs(prop.getTs());
							o.setSuf(prop.getSuf());
						}
						//pv.add(o.getSuf());
						pv = addToExistingList(o.getSuf(), pv);
					}
					
					for (Object i:proposedValues){
						if (!pv.contains(i)){
							pv.add(i);
						}
					}
					for (Address a: allAddresses){
						if (!readList.isEmpty()){
							if (decided.containsKey(a)){
							Integer nal = decided.get(a);
							trigger(new FplSend(a, new AcceptMessage(self, pts, suffix(pv, nal), nal, t)), fpl);
						}
						}
					}
				}else if (readList.size()>((allAddresses.size()/2)+1)){
					trigger(new FplSend(event.getSource(), new AcceptMessage(self, pts, suffix(pv, event.getAl()), event.getAl(), t)), fpl);
					if (!pl.equals(0)){
						trigger(new FplSend(event.getSource(), new DecideMessage(self, pts, pl, t)), fpl);
					}
				}
				
			}
		}
		
	};
	
	Handler<AcceptMessage> handleAcceptMessage = new Handler<AcceptMessage>(){

		@Override
		public void handle(AcceptMessage event) {
			// TODO Auto-generated method stub
			logger.info("Handling Accept message...");
			t = getMax(t,event.getT())+1;
			if (!event.getPts().equals(prepts)){
				trigger(new FplSend(event.getSource(), new NackMessage(self, event.getPts(), t)), fpl);
			}else {
				ats = event.getPts();
				if (event.getPvm()<av.size()){
					av = prefix(av, event.getPvm());
				}
				logger.info("AV size is now {}",av.size());
				av = addToExistingList(event.getV(), av);
				logger.info("AV size AFTER is now {}",av.size());
				trigger(new FplSend(event.getSource(), new AcceptAckMessage(self, event.getPts(), av.size(), t)), fpl);
			}
		}
		
	};
	
	Handler<AcceptAckMessage> handleAcceptAckMessage = new Handler<AcceptAckMessage>(){

		@Override
		public void handle(AcceptAckMessage event) {
			// TODO Auto-generated method stub
			logger.info("Handling accept ack message from {}",event.getSource().getId());
			t = getMax(t,event.getT())+1;
			if (event.getTs().equals(pts)){
				accepted.put(event.getSource(), event.getAv());
				if ((pl<event.getAv())&&(getHighValues(event.getAv())>(allAddresses.size()/2))){
					pl = event.getAv();
					for (Address a:allAddresses){
						if (readList.get(a)!=null){
							logger.info("Triggering decide message...");
							trigger(new FplSend(a, new DecideMessage(self, pts, pl, t)), fpl);
						}
					}
				}
			}
		}
		
		//return all the values of acc that are higher than l
		private Integer getHighValues(Integer l){
			List<Integer> temp = new ArrayList<Integer>();
			for (Address a:allAddresses){
				Integer acc = accepted.get(a);
				if ((acc!=null)&&(acc>=l)){
					temp.add(a.getId());
				}
			}
			logger.info("temp size is {}",temp.size());
			return temp.size();
		}
		
	};
	
	Handler<DecideMessage> handleDecideMessage = new Handler<DecideMessage>(){

		@Override
		public void handle(DecideMessage event) {
			// TODO Auto-generated method stub
			logger.info("Handling decide message....");
			t = getMax(t,event.getT())+1;
			if (event.getPts().equals(prepts)){
				while (al<event.getPl()){
					trigger(new AscDecide(av.get(al)), asc);
					al++;
				}
			}
			
		}
		
	};
}
