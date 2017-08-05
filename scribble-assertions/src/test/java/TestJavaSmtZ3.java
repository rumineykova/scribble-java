import org.sosy_lab.common.ShutdownManager;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.log.BasicLogManager;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.java_smt.SolverContextFactory;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;
import org.sosy_lab.java_smt.api.FormulaManager;
import org.sosy_lab.java_smt.api.IntegerFormulaManager;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;
import org.sosy_lab.java_smt.api.ProverEnvironment;
import org.sosy_lab.java_smt.api.QuantifiedFormulaManager;
import org.sosy_lab.java_smt.api.SolverContext;
import org.sosy_lab.java_smt.api.SolverContext.ProverOptions;
import org.sosy_lab.java_smt.api.SolverException;

public class TestJavaSmtZ3
{
	public static void main(String[] args)
			throws InvalidConfigurationException, SolverException, InterruptedException
	{

		Configuration config = Configuration.fromCmdLineArguments(args);
		LogManager logger = BasicLogManager.create(config);
		ShutdownManager shutdown = ShutdownManager.create();

		// System.loadLibrary("z3");
		// System.loadLibrary("z3java");

		// SolverContext is a class wrapping a solver context.
		// Solver can be selected either using an argument or a configuration option
		// inside `config`.
		SolverContext context = SolverContextFactory.createSolverContext(config, logger,
				shutdown.getNotifier(), Solvers.Z3);

		FormulaManager fmgr = context.getFormulaManager();
		BooleanFormulaManager bmgr = fmgr.getBooleanFormulaManager();
		IntegerFormulaManager imgr = fmgr.getIntegerFormulaManager();
		QuantifiedFormulaManager qmgr = fmgr.getQuantifiedFormulaManager();

		BooleanFormula constraint = makeFormula(bmgr, imgr, qmgr);

		try (ProverEnvironment prover = context.newProverEnvironment(ProverOptions.GENERATE_UNSAT_CORE))
		{
			prover.addConstraint(constraint);
			boolean isUnsat = prover.isUnsat();
			System.out.println("Formula: " + constraint + "\nSatisfiable: " + !isUnsat);
			if (!isUnsat)
			{
				// Model model =
				prover.getModel();
			}
		}

		//System.out.println("Hello World!");
	}

	private static BooleanFormula makeFormula(BooleanFormulaManager bmgr, IntegerFormulaManager imgr, QuantifiedFormulaManager qmgr)
	{
		IntegerFormula x1 = imgr.makeVariable("x");
		IntegerFormula x2 = imgr.makeVariable("x");
		BooleanFormula f = imgr.equal(x1, x2);
		return f;
		
		
		/*IntegerFormula x = imgr.makeVariable("x");
		IntegerFormula _6 = imgr.makeNumber(6);
		IntegerFormula y = imgr.makeVariable("y");
		
		BooleanFormula b1 = imgr.greaterOrEquals(x, _6);
		BooleanFormula b2 = imgr.lessOrEquals(y, x);
		BooleanFormula b3 = imgr.greaterThan(y,  _6);

		return bmgr.implication(b1, bmgr.and(b2, b3));*/
		
		/*IntegerFormula x = imgr.makeVariable("x");
		IntegerFormula _5 = imgr.makeNumber(5);
		BooleanFormula b1 = imgr.lessThan(x, _5);
		
		IntegerFormula y = imgr.makeVariable("y");
		IntegerFormula _4 = imgr.makeNumber(4);
		BooleanFormula b2 = imgr.lessThan(y, _4);

		BooleanFormula b3 = imgr.equal(x, y);
		
		//return bmgr.and(bmgr.and(b1, b2), b3);
		return bmgr.implication(b1, bmgr.and(b2, b3));*/
		
		
		
		
		//IntegerFormula x = imgr.makeVariable("x");
		//IntegerFormula _6 = imgr.makeNumber(6);

		//return qmgr.exists(x, bmgr.makeTrue());

		//return qmgr.exists(x, imgr.equal(x, x));

		//return qmgr.exists(x, imgr.equal(x, _6));

		//IntegerFormula y = imgr.makeVariable("y");
		//return qmgr.exists(y, imgr.equal(x, y));
		
		//return qmgr.exists(bmgr.makeVariable("x"), imgr.equal(x, _6));
		//return qmgr.forall(bmgr.makeVariable("x"), bmgr.and(imgr.equal(x, _6), bmgr.makeVariable("x")));

		/*IntegerFormula x = imgr.makeVariable("x");
		IntegerFormula _6 = imgr.makeNumber(6);
		IntegerFormula y = imgr.makeVariable("y");
		
		BooleanFormula b1 = imgr.greaterOrEquals(x, _6);
		BooleanFormula b2 = imgr.lessThan(y, x);
		BooleanFormula b3 = imgr.greaterThan(y, _6);
		//return bmgr.implication(b1, bmgr.and(b2, b3));
		//return qmgr.exists(x, bmgr.implication(b1, qmgr.exists(y, bmgr.and(b2, b3))));
		return qmgr.forall(x, bmgr.implication(b1, qmgr.exists(y, bmgr.and(b2, b3))));*/
		
		//return qmgr.exists(Arrays.asList(x, y), imgr.equal(x, y));
		//return qmgr.exists(Arrays.asList(bmgr.makeVariable("x"), bmgr.makeVariable("y")), imgr.equal(x, y));
		//return qmgr.exists(Arrays.asList(bmgr.makeVariable("z")), imgr.equal(x, y));
		//return qmgr.exists(Arrays.asList(bmgr.makeVariable("x")), imgr.equal(x, y));

		//return qmgr.exists(x, imgr.lessThan(x, _6));
		

		
		
		
		//BooleanFormula b1 = bmgr.makeTrue();
		//BooleanFormula b1 = bmgr.makeVariable("x");
		
		//b1 = qmgr.exists(x, b1);  // No: because x is IntegerFormula instead of Boolean?
		//b1 = qmgr.exists(bmgr.makeVariable("x"), b1);  // ??? -- x's treated distinctly? (i.e., integer x free)
		
		
		/*IntegerFormula a = imgr.makeVariable("a"), b = imgr.makeVariable("b");
		BooleanFormula f = bmgr.and(imgr.equal(b, a), imgr.lessThan(a, b));

		return f;*/
	}
}
