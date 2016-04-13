package com.shentanli.example.soot;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
import soot.toolkits.graph.UnitGraph;

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
	//private final static String appAPK = "/home/shentanli/tmpgithub/Soottest/Cfgbuild_S/src/com/shentanli/example/soot/app-debug.apk"; //debian-pc
   
	private String appAPK;
	public static void inialiseSoot(String appAPK)
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
	
	//get apkname string found in the given path
	public static List<String> Getapk(String path, List<String> data)
	{
		System.out.println("now to get apk list");
		int i ;
		File f = new File(path);
		File[] fs = f.listFiles();
		int len = f.listFiles().length;
		String tmp = new String();
		for (i =0;i<len;i++)
		{
			if (fs[i].getName().endsWith(".apk"))
			{
			//	System.out.println("get apk");
				tmp = path + "/" +fs[i].getName();
			//	System.out.println("the complete apkname is:"+tmp);
				data.add(tmp);
			}
		}
		
		return data;
		
	}
	
	//to apply this method to apk set; set the env cyclically. 
	@SuppressWarnings("unchecked")
	public static List<String> Setenv(){
		//to get the path from user
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String str = null;
		
		List data = new ArrayList<String>();
		
		try {
			System.out.println("Please enter your apk set path:");
			str = br.readLine();
			data = Getapk(str,data);	
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
		
		
	}
	
	
	//the global vars..
    static	UnitGraphLen var1 = new UnitGraphLen(); 
	static UnitGraph[] apkug;
	static int apkuglen=0;
	static FindPath[] apkfp;
	static int q;
	static boolean result = false;
	static UnitGraph var[];
	static UnitGraph tmp;
	static UnitGraph[] candidate;
	static UnitLen tgfh = new UnitLen();
	static UnitGraphLen findgt = new UnitGraphLen();
   
	
	
	
	
	
	public static void main (String[] args)
	{
		//inialiseSoot();
		
		List<String> data = new ArrayList<String>();
		data = Setenv();
		int i;
		boolean detect = false;
		
	//	data.add("/home/shentanli/tmpgithub/Soottest/Cfgbuild_S/src/com/shentanli/example/soot/app-debug.apk");
		
		for (i=0; i < data.size(); i++)
		{
			System.out.println("the apkname is:"+data.get(i));
			System.out.println("now to set this apk as input");
			inialiseSoot(data.get(i));
			System.out.println("inialise soot done,then to detect...");
			PackManager.v().getPack("wjtp").apply();
			PackManager.v().runPacks();
			System.out.println("to call issilentinstallapk");
			detect = isSilentInstallapk();
			if (detect)
				System.out.println("this apk:"+data.get(i)+"mostly has silent install");
			else
				System.out.println("this apk:"+data.get(i)+"may not has silent install");		
			
		}
		
		
		

		// the following codes are used to plugin 
	//	PackManager.v().getPack("jtp").add(new Transform("jtp.myAnalysis", new MyAnalysis()));

	/*	System.out.println("now to run packmanager.apply");
		PackManager.v().getPack("wjtp").apply();

		PackManager.v().runPacks();
		//String methodname = "getRuntime";
		
		//getgh();
	    
	//	boolean detect = false;
		detect = isSilentInstallapk();
		if (detect)
			System.out.println("this apk mostly has silent install");
		else
			System.out.println("this apk may not has silent install"); */
		
		//PackManager.v().writeOutput();
	//	PackManager.v().getPack("cg").apply();
	//	PackManager.v().getPack("wjtp").apply();
		
		//soot.Main.main(args);
	}
	
	
	//judge the apk
	public static boolean isSilentInstallapk()
	{
	//	System.out.println("to call the ufgl");
	//	UnitGraphLen var1 = new UnitGraphLen(); 
		ArrayList<UnitGraph> graphs = getGraphListOfApk();
	//	System.out.println("the length of the ufgl is "+ apkuglen);
		
	/*	for (int i = 0;i<apkuglen;i++)
		{
			if (apkug[i].size() != 0 && apkug[i].toString().isEmpty() ==false)
			{
				System.out.println("the size of apkup "+i+"is"+apkug[i].size());
			System.out.println("the returned apkgraph arrary---"+i + apkug[i].getHeads());
			}
		}
	*/
	//	System.out.println("to call the dectgraph");
		return DectGraph(graphs);
		//int q ;
	//	boolean result = false;
	

		/*if (apkfp.length != 0) //if there is one findpath at least,then this apk is classified to silent install
			return true;
		else
			return false;*/
	}
	
	
	//the call graph... just to test and print
	public static void getgh()
	{
		
		
		List<SootMethod> tmpent = new ArrayList<SootMethod>();
	
		for (SootClass c:Scene.v().getApplicationClasses())
		
		{
			System.out.println("sootclass "+ c);			
			for (SootMethod m:c.getMethods()){
			//	if ( m.getName().indexOf(methodname) != -1)
					tmpent.add(m);	
					
					System.out.println("method---"+m.toString());
					if (m.hasActiveBody() && m.getActiveBody().getUnits().isEmpty() == false) {
					//List<UnitBox> u = m.getActiveBody().getAllUnitBoxes();
									
						//graph relating
						Ugf(m.getActiveBody());
		
						
						for (java.util.Iterator<Unit> uit = m.getActiveBody().getUnits().iterator();uit.hasNext();)
						{
						    Stmt s = (Stmt) uit.next();
						//    System.out.println("from stmt all-----"+s.toString());
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
				//		System.out.println("stmt done----");
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
		System.out.println("-----to get the graph---");	
		
		System.out.println("next to call graph");  
		@SuppressWarnings("unused")
		CallGraph cg = Scene.v().getCallGraph();
		
	  java.util.Iterator<MethodOrMethodContext> tt = cg.sourceMethods();
	  
	 //  System.out.println("look at the method in the graph");
		while(tt.hasNext())
		{
			
		//	System.out.println("graph method---"+tt.next().toString());
		}

	    
		//System.out.println("to show the call graph");
		while (cg.iterator().hasNext())
		{
			
			//System.out.println("in the showing method");
		//	if (tt.next().toString().contains("exec"))
		//	{
				
			//System.out.println("the cg node ---"+cg.edgesInto(tt.next()));
		//	}
			Edge e = cg.iterator().next(); 
			System.out.println("the edge of the callgraph"+e.toString());
			Body b = e.src().getActiveBody();
			//internalTransform(b,methodname);
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
	
	
	static void Ugf(Body body)
	{
		UnitGraph ugf = new BriefUnitGraph(body);
	    //heads and tails of bfg
		java.util.Iterator<Unit>  hd = ugf.getHeads().iterator();
		java.util.Iterator<Unit> tl = ugf.getTails().iterator();
		while(hd.hasNext())
		{
			System.out.println("heads of briefunitgraph---"+hd.next().toString());
			
		}
		while (tl.hasNext()){
		//	if (tl.next().toString().isEmpty() == false && tl.next().toString().contains("install"))
			 System.out.println("tails of briefunitgraph--"+tl.next().toString());

			// if(tl.next().toString().contains("install"))
				// System.out.println("tail contails install---");
		}
		      
	//   java.util.Iterator<Unit> ut = ugf.getExtendedBasicBlockPathBetween(hd.next(), tl.next()).iterator(); 
	//   while (ut.hasNext())
	//	   System.out.println("path from hd to tl---**"+ut.next().toString());
		java.util.Iterator<Unit> it = ugf.iterator();
		while(it.hasNext())
		{
			//Unit u = it.next();
			System.out.println("the graph unit^^^^^"+it.next().toString());
		}   
		
		
	}
	
	
//	static int Max = 5000;
	//get the graph list of the apk[each method in each class]
	static ArrayList<UnitGraph> getGraphListOfApk()
	{
	//	System.out.println("the varlebn is "+ var1.uglen);
	//	System.out.println("in the ufgl method and to get UnitGraph array");
		//UnitGraphLen var1 = new UnitGraphLen(); 
		
	//	UnitGraph tmp ;
		ArrayList<UnitGraph> graphs = new ArrayList<UnitGraph>();
	   
		for (SootClass c:Scene.v().getApplicationClasses())	
			for (SootMethod m:c.getMethods())
			{
				if (m.hasActiveBody())
				{
			    	tmp = new BriefUnitGraph(m.getActiveBody());
		//		System.out.println("the value of the var1 "+ i +tmp.toString());
			    	if(tmp.toString().isEmpty()==false)
			    	{
			        	
			        	graphs.add(tmp);
				
		     		}
				}
			}
	//	System.out.println(" uglen is "+var1.uglen);
	//	System.out.println("the i is "+i);
	//	System.out.println("now to return--");
		return graphs;
		
	}
	
	
	static boolean  ContainsRun(UnitGraph ug)
	{
		 
		if (ug.size() <= 0)
			return false;
			
		java.util.Iterator<Unit> it = ug.iterator();

		while(it.hasNext())
		{
			Unit ut = it.next();

			//System.out.println("the ut is empty ??"+ut.toString().isEmpty());
			if (ut.toString().isEmpty() == false && ut.toString().contains("Runtime")) // if contains specialinvoke means that it is not the bottom 
			{
	
				   return true;  //candidate is not concrete so if in for use the length of candidate may get the wrong result.

			}
	
		}	
		
		return false;
	
	}
	
	
	
	
	
	//traverse graph list to find target graph
	static boolean DectGraph(ArrayList<UnitGraph> graphs)
	{
		System.out.println("in the Dectgraph method---");
	//	System.out.println("the length of the unitgraph is :"+apkuglen);
		if (graphs.size() != 0)
		{
		
		ArrayList<Unit> specialunits = new ArrayList<Unit>();
		ArrayList<UnitGraph> specialgraphs = new ArrayList<UnitGraph>();
		 for (UnitGraph ug: graphs)
		 {
			 if(ContainsRun(ug))
			 {
				 AddSpecialUnits(ug, specialunits, specialgraphs);
				 
			 }
		 }
		 for(int i = 0;i<specialunits.size();i++){
			 for (UnitGraph ug : graphs)
			 {
				 String scut = specialunits.get(i).toString().trim();
				 String hcut = ug.getHeads().get(0).toString();
			//	 System.out.println("hcut=="+ug.getHeads().get(0));
				 int left = scut.indexOf('<');
				 int rigth = scut.indexOf(':',left<0 ?0:left);
				 if (left <0 || rigth <0)
				 {
					 System.out.println("left-"+hcut);
					 System.out.println("scut-"+scut);
					 continue;
				 }
				 scut = scut.substring(left+1, rigth);
				// System.out.println("scut--"+scut);

				 
				 if (hcut.contains(scut)){
					 if (ContainsInstall(specialgraphs.get(i)) || ContainsInstall(ug))
					     return true;
				 }
				 
				 
			 }
			 
		 }
		 return false;
		
		}
		return false;
			
	}

		

	private static boolean ContainsInstall(UnitGraph unitGraph) {
		// TODO Auto-generated method stub
		for (Unit u: unitGraph)
			if (u.toString().isEmpty() == false && u.toString().contains("pm install"))
				return true;
		
		return false;
	}

	private static void AddSpecialUnits(UnitGraph ug,
			ArrayList<Unit> specialunits, ArrayList<UnitGraph> specialgraphs) {
		// TODO Auto-generated method stub
		for (Unit u : ug)
		{
			if (u.toString().isEmpty() == false && u.toString().contains("specialinvoke")){
				specialunits.add(u);
				specialgraphs.add(ug);
			}
			 
		}
	
	
}
}
