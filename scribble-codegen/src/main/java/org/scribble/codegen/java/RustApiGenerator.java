package org.scribble.codegen.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Arrays;

import org.scribble.ast.Module;
import org.scribble.ast.global.GProtocolDecl;
import org.scribble.codegen.rust.types.RoleTypesGenerator;
import org.scribble.codegen.rust.types.RustGenConstants;
import org.scribble.codegen.rust.types.Util;
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
	private void initialiseRoles() {
		Module mod = this.job.getContext().getModule(this.gpn.getPrefix());
		GProtocolName simpname = this.gpn.getSimpleName();
		GProtocolDecl gpd = (GProtocolDecl) mod.getProtocolDecl(simpname);
		for (Role r : gpd.header.roledecls.getRoles()) {
			// constructRoleClass(this.cb.newClass(), r);
			// constructRoleClass(new ClassBuilder(),
			// getEndpointApiRootPackageName(this.gpn), r);
			if (!this.roles.contains(r)) {
				this.roles.add(r);
			}
		}
	}

	public Map<String, String> generateAll() throws ScribbleException {
		Map<String, String> genAll = new HashMap<>();
		StringBuilder sb = new StringBuilder();
		List<String> roleImports = this.roles.stream().map(r -> r.toString())
				// .reduce("", String::concat)
				.collect(Collectors.toList());

		// add all imports
		sb.append(RustGenConstants.MPST_IMPORTS).append(generateRoleImports(roleImports));
		genAll.put("all", sb.toString());

		// Get the actual active roles for each Choice At (that are indexed by an
		// Integer
		Map<Integer, Role> actualActiveRoles = new HashMap<>();

		System.out.println("\nFirst pass\n");

		// Create the new map of actual senders of Choice
		for (int i = 0; i < roles.size(); i++) {
			Role curr = roles.get(i);
			// Creating the new RoleTypesGenerator which will create the new map of actual
			// senders of Choice
			RoleTypesGenerator gen = new RoleTypesGenerator(this.job, this.gpn, curr,
					roles.stream().filter(r -> r != curr).collect(Collectors.toList()), new HashMap<>(), true);

			// Running the new RoleTypesGenerator
//			String temp = gen.generateApi().values().stream().map(t -> t + "\n").reduce("", String::concat);

			gen.generateApi().entrySet().forEach(entry -> {
				System.out.println(
						"Values for gen.generateApi: " + entry.getKey() + " -> " + entry.getValue() + "    ////// END");
			});

			// Merging the new map with actualActiveRoles
			actualActiveRoles = Stream
					.concat(actualActiveRoles.entrySet().stream(), gen.getActiveRoles().entrySet().stream())
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (value1, value2) -> curr));

//			System.out.println("curr role" + curr);
//			System.out.println("temp: " + temp);
		}

		System.out.println("\nSecond pass\n");

		Util.resetCounter();

		// add all types
		for (int i = 0; i < roles.size(); i++) {
			Role curr = roles.get(i);
			RoleTypesGenerator gen = new RoleTypesGenerator(this.job, this.gpn, curr,
					roles.stream().filter(r -> r != curr).collect(Collectors.toList()), actualActiveRoles, false);

			// Running the new RoleTypesGenerator and adding the result to genAll
			genAll.put(roles.get(i).toString(),
					gen.generateApi().values().stream().map(t -> t + "\n").reduce("", String::concat));
		}

		return genAll;
	}

	public String generateRoleImports(List<String> roleImports) {
		StringBuilder sb = new StringBuilder();
		List<Integer> roles = new ArrayList<>();
		for (int i = 0; i < roleImports.size(); i++) {
			for (int j = 0; j < roleImports.size(); j++) {
				if (i != j && !roles.contains(j)) {
					sb.append(generateRoleImport(roleImports.get(j))).append(System.getProperty("line.separator"));
					roles.add(j);
				}
			}
		}
		return sb.toString();
	}

	private String generateRoleImport(String sndRole) {
		return String.format("use mpstthree::role::%s::Role%s;", sndRole.toLowerCase(), sndRole.toUpperCase());
	}

}
