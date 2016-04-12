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
		UnitGraphLen var1 = new UnitGraphLen(); 
		UnitGraph[] apkug = ufgl(var1).var;
		int apkuglen = ufgl(var1).uglen;
		System.out.println("the length of the ufgl is "+ apkuglen);
		
	/*	for (int i = 0;i<apkuglen;i++)
		{
			if (apkug[i].size() != 0 && apkug[i].toString().isEmpty() ==false)
			{
				System.out.println("the size of apkup "+i+"is"+apkug[i].size());
			System.out.println("the returned apkgraph arrary---"+i + apkug[i].getHeads());
			}
		}
	*/
		System.out.println("to call the dectgraph");
		FindPath[] apkfp = Dectgraph(apkug,apkuglen);
		int q ;
		boolean result = false;
		System.out.println("now to set the result");
		for (q=0;q<apkuglen;q++)
		{
		//	System.out.println("count is"+apkfp[q].count);
			if (apkfp[q].count > 0) {
			//	System.out.println("in the apkfg count cal");
				result = true;
			//	System.out.println("to return result" + result);
				return result;
			}
		}
		return result;


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
	
	
	static int Max = 5000;
	//get the graph list of the apk[each method in each class]
	static UnitGraphLen ufgl(UnitGraphLen var1)
	{
		System.out.println("the varlebn is "+ var1.uglen);
		System.out.println("in the ufgl method and to get UnitGraph array");
		//UnitGraphLen var1 = new UnitGraphLen(); 
		
		UnitGraph var[] = var1.var;
		UnitGraph tmp ;
		int i = -1;
	
		for (SootClass c:Scene.v().getApplicationClasses())	
			for (SootMethod m:c.getMethods())
			{
				tmp = new BriefUnitGraph(m.getActiveBody());
		//		System.out.println("the value of the var1 "+ i +tmp.toString());
				if(tmp.toString().isEmpty()==false)
				{
				i=i+1;
				var[i] = tmp;
				
				}
			}
		var1.uglen = i;
		System.out.println(" uglen is "+var1.uglen);
		System.out.println("the i is "+i);
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
		//UnitGraphLen candidatel = new UnitGraphLen();
		UnitGraph[] candidate = new UnitGraph[apkuglen];
        int cac = 0; //count the candidate 
        
		int i;int j=-1; int fi=-1;
		int d = 0;
		Unit ut;
		UnitLen tgfh = new UnitLen();
		Unit gfh[] = tgfh.ug; //just get the head[0] of one graph is ok...
		Unit[][] speciall = tgfh.specialll;
		boolean[] bl = new boolean[apkuglen];
		boolean[] bs = new boolean[apkuglen];
		//indicate the pm install unit
		boolean[] pi = new boolean[apkuglen];
		for (int tt = 0; tt<apkuglen;tt++)
		{
			bl[tt] = false;
			bs[tt] = false;
			pi[tt] = false;
		}
		
		boolean nz = false;
		System.out.println("to get head of graphs and candidate graphs~~~");
		for (i=0;i<apkuglen;i++)
		{
		//	System.out.println("the ug[i] size is .."+ug[i].size());
		//.out.println("i is: " + i);
			if (ug[i].size() > 0)
			{
			java.util.Iterator<Unit> it = ug[i].iterator();
		//	System.out.println("now to get the head");
			gfh[i] = ug[i].getHeads().get(0);
			while(it.hasNext())
			{
				
			//	System.out.println("in the candidate find for");
				ut = it.next();
		//		System.out.println("the  ut value:"+ut.toString());
				if (ut.toString().contains("pm install"))
				{
					pi[i] = true;
					System.out.println("this graph has 'pm install' unit");
					
				}
				//System.out.println("the ut is empty ??"+ut.toString().isEmpty());
				if (ut.toString().isEmpty() == false && ut.toString().contains("Runtime")) // if contains specialinvoke means that it is not the bottom 
				{
		//			System.out.println("in the runtime judge for");
				//	if (candidate[i] != ug[i]){
					    candidate[i] = ug[i];  //candidate is not concrete so if in for use the length of candidate may get the wrong result.
		//			    System.out.println("set the candidate to ug[i]");
				//	}
		//			System.out.println("set the bl true");
					bl[i] = true; //actually do not think about this condition: the ug is the one that been invoked , in this case is the bodymethod itself. 
					// so how to judge that the graph is the callee class??----if there is no other specialinvok, that means that the method of the class is the bottom.
		//			System.out.println("i in candidate is "+i);
					//continue;
				}
				// to judge this graph contain specialinvoke
				if (ut.toString().contains("specialinvoke"))
				{
					bs[i] = true;
				//	System.out.println("unit contains specialinvoke and set true");
				}
			}	
			
			
			
			
				
				if ( bl[i] ==  true && bs[i] == true) //&& /* ut.toString().isEmpty() == false && */ ut.toString().contains("specialinvoke"))
				{
					System.out.println("i is true and get the specialinvoke"+i);
			//		System.out.println("get the specialinvoke");
					java.util.Iterator<Unit> ttt = candidate[i].iterator();
					while (ttt.hasNext())
					{
						Unit tut = ttt.next();
					//	System.out.println("the unit from ttt is "+ tut.toString());
					//    if (tut.toString().contains("specialinvoke") )
					//    {
				    // 	System.out.println("get the specialinvoke");
				   //  	System.out.println("the specialinvoke is "+ tut.toString());
				     	j = j+1;
				     	fi = fi+1;
			//	     	System.out.println("j is "+j);
			//	     	System.out.println("i is in this time is "+i);
				    	speciall[i][j] = tut; // if so the speciall is not concrete...
				    	//j++;
				    	//fi++;
				    	
				    	tgfh.speicalfi[i] = fi;
						tgfh.specialtw[i] = j;
			//			System.out.println("i is "+ fi);
			//			System.out.println("j is "+ j);
					//    }
					   
					}
					
					
					//continue;
				}
						
			//}
	//		System.out.println("the nz is true");
			nz = true;
			}
		}
        
		if (nz= true)
		{
		//	System.out.println("to set the specialfi and tw count");
			tgfh.ul = i;//the length of the gfh
		//	tgfh.speicalfi[i] = fi;//the first length of the speciall;actually this may be unnecessay: can use the boolean array to indicate whether the speical is null;
		//	tgfh.specialtw[i] = j;//the secodn length of the speciall
	//		System.out.println("the length of the gfh is "+ tgfh.ul);
		//	System.out.println("i is "+ fi);
		//	System.out.println("j is "+ j);
			
		
		
		
		// traverse heads of graphs and compare with the specialinvoke list to find the target;
		// I find that some heads of the graph are mostly like  some units
		//I just consider the simple case I wrote.
		// extract string from head and then judge whether contained in the specialinvoke unit
		
        String tmp = new String();
        String tmp2 = new String();
        int count =0;
        boolean find = false;
        int c = 0;
		UnitGraphLen findgt = new UnitGraphLen();
        UnitGraph findg[] = findgt.var;
        System.out.println("the tgfh.ul is "+tgfh.ul);
        FindPath[] fp = new FindPath[tgfh.ul];
        
        //initial the start & end in fp
        for (int y = 0;y<tgfh.ul;y++)
        {
        	fp[y] = new FindPath();
        	//fp[y].Initial();
        }
        
        Unit tmpf;
		int e = 0;
        
		//boolean start = false;
		//boolean end = false; // start and end to indicate path 
		// they should be arraies
		boolean[] start = new boolean[tgfh.ul];
		boolean[] end = new boolean[tgfh.ul];
		for (int se = 0; se<tgfh.ul;se++)
		{
			start[se] = false;
			end[se] = false;
		}
		
	//	System.out.println("now to get the findg graph~~~");
		int findgtlen = 0;
 // find contain head from the specialinvoke unit
		//since the arrary is not concret i use the boolean array to mark whether it is null or not
		// so the length of the loop equals to the length of the graph array.
		//ACTUALLY I SHOULD USE THE POINTER NOT THE ARRAY.....------TODO 
		
		for (i = 0; i< tgfh.ul;i++) {
			for (c = 0; c < tgfh.ul; c++) //find the target from the graphs except the one
				if (c != i) {
		//			System.out.println("now i is --"+i);
		//			System.out.println("in the for to find target except the one");
		//			System.out.println("c is "+c);
			//		System.out.println("bl[c] is " +bl[c]);
			//		System.out.println("tgfh.specialtw[c] is "+ tgfh.specialtw[c]);
					if (bl[c] == true && bs[c] == true && tgfh.specialtw[c] > 0) 
					{
		//			System.out.println("c is in this time is"+c);
					count = tgfh.specialtw[c];
		//			System.out.println("the count of the speical[c]--"+count);
					tmp = gfh[c].toString().trim();
					//cause that the head like this (after trim): $r0:=this:com.shentanli.silentinstall.Bodymethod
					//and I just want to get the class name
			//		System.out.println("the gfh  :"+tmp.toString());
					tmp2 = tmp.substring(14); //by observing
			//		System.out.println("substring of the head:  "+tmp2.toString());
					
					// traverse the speciallist to find if it contains the head , if true add it to the findg
					for (d = 0; d < count; d++)
					
					{
						
				//		System.out.println("special arrary "+speciall[c][d].toString());
						if (speciall[c][d].toString().isEmpty() == false && speciall[c][d].toString().contains(tmp2)) {
							
							find = true;
							//the length of special list first dimensionality is equal to candidate.
					//		System.out.println("the candidate is "+candidate[c].toString());
		//					System.out.println("in this loop i is--"+i);
							findg[i] = candidate[c]; //add the found graph to the findgraph list, which then use to find cmd
							findgtlen ++;
		//					System.out.println("find is true");
							
							
							continue;

						}
					}
					}

				 }

		    findgt.uglen = findgtlen;
	    	//find cmd from find graphs
			// if find , add these two to the findpath list;
			// to judge if an apk has silent install equals to judge if the findpath is null
	      
		//  System.out.println("after for the find is:"+find);
		  if(find)
		  {
	//		System.out.println("this time i is:"+i);
			//test the null graph
		//	UnitGraph  tsts = null;
		//	System.out.println("the test graph is :"+tsts);
			
			
		    //fp[i].count ++;
			
			//System.out.println("before: the fp[i] is:"+ fp[i].start.toString());
			//there always is a nullpointerexception ---- caused by the initialization....the parameter(body/method) should be null.....
			//so think about how to initialize 
			
		//	System.out.println("the fp[i] is "+fp[i].start);
		//	System.out.println("the findg is:"+findg[i].toString());
	        fp[i].start = findg[i]; // TODO --- i just do not know why null pointerexception.....//fixed...
	   //     System.out.println("after: the fp[i] is:"+fp[i].toString());
	        //ignore these following note...
	        //if doing in this way , the fp will not continuous...
	        //maybe should build path from graph to unit...
	       // fp[i].end = findg[i];
	        start[i] = true;
	//        System.out.println("the start is true");
		  }
				//}
		
	//	System.out.println("now to build the findpath");//todo build path.....the following maybe wrong....
		if (find)
	    //	for ( e = 0;e<findgt.uglen;e++) //the findgt.uglen may be wrong....
			ok:
			for (e = 0;e<tgfh.ul;e++)
	    	{
				
	   // 		System.out.println("in the findgt for to get the end ");
	    		if (findg[e] != null)
	    		{
	 //   		System.out.println("the findg[e] is not null");
	    		java.util.Iterator<Unit> tra = findg[e].iterator();
	    		while(tra.hasNext())
	    		{
	    		//	System.out.println("traverse the findg unit");
	    			if(end[i] == false) // should be the start& end array...
	    			{
	   // 			System.out.println("the end is false");
	    			tmpf = tra.next();
	    	//		System.out.println("the unit of the findg is:"+tmpf.toString());
	    	//		if (tmpf.toString().isEmpty() == false/* && tmpf.toString().contains("install")*/)
	    			if (tmpf.toString().isEmpty() == false && pi[e] == true | tmpf.toString().isEmpty() == false && tmpf.toString().contains("pm install")) //actually the pm install should be contained in the original graph or the findg//TODO
	    			{
	    				
	    				//fp[i].count ++;
	    				//fp[i].start = findg[i];
	    				fp[i].end = ug[i];
	    				end[e] = true;
	  //  				System.out.println("the end is true");
	 //   				System.out.println("now to break");
	    				break ok;
	    				//fp[i].end = findg[e];
	    			}
	    			}
	    			
	    		}
	    	}
	    	}		
//		System.out.println("now to set the findpath count");
//		System.out.println("find is"+find);
//		System.out.println("start[i] is"+start[i]);
//		System.out.println("end[i] is"+end[i]);
		if (find && start[i] && end[i])
		{
			System.out.println("!in the count count");
			fp[i].count = fp[i].count+1;
//		    System.out.println("the fp["+i+"].count is "+fp[i].count);
		}
		
		find = false;
	}
		
		System.out.println("----return findpah---");
		return fp;
		}
		}
	
	return null;
	
}
}
