package org.scribble.ext.assrt.ast.global;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.ConnectionAction;
import org.scribble.ast.Constants;
import org.scribble.ast.Do;
import org.scribble.ast.MessageTransfer;
import org.scribble.ast.ProtocolBlock;
import org.scribble.ast.global.GProtocolBlock;
import org.scribble.ast.global.GRecursion;
import org.scribble.ast.local.LContinue;
import org.scribble.ast.local.LInteractionNode;
import org.scribble.ast.local.LProtocolBlock;
import org.scribble.ast.local.LRecursion;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.del.ScribDel;
import org.scribble.ext.assrt.ast.AssrtArithExpr;
import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.ext.assrt.ast.AssrtAstFactory;
import org.scribble.ext.assrt.ast.AssrtStateVarDeclAnnotNode;
import org.scribble.ext.assrt.ast.local.AssrtLRecursion;
import org.scribble.ext.assrt.ast.name.simple.AssrtIntVarNameNode;
import org.scribble.main.ScribbleException;
import org.scribble.type.kind.Global;
import org.scribble.type.name.RecVar;
import org.scribble.type.name.Role;
import org.scribble.visit.AstVisitor;


// N.B. non-empty ass currently only supported via proto def inlining -- no direct syntax for rec-with-annot yet
public class AssrtGRecursion extends GRecursion implements AssrtStateVarDeclAnnotNode
{
	public final List<AssrtIntVarNameNode> annotvars;
	public final List<AssrtArithExpr> annotexprs;
	public final AssrtAssertion ass;  // cf. AssrtGProtocolHeader  // FIXME: make specific syntactic expr

	public AssrtGRecursion(CommonTree source, RecVarNode recvar, GProtocolBlock block)
	{
		this(source, recvar, block, //null);
				Collections.emptyList(), Collections.emptyList(),
				null);
	}
	
	public AssrtGRecursion(CommonTree source, RecVarNode recvar, GProtocolBlock block, //AssrtAssertion ass)
			List<AssrtIntVarNameNode> annotvars, List<AssrtArithExpr> annotexprs,
			AssrtAssertion ass)
	{
		super(source, recvar, block);
		this.annotvars = Collections.unmodifiableList(annotvars);
		this.annotexprs = Collections.unmodifiableList(annotexprs);
		this.ass = ass;
	}

