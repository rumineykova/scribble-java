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
package org.scribble.trace.simulation.junit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.codehaus.jackson.map.ObjectMapper;
import org.scribble.resources.DirectoryResourceLocator;
import org.scribble.resources.ResourceLocator;
import org.scribble.trace.model.Step;
import org.scribble.trace.model.Trace;
import org.scribble.trace.simulation.DefaultSimulatorContext;
import org.scribble.trace.simulation.SimulationListener;
import org.scribble.trace.simulation.Simulator;
import org.scribble.trace.simulation.SimulatorContext;

/**
 * This class performs simulation of one or more trace files, and
 * records the output in a JUnit XML file format.
 *
 */
public class JUnitSimulator {
	
	private static final String TRACE = ".trace";
	private org.w3c.dom.Document _junitDoc;
	private org.w3c.dom.Element _currentTestSuite;
	
	private java.io.File _junitFile;
	
	private ResourceLocator _locator;
	
	private int _id=0;
	
	private static final ObjectMapper MAPPER=new ObjectMapper();

	/**
	 * This main method is invoked with the path of a single
	 * trace file, or a folder containing zero or more trace
	 * files.
	 * 
	 * @param args The path
	 */
	public static void main(String args[]) {
		if (args.length != 2) {
			System.err.println("Usage: JUnitSimulator path junitXmlFile");
			System.exit(1);
		}
		
		if (System.getProperty("MODULE_PATH") == null) {
			System.err.println("'MODULE_PATH' envionment parameter has not been set");
			System.exit(2);
		}
		
		JUnitSimulator sim=new JUnitSimulator();
		
		try {
			DirectoryResourceLocator locator=new DirectoryResourceLocator(System.getProperty("MODULE_PATH"));
			
			sim.setResourceLocator(locator);
			
			sim.simulate(args[0], args[1]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * This method sets the resource locator.
	 * 
	 * @param locator The resource locator
	 */
	public void setResourceLocator(ResourceLocator locator) {
		_locator = locator;
	}
	
	/**
	 * This method returns the resource locator.
	 * 
	 * @return The resource locator
	 */
	public ResourceLocator getResourceLocator() {
		return (_locator);
	}
	
	/**
	 * This method simulates the trace file located at the specified
	 * path, or the zero or more trace files, if a directory is specified.
	 * The results are recorded in the junit xml schema format in the
	 * file specified.
	 * 
	 * @param path The trace(s) location
	 * @param xmlFile The xml file to record the results
	 * @throws Exception Failed to simulate
	 */
	public void simulate(String path, String xmlFile) throws Exception {
		
		// Initialize the junit output file
		_junitFile = new java.io.File(xmlFile);
		
		if (_junitFile.getParentFile().exists() == false) {
			_junitFile.getParentFile().mkdirs();
		}
		
		// Initialize the junit result DOM
		DocumentBuilder builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
		_junitDoc = builder.newDocument();
		
		org.w3c.dom.Element testsuites=_junitDoc.createElement("testsuites");
		_junitDoc.appendChild(testsuites);
		
		updateResultFile();
		
		// Traverse path locating any trace files
		java.io.File root=new java.io.File(path);
		
		scanForTraceFiles(root);
	}
	
	/**
	 * This method recursively processes a directory hierarchy locating 'trace'
	 * files, which are the simulated to produce results in the output file.
	 * 
	 * @param file The file
	 * @throws Exception Failed to simulate
	 */
	protected void scanForTraceFiles(java.io.File file) throws Exception {
		
		if (file.isDirectory()) {
			java.io.File[] children=file.listFiles();
			
			for (java.io.File f : children) {
				scanForTraceFiles(f);
			}
		} else if (file.isFile() && file.getName().endsWith(TRACE)) {
			simulateTrace(file);
		}
	}
	
	/**
	 * This method simulates the supplied trace file.
	 * 
	 * @param traceFile The trace file
	 * @throws Exception Failed to simulate trace
	 */
	protected void simulateTrace(java.io.File traceFile) throws Exception {
		java.io.InputStream is=new java.io.FileInputStream(traceFile);

		Trace trace=MAPPER.readValue(is, Trace.class);

		is.close();

		SimulatorContext context=new DefaultSimulatorContext(_locator);

		Simulator simulator=new Simulator();
		
		SimulationListener l=new JUnitSimulationListener();
		
		simulator.addSimulationListener(l);

		try {
			simulator.simulate(context, trace);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		simulator.removeSimulationListener(l);
	}
	
	/**
	 * This method initialixes the result file.
	 * 
	 * @param file The file
	 * @throws Exception Failed to initialize
	 */
	public static void initResultsFile(java.io.File file) throws Exception {
		// Initialize the junit result DOM
		DocumentBuilder builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
		org.w3c.dom.Document doc = builder.newDocument();
		
		org.w3c.dom.Element testsuites=doc.createElement("testsuites");
		doc.appendChild(testsuites);
		
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		
		if (!file.exists()) {
			file.createNewFile();
		}

		updateResultFile(file, doc);
	}
	
	/**
	 * This method updates the results.
	 * 
	 * @throws Exception Failed to update
	 */
	protected void updateResultFile() throws Exception {
		updateResultFile(_junitFile, _junitDoc);
	}
	
	/**
	 * This method updates the results from the junit document into the output
	 * (result) file.
	 * 
	 * @param file The file
	 * @param results The results
	 * @throws Exception Failed to update the result file
	 */
	public static void updateResultFile(java.io.File file, org.w3c.dom.Document results) throws Exception {
		java.io.FileOutputStream fos=new java.io.FileOutputStream(file);
		
		javax.xml.transform.dom.DOMSource source=new javax.xml.transform.dom.DOMSource(results);
		javax.xml.transform.stream.StreamResult result=new javax.xml.transform.stream.StreamResult(fos);
		
		javax.xml.transform.Transformer transformer=
				javax.xml.transform.TransformerFactory.newInstance().newTransformer();
		
		transformer.transform(source, result);
		
		fos.flush();
		
		fos.close();
	}
	
	/**
	 * JUnit implementation of the simulation listener interface, used
	 * to record the intermediate and final results from simulating the
	 * trace files.
	 *
	 */
	public class JUnitSimulationListener implements SimulationListener {

		private long _stepStart;
		
		/**
		 * {@inheritDoc}
		 */
		public void start(Trace trace) {
			String name=trace.getName();
			
			_currentTestSuite = _junitDoc.createElement("testsuite");
			_currentTestSuite.setAttribute("name", name);
			_currentTestSuite.setAttribute("id", ""+(_id++));
			
			_junitDoc.getDocumentElement().appendChild(_currentTestSuite);
			
			try {
				updateResultFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public void start(Trace trace, Step step) {
			_stepStart = System.currentTimeMillis();
		}
		
		/**
		 * {@inheritDoc}
		 */
		public void successful(Trace trace, Step step) {
			createTestCase(step);

			try {
				updateResultFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * This method creates a testcase entry for the supplied step.
		 * 
		 * @param step The step being simulated
		 */
		protected org.w3c.dom.Element createTestCase(Step step) {
			org.w3c.dom.Element testcase=_junitDoc.createElement("testcase");
			testcase.setAttribute("name", step.toString());
			
			long timeTaken=System.currentTimeMillis()-_stepStart;
			timeTaken /= 1000;
			
			testcase.setAttribute("time", ""+timeTaken);
			
			_currentTestSuite.appendChild(testcase);
			
			return (testcase);
		}

		/**
		 * {@inheritDoc}
		 */
		public void failed(Trace trace, Step step) {
			org.w3c.dom.Element testcase=createTestCase(step);
			
			org.w3c.dom.Element error=_junitDoc.createElement("error");
			error.setAttribute("type", "Unexpected message");
			testcase.appendChild(error);
			
			org.w3c.dom.Text desc=_junitDoc.createTextNode("Unexpected step: "+step);
			error.appendChild(desc);
			
			try {
				updateResultFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public void stop(Trace trace) {
			_currentTestSuite = null;

			try {
				updateResultFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}			
	};
	

}
