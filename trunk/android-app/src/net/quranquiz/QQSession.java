package net.quranquiz;

import java.util.Vector;

public class QQSession {
	private Vector<Integer> vQStart;
	
	public QQSession(){
		vQStart = new Vector<Integer>();
	}
	
	public boolean addIfNew(int idx){
		if(vQStart.contains(Integer.valueOf(idx))){
			return false;
		}else if(vQStart.isEmpty()){
			vQStart.add(Integer.valueOf(idx));
			return true;
		}else{
			for(int i=0;i<vQStart.size();i++)
				if(Math.abs(vQStart.elementAt(i)-idx)<10)
					return false;
			
			vQStart.add(Integer.valueOf(idx));
			return true;
		}
	}
}
