package org.scribble.codegen.rust.types;

public class RustGenConstants {
	// TODO: Currenty supports only mpstthree
	public static final String MPST_IMPORTS = 
		"\n" + 
		"extern crate mpstthree;\n" + 
		"\n" + 
		"use std::boxed::Box;\n" + 
		"use std::error::Error;\n" + 
		"\n" + 
		"use mpstthree::binary::{End, Recv, Send, Session};\n" + 
		"use mpstthree::fork_mpst;\n" + 
		"use mpstthree::sessionmpst::SessionMpst;\n" + 
		"\n" + 
		"use mpstthree::functionmpst::close::close_mpst;\n";
}
