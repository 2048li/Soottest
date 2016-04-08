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
		//String methodname = "getRuntime";
		
		//getgh();
	    
		boolean detect = false;
		detect = isSilentInstallapk();
		if (detect)
			System.out.println("this apk mostly has silent install");
		else
			System.out.println("this apk may not has silent install");
		
		//PackManager.v().writeOutput();
	//	PackManager.v().getPack("cg").apply();
	//	PackManager.v().getPack("wjtp").apply();
		
		//soot.Main.main(args);
	}
	
	
	//judge the apk
	public static boolean isSilentInstallapk()
	{
		System.out.println("to call the ufgl");
		UnitGraph[] apkug = ufgl().var;
		int apkuglen = ufgl().uglen;
		System.out.println("to call the dectgraph");
		FindPath[] apkfp = Dectgraph(apkug,apkuglen);
		if (apkfp.length != 0) //if there is one findpath at least,then this apk is classified to silent install
			return true;
		else
			return false;
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
	
	
	static int Max = 5000;
	//get the graph list of the apk[each method in each class]
	static UnitGraphLen ufgl()
	{
		System.out.println("in the ufgl method and to get UnitGraph array");
		UnitGraphLen var1 = new UnitGraphLen(); 
		UnitGraph var[] = var1.var;
		UnitGraph tmp ;
		int i = 0;
		for (SootClass c:Scene.v().getApplicationClasses())	
			for (SootMethod m:c.getMethods())
			{
				tmp = new BriefUnitGraph(m.getActiveBody());
				var[i] = tmp;
				i++;
			}
		var1.uglen = i+1;
		System.out.println("now to return--");
		return var1;
		
	}
	
	//traverse graph list to find target graph
	static FindPath[] Dectgraph(UnitGraph[] ug, int apkuglen)
	{
		System.out.println("in the Dectgraph method---");
		System.out.println("the length of the unitgraph is :"+apkuglen);
		if (apkuglen != 0)
		{
	//	int len = ug.length;
		UnitGraph[] candidate = new UnitGraph[Max];

		int i;int j=0;
		Unit ut;
		Unit speciall[][] = new Unit[Max][Max];
		UnitLen tgfh = new UnitLen();
		Unit gfh[] = tgfh.ug; //just get the head[0] of one graph is ok...
		boolean bl = false;
		System.out.println("to get head of graphs and candidate graphs~~~");
		for (i=0;i<apkuglen;i++)
		{
			java.util.Iterator<Unit> it = ug[i].iterator();
			gfh[i] = ug[i].getHeads().get(0);
			while(it.hasNext())
			{
				ut = it.next();
				if (ut.toString().isEmpty() == false && ut.toString().contains("runtime"))
				{
					if (candidate[i] != ug[i])
					    candidate[i] = ug[i]; 		
					bl = true;
					continue;
				}
				if (bl == true && ut.toString().isEmpty() == false && ut.toString().contains("specialinvoke"))
				{
					speciall[i][j] = ut;
					j++;
				}
				
				
			}
		}
		
		// traverse heads of graphs and compare with the specialinvoke list to find the target;
		// I find that some heads of the graph are mostly like  some units
		//I just consider the simple case I wrote.
		// extract string from head and then judge whether contained in the specialinvoke unit
		
        String tmp = new String();
        String tmp2 = new String();
        int count =0;
        boolean find = false;
        int c = 0;
        UnitGraph findg[] = new UnitGraph[Max];
        FindPath[] fp = new FindPath[Max];
        Unit tmpf;
		int e = 0;
        
		System.out.println("now to get the findg graph~~~");
		for (i = 0; i< gfh.length;i++)
		{
		  for (c= 0;c<gfh.length;c++) //find the target from the graphs except the one
			  if (c != i )
			  {
			     count = speciall[c].length;
		         tmp = gfh[c].toString().trim();
			//cause that the head like this (after trim): $r0:=this:com.shentanli.silentinstall.Bodymethod
			//and I just want to get the class name
		       	tmp2 = tmp.substring(4);
		     	for (j=0;j<count;j++)
				if (speciall[c][j].toString().isEmpty() == false && speciall[c][j].toString().contains(tmp2))
				{
					find = true;
					//the length of special list first dimensionality is equal to candidate.
					findg[i] = candidate[c]; //add the found graph to the findgraph list, which then use to find cmd
					
				}	
		     	
			  }
		    
	    	//find cmd from find graphs
			// if find , add these two to the findpath list;
			// to judge if an apk has silent install equals to judge if the findpath is null
	       
		  boolean start = false;
		  boolean end = false;
		  if(find)
		  {
		    //fp[i].count ++;
	        fp[i].start = findg[i];
	        //ignore these following note...
	        //if doing in this way , the fp will not continuous...
	        //maybe should build path from graph to unit...
	       // fp[i].end = findg[i];
	        start = true;
		  }
		
		System.out.println("now to build the findpath");
		if (find)
	    	for ( e = 0;e<findg.length;e++)
	    	{
	    		java.util.Iterator<Unit> tra = findg[e].iterator();
	    		while(tra.hasNext())
	    		{
	    			if(end == false)
	    			{
	    			tmpf = tra.next();
	    			if (tmpf.toString().isEmpty() == false && tmpf.toString().contains("pm install"))
	    			{
	    				//how to add to findpath??
	    				//fp[i].count ++;
	    				//fp[i].start = findg[i];
	    				fp[i].end = ug[i];
	    				end = true;
	    				//fp[i].end = findg[e];
	    			}
	    			}
	    			
	    		}
	    	}		
		System.out.println("now to set the findpath count");
		if (find && start && end)
			fp[i].count = fp[i].count+1;	
	}
		
		System.out.println("----return findpah---");
		return fp;
		}
	
	return null;
	
}
}
