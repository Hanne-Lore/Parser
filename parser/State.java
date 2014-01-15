package parser;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class State {
	List<Production> productions;
	
	String action = ""; 
	int reduce_pos = 0;
	
	Map<String,Integer> go_to;
	
	
	public State(){
		productions = new ArrayList<>();
		go_to = new HashMap<String, Integer>();
	}
	
	
	
	public List<Production> getProductions(){
		return productions;
	}
	
	public void addProduction(Production p){
		productions.add(p);
	}
	
	public String toString(){
		
		String s="{ ";
		
		for(Production p: productions){
			s+= "[ " + p.toString() + "], ";
		}
		s+="} ";
		
		s+= "\n goto: ";
		
		for(Map.Entry<String, Integer> entry : go_to.entrySet()){
			s+= "< Elem: " + entry.getKey() + ", State: " + entry.getValue() + ">";
		}
		s+= " \naction: " + action + " by " + reduce_pos;
		
		return s;
	}
	
	
	
	

	
}
