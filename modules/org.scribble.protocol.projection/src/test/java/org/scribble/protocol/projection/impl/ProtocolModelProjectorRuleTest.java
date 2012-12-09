/*
 * Copyright 2009-11 www.scribble.org
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
package org.scribble.protocol.projection.impl;

import static org.junit.Assert.*;

import java.text.MessageFormat;

import org.scribble.common.logging.CachedJournal;
import org.scribble.common.logging.CachedJournal.IssueDetails;
import org.scribble.protocol.model.Block;
import org.scribble.protocol.model.Choice;
import org.scribble.protocol.model.Protocol;
import org.scribble.protocol.model.ProtocolModel;
import org.scribble.protocol.model.RecBlock;
import org.scribble.protocol.model.Role;
import org.scribble.protocol.model.Introduces;

public class ProtocolModelProjectorRuleTest {

    @org.junit.Test
    public void projectUnknownRole() {
        ProtocolModel pm=new ProtocolModel();
        
        Protocol p=new Protocol();
        
        Introduces rl=new Introduces();
        
        Role r=new Role();
        r.setName("role");
        
        rl.getIntroducedRoles().add(r);
        
        p.getBlock().add(rl);
        
        pm.setProtocol(p);
        
        Role unknown=new Role("unknown");
        
        ProtocolModelProjectorRule rule=new ProtocolModelProjectorRule();
        
        ProtocolProjectorContext context=new DefaultProjectorContext(null, null);
        
        CachedJournal l=new CachedJournal();
        
        rule.project(context, pm, unknown, l);
        
        if (l.getIssues().size() != 1 || l.hasErrors() == false) {
            fail("Expecting 1 error");
        }
        
        IssueDetails id=l.getIssues().get(0);
        
        if (id.getMessage().equals(MessageFormat.format(
                java.util.PropertyResourceBundle.getBundle(
                        "org.scribble.protocol.projection.impl.Messages").getString("_UNKNOWN_ROLE"),
                        unknown.getName())) == false) {
            fail("Unexpected error message: "+id.getMessage());
        }
    }

    @org.junit.Test
    public void projectKnownRole() {
        ProtocolModel pm=new ProtocolModel();
        
        Protocol p=new Protocol();
        
        Introduces rl=new Introduces();
        
        Role introducer=new Role();
        introducer.setName("introducer");
        
        rl.setIntroducer(introducer);
        
        Role r=new Role();
        r.setName("role");
        
        rl.getIntroducedRoles().add(r);
        
        p.getBlock().add(rl);
        
        pm.setProtocol(p);
        
        ProtocolModelProjectorRule rule=new ProtocolModelProjectorRule();
        
        ProtocolProjectorContext context=new DefaultProjectorContext(null, null);
         
        CachedJournal l=new CachedJournal();
        
        Role known=new Role();
        known.setName(r.getName());
        
        rule.project(context, pm, known, l);
        
        if (l.hasErrors()) {
            fail("Expecting no error");
        }
    }
    
    @org.junit.Test
    public void testCheckContainingConstructsNoChange() {
        
        Protocol prot1=new Protocol();

        Block b1=new Block();
        prot1.setBlock(b1);
        
        Choice c1=new Choice();
        b1.add(c1);
        
        Block b2=new Block();
        c1.getPaths().add(b2);
        
        if (ProtocolModelProjectorRule.checkContainingConstructs(b2) != b2) {
            fail("Block should be the same");
        }
    }
    
    @org.junit.Test
    public void testCheckContainingConstructsAddRecBlock() {
        
        Protocol prot1=new Protocol();

        Block b1=new Block();
        prot1.setBlock(b1);
        
        RecBlock rb1=new RecBlock();
        rb1.setLabel("label");
        b1.add(rb1);
        
        Choice c1=new Choice();
        rb1.getBlock().add(c1);
        
        Block b2=new Block();
        c1.getPaths().add(b2);
        
        Block result=ProtocolModelProjectorRule.checkContainingConstructs(b2);

        if (result == null) {
            fail("Result is null");
        }
        
        if (result.size() != 1) {
            fail("Result block should have 1");
        }
        
        if ((result.get(0) instanceof RecBlock) == false) {
            fail("RecBlock not found");
        }
        
        RecBlock rbresult=(RecBlock)result.get(0);
        
        if (rbresult.getLabel().equals(rb1.getLabel()) == false) {
            fail("RecBlock not the same");
        }
        
        if (rbresult.getBlock() != b2) {
            fail("RecBlock should contain b2");
        }
    }
}