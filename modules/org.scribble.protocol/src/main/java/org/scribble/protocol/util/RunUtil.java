/*
 * Copyright 2009-10 www.scribble.org
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
package org.scribble.protocol.util;

import org.scribble.protocol.model.Protocol;
import org.scribble.protocol.model.ProtocolReference;

/**
 * This class provides helper functions for the Run construct.
 *
 */
public final class RunUtil {

    /**
     * Private constructor.
     */
    private RunUtil() {
    }
    
    /**
     * This method attempts to locate an inner protocol associated with the supplied
     * protocol reference.
     * 
     * @param protocol The parent protocol
     * @param protocolRef The protocol reference
     * @return The inner protocol, or null if not found
     */
    public static Protocol getInnerProtocol(Protocol protocol, ProtocolReference protocolRef) {
        Protocol ret=null;
        
        for (int i=0; ret == null && i < protocol.getNestedProtocols().size(); i++) {
            Protocol nested=protocol.getNestedProtocols().get(i);
            
            if (protocolRef.getName().equals(nested.getName())
                    && ((protocolRef.getRole() == null && nested.getLocatedRole() == null)
                    || (protocolRef.getRole() != null && nested.getLocatedRole() != null
                    && protocolRef.getRole().equals(nested.getLocatedRole())))) {
                ret = nested;        
            }
        }
        
        return (ret);
    }
}