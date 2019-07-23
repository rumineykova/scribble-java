package org.scribble.ext.assrt.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.scribble.ast.global.GProtocolDecl;
import org.scribble.ext.assrt.main.AssrtJob;
import org.scribble.ext.assrt.type.formula.AssrtBinaryFormula;
import org.scribble.ext.assrt.type.formula.AssrtBoolFormula;
import org.scribble.ext.assrt.type.formula.AssrtQuantifiedIntVarsFormula;
import org.scribble.ext.assrt.type.formula.AssrtSmtFormula;
import org.scribble.ext.assrt.type.formula.AssrtTrueFormula;
import org.scribble.ext.assrt.type.formula.AssrtUnPredicateFormula;
import org.scribble.main.ScribbleException;
import org.scribble.util.ScribUtil;

// "Native" Z3 -- not Z3 Java API
public class Z3Wrapper
{

	// Based on CommandLine::runDot, JobContext::runAut, etc
	public static boolean checkSat(AssrtJob job, GProtocolDecl gpd, Set<AssrtBoolFormula> fs) //throws ScribbleException
	{
		fs = fs.stream().filter(f -> !f.equals(AssrtTrueFormula.TRUE)).collect(Collectors.toSet());
		if (fs.isEmpty())
		{
			return true;
		}

		String smt2 = toSmt2(job, gpd, fs);

		job.debugPrintln("\n[assrt-core] Running Z3 on:\n  " + smt2.replaceAll("\\n", "\n  "));

		return checkSat(smt2);
	}

	// smt2 is the full Z3 source
	private static boolean checkSat(String smt2) //throws ScribbleException
	{
		File tmp;
		try
		{
			tmp = File.createTempFile("gpd.header.name", ".smt2.tmp");
			try
			{
				String tmpName = tmp.getAbsolutePath();				
				ScribUtil.writeToFile(tmpName, smt2);
				String[] res = ScribUtil.runProcess("z3", tmpName);
				String trim = res[0].trim();
				if (trim.equals("sat"))  // FIXME: factor out
				{
					return true;
				}
				else if (trim.equals("unsat"))
				{
					return false;
				}
				else
				{
					throw new RuntimeException("[assrt] Z3 error: " + Arrays.toString(res));
				}
			}
			catch (ScribbleException e)
			{
				throw new RuntimeException(e);
			}
			finally
			{
				tmp.delete();
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	// fs shouldn't be empty (but OK)
	private static String toSmt2(AssrtJob job, GProtocolDecl gpd, Set<AssrtBoolFormula> fs)
	{
		String smt2 = "";
		List<String> rs = gpd.getHeader().roledecls.getRoles().stream().map(Object::toString).sorted().collect(Collectors.toList());
		smt2 += IntStream.range(0, rs.size())
				.mapToObj(i -> "(declare-const " + rs.get(i) + " Int)\n(assert (= " + rs.get(i) + " " + i +"))\n").collect(Collectors.joining(""));
						// FIXME: make a Role sort?

		Set<AssrtUnPredicateFormula> preds = fs.stream().flatMap(f -> getUnintPreds.func.apply(f).stream()).collect(Collectors.toSet());
		smt2 += preds.stream().map(p -> "(declare-fun " + p.name + " ("
				+ IntStream.range(0, p.args.size()).mapToObj(i -> ("(Int)")).collect(Collectors.joining(" "))
				+ ") Bool)\n").collect(Collectors.joining(""));
		if (preds.stream().anyMatch(p -> p.name.equals("port")))  // FIXME: factor out
		{
			smt2 += "(assert (forall ((p Int) (r Int)) (=> (port p r) (open p r))))\n";
		}
		
		return smt2
				+ fs.stream().map(f -> "(assert " + f.toSmt2Formula() + ")\n").collect(Collectors.joining())
				+ "(check-sat)\n"
				+ "(exit)";
	}

	public static final RecursiveFunctionalInterface<Function<AssrtSmtFormula<?>, Set<AssrtUnPredicateFormula>>> getUnintPreds  // FIXME: move to utils?
			= new RecursiveFunctionalInterface<Function<AssrtSmtFormula<?>, Set<AssrtUnPredicateFormula>>>()
	{{
		this.func = ff ->
		{
			if (ff instanceof AssrtBinaryFormula)
			{
				AssrtBinaryFormula<?> bf = (AssrtBinaryFormula<?>) ff;
				return Stream.of(bf.getLeft(), bf.getRight()).flatMap(x -> this.func.apply(x).stream()).collect(Collectors.toSet());
			}
			else if (ff instanceof AssrtQuantifiedIntVarsFormula)
			{
				return this.func.apply(((AssrtQuantifiedIntVarsFormula) ff).expr);
			}
			else if (ff instanceof AssrtUnPredicateFormula)
			{
				return Stream.of((AssrtUnPredicateFormula) ff).collect(Collectors.toSet());  // Nested predicates not possible
			}
			else
			{
				return Collections.emptySet();
			}
		};
	}};
}

	/*private static Set<AssrtUnPredicateFormula> getUnPredicates(AssrtSmtFormula<?> f) 
	{
		Recursive<Function<AssrtSmtFormula<?>, Set<AssrtUnPredicateFormula>>> rec = new Recursive<>();
		rec.func = ff ->
		{
			if (ff instanceof AssrtBinaryFormula)
			{
				AssrtBinaryFormula<?> bf = (AssrtBinaryFormula<?>) ff;
				return Stream.of(bf.getLeft(), bf.getRight()).flatMap(x -> rec.func.apply(x).stream()).collect(Collectors.toSet());
			}
			else if (ff instanceof AssrtQuantifiedIntVarsFormula)
			{
				return rec.func.apply(((AssrtQuantifiedIntVarsFormula) ff).expr);
			}
			else if (ff instanceof AssrtUnPredicateFormula)
			{
				return Stream.of((AssrtUnPredicateFormula) ff).collect(Collectors.toSet());  // Nested predicates not possible
			}
			else
			{
				return Collections.emptySet();
			}
		};
		return rec.func.apply(f);
	}*/
