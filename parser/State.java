package parser;

import java.util.ArrayList;
import java.util.List;

public class State {
	List<Production> productions;
	
	public State(){
		productions = new ArrayList<>();
	}
	
	public List<Production> getProductions(){
		return productions;
	}
	
	public void addProduction(Production p){
		productions.add(p);
	}
	
	
}
