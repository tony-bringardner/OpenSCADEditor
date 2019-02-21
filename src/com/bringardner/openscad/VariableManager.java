package com.bringardner.openscad;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableManager {
	private static 		Pattern rx = Pattern.compile("\"[^{\"]*\"|((?=_[A-Za-z_0-9]|[A-Za-z])[A-Za-z_0-9]+(?=\\s*=))");
	
	
	static class Scope {
		public Scope(int start) {
			this.start = start;
		}
		public int start;
		public int end;
		public StringBuilder data = new StringBuilder();
	}

	public static List<Scope> getScopes(String code) {
		List<Scope> ret = new ArrayList<>();
		Stack<Scope> stack = new Stack<Scope>();
		byte [] data = code.getBytes();
		Scope scope = new Scope(0);
		scope.end = data.length;

		for (int idx = 0; idx < data.length; idx++) {
			switch (data[idx]) {
			case '/':
				if(idx < data.length-1) {
					if( data[idx+1] == '/') {
						while(idx < data.length && data[++idx] != '\n') {
							//  move to the end of line
						}
					} else if( data[idx+1] == '*') {
						int strEnd = data.length-2;  //  calculate this before the loop
						while(++idx < strEnd && (data[idx] != '*' && data[idx+1] != '/')) {
							//  move to the end of the comment
						}
					}
				}
				break;

			case '(':
			case '{':
				stack.push(scope);
				scope = new Scope(idx);
				break;
			case ')':
			case '}':
				scope.end = idx;
				ret.add(scope);
				scope = stack.pop();
				break;

			default:
				scope.data.append((char)data[idx]);
				break;
			}
		}

		ret.add(scope);
		return ret;
	}

	public static int findPosOfLine(String code, int lineNUmber) {
		int ret = 0;
		int cnt = 0;
		int pos = code.indexOf('\n');
		while( pos >=0 ) {
			if( ++cnt >= lineNUmber) {
				ret = pos;
				break;
			}
			pos = code.indexOf('\n',pos+1);
		}

		return ret;
	}

	public static List<String> findVariables(String code  ) {
		List<String> ret = new ArrayList<>();
		Matcher m = rx.matcher(code);
		int minNameLen = Configuration.getInstance().getMinVariableNameLength();

		while(m.find()) {
			String nm = m.group(1);
			if( nm != null ) {
				nm = nm.trim();
				if( nm.length()>=minNameLen) {
					if( !ret.contains(nm)) {
						ret.add(nm);
					}

				}
			}
		}

		return ret;
	}

	public static List<Scope> findScope(List<Scope> scopes, int pos) {
		List<Scope> ret = new ArrayList<>();

		for (Scope s : scopes) {
			if( s.end >= pos && s.start < pos) {
				ret.add(s);
			}
		}

		return ret;
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException {
		String code = Editor.readFile(new File("T:\\Applications\\OpenSCAD\\workspace\\SmalleDriverPegV2.scad"));

		DataInputStream in = new DataInputStream(System.in);
		String line = in.readLine();
		while(!line.equals("x")) {
			int lineNumber = Integer.parseInt(line);
			int pos = findPosOfLine(code, lineNumber);
			System.out.println("line="+lineNumber+" pos="+pos);
			long start = System.currentTimeMillis();
			List<String> vars = findVariables(code,pos,null);
			long time = System.currentTimeMillis()-start;
			System.out.println("time="+time);
			System.out.println("\tvars count = "+vars.size());
			for (String v : vars) {
				System.out.println("\t"+v);
			}
			line = in.readLine();
		}
	}
	public static void main2(String[] args) throws IOException {


		//Pattern rx = Pattern.compile("\"[^\"]*\"|((?=_[a-z_0-9]|[a-z])[a-z_0-9]+(?=\\s*=))");
		// ^[a-z_]\\w*$
		// "[^\"]*"

		String code = "xxx=5;\n name = \"steve\", { bro = \"4, hi = bye\", lolwot = \"wait wot\"}";
		//code = Editor.readFile(new File("T:\\Applications\\OpenSCAD\\workspace\\SmalleDriverPegV2.scad"));
		Matcher m = rx.matcher(code);
		Map<String,String> map = new TreeMap<>();

		while(m.find()) {
			String nm = m.group(1);
			if( nm != null ) {
				nm = nm.trim();
				if( nm.length()>2) {
					map.put(nm, nm);
				}
			}
		}

		for (String nm : map.keySet()) {
			System.out.println(nm);
		}

		System.out.println("Done");

	}

	public static List<String> findVariables(String code, int dot, List<ModuleAutoCompleteManager> modules)  {
		List<String> ret = null;
		List<Scope> s = findScope(getScopes(code), dot);
		StringBuilder sb = new StringBuilder();
		Map<Integer,Scope> starts = new HashMap<>();
		for (Scope scope : s) {
			sb.append(scope.data.toString());
			starts.put(scope.start, scope);
			//System.out.println("start="+scope.start);
		}
		ret = findVariables(sb.toString());
		if( modules != null ) {
			for (ModuleAutoCompleteManager mod : modules) {
				if( starts.containsKey(mod.getScopeStart()-1) || starts.containsKey(mod.getScopeStart()) ||starts.containsKey(mod.getScopeStart()+1)) {
					for (String arg : mod.getArgs()) {
						ret.add(arg.split("=")[0]);
					}
				}
			}
		}
		return ret;
	}

	@SuppressWarnings("deprecation")
	public static void main3(String[] args) throws IOException {
		String code = Editor.readFile(new File("T:\\Applications\\OpenSCAD\\workspace\\SmalleDriverPegV2.scad"));
		List<Scope> scopes = getScopes(code);
		Collections.sort(scopes, new Comparator<Scope>() {

			@Override
			public int compare(Scope o1, Scope o2) {
				int ret = o1.start - o2.start;
				if( ret == 0 ) {
					ret = o1.end = o2.end;
				}
				return ret;
			}
		});

		System.out.println("size="+scopes.size());
		for (Scope s : scopes) {
			System.out.println("start="+s.start+" end="+s.end);
		}

		DataInputStream in = new DataInputStream(System.in);
		String line = in.readLine();
		while(!line.equals("x")) {
			try {
				int lineNumber = Integer.parseInt(line);
				int pos = findPosOfLine(code, lineNumber);
				System.out.println("line="+lineNumber+" pos="+pos);
				List<Scope> s = findScope(scopes, pos);
				System.out.println("scope count = "+s.size());
				if( s.size() > 0 ) {
					StringBuilder sb = new StringBuilder();
					for (Scope scope : s) {
						sb.append("--------------------'\n"+scope.data+"'\n----------------------");
					}
					System.out.println(sb);
					List<String> vars = findVariables(sb.toString());
					System.out.println("\tvars count = "+vars.size());
					for (String v : vars) {
						System.out.println(v);
					}
				}
				//String txt = code.substring(s.start, s.end);
				//System.out.println("--------------------'\n"+txt+"'\n----------------------");
			} catch (Exception e) {
				// TODO: handle exception
			}
			line = in.readLine();
		}
	}
}
