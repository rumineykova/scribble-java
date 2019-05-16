package org.scribble.ext.assrt.visit.wf;

import java.util.HashSet;
import java.util.Set;

import org.scribble.job.Job;
import org.scribble.visit.NameDisambiguator;

public class AssrtNameDisambiguator extends NameDisambiguator
{
	private Set<String> apays = new HashSet<>();

	public AssrtNameDisambiguator(Job job)
	{
		super(job);
	}

	@Override
	public void clear()
	{
		super.clear();
		this.apays.clear(); 
	}

	public boolean isVarnameInScope(String name)
	{
		return this.apays.contains(name); 
	}
	
	public void addAnnotPaylaod(String name)
	{
		this.apays.add(name); 
	}
}
