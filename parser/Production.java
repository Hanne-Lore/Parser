package parser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Production {
		String left;
		List<String> right;
		int dot;
		
		Production(String left , List<String> right) {
			this.left = left;
			this.right = right;
			this.dot = 0;
		}
		
		public String getLeft() {
			return left;
		}
		
		public List<String> getRight() {
			return right;
		}
		
		public void dotplusplus(){
			this.dot ++;
		}
		
		public boolean is_dot_last(){
			return this.dot >= right.size();
		}
		
		public String getElementAfterDot(){
			return right.get(dot);
		}
		
		public String toString(){
			String line="";
			
			line = this.left;
			
			line+=" -> ";
			for( String s: right){
				line+=s+" ";
			}
			return line;
		}
		
		
		
}