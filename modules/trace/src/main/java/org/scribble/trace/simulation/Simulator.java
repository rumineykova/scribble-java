/*
 * Copyright 2009-14 www.scribble.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.scribble.trace.simulation;

import org.scribble.trace.model.RoleSimulator;
import org.scribble.trace.model.Role;
import org.scribble.trace.model.Step;
import org.scribble.trace.model.Trace;

/**
 * This class represents a simulator.
 *
 */
public class Simulator {
	
	private java.util.Set<SimulationListener> _listeners=new java.util.HashSet<SimulationListener>();

	/**
	 * This method registers a simulation listener.
	 * 
	 * @param l The simulation listener
	 */
	public void addSimulationListener(SimulationListener l) {
		synchronized (_listeners) {
			_listeners.add(l);
		}
	}
	
	/**
	 * This method unregisters a simulation listener.
	 * 
	 * @param l The simulation listener
	 */
	public void removeSimulationListener(SimulationListener l) {
		synchronized (_listeners) {
			_listeners.remove(l);
		}
	}
	
	/**
	 * This method initiates the simulation of a supplied trace.
	 * 
	 * @param context The context
	 * @param trace The trace
	 * @return Whether the simulation was successful
	 */
	public boolean simulate(SimulatorContext context, Trace trace) {
		boolean ret=true;
		java.util.Map<String, RoleSimulator> sims=new java.util.HashMap<String, RoleSimulator>();
		
		// Initialize the role simulators
		for (Role role : trace.getRoles()) {			
			if (role.getSimulator() != null) {
				role.getSimulator().init(context);
				
				sims.put(role.getName(), role.getSimulator());
			}
		}

		for (SimulationListener l : _listeners) {
			l.start(trace);
		}


		for (Step step : trace.getSteps()) {
			for (SimulationListener l : _listeners) {
				l.start(trace, step);
			}
			
			if (step.simulate(context, sims)) {
				for (SimulationListener l : _listeners) {
					l.successful(trace, step);
				}
			} else {
				ret = false;
				
				for (SimulationListener l : _listeners) {
					l.failed(trace, step);
				}
			}
		}
			
		for (SimulationListener l : _listeners) {
			l.stop(trace);
		}

		// Close the role simulators
		for (Role role : trace.getRoles()) {			
			if (role.getSimulator() != null) {
				role.getSimulator().close(context);
			}
		}
		
		return (ret);
	}
}
