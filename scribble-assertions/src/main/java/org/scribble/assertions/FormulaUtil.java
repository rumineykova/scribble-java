package org.scribble.assertions;

import org.sosy_lab.common.ShutdownManager;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.log.BasicLogManager;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.java_smt.SolverContextFactory;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.FormulaManager;
import org.sosy_lab.java_smt.api.IntegerFormulaManager;
import org.sosy_lab.java_smt.api.Model;
import org.sosy_lab.java_smt.api.ProverEnvironment;
import org.sosy_lab.java_smt.api.SolverContext;
import org.sosy_lab.java_smt.api.SolverContext.ProverOptions;
import org.sosy_lab.java_smt.api.SolverException;

public class FormulaUtil {
	private static FormulaUtil instance = null;
	public final FormulaManager fmanager;    
	public final BooleanFormulaManager bmanager; 
	public final IntegerFormulaManager imanager; 
	public final SolverContext context; 
	protected FormulaUtil() throws InvalidConfigurationException{
		Configuration config = Configuration.defaultConfiguration(); // fromCmdLineArguments([]);
	    LogManager logger = BasicLogManager.create(config);
	    ShutdownManager shutdown = ShutdownManager.create();

	    // SolverContext is a class wrapping a solver context.
	    // Solver can be selected either using an argument or a configuration option
	    // inside `config`.
	    this.context = SolverContextFactory.createSolverContext(
	        config, logger, shutdown.getNotifier(), Solvers.Z3);
	    
	    fmanager = context.getFormulaManager();
	    this.bmanager = fmanager.getBooleanFormulaManager();
	    this.imanager = fmanager.getIntegerFormulaManager();
	}
	
	public static FormulaUtil getInstance()  {
	      try {
			if(instance == null) {
	         instance = new FormulaUtil();
	      } 
	      }catch (InvalidConfigurationException e)
	      {
				System.err.println("InvalidConfigurationException: " + e.getMessage());
	      }
	      
	      return instance;
	   }
	
	public BooleanFormula addFormula(BooleanFormula f1, BooleanFormula f2){
		return this.bmanager.and(f1, f2); 
	}
	
	public Boolean IsValid(BooleanFormula f1, BooleanFormula context) {
		boolean isUnsat = false;   
		 BooleanFormula formula = context==null? f1 : this.bmanager.implication(f1, context);
		 
		 try (ProverEnvironment prover = this.context.newProverEnvironment(ProverOptions.GENERATE_UNSAT_CORE)) {
		        prover.addConstraint(formula);
		        isUnsat = prover.isUnsat();
		        System.out.print(isUnsat);
		        
		        if (!isUnsat) {
		          Model model = prover.getModel();
		        }
		      }
		 catch (SolverException e) {}
		 catch (InterruptedException e) {}
		 
		 return  !isUnsat; 
	}
}
