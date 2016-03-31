package com.shentanli.example.soot;

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

public class MyAnalysis extends BodyTransformer{

	@Override
	protected void internalTransform(Body body, String phase, Map options) {
		// TODO Auto-generated method stub
		for (SootClass c:Scene.v().getApplicationClasses())
		{
			//System.out.println("sootclass "+ c);			
			
			for (SootMethod m:c.getMethods())
			{
				if (m.isConcrete())
				//if (!m.isJavaLibraryMethod())
				{
					Body b = m.retrieveActiveBody();
				//	List<ValueBox> vale = b.getDefBoxes();					
				//	List<Value> pararef = b.getParameterRefs();
				//	String[] y = pararef.toArray(new String[0]);
				//	int len = y.length;
				/*	int len = vale.size();
					for (int i = 0;i<len;i++)
					{
						String tmp = vale.get(i).toString();
						//System.out.println("pararef--"+y[i]);
						System.out.println("boxvalue ---"+tmp);
					} */
					String valuename = "pm install";
					String methodname = "exec";
					boolean l1 = judgestrvalue(valuename, b);
					boolean l2 = judgemethod(methodname, b);
						
					/*	if (l1 && l2)
						{
							System.out.println("both pminstall and getruntime body");
						}
						*/
					if (l2)
						System.out.println("getruntime method");
					}
					
	
				}
			}
		}

	
	public boolean judgestrvalue(String valuename, Body b)
	{
		java.util.Iterator<Unit> i = b.getUnits().snapshotIterator();
		boolean result = false;
		while(i.hasNext())
		{
			Unit u = i.next();
			List usebox = u.getUseAndDefBoxes();
			int len = usebox.size();
				for (int count = 0;count<len;count++)
				{
					if (usebox.get(count).toString().indexOf(valuename) != -1){
						result = true;
					    System.out.println("---contains pm install--"+usebox.get(count));	
					}
						
				}
				
			}
		return result;
		
		//get the value
		/*	for (Unit ut: b.getUnits())
			{
				for (ValueBox vb : ut.getUseBoxes())
					if (vb.getValue().equals("pm install"))
						System.out.println("value--"+vb.getValue().toString());
			}*/
		
		 /*  	while(i.hasNext())
		{
			Unit u = i.next();
			List usebox = u.getUseBoxes();
		    int len = usebox.size();
		    
			for (int count = 0;count<len;count++) 
			{
				if (usebox.get(count).toString().indexOf("pm install") != -1){
					l1 = true;
					//System.out.println("usebox value--"+usebox.get(count));
				}
				//System.out.println("usebox value--"+usebox.get(count));
			}*/
	}
     
	public boolean judgemethod(String methodname, Body b)
	{
		boolean result = false;

		java.util.Iterator<Unit> i = b.getUnits().snapshotIterator();
		Unit u = i.next();
		
		if (u.toString().indexOf(methodname) != -1)
		{
			System.out.println("***unit contains method"+u.toString());  
			result = true;
			
		}
		//else
		//	System.out.println("no the method you search");
		return result;
		
		
		//another way
		
	}
	
	
	}
	



