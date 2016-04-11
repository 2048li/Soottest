package com.shentanli.example.soot;

import java.util.List;

import soot.SootClass;
import soot.SootMethod;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class FindPath {

	//should not write in this way..... the body should not be null. or it will throw nullpointerexception
	//UnitGraph start = new  BriefUnitGraph(null);
	//UnitGraph end = new  BriefUnitGraph(null);
	//UnitGraph start = null;
	//UnitGraph end = null;
	UnitGraph start;
	UnitGraph end;
	int count = 0;

	public SootMethod FindPatht(){
		return null;
		//unnecessary...
		//what should be added??just think about it
	}
	
	public void Initial(){
		
		SootClass sc = new SootClass("Testclass");
		SootMethod sm = sc.getMethods().get(0);
		
		this.start = new BriefUnitGraph(sm.getActiveBody());
		this.end = new BriefUnitGraph(sm.getActiveBody());
		
	}
	
}
