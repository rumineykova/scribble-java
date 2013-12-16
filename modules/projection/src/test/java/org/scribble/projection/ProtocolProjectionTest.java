/*
 * Copyright 2009 www.scribble.org
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
package org.scribble.projection;

import static org.junit.Assert.*;

import java.util.Map;

import org.scribble.model.Module;
import org.scribble.parser.ProtocolParser;
import org.scribble.parser.ProtocolModuleLoader;
import org.scribble.common.logging.ConsoleScribbleLogger;
import org.scribble.common.resources.DirectoryResourceLocator;
import org.scribble.common.resources.InputStreamResource;

public class ProtocolProjectionTest {

	@org.junit.Test
    public void testGProtocolDefinition() {
    	testParser("GProtocolDefinition");
    }
	
	@org.junit.Test
    public void testGProtocolInstance() {
    	testParser("GProtocolInstance");
    }
	
	@org.junit.Test
    public void testGChoice() {
    	testParser("GChoice");
    }
	
	@org.junit.Test
    public void testGRecursion() {
    	testParser("GRecursion");
    }
	
	@org.junit.Test
    public void testGParallel() {
    	testParser("GParallel");
    }
	
	@org.junit.Test
    public void testGInterruptible() {
    	testParser("GInterruptible");
    }
	
	@org.junit.Test
    public void testGDo() {
    	testParser("GDo");
    }
	
    protected void testParser(String name) {
    	
    	try {
    		String path="scribble/examples/"+name+".scr";
    		
    		java.net.URL scrurl=ClassLoader.getSystemResource(path);
    		java.io.File scrFile=new java.io.File(scrurl.getFile());
    		java.io.InputStream is=new java.io.FileInputStream(scrFile);
    		
    		java.net.URL url=ClassLoader.getSystemResource("scribble");
    		java.io.File f=new java.io.File(url.getFile());
    		
    		ProtocolParser pp=new ProtocolParser();
    		
    		TestIssueLogger logger=new TestIssueLogger();
    		
    		DirectoryResourceLocator locator=new DirectoryResourceLocator(f.getParentFile().getAbsolutePath());
    		
    		ProtocolModuleLoader loader=new ProtocolModuleLoader(pp, locator, logger);
    		
    		InputStreamResource isr=new InputStreamResource(path, scrFile.getAbsolutePath(), is);
    		
    		Module module=pp.parse(isr, loader, logger);
    		
    		if (module == null) {
    			fail("Module is null");
    		}
    		
    		if (logger.isErrorsOrWarnings()) {
    			fail("Unexpected errors and/or warnings in "+name+".scr");
    		}
    		
    		ProtocolProjector projector=new ProtocolProjector();
    		
    		Module projected=projector.project(isr, module, loader, logger);
    		
    		is = ClassLoader.getSystemResourceAsStream("scribble/results/"+name+".scr");
    		
    		byte[] b=new byte[is.available()];
    		is.read(b);
    		
    		is.close();
    		
    		String projd=projected.toString().trim();
    		String expecting=new String(b).trim();
    		
    		if (!projd.equals(expecting)) {
    			int len=projd.length();
    			if (len > expecting.length()) {
    				len = expecting.length();
    			}
    			for (int i=0; i < len; i++) {
    				if (projd.charAt(i) != expecting.charAt(i)) {
    					System.out.println("DIFF AT POSITION: "+i);
    					int showto=i+30;
    					if (i+10 >= len) {
    						showto = len;
    					}    					
    					System.out.println("PROJECTED: "+projd.substring(i, showto));
    					System.out.println("EXPECT: "+expecting.substring(i, showto));
    					break;
    				}
    			}
    			
    			System.err.println("Projected protocol '"+name+
    					"' mismatch\nExpecting:\n"+expecting+"\nProjected:\n"+projd);
    			fail("Projected protocol '"+name+"' mismatch");
    		}
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		fail("Failed to project '"+name+".scr'");
    	}
    }
    
    protected class TestIssueLogger extends ConsoleScribbleLogger {
    	
    	private java.util.List<String> _errors=new java.util.ArrayList<String>();
    	private java.util.List<String> _warnings=new java.util.ArrayList<String>();

		public void error(String issue, Map<String, Object> props) {
			super.error(issue, props);
			_errors.add(issue);
		}

		public void warning(String issue, Map<String, Object> props) {
			super.warning(issue, props);
			_warnings.add(issue);
		}

		public void info(String issue, Map<String, Object> props) {
			super.info(issue, props);
		}
    	
		public boolean isErrorsOrWarnings() {
			return (_errors.size() > 0 || _warnings.size() > 0);
		}
    }
}