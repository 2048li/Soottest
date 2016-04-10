package com.shentanli.example.soot;

import soot.toolkits.graph.UnitGraph;

public class UnitGraphLen {
	
	int Max = 5000;
	UnitGraph var[] = new UnitGraph[Max]; 
	
	int uglen = 0;
	
	public UnitGraph[] UnitAInitial()
	{
		UnitGraph var1[] = this.var;
		for (int i = 0; i<Max; i++){
			var[i] = null;
		}
		return var1;
	}
	public void UnitGraphLen(){
		
	}

}
