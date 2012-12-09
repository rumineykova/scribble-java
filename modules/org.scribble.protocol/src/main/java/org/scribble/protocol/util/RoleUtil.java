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

import org.scribble.protocol.model.Activity;
import org.scribble.protocol.model.Block;
import org.scribble.protocol.model.Choice;
import org.scribble.protocol.model.DefaultVisitor;
import org.scribble.protocol.model.DirectedChoice;
import org.scribble.protocol.model.Do;
import org.scribble.protocol.model.End;
import org.scribble.protocol.model.Inline;
import org.scribble.protocol.model.Interaction;
import org.scribble.protocol.model.Interrupt;
import org.scribble.protocol.model.Introduces;
import org.scribble.protocol.model.ModelObject;
import org.scribble.protocol.model.OnMessage;
import org.scribble.protocol.model.Parallel;
import org.scribble.protocol.model.Parameter;
import org.scribble.protocol.model.ParameterDefinition;
import org.scribble.protocol.model.Protocol;
import org.scribble.protocol.model.RecBlock;
import org.scribble.protocol.model.Recursion;
import org.scribble.protocol.model.Repeat;
import org.scribble.protocol.model.Role;
import org.scribble.protocol.model.Run;
import org.scribble.protocol.model.Unordered;

/**
 * This class provides utility functions related to the Role protocol
 * component.
 *
 */
public final class RoleUtil {

    /**
     * Private constructor.
     */
    private RoleUtil() {
    }
    
    /**
     * This method returns the roles defined within the parent scope of the
     * supplied activity.
     * 
     * @param block The parent scope
     * @return The set of roles
     */
    public static java.util.Set<Role> getDeclaredRoles(Block block) {
        final java.util.Set<Role> roles=new java.util.HashSet<Role>();
        
        block.visit(new DefaultVisitor() {
            
            protected void addRole(Role r) {
                if (r != null && !roles.contains(r)) {
                    roles.add(r);
                }
            }
            
            public void accept(Introduces elem) {
                for (Role r : elem.getIntroducedRoles()) {
                    addRole(r);
                }
            }
            
            public boolean start(Protocol elem) {
                return (false);
            }
        });
        
        return (roles);
    }
    
    /**
     * This method returns the enclosing protocol associated with the
     * supplied role.
     * 
     * @param role The role
     * @return The protocol
     */
    public static Protocol getEnclosingProtocol(Role role) {
        Protocol ret=null;
        
        if (role.getParent() instanceof Introduces) {
            ret = ((Introduces)role.getParent()).getEnclosingProtocol();
        } else if (role.getParent() instanceof ParameterDefinition
                && role.getParent().getParent() instanceof Protocol) {
            ret = (Protocol)role.getParent().getParent();
        }
        
        return (ret);
    }
    
    /**
     * This method returns the set of roles that are associated with
     * the supplied activity.
     * 
     * @param act The activity
     * @return The set of roles associated with the activity and its
     *                 sub components if a grouping construct
     */
    public static java.util.Set<Role> getUsedRoles(final ModelObject act) {
        final java.util.Set<Role> roles=new java.util.HashSet<Role>();
        
        act.visit(new DefaultVisitor() {
            
            protected void addRole(Role r) {
                if (r != null && !roles.contains(r)) {
                    roles.add(r);
                }
            }
            
            public void accept(Interaction elem) {
                addRole(elem.getFromRole());
                for (Role r : elem.getToRoles()) {
                    addRole(r);
                }
            }
            
            public void accept(Inline elem) {
                java.util.Set<Role> inscope=getRolesInScope(elem);

                for (Parameter p : elem.getParameters()) {
                    
                    // Determine if the parameter is a role
                    for (Role r : inscope) {
                        if (r.getName().equals(p.getName())) {
                            addRole(r);
                            break;
                        }
                    }
                }
            }
            
            public void accept(Run elem) {
                java.util.Set<Role> inscope=getRolesInScope(elem);

                for (Parameter p : elem.getParameters()) {
                    
                    // Determine if the parameter is a role
                    for (Role r : inscope) {
                        if (r.getName().equals(p.getName())) {
                            addRole(r);
                            break;
                        }
                    }
                }
            }
            
            public boolean start(Choice elem) {
                addRole(elem.getRole());
                return (true);
            }
            
            public boolean start(DirectedChoice elem) {
                addRole(elem.getFromRole());
                for (Role r : elem.getToRoles()) {
                    addRole(r);
                }
                return (true);
            }
            
            public boolean start(Protocol elem) {
                // If protocol was initial activity, then traverse, otherwise
                // don't enter nested protocol
                return (elem == act);
            }
        });
        
        return (roles);
    }
    
