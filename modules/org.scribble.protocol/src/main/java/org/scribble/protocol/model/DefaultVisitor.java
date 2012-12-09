/*
 * Copyright 2009-10 www.scribble.org
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
package org.scribble.protocol.model;

/**
 * This class provides a default visitor which can be used
 * to traverse a model.
 */
public class DefaultVisitor implements Visitor {
    
    private boolean _defaultGroupReturn=true;
    
    /**
     * This is the default constructor.
     */
    public DefaultVisitor() {
    }
    
    /**
     * This constructor configures the default group return value.
     * 
     * @param defaultGroupReturn The default group return value
     */
    public DefaultVisitor(boolean defaultGroupReturn) {
        _defaultGroupReturn = defaultGroupReturn;
    }

    /**
     * This method sets the default group construct return value.
     * 
     * @param b The value
     */
    public void setDefaultGroupReturn(boolean b) {
        _defaultGroupReturn = b;
    }
    
    /**
     * This method indicates the start of a
     * block.
     * 
     * @param elem The block
     * @return Whether to process the contents
     */
    public boolean start(Block elem) {
        return (_defaultGroupReturn);
    }
    
    /**
     * This method indicates the end of a
     * block.
     * 
     * @param elem The block
     */
    public void end(Block elem) {
    }
    
    /**
     * This method visits an import component.
     * 
     * @param elem The import
     */
    public void accept(TypeImportList elem) {
    }
    
    /**
     * This method visits an import component.
     * 
     * @param elem The import
     */
    public void accept(ProtocolImportList elem) {
    }
    
    /**
     * This method visits an interaction component.
     * 
     * @param elem The interaction
     */
    public void accept(Interaction elem) {
    }
    
    /**
     * This method visits a use component.
     * 
     * @param elem The use component
     */
    public void accept(Inline elem) {
    }
    
    /**
     * This method visits a recursion component.
     * 
     * @param elem The recursion
     */
    public void accept(Recursion elem) {
    }
    
    /**
     * This method visits the role list.
     * 
     * @param elem The role list
     */
    public void accept(Introduces elem) {
    }
    
    /**
     * This method indicates the start of a
     * protocol.
     * 
     * @param elem The protocol
     * @return Whether to process the contents
     */
    public boolean start(Protocol elem) {
        return (_defaultGroupReturn);
    }
    
    /**
     * This method indicates the end of a
     * protocol.
     * 
     * @param elem The protocol
     */
    public void end(Protocol elem) {
    }
    
    /**
     * This method indicates the start of a
     * choice.
     * 
     * @param elem The choice
     * @return Whether to process the contents
     */
    public boolean start(Choice elem) {
        return (_defaultGroupReturn);
    }
    
    /**
     * This method indicates the end of a
     * choice.
     * 
     * @param elem The choice
     */
    public void end(Choice elem) {
    }
    
    /**
     * This method indicates the start of a
     * directed choice.
     * 
     * @param elem The directed choice
     * @return Whether to process the contents
     */
    public boolean start(DirectedChoice elem) {
        return (_defaultGroupReturn);
    }
    
    /**
     * This method indicates the end of a
     * directed choice.
     * 
     * @param elem The directed choice
     */
    public void end(DirectedChoice elem) {
    }
    
    /**
     * This method indicates the start of a
     * on-message.
     * 
     * @param elem The on-message element
     * @return Whether to process the contents
     */
    public boolean start(OnMessage elem) {
        return (_defaultGroupReturn);
    }
    
    /**
     * This method indicates the end of a
     * on-message.
     * 
     * @param elem The on-message element
     */
    public void end(OnMessage elem) {
    }
    
    /**
     * This method indicates the start of a
     * parallel.
     * 
     * @param elem The parallel
     * @return Whether to process the contents
     */
    public boolean start(Parallel elem) {
        return (_defaultGroupReturn);
    }
    
    /**
     * This method indicates the end of a
     * parallel.
     * 
     * @param elem The parallel
     */
    public void end(Parallel elem) {
    }
    
    /**
     * This method indicates the start of a
     * repeat.
     * 
     * @param elem The repeat
     * @return Whether to process the contents
     */
    public boolean start(Repeat elem) {
        return (_defaultGroupReturn);
    }
    
    /**
     * This method indicates the end of a
     * repeat.
     * 
     * @param elem The repeat
     */
    public void end(Repeat elem) {
    }
    
    /**
     * This method indicates the start of a
     * labelled block.
     * 
     * @param elem The labelled block
     * @return Whether to process the contents
     */
    public boolean start(RecBlock elem) {
        return (_defaultGroupReturn);
    }
    
    /**
     * This method indicates the end of a
     * labelled block.
     * 
     * @param elem The labelled block
     */
    public void end(RecBlock elem) {
    }
    
    /**
     * This method indicates the start of an
     * Unordered construct.
     * 
     * @param elem The Unordered construct
     * @return Whether to process the contents
     */
    public boolean start(Unordered elem) {
        return (_defaultGroupReturn);
    }
    
    /**
     * This method indicates the end of an
     * Unordered construct.
     * 
     * @param elem The Unordered construct
     */
    public void end(Unordered elem) {
    }
    
    /**
     * This method indicates the start of a
     * try escape.
     * 
     * @param elem The try escape
     * @return Whether to process the contents
     */
    public boolean start(Do elem) {
        return (_defaultGroupReturn);
    }
    
    /**
     * This method indicates the end of a
     * try escape.
     * 
     * @param elem The try escape
     */
    public void end(Do elem) {
    }
    
    /**
     * This method indicates the start of a
     * catch block.
     * 
     * @param elem The catch block
     * @return Whether to process the contents
     */
    public boolean start(Interrupt elem) {
        return (_defaultGroupReturn);
    }
    
    /**
     * This method indicates the end of a
     * catch block.
     * 
     * @param elem The catch block
     */
    public void end(Interrupt elem) {
    }
    
    /**
     * This method indicates the start of a
     * run construct.
     * 
     * @param elem The run
     */
    public void accept(Run elem) {
    }
    
    /**
     * This method visits a type component.
     * 
     * @param elem The type
     */
    public void accept(TypeImport elem) {
    }
    
    /**
     * This method visits a protocol import component.
     * 
     * @param elem The protocol import
     */
    public void accept(ProtocolImport elem) {
    }
    
    /**
     * This method visits an end statement.
     * 
     * @param elem The end statement
     */
    public void accept(End elem) {
    }
    
    /**
     * This method visits any extensible activity that is not
     * part of the base protocol model.
     * 
     * @param elem The custom activity
     */
    public void accept(CustomActivity elem) {
    }
    
}