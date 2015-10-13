package se.kth.ict.id2203.components.epfd;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.ports.epfd.EventuallyPerfectFailureDetector;
import se.kth.ict.id2203.ports.epfd.Restore;
import se.kth.ict.id2203.ports.epfd.Suspect;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.address.Address;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

public class Epfd extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(Epfd.class);
	private Positive<Timer> timer = requires(Timer.class);

	private Negative<EventuallyPerfectFailureDetector> epfd = provides(EventuallyPerfectFailureDetector.class);
	private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);

	private Set<Address> suspected;
	private Address self;
	private Set<Address> alive;
	private long delay;
	private long deltaDelay;
	private int seqNumber;
	private EpfdInit init;

	public Epfd(EpfdInit init) {
		this.init = init;
		subscribe(handleStart, control);
		subscribe(handleTimeout, timer);
		subscribe(handleHeartbeatRequest, pp2p);
		subscribe(handleHeartbeatReply, pp2p);
	}

	Handler<Start> handleStart = new Handler<Start>() {

		@Override
		public void handle(Start event) {
			alive = new HashSet<Address>(init.getAllAddresses());
			self = init.getSelfAddress();
			delay = init.getInitialDelay();
			deltaDelay = init.getDeltaDelay();
			alive.remove(self);
			suspected = new HashSet<Address>();
			seqNumber = 0;

			startTimer();
		}
	};

	private Handler<CheckTimeout> handleTimeout = new Handler<CheckTimeout>() {
		@Override
		public void handle(CheckTimeout event) {
			Set<Address> tempAlive = new HashSet<Address>(alive);
			Set<Address> tempSuspected = new HashSet<Address>(suspected);
			tempAlive.retainAll(tempSuspected);
			if (!tempAlive.isEmpty()) {
				delay = delay + deltaDelay;
				logger.info("The delay is now... "+delay);
			}
			seqNumber++;
			for (Address nodeAddress : init.getAllAddresses()) {
				if (!alive.contains(nodeAddress) && !suspected.contains(nodeAddress)) {
					suspected.add(nodeAddress);
					Suspect suspect = new Suspect(nodeAddress);
					trigger(suspect, epfd);

				} else if (alive.contains(nodeAddress)
						&& suspected.contains(nodeAddress)) {
					suspected.remove(nodeAddress);
					Restore restore = new Restore(nodeAddress);
					trigger(restore, epfd);

				}
				HeartbeatRequestMessage request = new HeartbeatRequestMessage(
						self, seqNumber);
				trigger(new Pp2pSend(nodeAddress, request), pp2p);
			}
			alive.clear();
			startTimer();
		}
	};
	private Handler<HeartbeatRequestMessage> handleHeartbeatRequest = new Handler<HeartbeatRequestMessage>() {
		@Override
		public void handle(HeartbeatRequestMessage event) {
			trigger(new Pp2pSend(event.getSource(), new HeartbeatReplyMessage(
					self, event.getSn())), pp2p);
		}

	};
	
	private Handler<HeartbeatReplyMessage> handleHeartbeatReply = new Handler<HeartbeatReplyMessage>() {
		@Override
		public void handle(HeartbeatReplyMessage event) {

			if((event.getSn() == seqNumber) || (suspected.contains(event.getSource()))){
				alive.add(event.getSource());
				
			}
		}

	};

	private void startTimer() {
		ScheduleTimeout stt = new ScheduleTimeout(delay);
		stt.setTimeoutEvent(new CheckTimeout(stt));
		trigger(stt, timer);
	}

}