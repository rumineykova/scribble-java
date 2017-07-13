package org.scribble.ext.assrt.visit.wf;

import java.util.HashSet;
import java.util.Set;

import org.scribble.main.Job;
import org.scribble.visit.wf.NameDisambiguator;

public class AssrtNameDisambiguator extends NameDisambiguator
{
	private Set<String> annotPayloads = new HashSet<>();

	public AssrtNameDisambiguator(Job job)
	{
		super(job);
	}

	@Override
	public void clear()
	{
		super.clear();
		this.annotPayloads.clear(); 
	}

	public boolean isVarnameInScope(String name)
	{
		return this.annotPayloads.contains(name); 
	}
	
	public void addAnnotPaylaod(String name)
	{
		this.annotPayloads.add(name); 
	}
}
