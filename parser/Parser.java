package parser;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Parser {
	List<State> states;
	List<Production> productions;
	List<String> X;
	// this is how we solved problem state = another state
	List<Production> generatorProd;
	
	public Parser(){
		states = new ArrayList<>();
		productions = new ArrayList<>();
		generatorProd = new ArrayList<>();
		X = new ArrayList<>();
	}
	
	public void createStates(){
		
		if( productions.size() == 0)
			return;
		String start = productions.get(0).getLeft();
		String sPrime = start + "'";
		
		closure(new Production(sPrime, new ArrayList<>(Arrays.asList(start))));
		states.get(0).action="shift";
		
		for( int i = 0; i < states.size(); i++ ){
			
			//System.out.println("State " + i+": " + states.get(i).toString()+"\n\n");
			
			if( i == 12){
				break;
			}
			if( i == 6){
				String ss="";
			}
			for(Production prod : states.get(i).productions){
				
				Production p = prod.clone();
				if( !p.is_dot_last() ){
					
					String elem = p.getElementAfterDot();
					
					
					p.dotplusplus();
					int pos =generatorProd.indexOf(p);
					
					if( pos == -1){
						this.generatorProd.add(p.clone());
						closure(p);
						states.get(i).go_to.put(elem, states.size()-1);
						states.get(i).action = "shift";
												
						if(p.is_dot_last() && !p.getLeft().equals("S'")) {
							states.get(states.size()-1).reduce_pos = productions.indexOf(new Production(p.getLeft(), p.getRight())) + 1;
							states.get(states.size()-1).action = "reduce";
						}else if (p.getLeft().equals("S'") && p.equals(new Production("S'", new ArrayList<>(Arrays.asList("S")), 1))){
							states.get(states.size()-1).action = "accept";
						}
						
					}else{
						states.get(i).go_to.put(elem, pos+1);
					}
				}
			}
			//System.out.println("State " + i+": " + states.get(i).toString()+"\n\n");  this is the preeteeeh state print
		}
		
		int ct = 0;
		for( State s: states){
			//System.out.print("State "	+ ct+": ");
			//System.out.println(s.toString());
			ct++;
		}
		System.out.println("------------ gen prod ------------");
		for( Production p: generatorProd){
			System.out.println(p.toString());
		}
		System.out.println("------------ ------------------");
	}
	
	public boolean isTerminal(String token) {
		return token.matches("[0-9]*");
	}
	
	public String analyse(String input){
		//System.out.println("------------ analyse prods ------------");
		
		String output="";
		List<Object> stack = new ArrayList<>();
		stack.add(states.get(0));
		
		while(true){
			
			int last_pos= stack.size()-1;
			Object obj =  stack.get(last_pos);
			
			System.out.println( "---------------------STATE----------------------");
			for(Object obj1: stack){
				if( obj1 instanceof State){
					System.out.println( "State :"+ obj1.toString()+"\n\n");
				}
				else{
					System.out.println( "terminal :"+ obj1+"\n\n");
				}
				
			}
			
			
			if( obj instanceof State){
				
				State s = (State)obj;
				
				switch(s.action){
					case "shift":
						
						if( input.length() == 0)
							return "error: shift but no more input left";
						String inp_shift = input.substring(0, 1);
						input = input.substring(1);
						stack.add(inp_shift);
						
						if ( !s.go_to.containsKey(inp_shift)){
							return "ERROR: goto from state: "+s.toString()+" \nto "+ inp_shift + " is not possible";
						}
						
						int new_state_index = s.go_to.get(inp_shift);
						State new_state = states.get(new_state_index);
						stack.add(new_state);
						
						break;
						
					case "accept":
						return output;
						
					case "reduce":
						
						
						
						 //reduce state print
						
						int prod_index = s.reduce_pos;
						Production p = productions.get(prod_index-1);
						int rhp_size = p.getRight().size();
						//stack = stack.subList(0, stack.size()-rhp_size*2);
						//stack.subList(stack.size()-1 - rhp_size*2, stack.size()-1).clear();
						for( int i = 0; i < rhp_size*2; i++){
							stack.remove(stack.size()-1);
						}
						
						
						State last_state = (State)stack.get(stack.size()-1);
						stack.add(p.getLeft());
						
						if ( !last_state.go_to.containsKey(p.getLeft())){
							return "ERROR reduce: goto from state "+s.toString()+" \nto "+ p.getLeft() + " is not possible";
						}
						new_state_index = last_state.go_to.get(p.getLeft());
						new_state = states.get(new_state_index);
						stack.add(new_state);
						output= prod_index+", "+output;
						
						break;
				}
			}
		}
		
	}
	
	public void closure(Production p){
		State s = new State();
		List<Production> prList = new ArrayList<>();
		prList.add(p);
		
		for(int i = 0; i < prList.size(); i++){
			
			Production prod = prList.get(i);
			String elem="";
			if( !prod.is_dot_last()){
				 elem = prList.get(i).getElementAfterDot();
			}
			if( !prod.is_dot_last() && !isTerminal(elem)){
				List<Production> l = getProductionsByLeft(prod.getElementAfterDot());
				prList.addAll(l);
			}
		}
		
		// prList is expanded list
		
		s.productions.addAll(prList);
		
		
		states.add(s);
		
	}
	
	public List<Production> getProductionsByLeft(String left){
		List<Production> prods = new ArrayList<>();
		for (Production p : productions){
			if( p.getLeft().equals(left)){
				prods.add(new Production(p.getLeft(), p.getRight()));
			}
		}
		
		return prods;
	}
	
	public void readFromFile(String file) {
		try {
			BufferedReader fin = new BufferedReader(
					new FileReader((file)));
			String line;
			while ((line = fin.readLine()) != null) {
				line = line.replace("\t", "");
				String side[] = line.split("->");
				List<String> rhp = null,lhp = null;
				
				if( side.length == 2 ){
					lhp = new ArrayList<String>(Arrays.asList(side[0].split(" ")));
					lhp.removeAll(Collections.singleton(""));
					
					if( lhp.size() > 1){
						productions.clear();
						System.err.println("Bad production: "+side[0]+" size must be 1.");
						break;
					}
					
					rhp = new ArrayList<String>(Arrays.asList(side[1].split(" ")));
					rhp.removeAll(Collections.singleton(""));
					
					productions.add(new Production(lhp.get(0), rhp));
				}
				else{
					productions.clear();
					System.err.println("Bad line "+line+" size must be 1.");
				}
			}
			fin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(Production p : productions) {
			//System.out.println(p.toString());
			String left = p.getLeft();
			if( !X.contains(p.getLeft())){
				X.add(p.getLeft());
			}
			
			for ( String s: p.getRight()){
				if( !X.contains(s)){
					X.add(s);
				}
			}
		}
		for ( String s: X){
			//System.out.println(s);
		}
	}
	
	
	
}
