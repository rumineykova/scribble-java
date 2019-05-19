package org.scribble.ext.assrt.ast.global;

import java.util.List;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.scribble.ast.ProtoBlock;
import org.scribble.ast.global.GProtoBlock;
import org.scribble.ast.global.GRecursion;
import org.scribble.ast.name.simple.RecVarNode;
import org.scribble.core.type.kind.Global;
import org.scribble.del.DelFactory;
import org.scribble.ext.assrt.ast.AssrtArithExpr;
import org.scribble.ext.assrt.ast.AssrtAssertion;
import org.scribble.ext.assrt.ast.AssrtStateVarDeclAnnotNode;
import org.scribble.ext.assrt.ast.name.simple.AssrtIntVarNameNode;
import org.scribble.ext.assrt.del.AssrtDelFactory;
import org.scribble.util.Constants;
import org.scribble.util.ScribException;
import org.scribble.visit.AstVisitor;


// N.B. non-empty ass currently only supported via proto def inlining -- no direct syntax for rec-with-annot yet
@Deprecated
public class AssrtGRecursion extends GRecursion
		implements AssrtStateVarDeclAnnotNode
{
	//public static final int BODY_CHILD_INDEX = 1;
	public static final int ASSRT_EXT_CHILD_INDEX = 3;  // null if no @-annotation

	// N.B. EXTID-parsed children of ASSRT_CHILD_INDEX subtree (i.e., grandchildren of this) -- cf. Assertions.g
	public static final int EXT_ASSERT_CHILD_INDEX = 0;  // null if not specified (means "true", but not written syntactically)
	public static final int EXT_STATEVAR_CHILDREN_START_INDEX = 1;

	// ScribTreeAdaptor#create constructor
	public AssrtGRecursion(Token t)
	{
		super(t);
	}
	
	// Tree#dupNode constructor
	protected AssrtGRecursion(AssrtGRecursion node)
	{
		super(node);
	}

	// Following duplicated from AssrtGProtoHeader

	@Override
	public CommonTree getExtChild()
	{
		return (CommonTree) getChild(ASSRT_EXT_CHILD_INDEX);
	}

	// N.B. null if not specified -- currently duplicated from AssrtGMessageTransfer
	@Override
	public AssrtAssertion getAssertionChild()
	{
		CommonTree ext = getExtChild();
		if (ext == null)
		{
			return null;
		}
		Tree n = ext.getChild(EXT_ASSERT_CHILD_INDEX);
		return (n.getText().equals("ASSRT_EMPTYASS"))  // TODO: factor out constant
				? null
				: (AssrtAssertion) n;
	}
	
	@Override
	public List<AssrtIntVarNameNode> getAnnotVarChildren()
	{
		/*List<? extends ScribNode> cs = getChildren();
		return cs.subList(ANNOT_CHILDREN_START_INDEX, cs.size()).stream()  // TODO: refactor, cf. Module::getMemberChildren
				.filter(x -> x instanceof AssrtIntVarNameNode)
				.map(x -> (AssrtIntVarNameNode) x).collect(Collectors.toList());*/
		throw new RuntimeException("[TODO] : ");
	}

	@Override
	public List<AssrtArithExpr> getAnnotExprChildren()
	{
		/*List<? extends ScribNode> cs = getChildren();
		return cs.subList(ANNOT_CHILDREN_START_INDEX, cs.size()).stream()  // TODO: refactor, cf. Module::getMemberChildren
				.filter(x -> x instanceof AssrtArithExpr)
				.map(x -> (AssrtArithExpr) x).collect(Collectors.toList());*/
		throw new RuntimeException("[TODO] : ");
	}

	@Override
	public void addScribChildren(RecVarNode rv, ProtoBlock<Global> block)
	{
		throw new RuntimeException("Deprecated for " + getClass() + ":\n\t" + this);
	}

	// "add", not "set"
	public void addScribChildren(RecVarNode rv, ProtoBlock<Global> block,
			AssrtAssertion ass, List<AssrtIntVarNameNode> avars,
			List<AssrtArithExpr> aexprs)
	{
		// Cf. above getters and Scribble.g children order
		super.addScribChildren(rv, block);
		addChild(ass);
		addChildren(avars);
		addChildren(aexprs);
	}
	
	@Override
	public AssrtGRecursion dupNode()
	{
		return new AssrtGRecursion(this);
	}
	
	@Override
	public void decorateDel(DelFactory df)
	{
		((AssrtDelFactory) df).AssrtGRecursion(this);
	}

	@Override
	public AssrtGRecursion reconstruct(RecVarNode recvar,
			ProtoBlock<Global> block)
	{
		throw new RuntimeException(
				"[assrt] Deprecated for " + getClass() + ": " + this);
	}

	public AssrtGRecursion reconstruct(RecVarNode recvar,
			ProtoBlock<Global> block, AssrtAssertion ass,
			List<AssrtIntVarNameNode> avars, List<AssrtArithExpr> aexprs)
	{
		AssrtGRecursion dup = dupNode();
		dup.addScribChildren(recvar, block, ass, avars, aexprs);
		dup.setDel(del());  // No copy
		return dup;
	}

	@Override
	public AssrtGRecursion visitChildren(AstVisitor v) throws ScribException
	{
		RecVarNode recvar = (RecVarNode) visitChild(getRecVarChild(), v);
		GProtoBlock block = visitChildWithClassEqualityCheck(this, getBlockChild(),
				v);
		AssrtAssertion tmp = getAssertionChild();
		AssrtAssertion ass = (tmp == null) 
				? null
				: (AssrtAssertion) visitChild(tmp, v);
		List<AssrtIntVarNameNode> avars = visitChildListWithClassEqualityCheck(
				this, getAnnotVarChildren(), v);
		List<AssrtArithExpr> aexprs = visitChildListWithClassEqualityCheck(this,
				getAnnotExprChildren(), v);
		return reconstruct(recvar, block, ass, avars, aexprs);
	}

	@Override
	public String toString()
	{
		return Constants.REC_KW + " " + getRecVarChild() //+ " " + this.ass
				+ annotToString()
				+ " " + getBlockChild();
	}
}















/*
	public final List<AssrtIntVarNameNode> annotvars;
	public final List<AssrtArithExpr> annotexprs;
	public final AssrtAssertion ass;  // cf. AssrtGProtoHeader  // FIXME: make specific syntactic expr

	public AssrtGRecursion(CommonTree source, RecVarNode recvar, GProtoBlock block)
	{
		this(source, recvar, block, //null);
				Collections.emptyList(), Collections.emptyList(),
				null);
	}
	
	public AssrtGRecursion(CommonTree source, RecVarNode recvar, GProtoBlock block, //AssrtAssertion ass)
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
//*/
