package com.shentanli.example.soot;

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

	public static SootMethod FindPath(){
		return null;
		//unnecessary...
		//what should be added??just think about it
	}
	
	public void Initial(){
		SootMethod sm = this.FindPath();
		this.start = new BriefUnitGraph(sm.getActiveBody());
		this.end = new BriefUnitGraph(sm.getActiveBody());
		
	}
	
}
