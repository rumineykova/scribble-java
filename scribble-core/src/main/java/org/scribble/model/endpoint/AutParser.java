/**
 * Copyright 2008 The Scribble Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.scribble.model.endpoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.job.Job2;
import org.scribble.model.endpoint.actions.EAction;
import org.scribble.type.name.DataType;
import org.scribble.type.name.MessageId;
import org.scribble.type.name.MessageSigName;
import org.scribble.type.name.Op;
import org.scribble.type.name.Role;
import org.scribble.type.session.Payload;

public class AutParser
{
	private final Job2 job2;

	public AutParser(Job2 job2)
	{
		this.job2 = job2;
	}
	
	public EGraph parse(String aut)
	{
		EModelFactory ef = this.job2.config.ef; 

		//Map<Integer, Map<String, Integer>> edges = new HashMap<>();
		Map<Integer, List<String>> as = new HashMap<>();
		Map<Integer, List<Integer>> succs = new HashMap<>();
		int init = -1;
		try
		{
			BufferedReader br = new BufferedReader(new StringReader(aut));
			String line = br.readLine();
			if (line == null || !line.startsWith("des (") || !line.endsWith(")"))
			{
				throw new RuntimeException("Unexpected first line: " + line);
			}
			String[] first = line.substring("des (".length(), line.length() - 1).split(",");
			if (first.length != 3)
			{
				throw new RuntimeException("Unexpected first line: " + line);
			}
			init = Integer.parseInt(first[0]);
			//int trans = Integer.parseInt(first[1]);
			//int states = Integer.parseInt(first[2]);
			while ((line = br.readLine()) != null)
			{
				if (!line.startsWith("(") || !line.endsWith(")"))
				{
					throw new RuntimeException("Unexpected line: " + line);
				}
				//String[] read = line.substring(1, line.length()-1).split(",");
				String[] read = new String[] {
						line.substring(1, line.indexOf(',')),
						line.substring(line.indexOf(',')+1, line.lastIndexOf(',')),
						line.substring(line.lastIndexOf(',')+1, line.length()-1)
				};
				int s = Integer.parseInt(read[0]);
				String a = read[1].substring(1, read[1].length()-1);
				int succ = Integer.parseInt(read[2]);
				//Map<String, Integer> tmp = edges.get(s);
				List<String> tmp1 = as.get(s);
				List<Integer> tmp2 = succs.get(s);
				if (tmp1 == null)
				{
					//tmp = new HashMap<>();
					//edges.put(s, tmp);
					tmp1 = new LinkedList<>();
					as.put(s, tmp1);
					tmp2 = new LinkedList<>();
					succs.put(s, tmp2);
				}
				//tmp.put(a, succ);
				tmp1.add(a);
				tmp2.add(succ);
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		//Set<Integer> allSuccs = edges.values().stream().flatMap((j) -> j.values().stream()).collect(Collectors.toSet());
		Set<Integer> allSuccs = succs.values().stream().flatMap((j) -> j.stream()).collect(Collectors.toSet());
		int term = -1;
		//Set<Integer> terms = allSuccs.stream().filter((j) -> !edges.containsKey(j)).collect(Collectors.toSet());
		Set<Integer> terms = allSuccs.stream().filter((j) -> !succs.containsKey(j)).collect(Collectors.toSet());
		if (terms.size() > 0)
		{
			term = terms.iterator().next();
		}
		//EGraphBuilderUtil util = new EGraphBuilderUtil(ef);
		EGraphBuilderUtil util = this.job2.newEGraphBuilderUtil();
		Map<Integer, EState> map = new HashMap<>();
		map.put(init, util.getEntry());
		if (term != -1)
		{
			map.put(term, util.getExit());
		}
		map.put(init, util.getEntry());
		//for (int i : edges.keySet())
		for (int i : as.keySet())
		{
			if (i != init && i != term)
			{
				map.put(i, util.ef.newEState(Collections.emptySet()));
			}
		}
		//for (int i : succs)
		for (int i : succs.keySet())
		{
			if (!map.containsKey(i) && i != init && i != term)
			{
				map.put(i, util.ef.newEState(Collections.emptySet()));
			}
		}
		//for (int i : edges.keySet())
		for (int i : as.keySet())
		{
			EState s = map.get(i);
			//Map<String, Integer> tmp = edges.get(i);
			List<String> tmp1 = as.get(i);
			List<Integer> tmp2 = succs.get(i);
			//if (tmp != null)
			if (tmp1 != null)
			{
				//for (String a : tmp.keySet())
				Iterator<Integer> is = tmp2.iterator();
				for (String a : tmp1)
				{
					int succ = is.next();
					//builder.addEdge(s, parseIOAction(a), map.get(tmp.get(a)));
					util.addEdge(s, parseIOAction(ef, a), map.get(succ));
				}
			}
		}
		//return builder.finalise();
		return new EGraph(util.getEntry(), util.getExit());
	}
	
	// Cf. getCommSymbol of IOActions
	// FIXME: simply do a match for getCommSymbol?
	private static EAction parseIOAction(EModelFactory ef, String a)
	{
		String peer;
		String action;
		String msg;  // Could be an Op or a MessageSigName (affects API generation)
		String[] pay = null;
		
		/*int i = a.indexOf("!");
		i = (i == -1) ? a.indexOf("?") : i;
		int j = i+1;
		String tmp = a.substring(j, j+1);
		if (tmp.equals("!") || tmp.equals("?"))
		{
			j++;
		}
		action = a.substring(i, j);*/
		int i, j;
		if ((i = a.indexOf("!")) != -1)
		{
			j = i+1;
			if (a.charAt(j) == '!')
			{
				j++;
				if (a.charAt(i-1) == '(')
				{
					if (a.charAt(j+1) != ')')
					{
						throw new RuntimeException("Shouldn't get in here: " + a);
					}
					i--;
					j++;
				}
			}
		}
		else if ((i = a.indexOf("?")) != -1)
		{
			j = i+1;
			if (a.charAt(j) == '?')
			{
				j++;
				if (a.charAt(i-1) == '(')
				{
					if (a.charAt(j+1) != ')')
					{
						throw new RuntimeException("Shouldn't get in here: " + a);
					}
					i--;
					j++;
				}
			}
		}
		else if ((i = a.indexOf('/')) != -1)
		{
			if (a.charAt(i-1) != '-' || a.charAt(i+1) != '-')
			{
				throw new RuntimeException("Shouldn't get in here: " + a);
			}
			j = i+2;
			i--;
		}
		else
		{
			throw new RuntimeException("[TODO] aut parsing not supported for: " + a);
		}
		action = a.substring(i, j);
	
		peer = a.substring(0, i);
		int k = a.indexOf("(");
		msg = a.substring(j, k);
		String p = a.substring(k+1, a.length()-1);
		if (!p.isEmpty())
		{
			pay = p.split(",");
		}
		switch (action)
		{
			case "!":
			{
				Payload payload = (pay != null) ? new Payload(Arrays.asList(pay).stream().map((pe) -> new DataType(pe)).collect(Collectors.toList())) : Payload.EMPTY_PAYLOAD;
				return ef.newESend(new Role(peer), getMessageIdHack(msg), payload);  // FIXME: how about MessageSigNames? -- currently OK, treated as empty payload (cf. ModelAction)
			}
			case "?":
			{
				Payload payload = (pay != null) ? new Payload(Arrays.asList(pay).stream().map((pe) -> new DataType(pe)).collect(Collectors.toList())) : Payload.EMPTY_PAYLOAD;
				return ef.newEReceive(new Role(peer), getMessageIdHack(msg), payload);  // FIXME: how about MessageSigNames?)
			}
			case "!!":
			{
				//return new Connect(new Role(peer));
				Payload payload = (pay != null) ? new Payload(Arrays.asList(pay).stream().map((pe) -> new DataType(pe)).collect(Collectors.toList())) : Payload.EMPTY_PAYLOAD;
				return ef.newERequest(new Role(peer), getMessageIdHack(msg), payload);
			}
			case "??":
			{
				//return new Accept(new Role(peer));
				Payload payload = (pay != null) ? new Payload(Arrays.asList(pay).stream().map((pe) -> new DataType(pe)).collect(Collectors.toList())) : Payload.EMPTY_PAYLOAD;
				return ef.newEAccept(new Role(peer), getMessageIdHack(msg), payload);
			}
			case "(!!)":
			{				
				return ef.newEWrapClient(new Role(peer));
			}
			case "(??)":
			{				
				return ef.newEWrapServer(new Role(peer));
			}
			case "-/-":
			{				
				return ef.newEDisconnect(new Role(peer));
			}
			default:
			{
				throw new RuntimeException("[TODO] aut parsing not supported for: " + msg);
			}
		}
	}
	
	// Cf. ModelState.toAut, ModelAction.toStringWithMessageIdHack
	private static MessageId<?> getMessageIdHack(String msg)
	{
		return (msg.startsWith("^")) ? new MessageSigName(msg.substring(1)) : new Op(msg);
	}
}
