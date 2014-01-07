package parser;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Scanner {

	class pif {
		String token;
		int code;
		int positionST = -1;
	}

	FileReader inputStream = null;
	List<String> identifiersST;
	List<String> constantsST;
	List<pif> PIF;
	int linenr = 1;

	public Scanner(String file) {

		identifiersST = new ArrayList<String>();
		constantsST = new ArrayList<String>();
		PIF = new ArrayList<pif>();

		try {
			inputStream = new FileReader(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public boolean isIdentifier(String token) {

		return token.matches("[a-z][a-zA-Z0-9_]{0,}");

	}

	public boolean isConstant(String token) {
		return token.matches("[+-]?[1-9][0-9]*");
	}

	public boolean isString(String token) {
		return token.matches("\"[a-zA-Z0-9]*\"");
	}

	private String lookahead;

	public String detectToken() {

		int c;
		String temp_tokens = "";

		try {
			String token;

			if (lookahead == null) {
				c = inputStream.read();
				if (c == -1)
					return "EOF";
				token = String.valueOf((char) c);
			} else {
				token = lookahead;
				lookahead = null;
			}

			if (token.matches("[a-zA-Z0-9_]")) {
				temp_tokens += token;

				while ((c = inputStream.read()) != -1) {
					token = String.valueOf((char) c);
					if (!token.matches("[a-zA-Z0-9_]")) {
						lookahead = token;
						return temp_tokens;
					}
					temp_tokens += token;
				}

				return temp_tokens;
			} else if (token.matches("[=<>]")) {
				temp_tokens += token;

				while ((c = inputStream.read()) != -1) {
					token = String.valueOf((char) c);
					if (!token.matches("[=<>]")) {
						lookahead = token;
						return temp_tokens;
					}
					temp_tokens += token;
				}

				return temp_tokens;
			} else if (token.equals("\"")) {
				temp_tokens += token;
				while ((c = inputStream.read()) != -1) {
					token = String.valueOf((char) c);
					if (token.equals("\"")) {
						temp_tokens += token;
						return temp_tokens;
					}
					temp_tokens += token;
				}

				return temp_tokens;
			}
			else if (token.matches("[:\\-+\\-*()\\[\\{},;]")) {
				return token;
			} else if (token.equals("\n")) {
				linenr++;
				return null;
			} else if (token.equals("\t")) {
				return null;
			} else if (!token.equals(" ")) {
				return token;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}

	public void detectUnarySign(){
		
		for(int i = 0; i<PIF.size()-2; i++){
			String temp1 =  PIF.get(i).token;
			String temp2 =  PIF.get(i+1).token;
			String temp3 =  PIF.get(i+2).token;
			if( PIF.get(i).token.matches("[=\\(\\[\\{<>\\-\\+]") && PIF.get(i+1).token.matches("-") && PIF.get(i+2).code == 1  ){
				
				int posSt = PIF.get(i+2).positionST - 1;
				String nr = constantsST.get(posSt);
				if(constantsST.contains("-"+ nr)) {
					PIF.get(i+2).positionST = constantsST.indexOf("-"+ nr);
				} else {
					constantsST.add("-"+ nr);
					PIF.get(i+2).positionST = constantsST.size() - 1;
				}
				PIF.remove(i+1);	
				i++;
			}
		}
	}
	
	
	public void processInput() {

		String token;
		while (true) {

			token = detectToken();
			if (token == null) {
				continue;
			} else if (token.equals("EOF")) {
				break;
			} else {
				if (isIdentifier(token) && token.length() <= 250) {
					// System.out.println(token + "	IDENTIFIER");
					pif val = new pif();

					if (getTokenCode(token) == 404) {
						val.token = token;
						insertAsc(token, identifiersST);
					} else {
						val.token = token;
						val.positionST = 0;
						val.code = getTokenCode(token);
					}

					PIF.add(val);

				} else if (isIdentifier(token) && token.length() > 250) {
					System.err.print("error on line " + linenr
							+ ": token exceeds 250 chars  ->" + token + " \n");
				} else if (isConstant(token)) {
					// System.out.println(token + "	CONSTANT");
					pif val = new pif();
					val.token = token;
					val.code = 1;
					insertAsc(token, constantsST);
					PIF.add(val);
				} else if (isString(token)) {
					pif val = new pif();
					val.token = token;
					val.code = 1;
					insertAsc(token, constantsST);
					PIF.add(val);
				} else {

					if (getTokenCode(token) == 404) {
						System.err.print("error 404 on line " + linenr
								+ ": symbol not found -> " + token + '\n');
					} else {
						pif val = new pif();
						val.token = token;
						val.positionST = 0;
						val.code = getTokenCode(token);
						PIF.add(val);
					}

				}
			}

		}

		assignPosToPif();
		
		detectUnarySign();
	
		generateSymbolTables(identifiersST, "identifierST.txt");
		generateSymbolTables(constantsST, "constantST" +
				".txt");
		generatePif(PIF, "pif.txt");

		// System.out.println("\n identifiers \n");

		for (String s : identifiersST) {
			// System.out.println(s);
		}

		// System.out.println("\n constants \n");

		for (String s : constantsST) {
			// System.out.println(s);
		}

		// System.out.println("\n pif \n");

		for (pif p : PIF) {
			// System.out.println("Token: " + p.token + " Code: " + p.code +
			// " Pos: " + p.positionST);
		}
	}

	public void insertAsc(String token, List<String> l) {

		int pos = 0;
		while (pos < l.size()) {

			if (token.compareTo(l.get(pos)) < 0) {
				l.add(pos, token);
				return;
			} else if (token.compareTo(l.get(pos)) == 0) {
				return;
			}
			pos++;
		}

		l.add(token);
	}

	public void generatePif(List<pif> p, String filename) {

		FileWriter outputStream = null;
		try {
			outputStream = new FileWriter(filename);

			for (pif f : p) {
				outputStream.write(f.code + "   " + f.positionST + "   ("
						+ f.token + ")\n");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generateSymbolTables(List<String> st, String filename) {

		FileWriter outputStream = null;
		try {
			outputStream = new FileWriter(filename);
			int ct = 1;

			for (String s : st) {
				outputStream.write(ct + "   " + s + "\n");
				ct++;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int getSTPosition(String token, List<String> st) {

		int pos = 0;
		for (String s : st) {

			if (s.compareTo(token) == 0) {
				return pos + 1;
			}
			pos++;
		}

		return -1;
	}

	private void assignPosToPif() {

		int index = 0;
		for (pif p : PIF) {
			int pos = 0;
			if (p.code == 0) {
				pos = getSTPosition(p.token, identifiersST);
				pif n = new pif();
				n.code = p.code;
				n.token = p.token;
				n.positionST = pos;
				PIF.set(index, n);
			} else if (p.code == 1) {
				pos = getSTPosition(p.token, constantsST);
				pif n = new pif();
				n.code = p.code;
				n.token = p.token;
				n.positionST = pos;
				PIF.set(index, n);
			} else {

			}

			index++;
		}
	}

	private int getTokenCode(String token) {

		if (token.equals("+")) {
			return 3;
		} else if (token.equals("-")) {
			return 4;
		} else if (token.equals("*")) {
			return 5;
		} else if (token.equals("<")) {
			return 6;
		} else if (token.equals("<=")) {
			return 7;
		} else if (token.equals(">")) {
			return 8;
		} else if (token.equals(">=")) {
			return 9;
		} else if (token.equals("==")) {
			return 10;
		} else if (token.equals("=")) {
			return 11;
		} else if (token.equals(":")) {
			return 12;
		} else if (token.equals("{")) {
			return 13;
		} else if (token.equals("}")) {
			return 14;
		} else if (token.equals("[")) {
			return 15;
		} else if (token.equals("]")) {
			return 16;
		} else if (token.equals("(")) {
			return 17;
		} else if (token.equals(")")) {
			return 18;
		} else if (token.equals(";")) {
			return 19;
		} else if (token.equals(",")) {
			return 20;
		} else if (token.equals("or")) {
			return 21;
		} else if (token.equals("and")) {
			return 22;
		} else if (token.equals("not")) {
			return 23;
		} else if (token.equals("integer")) {
			return 24;
		} else if (token.equals("string")) {
			return 25;
		} else if (token.equals("if")) {
			return 26;
		} else if (token.equals("else")) {
			return 27;
		} else if (token.equals("while")) {
			return 28;
		} else if (token.equals("hin")) {
			return 29;
		} else if (token.equals("hout")) {
			return 30;
		}

		return 404;

	}

}
