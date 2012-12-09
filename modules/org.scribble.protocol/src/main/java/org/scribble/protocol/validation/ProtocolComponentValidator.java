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
package org.scribble.protocol.validation;

import org.scribble.common.logging.Journal;
import org.scribble.protocol.model.Block;
import org.scribble.protocol.model.ModelObject;
import org.scribble.protocol.model.Protocol;

//import java.util.logging.*;

/**
 * This class provides an implementation of the ProtocolValidator
 * interface. It enables individual validation rules, associated with
 * specific model components, to be registered and invoked when
 * validating a protocol model.
 */
public class ProtocolComponentValidator implements ProtocolValidator {
    
    private java.util.List<ProtocolComponentValidatorRule> _rules=null;

    /**
     * The default constructor.
     */
    public ProtocolComponentValidator() {
    }
    
    /**
     * This method returns the validator rules.
     * 
     * @return The rules
     */
    public java.util.List<ProtocolComponentValidatorRule> getRules() {
        if (_rules == null) {
            _rules = new java.util.ArrayList<ProtocolComponentValidatorRule>();
        }
        
        return (_rules);
    }
    
    /**
     * This method sets the validator rules.
     * 
     * @param rules The rules
     */
    public void setRules(java.util.List<ProtocolComponentValidatorRule> rules) {
        _rules = rules;
    }
    
    /**
     * {@inheritDoc}
     */
    public void validate(ProtocolValidatorContext pvc,
            org.scribble.protocol.model.ProtocolModel model, Journal journal) {
        ValidatingVisitor vv=new ValidatingVisitor(pvc, journal);
        
        model.visit(vv);
        
        // Validate the process model - this needs to be done separately as the
        // model is not included in the visitor, only its contents
        vv.process(model);
    }

    /**
     * This class traverses the protocol model to validate the individual constructs.
     *
     */
    public class ValidatingVisitor extends org.scribble.protocol.model.AbstractModelObjectVisitor {
        
        private ProtocolValidatorContext _pvc=null;
        private Journal _logger=null;
        
        /**
         * This is the constructor for the vaidating visitor.
         * 
         * @param pvc The protocol validator context
         * @param logger The journal
         */
        public ValidatingVisitor(ProtocolValidatorContext pvc, Journal logger) {
            _pvc = pvc;
            _logger = logger;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean start(Protocol elem) {
            _pvc.pushScope();
            return (super.start(elem));
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void end(Protocol elem) {
            super.end(elem);
            _pvc.popScope();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean start(Block elem) {
            _pvc.pushState();
            return (super.start(elem));
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void end(Block elem) {
            super.end(elem);
            _pvc.popState();
        }
        
        /**
         * This method can be implemented to process all of the model
         * objects within a particular protocol model.
         * 
         * @param obj The model object
         * @return Whether to process the contents
         */
        public boolean process(ModelObject obj) {
            
            // Iterate through rules processing those that support the supplied
            // model object
            java.util.Iterator<ProtocolComponentValidatorRule> iter=getRules().iterator();
            
            while (iter.hasNext()) {
                ProtocolComponentValidatorRule rule=iter.next();
                
                if (rule.isSupported(obj)) {
                    rule.validate(_pvc, obj, _logger);
                }
            }
            
            return (true);
        }
    }
}