    /**
     * This method determines the set of roles that are in the
     * scope for the supplied activity.
     * 
     * @param activity The activity
     * @return The set of roles in scope for the supplied activity
     */
    public static java.util.Set<Role> getRolesInScope(Activity activity) {
        java.util.Set<Role> ret=new java.util.HashSet<Role>();
        
        if (activity != null) {
            
            // Identify enclosing protocol definition
            Protocol protocol=activity.getEnclosingProtocol();
            
            if (protocol != null) {
                
                // Add role parameters
                for (ParameterDefinition pd : protocol.getParameterDefinitions()) {
                    if (pd.isRole()) {
                        ret.add(new Role(pd.getName()));
                    }
                }
                
                RoleLocator visitor=new RoleLocator(protocol, activity, ret);
                
                protocol.visit(visitor);
            }
        }
        
        return (ret);
    }
    
    /**
     * This method returns the innermost block that encloses all of the activities
     * associated with the supplied role.
     * 
     * @param protocol The protocol
     * @param role The role
     * @param includeDeclaration Whether the role's declaration should be taken into account
     * @return The block
     */
    public static Block getEnclosingBlock(final Protocol protocol, final Role role,
                                    final boolean includeDeclaration) {
        final java.util.List<Block> blocks=new java.util.Vector<Block>();
        
        // Find all blocks enclosing an activity associated with the supplied role
        protocol.visit(new DefaultVisitor() {
            
            public boolean start(Protocol elem) {
                
                if (protocol == elem) {
                    if (protocol.getParameterDefinition(role.getName()) != null) {
                        blocks.add(elem.getBlock());
                    }
                }
                
                // Don't visit contained protocols
                return (protocol == elem);
            }
            
            public void accept(org.scribble.protocol.model.Interaction elem) {
                if (role.equals(elem.getFromRole()) || elem.getToRoles().contains(role)
                        || ((elem.getFromRole() == null || elem.getToRoles().size() == 0)
                                && role.equals(elem.getEnclosingProtocol().getLocatedRole()))) {
                    blocks.add((Block)elem.getParent());
                }
            }
            
            public boolean start(Choice elem) {
                if (role.equals(elem.getRole()) /*|| (elem.getRole() == null &&
                                role.equals(elem.enclosingProtocol().getRole()))*/) {
                    blocks.add((Block)elem.getParent());
                }
                
                return (true);
            }
            
            public boolean start(DirectedChoice elem) {
                if (role.equals(elem.getFromRole()) || elem.getToRoles().contains(role)
                        || ((elem.getFromRole() == null || elem.getToRoles().size() == 0)
                        && role.equals(elem.getEnclosingProtocol().getLocatedRole()))) {
                    blocks.add((Block)elem.getParent());
                }
                
                return (true);
            }
            
            public void accept(Introduces elem) {
                if (includeDeclaration && elem.getIntroducedRoles().contains(role)) {
                    blocks.add((Block)elem.getParent());
                }
            }
        });
        
        return (ActivityUtil.getEnclosingBlock(blocks));
    }
    
    /**
     * Visitor for locating roles.
     *
     */
    public static class RoleLocator extends DefaultVisitor {
        
