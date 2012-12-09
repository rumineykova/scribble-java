/*
 * Copyright 2009 www.scribble.org
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
package org.scribble.protocol.validation.rules;

import java.util.Map;

import org.scribble.common.logging.Journal;

import static org.junit.Assert.*;

@org.junit.Ignore
public class TestScribbleLogger implements Journal {

    public void debug(String issue, Map<String, Object> props) {
    }

    public void error(String issue, Map<String, Object> props) {
        m_errors.add(issue);
    }
    
    /**
     * This method verifies the supplied list of errors,
     * not expecting any other errors to have occurred.
     * 
     * @param errors The errors
     */
    public void verifyErrors(String[] errors) {
    	verifyErrors(errors, true);
    }

    /**
     * This method verifies the supplied errors. If the 'completeList'
     * flag is true, then the supplied list of errors is the complete
     * list of expected errors, so any additional errors should be
     * flagged as an error.
     * 
     * @param errors The list of errors
     * @param completeList Whether this is a complete list
     */
    public void verifyErrors(String[] errors, boolean completeList) {
        java.util.List<String> logged=new java.util.Vector<String>(m_errors);
        
        for (int i=0; i < errors.length; i++) {
            int ind=logged.indexOf(errors[i]);
            
            if (ind == -1) {
                fail("Expected message '"+errors[i]+"' did not occur");
            } else {
                logged.remove(ind);
            }
        }
        
        if (completeList && logged.size() > 0) {
            fail("Unexpected errors occurred: "+logged);
        }
    }

    public void info(String issue, Map<String, Object> props) {
    }

    public void trace(String issue, Map<String, Object> props) {
    }

    public void warning(String issue, Map<String, Object> props) {
    }

    private java.util.List<String> m_errors=new java.util.Vector<String>();
}