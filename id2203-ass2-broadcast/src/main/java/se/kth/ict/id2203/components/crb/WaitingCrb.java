/**
 * This file is part of the ID2203 course assignments kit.
 * 
 * Copyright (C) 2009-2013 KTH Royal Institute of Technology
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package se.kth.ict.id2203.components.crb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.pa.broadcast.CrbMessage;
import se.kth.ict.id2203.ports.crb.CausalOrderReliableBroadcast;
import se.kth.ict.id2203.ports.crb.CrbBroadcast;
import se.kth.ict.id2203.ports.crb.CrbDeliver;
import se.kth.ict.id2203.ports.rb.RbBroadcast;
import se.kth.ict.id2203.ports.rb.RbDeliver;
import se.kth.ict.id2203.ports.rb.ReliableBroadcast;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.address.Address;

public class WaitingCrb extends ComponentDefinition {

	private static final Logger logger = LoggerFactory
			.getLogger(WaitingCrb.class);

	private WaitingCrbInit init;
	private Set<Address> allAddresses;
	private Address selfAddress;
	private int[] vector;
	private Set<CrbClone> pendingMessages;
	private Integer lsn;

	Negative<CausalOrderReliableBroadcast> corb = provides(CausalOrderReliableBroadcast.class);
	Positive<ReliableBroadcast> rb = requires(ReliableBroadcast.class);

	public WaitingCrb(WaitingCrbInit init) {
		this.init = init;
		subscribe(handleStart, control);
		subscribe(handleCrbBroadcast, corb);
		subscribe(handleCrbDataMessage, rb);
	}

	Handler<Start> handleStart = new Handler<Start>() {

		@Override
		public void handle(Start event) {
			// TODO Auto-generated method stub
			allAddresses = init.getAllAddresses();
			selfAddress = init.getSelfAddress();

			vector = new int[init.getAllAddresses().size()];
            for (int i = 0; i < vector.length; i++) {
            	vector[i] = 0;
            }
			lsn = 0;
			pendingMessages = new LinkedHashSet<CrbClone>();
		}

	};

	Handler<CrbBroadcast> handleCrbBroadcast = new Handler<CrbBroadcast>() {

		@Override
		public void handle(CrbBroadcast event) {
			// TODO Auto-generated method stub
			int[] lv = new int[vector.length];
			for (int i = 0; i < vector.length; i++) {
                lv[i] = vector[i];
		}
			lv[selfAddress.getId() - 1] = lsn;
			lsn++;
			logger.info("Vector size is: {}",lv.length);
			logger.info("Will  causally broadcast message {}&&&&&&&&&&&&&&&&&&&"+lsn);
			trigger(new RbBroadcast(new CrbDataMessage(init.getSelfAddress(),
					lv, event.getDeliverEvent())), rb);

		}

	};

	Handler<CrbDataMessage> handleCrbDataMessage = new Handler<CrbDataMessage>() {

		@Override
		public void handle(CrbDataMessage event) {
			// TODO Auto-generated method stub
			logger.info("AM I HERE, INSIDE CRBMESSAGE");
			 CrbClone crbObj = new CrbClone(event.getSource(), event.getVector(), event.getDeliver());
			pendingMessages.add(crbObj);
			deliverHandling();
		}

		private void deliverHandling() {
			while (true) {
                Iterator<CrbClone> iterator = new LinkedHashSet<CrbClone>(
                        pendingMessages).iterator();
                boolean found = false;
                while (iterator.hasNext()) {
                	CrbClone other = iterator.next();
                   logger.info("Process " + selfAddress.getId()
                            + " comparing "
                            + Arrays.toString(other.getVector()) + " and "
                            + Arrays.toString(vector));
                    if (canDeliver(other.getVector(), vector)) {
                        pendingMessages.remove(other);
                        vector[other.getSource().getId() - 1] = vector[other
                                .getSource().getId() - 1] + 1;
                        logger.info("Process " + selfAddress.getId()
                                + " delivering "
                                + Arrays.toString(other.getVector()));
                        trigger(other.getDeliver(), corb);
                        found = true;
                    }
                }
                if (!found) {
                    return;
                }
            }
		}
		
		 private boolean canDeliver(int[] other, int[] vector) {
	            for (int i = 0; i < vector.length; i++) {
	                if (other[i] <= vector[i])
	                    continue;
	                return false;
	            }
	            return true;
	        }



	};

}
