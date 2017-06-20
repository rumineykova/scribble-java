package org.scribble.assertions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

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

public class SMTWrapper {
	private static SMTWrapper instance = null;
	
	public final ShutdownManager shutdownManager;
	
	// TODO: MOVE to config
	private int timeOutInMs = 2000;
	public final FormulaManager fmanager;    
	public final BooleanFormulaManager bmanager; 
	public final IntegerFormulaManager imanager; 
	public final QuantifiedFormulaManager qmanager;
	public final LogManager logger; 
	public final SolverContext context;
	
	protected SMTWrapper() throws InvalidConfigurationException{
		// TODO: maybe use parameter solver.z3.usePhantomReferences to garbage collect Z3 formula references
		Configuration config = Configuration.fromCmdLineArguments(new String[]{"--usePhantomReferences=true"}); 
				//defaultConfiguration(); // fromCmdLineArguments([]);
	    logger = BasicLogManager.create(config);
	    shutdownManager = ShutdownManager.create();
	    
	    // SolverContext is a class wrapping a solver context.
	    // Solver can be selected either using an argument or a configuration option
	    // inside `config`.
	    this.context = SolverContextFactory.createSolverContext(
	        config, logger, shutdownManager.getNotifier(), Solvers.Z3);
	    // solver.z3.usePhantomReferences
	    
	    fmanager = context.getFormulaManager();
	    this.qmanager = fmanager.getQuantifiedFormulaManager(); 
	    this.bmanager = fmanager.getBooleanFormulaManager();
	    this.imanager = fmanager.getIntegerFormulaManager();
	}
	
	public static SMTWrapper getInstance()  {
	      try {
			if(instance == null) {
	         instance = new SMTWrapper();
	      } 
	      }catch (InvalidConfigurationException e)
	      {
				System.err.println("InvalidConfigurationException: " + e.getMessage());
	      }
	      
	      return instance;
	   }
	
	public AssertionLogFormula addFormula(StmFormula f1, StmFormula f2) throws AssertionException{
		BooleanFormula formula = this.bmanager.and((BooleanFormula)f1.getFormula(), (BooleanFormula)f2.getFormula());
		Set<String> vars = new HashSet<String>(f1.getVars()); 
		vars.addAll(f2.getVars()); 
		return new AssertionLogFormula(formula, vars); 
	}
	
	public Boolean isSat(StmFormula assertionFormula, AssertionLogFormula context) {
		boolean isUnsat = false;
		
		try {
			BooleanFormula formula = buildFormula(assertionFormula, context);
			final ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
			executor.setRemoveOnCancelPolicy(true);
			Runnable solverInterruptThread = () -> shutdownManager.requestShutdown("Timeout");
			
			try (ProverEnvironment prover = this.context.newProverEnvironment(ProverOptions.GENERATE_UNSAT_CORE)) {
		        prover.addConstraint(formula);
		        
	            ScheduledFuture<?> timeoutFuture = executor.schedule(solverInterruptThread,
	            					timeOutInMs, TimeUnit.MILLISECONDS);
	            isUnsat = prover.isUnsat();
	            cancelTimeoutFuture(timeoutFuture);
		      }
		
			catch (SolverException e) {
				this.logger.logUserException(Level.INFO, e, "Error thrown by the SMT solver");
				System.err.print("Error thrown by the SMT solver" + e.getMessage());
			}
			catch (InterruptedException e) {
				this.logger.logUserException(Level.INFO, e, "The formula was interrupted. Took too long");
				System.err.print("The formula was interrupted. Took too long." + e.getMessage()); 
			}
			finally {
				executor.shutdown();
			}
		}
		catch (AssertionException e) {
			this.logger.logUserException(Level.INFO, e, "The assertion is not a valid Z3 expression");
			System.err.print("The assertion is not a valid Z3 expression" + e.getMessage()); 
		}
		
		return  !isUnsat;
	}

	private BooleanFormula buildFormula(StmFormula assertionFormula,
			AssertionLogFormula context) throws AssertionException {
		BooleanFormula currFormula;
		currFormula = (BooleanFormula) assertionFormula.getFormula();
		List<IntegerFormula> contextVars; 
		List<IntegerFormula> vars;
		Set<String> setVars = assertionFormula.getVars(); 
				
		BooleanFormula formula; 
		if (context!=null)
		{
			setVars.removeAll(context.getVars());
			vars = this.makeVars(new ArrayList<String>(setVars));
			
			BooleanFormula contextF = (BooleanFormula) context.getFormula(); 
			contextVars = this.makeVars(new ArrayList<String>(context.getVars()));
			formula = vars.isEmpty()?
					this.qmanager.forall(contextVars, this.bmanager.implication(contextF, currFormula)):
					this.qmanager.forall(contextVars, 
										this.bmanager.implication(contextF, 
											this.qmanager.exists(vars, currFormula)));  
				
		} else {
			vars = this.makeVars(new ArrayList<String>(setVars));
			formula = vars.isEmpty()? 
						currFormula:	
						this.qmanager.exists(vars, currFormula);
		}
		return formula;
	}
	
	 private void cancelTimeoutFuture(ScheduledFuture<?> timeoutFuture) {
	        try {
	            timeoutFuture.cancel(true);
	        } catch (Throwable ignore) {
	        }
	    }
	    
	    
	/*
	TODO: Should we close the ShutdownManger  
	public void close() {
		if (this.context != null) {
			// this.executor.shutdownNow();
			this.shutdownManager.requestShutdown("Closed");
	        this.context.close();
	       }
	    }
	    
	*/
	 
	public List<IntegerFormula> makeVars(List<String> vars) {
		return  vars.stream().map(v -> this.imanager.makeVariable(v)).collect(Collectors.toList()); 
	}
}
