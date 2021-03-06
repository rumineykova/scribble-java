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
package org.scribble.model.local;

import org.scribble.model.ModelObject;
import org.scribble.model.Visitor;

/**
 * This class represents the base class for all Scribble definition
 * context.
 */
public abstract class LActivity extends ModelObject {
    
    /**
     * The default constructor.
     */
    public LActivity() {
    }
    
    /**
     * The copy constructor.
     * 
     * @param act The activity
     */
    public LActivity(LActivity act) {
        super(act);
    }
    
    /**
     * {@inheritDoc}
     */
    public void visit(Visitor visitor) {
    	if (visitor instanceof LVisitor) {
    		visit((LVisitor)visitor);
    	}
    }
    
    /**
     * This method visits the model object using the supplied
     * visitor.
     * 
     * @param visitor The visitor
     */
    public abstract void visit(LVisitor visitor);
    
}
