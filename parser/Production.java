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
		
		Production(String left , List<String> right, int dot) {
			this.left = left;
			this.right = right;
			this.dot = dot;
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
			int ct =0;
			for( String s: right){
				if( ct == dot){
					line += ".";
				}
				line+=s+" ";
				ct++;
			}
			if( ct == dot){
				line += ".";
			}
			return line;
		}
		
		public boolean equals(Object o){
			Production p = (Production)o;
			if( this.dot == p.dot && this.left.equals(p.getLeft())){
				
				if( this.right.size() == p.right.size()){
					for( int i = 0; i < this.right.size(); i++){
						if( !this.right.get(i).equals(p.right.get(i))){
							return false;
						}
					}
				}else{
					return false;
				}
				
				return true;
			}
			
			return false;
		}
		
		public Production clone(){
			
			return new Production(left, right, dot);
		}
		
		
}