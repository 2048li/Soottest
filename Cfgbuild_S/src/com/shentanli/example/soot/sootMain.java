package com.shentanli.example.soot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.text.html.HTMLDocument.Iterator;

import soot.*;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.CompleteUnitGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;

public class sootMain {
	private static boolean SOOT_INITIALIZED = false;
	//private final static String androidJAR = "android.jar";
	//private final static String androidJAR = "/home/shentanli/eclipseworkspace/Cfgbuild_S/src/com/shentanli/example/soot/android.jar";
	private final static String androidJAR = "/Users/shentanli/Documents/githubfile/Soottest/Cfgbuild_S/src/com/shentanli/example/soot/android.jar";
	//private String force_android_jar = "android.jar";
	//private final static String appAPK = "xiaoxiaole.apk"; //replace the name you want
	//private final static String appAPK = "/home/shentanli/eclipseworkspace/Cfgbuild_S/src/com/shentanli/example/soot/xiaoxiaole.apk";
	private final static String appAPK = "/Users/shentanli/Documents/githubfile/Soottest/Cfgbuild_S/src/com/shentanli/example/soot/app-debug.apk";
	//private final static String appAPK = "/home/shentanli/eclipseworkspace/Cfgbuild_S/src/com/shentanli/example/soot/app-debug.apk";
	public static void inialiseSoot()
	{
		if (SOOT_INITIALIZED)
			return;
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_prepend_classpath(true);
		Options.v().set_validate(true);
		Options.v().set_output_format(Options.output_format_jimple);
		Options.v().set_process_dir(Collections.singletonList(appAPK));
		Options.v().set_force_android_jar(androidJAR);
		Options.v().set_src_prec(Options.src_prec_apk);
		Options.v().set_soot_classpath(androidJAR);
		Options.v().set_whole_program(true);
		
		//Scene.v().addBasicClass(android.widget.TextView,BODIES);
		Scene.v().loadNecessaryClasses();
		SOOT_INITIALIZED = true;	
		
	}
	
	public static void main (String[] args)
	{
		inialiseSoot();

		// the following codes are used to plugin 
	//	PackManager.v().getPack("jtp").add(new Transform("jtp.myAnalysis", new MyAnalysis()));
		PackManager.v().runPacks();
		String methodname = "getRuntime";
		getgh(methodname);
		System.out.println("done");
		
		//PackManager.v().writeOutput();
	//	PackManager.v().getPack("cg").apply();
	//	PackManager.v().getPack("wjtp").apply();
		
		//soot.Main.main(args);
	}
	
	
	public static void getgh(String methodname)
	{
		
		
		List<SootMethod> tmpent = new ArrayList<SootMethod>();
	
		for (SootClass c:Scene.v().getApplicationClasses())
		
		{
			System.out.println("sootclass "+ c);			
			for (SootMethod m:c.getMethods()){
			//	if ( m.getName().indexOf(methodname) != -1)
					tmpent.add(m);	
					System.out.println("method---"+m.toString());
					if ( m.getActiveBody().getUnits().isEmpty() == false) {
						List<ValueBox> value = m.getActiveBody().getDefBoxes();
						List<UnitBox> u = m.getActiveBody().getAllUnitBoxes();
						int len = u.size();
						int len1 = value.size();
						int i;

						for (i= 0; i<len1;i++)
							System.out.println("the value--" + value.get(i).toString());
						for (i = 0; i < len; i++)
						System.out.println("the box---" + u.get(i).toString());
					}
			}
			//Scene.v().setEntryPoints(tmpent);
		}
		//System.out.println("-----to get the graph---");	
		
		System.out.println("next to call graph");  
		@SuppressWarnings("unused")
		CallGraph cg = Scene.v().getCallGraph();
		java.util.Iterator<MethodOrMethodContext> tt = cg.sourceMethods();
		System.out.println("method context ----"+tt.toString());

	    
		System.out.println("to show the graph");
		if (cg.iterator().hasNext())
		{
			
			System.out.println("in the showing method");
			Edge e = cg.iterator().next();
			Body b = e.src().getActiveBody();
			internalTransform(b,methodname);
		}
	
		
	//	CompleteUnitGraph cug = new CompleteUnitGraph(null);
	
		
	}
	
	
	protected static void internalTransform(Body body,String name){
		SootMethod method = body.getMethod();
/*		if (method.getName().equals(name) == false) // replace the methodname with the name u want to analysis
		{MethodOrMethodContext lll = tt.next(); 
		System.out.println("methodorcmethodcontext ---"+lll.toString());
			System.out.println("method name --"+method.getName().toString()) ;
		}
		*/
		BriefUnitGraph bg = new BriefUnitGraph(body);
		List<Unit> ulist = bg.getHeads();
		Unit u = ulist.get(0);
		while (!unitBelongList(u, bg.getTails()))
		{
			u=bg.getSuccsOf(u).get(0);
			System.out.println("ege name:--"+u.toString());
		}
	/*	ExceptionalUnitGraph g = new ExceptionalUnitGraph(body);
		List<Unit> ulist = g.getHeads();
		Unit u = ulist.get(0);//make one of the funcs as entry: get one control flow
		while(!unitBelongList(u,g.getTails()))//traverse till the last element
		{
			u=g.getSuccsOf(u).get(0);//get the next node
			System.out.println("this is the result---:"+u.toString());
		}*/
	}
	
	static boolean unitBelongList(Unit u, List<Unit> g)//judge if the node is in the list
	{
		for (Unit i:g)
		{
			if (i.equals(u))
				return true;
		}
		return false;
	}
	
	

}
