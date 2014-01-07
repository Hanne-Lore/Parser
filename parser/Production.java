package parser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Production {
		List<String> lhp;
		List<String> rhp;

		Production(String lhp, String value, String nt) {
			this.lhp = Arrays.asList(lhp);
			this.rhp = Arrays.asList(value, nt);
		}
		
		Production(String lhp, String value) {
			this.lhp = Arrays.asList(lhp);
			this.rhp = Arrays.asList(value);
		}
		
		Production(String lhp) {
			this.lhp = Arrays.asList(lhp);
			this.rhp = new ArrayList<String>();
		}
		
		Production(List<String> lhp, List<String> rhp) {
			this.lhp = lhp;
			this.rhp = rhp;
		}

		String getNt() {
			return lhp.get(0);
		}
		
		String getValue() {
			return rhp.get(0);
		}
		
		String getTo() {
			return rhp.get(1);
		}
		
		List<String> getLhp() {
			return lhp;
		}

		List<String> getRhp() {
			return rhp;
		}
		
		public String toString(){
			String line="";
			
			for( String s: lhp){
				line+=s+" ";
			}
			line+="->";
			for( String s: rhp){
				line+=s+" ";
			}
			return line;
		}
	}