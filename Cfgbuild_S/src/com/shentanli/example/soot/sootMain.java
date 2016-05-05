package com.shentanli.example.soot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
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
	
		
//		data.add("/home/shentanli/AndroidStudioProjects/Silentinstall/app/build/outputs/apk/app-debug.apk");
		
		
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
	

	static boolean unitBelongList(Unit u, List<Unit> g)//judge if the node is in the list
	{
		for (Unit i:g)
		{
			if (i.equals(u))
				return true;
		}
		return false;
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
	
	static boolean ContainsString(UnitGraph ug, String s)
	{
		if (ug.size() <= 0)
			return false;
			
		java.util.Iterator<Unit> it = ug.iterator();

		while(it.hasNext())
		{
			Unit ut = it.next();

			if (ut.toString().isEmpty() == false && ut.toString().contains(s)) 
			   return true; 
	
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
			boolean sr = SearchWholeapk(graphs);
			if (sr)
				return true;
	
		ArrayList<Unit> specialunits = new ArrayList<Unit>();
		ArrayList<UnitGraph> specialgraphs = new ArrayList<UnitGraph>();
		 for (UnitGraph ug: graphs)
		 {
//			 if (ContainsString(ug, "pm install"))
//				 if (SearchDepthofoneGraph(ug, graphs)) // one containing install specialinvoke another containing getruntime and dataoutputstream to exec
//				    return true;
			 
			 if(ContainsString(ug, "getRuntime"))
			 {
				 AddSpecialUnits(ug, specialunits, specialgraphs);
				 
			 }
		 }
		 for(int i = 0;i<specialunits.size();i++){
			 boolean suj = ContainsString(specialgraphs.get(i),"pm install");
			 for (UnitGraph ug : graphs)
			 {
				 String scut = specialunits.get(i).toString().trim();
				 String hcut = ug.getHeads().get(0).toString();
				 if (ComparespecialTohead(scut, hcut)) 
					 if ( suj || ContainsString(ug, "pm install")) // the index of specialgraph not equals to that of the specialunits...
					     return true;
 
			 }
			 
		 }
		 return false;
		
		} 
		return false;
			
	}

	
	private static boolean ComparespecialTohead(String special, String head)
	{
		int left = special.indexOf('<');
		int right = special.indexOf(':',left<0 ? 0:left);
		if (left <0 || right <0)
			return false;
		String scut = special.substring(left+1, right);
		return head.contains(scut);
		
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
		//check single unit of a graph
		
		for(Unit u: ug){
			if (u.toString().isEmpty() == false && u.toString().contains("pm install") && u.toString().contains("getRuntime"))
				return true;
			boolean b1 = u.toString().contains("pm install");
			for(Unit u2: ug)
				if(u.toString().isEmpty() == false && u2.toString().isEmpty() == false){
		    		
		    		boolean b2 = u2.toString().contains("getRuntime");
			    	boolean b4 = u2.toString().contains("DataOutputStream");
			    	if (b1 && b2 || b4 && b1)
			    		return true;
				}
			
		}
		
		
		return false;
	}
	
	private static boolean CheckSingleMethodwithTwoinvoke(UnitGraph ug, ArrayList<UnitGraph> graphs) //that is command and runtime are seperated  but related by this method
	{
		ArrayList<Unit> specialunits = new ArrayList();
		ArrayList<UnitGraph> specialgraphs = new ArrayList();
		AddSpecialUnits(ug, specialunits, specialgraphs );
		
		//check first install way--getruntime
		boolean invokecmd = false;
		boolean invokeruntime = false;
		for (int i =0;i<specialunits.size();i++)
		{
			
			for (UnitGraph ugt : graphs)
				if(ComparespecialTohead(specialunits.get(i).toString().trim(), ugt.getHeads().get(0).toString()))
				{
					if (ContainsString(ugt, "pm install"))
						invokecmd = true;
				    if (ContainsString(ugt, "getRuntime"))
						invokeruntime = true;
				}
			if (invokecmd && invokeruntime)
			{
				return true;
			}			
				
		}
		//check second install way -- dataoutputstream + pm install : two depth call
		for (Unit u:ug){
			if (u.getUseAndDefBoxes().toString().contains("pm install")) //pass directly
				for (UnitGraph ugt :graphs){	
					if (ComparespecialTohead(u.toString().trim(), ugt.getHeads().get(0).toString()))
						if (ContainsString(ugt, "DataOutputStream"))
							return true;
				}
		} 
		boolean bdataoutput = false;
		UnitGraph result = null;
		
		for(Unit u:ug)
			if (u.getUseAndDefBoxes().toString().contains("virtualinvoke")){// pass indirectly
				for(UnitGraph ugt:graphs){
					if(ComparespecialTohead(u.toString().trim(), ugt.getHeads().get(0).toString()))
						if(ContainsString(ugt, "pm install")){
							invokecmd = true;
						}
		    	}
				Unit preds = ug.getPredsOf(u).get(0);
				if ( preds.toString().isEmpty() == false && preds.toString().contains("virtualinvoke")){
					for(UnitGraph ugtt:graphs){
						if(ComparespecialTohead(preds.toString().trim(), ugtt.getHeads().get(0).toString()))
							if(ContainsString(ugtt, "DataOutputStream") )
								bdataoutput = true;
					}
				}
		
			  }
		
		
		return false;
	}

	
	private static boolean SearchDepthofoneGraph(UnitGraph ug, ArrayList<UnitGraph> graphs) //condition is this graph has install string and to find exec
	{
		for (Unit u:ug)
		{
			if (u.toString().contains("specialinvoke") || u.toString().contains("virtualinvoke") || u.toString().contains("staticinvoke"))
				for(UnitGraph ugt :graphs)
					if (ComparespecialTohead(u.toString().trim(), ugt.getHeads().get(0).toString()))
						for (Unit ut: ugt)
							if (ut.toString().contains("invoke"))
								for (UnitGraph ugtt : graphs)
								
								 	if (ComparespecialTohead(ut.toString().trim(), ugtt.getHeads().get(0).toString()))
								    	if (ContainsString(ugtt, "getRuntime") && ContainsString(ugtt, "DataOutputStream") && ContainsString(ugtt, "Process"))
									    	return true;		
			
		}
		return false;
	}
	
	//considering there is no useless command---state command but not use it
	public static boolean SearchWholeapk(ArrayList<UnitGraph> graphs)
	{

		if(SearchgetRuntimeAndOutputStreamwithcmd(graphs))
			return true;
		if(SearchAdbinstall(graphs))
			return true;
		return false;
	}

	private static  boolean SearchgetRuntimeAndOutputStreamwithcmd(
			ArrayList<UnitGraph> graphs) {
		// TODO Auto-generated method stub
		for (UnitGraph ug:graphs)
			if (CheckSingleMethod(ug) ||  CheckSingleMethodwithTwoinvoke(ug, graphs))
				return true;
		return false;
	}

	private static  boolean SearchAdbinstall(ArrayList<UnitGraph> graphs) {
		// TODO Auto-generated method stub
		boolean bcmd = false;
		boolean badb = false;
		boolean adb = false;
		for (UnitGraph ug : graphs)
			for (Unit u : ug){
				if (u.toString().contains("pm install"))
					bcmd = true;
				if (u.toString().contains("Adb"))
					adb = true;
				if ((u.toString().contains("InetAddress") || u.toString().contains("InetSocketAddress") || u.toString().contains("SocketChannel") ) && adb )
					badb = true;
			}
		if(bcmd && badb)
			return true;

		return false;
	}


	
}

