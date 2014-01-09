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
	
	public Parser(){
		states = new ArrayList<>();
		productions = new ArrayList<>();
		X = new ArrayList<>();
	}
	
	public void createStates(){
		
		String start = productions.get(0).getLeft();
		String sPrime = start + "'";
		
		closure(new Production(start, new ArrayList<>(Arrays.asList(sPrime))));
		
		for( int i = 0; i < states.size(); i++ ){
			
			// for each prod in state
			// for each elem in X 
			//      call closure(prod.get(j).dotpluplus();
		}
	}
	
	public boolean isTerminal(String token) {
		return token.matches("[0-9]*");
	}
	
	public void closure(Production p){
		State s = new State();
		List<Production> prList = new ArrayList<>();
		prList.add(p);
		
		for(int i = 0; i < prList.size(); i++){
			if( !isTerminal(prList.get(i).getElementAfterDot())){
				prList.addAll(getProductionsByLeft(prList.get(i).getElementAfterDot()));
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
				prods.add(p);
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
						System.err.println("Bad production: "+side[0]+" size must be 1.");
						break;
					}
					
					rhp = new ArrayList<String>(Arrays.asList(side[1].split(" ")));
					rhp.removeAll(Collections.singleton(""));
					
					productions.add(new Production(lhp.get(0), rhp));
				}
				else{
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
			System.out.println(s);
		}
	}
	
	
	
}
