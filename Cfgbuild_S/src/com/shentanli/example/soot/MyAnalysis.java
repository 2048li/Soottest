package com.shentanli.example.soot;

import java.util.List;
import java.util.Map;

import javax.swing.text.html.HTMLDocument.Iterator;

import soot.Body;
import soot.BodyTransformer;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;

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
					java.util.Iterator<Unit> i = b.getUnits().snapshotIterator();
			     	while(i.hasNext())
					{
						Unit u = i.next();
						List usebox = u.getUseBoxes();
					    int len = usebox.size();
					    
						for (int count = 0;count<len;count++)
						{
							if (usebox.get(count).toString().indexOf("pm install") != -1)
								System.out.println("usebox value--"+usebox.get(count));
							//System.out.println("usebox value--"+usebox.get(count));
						}
						
					/*	String substr = "getRuntime";
						if (u.toString().indexOf(substr) != -1)
							System.out.println("unit contains getpah :"+u.toString());   
						//System.out.println("unit --"+u.toString());
						else
							System.out.println("no getruntime method");*/
					}
				}
			}
		}
		
	}
	

}
