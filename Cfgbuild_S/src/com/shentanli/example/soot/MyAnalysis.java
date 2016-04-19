package com.shentanli.example.soot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.text.html.HTMLDocument.Iterator;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.PatchingChain;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class MyAnalysis extends BodyTransformer{
	
	
	static UnitGraph tmp;
	boolean detect = false;

	@Override
	protected void internalTransform(Body body, String phase, Map options) {
		// TODO Auto-generated method stub
	    System.out.println("in the internaltransform method---"+Scene.v().getApplicationClasses().toString());
		detect = isSilentInstallapk();
		}

	//judge the apk
		public static boolean isSilentInstallapk()
		{
		//	System.out.println("to call the ufgl");
		//	UnitGraphLen var1 = new UnitGraphLen(); 
			ArrayList<UnitGraph> graphs = getGraphListOfApk();
			return DectGraph(graphs);

		}
		
		
		static ArrayList<UnitGraph> getGraphListOfApk()
		{
		//	System.out.println("the varlebn is "+ var1.uglen);
		//	System.out.println("in the ufgl method and to get UnitGraph array");
			//UnitGraphLen var1 = new UnitGraphLen(); 
			
		//	UnitGraph tmp ;
			ArrayList<UnitGraph> graphs = new ArrayList<UnitGraph>();
			System.out.println("applicationclasses is :"+Scene.v().getApplicationClasses().toString());
		   
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
				//	 System.out.println("scut=="+specialunits.get(i));
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
}
	



