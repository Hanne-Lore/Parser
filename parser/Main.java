package parser;
public class Main {

	
	public static void main(String[] args) {
		
		//Scanner s = new Scanner("source_program");
		//s.processInput();
		
		Parser p = new Parser();
		//p.readFromFile("productions.txt");
		p.readFromFile("good_example.txt");
		p.createStates();
		System.out.println(p.analyse("1223"));
		
	}

}
