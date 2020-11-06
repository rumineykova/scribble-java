package org.scribble.codegen.rust.types;

public class RustGenConstants {
	// TODO: Currenty supports only mpstthree
	public static final String MPST_IMPORTS = "\n" + "extern crate mpstthree;\n" + "\n"
			+ "use mpstthree::binary::{End, Recv, Send};\n" + "use mpstthree::sessionmpst::SessionMpst;\n"
			+ "use mpstthree::role::end::RoleEnd;\n" + "use std::marker;\n" + "\n";
}
