package org.scribble.codegen.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.scribble.ast.Module;
import org.scribble.ast.global.GProtocolDecl;
import org.scribble.codegen.rust.types.RoleTypesGenerator;
import org.scribble.codegen.rust.types.RustGenConstants;
import org.scribble.main.Job;
import org.scribble.main.ScribbleException;
import org.scribble.type.name.GProtocolName;
import org.scribble.type.name.Role;

public class RustApiGenerator {
	public final Job job;
	public final GProtocolName gpn; 
	public List<Role> roles = new ArrayList<Role>(); 
	
	private StringBuilder builder = new StringBuilder(); 
	
	public RustApiGenerator(Job job, GProtocolName gpn) {
		this.job = job;
		this.gpn = gpn;
		this.initialiseRoles();
	}
	
	@SuppressWarnings("unused")
	private void initialiseRoles()
	{
		Module mod = this.job.getContext().getModule(this.gpn.getPrefix());
		GProtocolName simpname = this.gpn.getSimpleName();
		GProtocolDecl gpd = (GProtocolDecl) mod.getProtocolDecl(simpname);
		for (Role r : gpd.header.roledecls.getRoles())
		{
			//constructRoleClass(this.cb.newClass(), r);
			//constructRoleClass(new ClassBuilder(), getEndpointApiRootPackageName(this.gpn), r);
			this.roles.add(r);	
		}
	}
	
	public Map<String, String> generateAll() throws ScribbleException {
		Map<String, String> genAll = new HashMap<>();
		StringBuilder sb = new StringBuilder(); 
		List<String> roleImports  = 
				this.roles
				.stream().map(r -> r.toString())
				//.reduce("", String::concat)
				.collect(Collectors.toList());

		sb.append(RustGenConstants.MPST_IMPORTS)
		  .append(generateRoleImports(roleImports)); 
		 genAll.put("all",sb.toString());
		 
		 for (int i=0; i<roles.size(); i++) {
			RoleTypesGenerator gen = new RoleTypesGenerator(this.job, this.gpn, roles.get(i));
			genAll.putAll(gen.generateApi());	
		}
		 return genAll; 
	}

	public String generateRoleImports(List<String> roles) {
		StringBuilder sb = new StringBuilder(); 
		for (int i= 0; i<roles.size(); i++) {
			for (int j=0; j< roles.size(); j++) {
				if (i!=j) {
					sb.append(generateRoleImport(roles.get(i), roles.get(j)))
					.append(System.getProperty("line.separator"));
				}
			}
		}
		return sb.toString();
	}
	
	private String generateRoleImport(String fstRole, String sndRole) {
		return String.format(
				"use mpstthree::role::%s_%s::Role%sto%s;", 
				fstRole.toLowerCase(), sndRole.toLowerCase(), 
				fstRole.toUpperCase(), sndRole.toUpperCase()); 
	}
	
}
