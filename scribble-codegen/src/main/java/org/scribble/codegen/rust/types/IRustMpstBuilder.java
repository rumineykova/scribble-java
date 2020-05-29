package org.scribble.codegen.rust.types;

import org.scribble.type.name.Role;

enum BuilderKind {Choice, Offer, Simple, Rec};
public interface IRustMpstBuilder {
	public Role getSelf(); 
	public BuilderKind getKind();
	public String build(); 
	public String getFinalTypeName();
}
