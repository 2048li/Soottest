package com.shentanli.example.soot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.text.html.HTMLDocument.Iterator;

import soot.Body;
import soot.MethodOrMethodContext;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.UnitBox;
import soot.jimple.Stmt;
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
	//private String force_android_jar = "android.jar";
	//private final static String appAPK = "xiaoxiaole.apk"; //replace the name you want
	//private final static String appAPK = "/home/shentanli/eclipseworkspace/Cfgbuild_S/src/com/shentanli/example/soot/xiaoxiaole.apk";
	//private final static String appAPK = "/home/shentanli/eclipseworkspace/Cfgbuild_S/src/com/shentanli/example/soot/app-debug.apk";

	private final static String androidJAR = "/home/shentanli/tmpgithub/Soottest/Cfgbuild_S/src/com/shentanli/example/soot/android.jar";
	//private final static String androidJAR = "/Users/shentanli/Documents/githubfile/Soottest/Cfgbuild_S/src/com/shentanli/example/soot/android.jar";
	//private String force_android_jar = "android.jar";
	//private final static String appAPK = "xiaoxiaole.apk"; //replace the name you want
	//private final static String appAPK = "/Users/shentanli/Documents/githubfile/Soottest/Cfgbuild_S/src/com/shentanli/example/soot/app-debug.apk"; mac-pc
	private final static String appAPK = "/home/shentanli/tmpgithub/Soottest/Cfgbuild_S/src/com/shentanli/example/soot/app-debug.apk"; //debian-pc

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

		System.out.println("now to run packmanager.apply");
		PackManager.v().getPack("wjtp").apply();

		PackManager.v().runPacks();
		String methodname = "getRuntime";
		System.out.println("now to go to getgh mtehod");
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

				//	System.out.println("method---"+m.toString());
					if (m.hasActiveBody() && m.getActiveBody().getUnits().isEmpty() == false) {
					//List<UnitBox> u = m.getActiveBody().getAllUnitBoxes();
						for (java.util.Iterator<Unit> uit = m.getActiveBody().getUnits().iterator();uit.hasNext();)
						{
						    Stmt s = (Stmt) uit.next();
						    System.out.println("from stmt all-----"+s.toString());
						    if (s.toString().contains("install"))
						    {
						    	List<UnitBox> pu = s.getBoxesPointingToThis();
						    	for (int p = 0;p<pu.size();p++)
						    		System.out.println("the unitbox pointing to installstr"+pu.toString());
						    	
						    }
						    
							if (s.containsInvokeExpr())
							{
								//System.out.println("from stmt--"+s.toString());
							}
							
						}
						System.out.println("stmt done----");
				/*	int len = u.size();
					for (int i = 0;i<len;i++)
						System.out.println("the box---"+u.get(i).toString());
					}*/

			//		System.out.println("method---"+m.toString());
		//			String sig = m.getSignature();
			//		System.out.println("the signature of method---"+sig);
					
					/*Class<? extends SootMethod> classname = m.getClass();
					System.out.println("class of sootmethod:"+classname.getName());
					System.out.println("the classloader of the last class:"+classname.getClassLoader().toString());*/
					
					if ( m.hasActiveBody() && m.getActiveBody().getUnits().isEmpty() == false) {
						
						List<UnitBox> tmp1 = m.getActiveBody().getAllUnitBoxes();
			//			for (int tc = 0; tc<tmp1.size(); tc++)
			//				System.out.println("----getallunitboxes---:"+tmp1.get(tc).toString());
						List tmp2 = m.getActiveBody().getUseAndDefBoxes();
						for (int tc=0; tc<tmp2.size();tc++)
							//if (tmp2.get(tc).equals("runtime")) 
						{
					//		if (tmp2.get(tc).getClass().getName().equalsIgnoreCase(c1) || tmp2.get(tc).getClass().getName().equalsIgnoreCase(c2) || 
					//				tmp2.get(tc).getClass().getName().equalsIgnoreCase(c3) )
					//		if(tmp2.get(tc).getClass().getName().equalsIgnoreCase(c2))
				//			{				
							//System.out.println("the classname from the use&defboxes###---"+tmp2.get(tc).getClass().getName());
				//			if (tmp2.get(tc).toString().contains("Runtime") || tmp2.get(tc).toString().contains("install"))
				//			{
								
								/*File file = new File("output.txt");
								FileOutputStream fis = new FileOutputStream(file);
								PrintStream out = new PrintStream(fis);*/
				//				PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
				//				System.setOut(out);
								
								System.out.println("!!!getuseanddefboxes---**"+tmp2.get(tc).toString());
								
								if (tmp2.get(tc).toString().contains("install"))
								{
								
								}
			//				}
							
			//				}
						    	
						}
						
				/*		List<ValueBox> value = m.getActiveBody().getDefBoxes();
						int len1 = value.size();
						int i;
						for (i= 0; i<len1;i++)
						{
							//if (value.get(i).toString().contains("install"))
							System.out.println("getdefboxes&&--" + value.get(i).toString());
							
						}
		
						java.util.Iterator<ValueBox> tm = m.getActiveBody().getUseBoxes().iterator();
						if (tm.hasNext())
							System.out.println("getuseboxes ----"+tm.toString());
						java.util.Iterator<Unit> u = m.getActiveBody().getUnits().snapshotIterator();
						if(u.hasNext()){
							System.out.println("getUnits**---" + u.toString());
						
							}*/
						    

					}
			}
			//Scene.v().setEntryPoints(tmpent);
		}
	}
		//System.out.println("-----to get the graph---");	
		
	/*	System.out.println("next to call graph");  
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
		}*/
	
		
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
