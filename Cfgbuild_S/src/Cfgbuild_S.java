import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Local;
import soot.PackManager;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.BodyTransformer;
import soot.Body;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraph;


public class Cfgbuild_S {
	
	
	public static void main(String[] args){
		Options.v().set_src_prec(Options.src_prec_apk);
		
		Options.v().set_output_format(Options.output_format_dex);
		
		Scene.v().addBasicClass("java.io.PrintStream",SootClass.SIGNATURES);
		Scene.v().addBasicClass("java.lang.System",SootClass.SIGNATURES);
		
		PackManager.v().getPack("jtp").add(new Transform("jtp.myInstrumenter", new BodyTransformer(){
			@SuppressWarnings("rawtypes")
			@Override
			protected void internalTransform(final Body b, String phaseName, Map<String, String> options)
			{
				final PatchingChain units = b.getUnits();
				for (Iterator iter = units.snapshotIterator(); iter.hasNext();){
					final Unit u = (Unit) iter.next();
					u.apply(new AbstractStmtSwitch(){
						@SuppressWarnings("unchecked")
						public void caseInvokeStmt(InvokeStmt stmt){
							//TODO
							InvokeExpr invokeExpr = stmt.getInvokeExpr();
							if (invokeExpr.getMethod().getName().equals("OnDraw"))
							{
								Local tmpRef = addTmpRef(b);
								Local tmpString = addTmpString(b);
								
								units.insertBefore(Jimple.v().newAssignStmt(tmpRef, Jimple.v().newStaticFieldRef(Scene.v().getField("").makeRef())), u);
								units.insertBefore(Jimple.v().newAssignStmt(tmpString, StringConstant.v("hello")), u);
								SootMethod toCall = Scene.v().getSootClass("java.io.PrintStream").getMethod("void println(java.lang.String)");
								units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef(), tmpString)), u);
								
								b.validate();
							}
							
						}
					});
				}
				
			}

			
		}));
		soot.Main.main(args);
		
	}
	
	private static Local addTmpRef(Body body)
	{
		Local tmpRef = Jimple.v().newLocal("tmpRef", RefType.v("java.io.PrintStream"));
		body.getLocals().add(tmpRef);
		return tmpRef;
		
	}
	
	private static Local addTmpString(Body body)
	{
		Local tmpString = Jimple.v().newLocal("tmpString", RefType.v("java.lang.String"));
		body.getLocals().add(tmpString);
		return tmpString;
	}
	
	
	protected void internalTransform(Body body, String string, Map map){
		SootMethod method = body.getMethod();
		if (method.getName().equals("MethodName") == false) // replace the methodname with the name u want to analysis
		{
			return ;
		}
		ExceptionalUnitGraph g = new ExceptionalUnitGraph(body);
		List<Unit> ulist = g.getHeads();
		Unit u = ulist.get(0);//make one of the funcs as entry: get one control flow
		while(!unitBelongList(u,g.getTails()))//traverse till the last element
		{
			u=g.getSuccsOf(u).get(0);//get the next node
			System.out.println("this is the result---:"+u.toString());
		}
	}
	
	boolean unitBelongList(Unit u, List<Unit> g)
	{
		for (Unit i:g)
		{
			if (i.equals(u))
				return true;
		}
		return false;
	}

}
