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
import org.sosy_lab.java_smt.api.SolverContext;
import org.sosy_lab.java_smt.api.SolverContext.ProverOptions;
import org.sosy_lab.java_smt.api.SolverException;

public class Z3Test 
{
    public static void main( String[] args )  throws InvalidConfigurationException, SolverException, InterruptedException {
  	
    	    Configuration config = Configuration.fromCmdLineArguments(args);
    	    LogManager logger = BasicLogManager.create(config);
    	    ShutdownManager shutdown = ShutdownManager.create();
    	    
    	    //System.loadLibrary("z3");
    	    //System.loadLibrary("z3java");
    	    
    	    // SolverContext is a class wrapping a solver context.
    	    // Solver can be selected either using an argument or a configuration option
    	    // inside `config`.
    	    SolverContext context = SolverContextFactory.createSolverContext(
    	        config, logger, shutdown.getNotifier(), Solvers.Z3);
    	
    	    FormulaManager fmgr = context.getFormulaManager();

    	    BooleanFormulaManager bmgr = fmgr.getBooleanFormulaManager();
    	    IntegerFormulaManager imgr = fmgr.getIntegerFormulaManager();

    	    IntegerFormula a = imgr.makeVariable("a"),
    	                   b = imgr.makeVariable("b");
    	                   //c = imgr.makeVariable("c");
    	    BooleanFormula constraint = bmgr.and(
    	        imgr.equal(b, a),
    	        imgr.lessThan(a, b));
    	    
    	    try (ProverEnvironment prover = context.newProverEnvironment(ProverOptions.GENERATE_MODELS)) {
    	        prover.addConstraint(constraint);
    	        boolean isUnsat = prover.isUnsat();
    	        System.out.println(isUnsat);
    	        if (!isUnsat) {
    	          //Model model = 
    	        	prover.getModel();
    	        }
    	      }
    	    
        System.out.println( "Hello World!" );
    }
}
