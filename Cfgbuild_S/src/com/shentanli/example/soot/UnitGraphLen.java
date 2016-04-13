package com.shentanli.example.soot;

import java.util.ArrayList;

import soot.toolkits.graph.UnitGraph;

public class UnitGraphLen {
	
	ArrayList<UnitGraph> graphs;
	
	public void UnitGraphLen(){
		graphs = new ArrayList<UnitGraph>();
		
		
	}
	public void addGraph(UnitGraph ug)
	{
		graphs.add(ug);
	}
	public int getLen()
	{
		return graphs.size();
	}

}