        private boolean _recurse=true;
        private Protocol _protocol=null;
        private Activity _activity=null;
        private java.util.Set<Role> _result=null;
        private java.util.List<java.util.List<Role>> _roleStack=
                    new java.util.Vector<java.util.List<Role>>();
        
        /**
         * Constructor.
         * 
         * @param protocol The protocol
         * @param activity The activity
         * @param result The set of roles
         */
        public RoleLocator(Protocol protocol, Activity activity, 
                                java.util.Set<Role> result) {
            _protocol = protocol;
            _activity = activity;
            _result = result;
        }
        
        @Override
        public boolean start(Block elem) {
            return (startBlock(elem));    
        }
        
        /**
         * Indicates the start of a block.
         * 
         * @param elem The block
         * @return Whether to process contents
         */
        protected boolean startBlock(Block elem) {
            // Push new role list onto the stack
            _roleStack.add(new java.util.Vector<Role>());
            
            return (true);
        }
        
        @Override
        public void end(Block elem) {
            endBlock(elem);
        }
        
        /**
         * Process the end of the block.
         * 
         * @param elem The block
         */
        protected void endBlock(Block elem) {
            // Pop top role list from the stack
            _roleStack.remove(_roleStack.size()-1);
        }

        @Override
        public boolean start(Choice elem) {
            checkActivity(elem);
            return (_recurse);
        }

        @Override
        public boolean start(DirectedChoice elem) {
            checkActivity(elem);
            return (_recurse);
        }

        @Override
        public boolean start(OnMessage elem) {
            return (_recurse);
        }

        @Override
        public boolean start(Unordered elem) {
            checkActivity(elem);
            return (_recurse);
        }

        @Override
        public boolean start(Parallel elem) {
            checkActivity(elem);
            return (_recurse);
        }

        @Override
        public boolean start(Protocol elem) {
            // If this is the enclosing protocol, then
            // recursively visit it - otherwise don't
            boolean ret=(_protocol == elem);
            
            if (ret) {
                java.util.List<Role> rlist=new java.util.Vector<Role>();
                _roleStack.add(rlist);
                
                if (elem.getLocatedRole() != null) {
                    rlist.add(elem.getLocatedRole());
                }
                
                for (ParameterDefinition p : elem.getParameterDefinitions()) {
                    if (p.getType() == null) {
                        rlist.add(new Role(p.getName()));
                    }
                }
                
            }
            
            return (ret);
        }

        @Override
        public boolean start(Repeat elem) {
            checkActivity(elem);
            return (_recurse);
        }

        @Override
        public boolean start(RecBlock elem) {
            checkActivity(elem);
            return (_recurse);
        }

        @Override
        public boolean start(Do elem) {
            checkActivity(elem);
            return (_recurse);
        }

        @Override
        public boolean start(Interrupt elem) {
            return (_recurse);
        }

        @Override
        public void accept(Run elem) {
            checkActivity(elem);
        }

        @Override
        public void accept(Interaction elem) {
            checkActivity(elem);
        }

        @Override
        public void accept(Introduces elem) {
            java.util.List<Role> rlist=_roleStack.get(_roleStack.size()-1);
            
            rlist.addAll(elem.getIntroducedRoles());
        }

        @Override
        public void accept(Inline elem) {
            checkActivity(elem);
        }
        
        @Override
        public void accept(Recursion elem) {
            checkActivity(elem);
        }
        
        /**
         * This method visits an end statement.
         * 
         * @param elem The end statement
         */
        public void accept(End elem) {
            checkActivity(elem);
        }
        
        /**
         * This method checks the supplied activity to
         * determine if it is the activity being searched
         * for, and if so identifies the roles in scope
         * as the result.
         * 
         * @param elem The activity
         */
        protected void checkActivity(Activity elem) {
            
            if (elem == _activity) {
                // Record the current stack of roles in the result
                for (java.util.List<Role> plist : _roleStack) {
                    
                    for (Role p : plist) {
                        _result.add(p);
                    }
                }
                
                // Remainder of description should be traversed quickly
                _recurse = false;
            }
        }
    }
}