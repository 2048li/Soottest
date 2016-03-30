package com.shentanli.example.soot;

import java.util.Map;

import javax.swing.text.html.HTMLDocument.Iterator;

import soot.Body;
import soot.BodyTransformer;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;

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
					java.util.Iterator<Unit> i = b.getUnits().snapshotIterator();
					while(i.hasNext())
					{
						Unit u = i.next();
						
						String substr = "getRuntime";
						if (u.toString().indexOf(substr) != -1)
							System.out.println("unit contains getpah :"+u.toString());
						//System.out.println("unit --"+u.toString());
					//	else
					//		System.out.println("no getruntime method");
					}
				}
			}
		}
		
	}
	

}
