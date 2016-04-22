package com.shentanli.example.soot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.text.html.HTMLDocument.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import soot.Body;
import soot.G;
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

	
	private final static String androidJAR = "/home/shentanli/tmpgithub/Soottest/Cfgbuild_S/src/com/shentanli/example/soot/android.jar";
   
	private String appAPK;
	public static void inialiseSoot(String appAPK)
	{
	
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_prepend_classpath(true);
		Options.v().set_validate(true);
		Options.v().set_output_format(Options.output_format_jimple);
		System.out.println("setprocessdir:"+Collections.singletonList(appAPK).toString());
		Options.v().set_process_dir(Collections.singletonList(appAPK));
		Options.v().set_force_android_jar(androidJAR);
		Options.v().set_src_prec(Options.src_prec_apk);
		System.out.println("apkname----"+appAPK);
		Options.v().set_soot_classpath(androidJAR);
		Options.v().set_whole_program(true);
		Scene.v().allowsPhantomRefs();
		
		Scene.v().loadNecessaryClasses();
		

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
				tmp = path + "/" +fs[i].getName();
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
//	static UnitLen tgfh = new UnitLen();
	static UnitGraphLen findgt = new UnitGraphLen();
   

	
	public static void main (String[] args)
	{
		//inialiseSoot();
		long begin = System.currentTimeMillis();
		
		List<String> data = new ArrayList<String>();
	//	data = Setenv();
		int i;
		boolean detect = false;
		String str = args[0];
	
		data.add(str);
		System.out.println("add "+str +" to soot sucess");
		
		
	//	data.add("/home/shentanli/tmpgithub/Soottest/Cfgbuild_S/src/com/shentanli/example/soot/app-debug.apk");
		
		
		for (i=0; i < data.size(); i++)
		{
			
			inialiseSoot(data.get(i));
		
			System.out.println("inialise soot done,then to detect...");
		//	MyAnalysis manalysis = new MyAnalysis();
		//	PackManager.v().getPack("wjtp").add(new Transform("wjtp.myanalysis", manalysis));
			PackManager.v().getPack("wjtp").apply();
			System.out.println("apply done then to runpacks");
			PackManager.v().runPacks();
			
			
			System.out.println("to call issilentinstallapk");
			detect = isSilentInstallapk();
		//	detect = manalysis.detect;
			
			if (detect){
				System.out.println("this apk: "+data.get(i)+" mostly has silent install");
			    //copy this apk to resultdir
				String rd = "resultdir";
				File fp = new File(rd);
				if (!fp.exists()){
					fp.mkdir();
				}
				
				File from = new File(data.get(i));
				try {
					FileUtils.copyFileToDirectory(from, fp);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
				
			}
			else
				System.out.println("this apk: "+data.get(i)+" may not has silent install");		
			System.out.println("Spend time:"+(System.currentTimeMillis()-begin));
			
		}
		
	}
	
	
	
	//judge the apk
	public static boolean isSilentInstallapk()
	{

		ArrayList<UnitGraph> graphs = getGraphListOfApk();
		return DectGraph(graphs);
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

					
					if ( m.hasActiveBody() && m.getActiveBody().getUnits().isEmpty() == false) {
						
						List<UnitBox> tmp1 = m.getActiveBody().getAllUnitBoxes();
			//			for (int tc = 0; tc<tmp1.size(); tc++)
			//				System.out.println("----getallunitboxes---:"+tmp1.get(tc).toString());
						List tmp2 = m.getActiveBody().getUseAndDefBoxes();
						for (int tc=0; tc<tmp2.size();tc++)
							//if (tmp2.get(tc).equals("runtime")) 
						{
							
								System.out.println("!!!getuseanddefboxes---**"+tmp2.get(tc).toString());
								
								if (tmp2.get(tc).toString().contains("install"))
								{
								
								}
						    	
						}

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
		ArrayList<UnitGraph> graphs = new ArrayList<UnitGraph>();
	//	System.out.println("applicationclasses is :"+Scene.v().getApplicationClasses().toString());
	   
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

			if (ut.toString().isEmpty() == false && ut.toString().contains("getRuntime")) // if contains specialinvoke means that it is not the bottom 
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
			 if (CheckSingleMethod(ug))
				 return true;
			 
			 if(ContainsRun(ug))
			 {
				 AddSpecialUnits(ug, specialunits, specialgraphs);
				 
			 }
		 }
		 for(int i = 0;i<specialunits.size();i++){
			// System.out.println("size of specialunits is:"+specialunits.size());
			 for (UnitGraph ug : graphs)
			 {
				 String scut = specialunits.get(i).toString().trim();
				 String hcut = ug.getHeads().get(0).toString();
			
				 int left = scut.indexOf('<');
				 int rigth = scut.indexOf(':',left<0 ?0:left);
				 if (left <0 || rigth <0)
				 {
		
					 continue;
				 }
				 scut = scut.substring(left+1, rigth);	 
				 if (hcut.contains(scut)){
					 if (ContainsInstall(specialgraphs.get(i)) || ContainsInstall(ug)) // the index of specialgraph not equals to that of the specialunits...
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
	
	
	private static boolean CheckSingleMethod(UnitGraph ug)
	{
		for(Unit u: ug)
			for(Unit u2: ug)
				if (u.toString().isEmpty() == false && u.toString().contains("pm install") && u2.toString().isEmpty() == false && u2.toString().contains("getRuntime") || u.toString().isEmpty() == false && u.toString().contains("pm instlal") && u.toString().contains("getRuntime") || u2.toString().isEmpty() == false && u2.toString().contains("pm install") && u2.toString().contains("getRuntime"))
					return true;
		return false;
		
	}
}