	public LRecursion project(AstFactory af, Role self, LProtocolBlock block)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: " + this);
	}

	public AssrtLRecursion project(AstFactory af, Role self, LProtocolBlock block, //AssrtAssertion ass)
			List<AssrtIntVarNameNode> annotvars, List<AssrtArithExpr> annotexprs,
			AssrtAssertion ass)
	{
		RecVarNode recvar = this.recvar.clone(af);
		AssrtLRecursion lr = null;
		Set<RecVar> rvs = new HashSet<>();
		rvs.add(recvar.toName());
		LProtocolBlock pruned = prune(af, block, rvs);
		if (!pruned.isEmpty())
		{
			lr = ((AssrtAstFactory) af).AssrtLRecursion(this.source, recvar, pruned, //ass);
					annotvars, annotexprs,
					ass);
		}
		return lr;
	}

	// FIXME: factor out, e.g., pruneRecursion?
	protected static LProtocolBlock prune(AstFactory af, LProtocolBlock block, Set<RecVar> rvs)  // FIXME: Set unnecessary
	{
		if (block.isEmpty())
		{
			return block;
		}
		List<? extends LInteractionNode> lis = block.getInteractionSeq().getInteractions();
		if (lis.size() > 1)
		{
			return block;
		}
		else //if (lis.size() == 1)
		{
			// Only pruning if single statement body: if more than 1, must be some (non-empty?) statement before a continue -- cannot (shouldn't?) be a continue followed by some other statement due to reachability
			LInteractionNode lin = lis.get(0);
			if (lin instanceof LContinue)
			{
				if (rvs.contains(((LContinue) lin).recvar.toName()))
				{
					// FIXME: need equivalent for projection-irrelevant recursive-do in a protocoldecl
					return af.LProtocolBlock(block.getSource(),
							af.LInteractionSeq(block.seq.getSource(), Collections.emptyList()));
				}
				else
				{
					return block;
				}
			}
			else if (lin instanceof MessageTransfer<?> || lin instanceof Do<?> || lin instanceof ConnectionAction<?>)
			{
				return block;
			}
			else
			{
				//if (lin instanceof LRecursion)
				if (lin instanceof AssrtLRecursion)
				{
					rvs = new HashSet<>(rvs);
					LProtocolBlock pruned = prune(af, ((LRecursion) lin).getBlock(), rvs);
					if (pruned.isEmpty())
					{
						return pruned;
					}
					else
					{
						AssrtLRecursion lr = (AssrtLRecursion) lin;
						return af.LProtocolBlock(block.getSource(),
								af.LInteractionSeq(block.seq.getSource(), Arrays.asList(
										((AssrtAstFactory) af).AssrtLRecursion(lr.getSource(), lr.recvar, pruned, //lr.ass))));
												lr.annotvars, lr.annotexprs,
												lr.ass)
										)));
					}
				}
				else
				{
					return GRecursion.prune(af, block, rvs);
				}
			}
		}
	}

	@Override
	protected AssrtGRecursion copy()
	{
		return new AssrtGRecursion(this.source, this.recvar, getBlock(), //this.ass);
				this.annotvars, this.annotexprs,
				this.ass);
	}
	
	@Override
	public AssrtGRecursion clone(AstFactory af)
	{
		RecVarNode recvar = this.recvar.clone(af);
		GProtocolBlock block = getBlock().clone(af);

		List<AssrtIntVarNameNode> annotvars = this.annotvars.stream().map(v -> v.clone(af)).collect(Collectors.toList());
		List<AssrtArithExpr> annotexprs = this.annotexprs.stream().map(e -> e.clone(af)).collect(Collectors.toList());
		AssrtAssertion ass = (this.ass == null) ? null : this.ass.clone(af);

		return ((AssrtAstFactory) af).AssrtGRecursion(this.source, recvar, block, //ass);
				annotvars, annotexprs,
				ass);
	}

	@Override
	public AssrtGRecursion reconstruct(RecVarNode recvar, ProtocolBlock<Global> block)
	{
		throw new RuntimeException("[assrt] Shouldn't get in here: " + this);
	}

	public AssrtGRecursion reconstruct(RecVarNode recvar, ProtocolBlock<Global> block, //AssrtAssertion ass)
			List<AssrtIntVarNameNode> annotvars, List<AssrtArithExpr> annotexprs,
			AssrtAssertion ass)
	{
		ScribDel del = del();
		AssrtGRecursion gr = new AssrtGRecursion(this.source, recvar, (GProtocolBlock) block, //ass);
				annotvars, annotexprs,
				ass);

		gr = (AssrtGRecursion) gr.del(del);
		return gr;
	}

	@Override
	public AssrtGRecursion visitChildren(AstVisitor nv) throws ScribbleException
	{
		RecVarNode recvar = (RecVarNode) visitChild(this.recvar, nv);
		GProtocolBlock block = visitChildWithClassEqualityCheck(this, getBlock(), nv);

		List<AssrtIntVarNameNode> annotvars = visitChildListWithClassEqualityCheck(this, this.annotvars, nv);
		List<AssrtArithExpr> annotexprs = visitChildListWithClassEqualityCheck(this, this.annotexprs, nv);
		AssrtAssertion ass = (this.ass == null) ? null : (AssrtAssertion) visitChild(this.ass, nv);

		return reconstruct(recvar, block, //ass);
				annotvars, annotexprs,
				ass);
	}
	
	@Override
	public List<AssrtIntVarNameNode> getAnnotVars()
	{
		return this.annotvars;
	}

	@Override
	public List<AssrtArithExpr> getAnnotExprs()
	{
		return this.annotexprs;
	}

	@Override
	public AssrtAssertion getAssertion()
	{
		return this.ass;
	}

	@Override
	public String toString()
	{
		return Constants.REC_KW + " " + this.recvar //+ " " + this.ass
				+ annotToString()
				+ " " + this.block;
	}
}
