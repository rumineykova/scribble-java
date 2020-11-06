package org.scribble.codegen.rust.types;

import org.scribble.model.endpoint.actions.EAction;
import org.scribble.type.name.Role;

public class BinaryTypeBuilder {
	private EAction action;
	private Role self;

	public BinaryTypeBuilder(EAction action, Role self) {
		this.self = self;
		this.action = action;
	}
}
