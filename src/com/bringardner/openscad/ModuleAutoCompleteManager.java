package com.bringardner.openscad;

import java.util.ArrayList;
import java.util.List;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;
import org.fife.ui.autocomplete.TemplateCompletion;

public class ModuleAutoCompleteManager {


	public static final Object EDIT_MODULE_NAME = "Module~Name";

	private static long lastModuleName;

	private String name;
	private List<String> args;
	private int start;
	private String type;

	public ModuleAutoCompleteManager(String name, List<String> args, int scopeStart,String type) {
		this.name = name;
		this.start = scopeStart;
		this.type = type;
		if( args == null ) {
			this.args = new ArrayList<>();
		} else {
			this.args = args;
		}
	}



	public String getType() {
		return type;
	}



	public int getScopeStart() {
		return start;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public List<String> getArgs() {
		return args;
	}



	public void setArgs(List<String> args) {
		if( args == null ) {
			this.args = new ArrayList<>();
		} else {
			this.args = args;
		}
	}



	public String getCompletionString() {
		String ret = null;
		if( args != null && args.size()>0) {
			StringBuilder buf = new StringBuilder();
			for (String arg : args) {
				if( buf.length()>0) {
					buf.append(", ");
				}
				int idx= arg.indexOf('=');
				if( idx > 0 ) {
					//String name = arg.substring(0,idx);

					buf.append("${"+arg+"}");
				} else {
					buf.append("${"+arg+"}");
				}
			}
			ret = name+"("+buf+") ${cursor}";
		} else {
			ret = name+" ()";
		}
		return ret;
	}

	@Override
	public boolean equals(Object obj) {
		boolean ret = false;
		if (obj instanceof ModuleAutoCompleteManager) {
			ModuleAutoCompleteManager m = (ModuleAutoCompleteManager) obj;
			ret = m.name.equals(name);
			if( ret ) {
				ret=args.equals(m.args);
			}
		}

		return ret;
	}

	public Completion getCompletion(CompletionProvider provider) {
		Completion ret = null;
		if( args != null && args.size()>0) {
			ret = new TemplateCompletion(provider, name, name+"-"+type,getCompletionString());
		} else {
			ret = new ShorthandCompletion(provider,name+"-"+type,name+" ()");
		}
		return ret;
	}

	public String toString() {
		return getCompletionString();
	}

	public static List<ModuleAutoCompleteManager> getModules(String type,String code){
	
		List<ModuleAutoCompleteManager> ret = new ArrayList<>();
		if( code != null ) {
			int start = code.indexOf(type);
			while(start >=0 ) {
				start += 6;
				int end = code.indexOf('(',start);
				if( end > start ) {
					String name = code.substring(start, end).trim();
					if(name.equals(EDIT_MODULE_NAME)) {
						lastModuleName = System.currentTimeMillis();
						return null;
					}
					start = end+1;
					end = code.indexOf(')',start);
					List<String> args = null;
					if( end > start) {
						String argString = code.substring(start, end);
						args = parseArgs(argString);
						
					}
					int scopeStart = code.indexOf('{',start);
					ModuleAutoCompleteManager mod = new ModuleAutoCompleteManager(name,args,scopeStart,type);
					if( !ret.contains(mod) ) {
						ret.add(mod);
					}
				}
				start = code.indexOf(type,start);
			}
		}
		return ret;
	}



	public static List<String> parseArgs(String argString){
		List<String> ret = new ArrayList<>();
		argString = argString.trim();
		if( !argString.isEmpty()) {
			for(String name : argString.split("[,]") ) {
				//int idx=name.indexOf('=');
				ret.add(name.trim());
			}

		}

		return ret;
	}

	public static List<ModuleAutoCompleteManager> getModules(String code){

		List<ModuleAutoCompleteManager> ret = getModules("module", code);
		//ret.addAll(getModules("function",code));
		return ret;
	}

	
	/*
	 * 	provider.addCompletion(new TemplateCompletion(provider, "for", "for", 
				"for(${idx} = [${start} : ${increment} : ${end}]) {\n\t${cursor}\n}"));
	 */

	public static long getLastModuleName() {
		return lastModuleName;
	}



	public static void main(String[] args) {
		String code = "module myCube(width,length,height,radius,usesquare=false) {\n\tstuff\n} \nmodule test () {\n}\nmodule test2() {}";
		String code2 = "module myCube(widthx,length,height,radius,usesquare=false) {\n\tstuff\n} \nmodule test () {\n}\nmodule test2() {}";
		List<ModuleAutoCompleteManager> list = getModules(code);
		List<ModuleAutoCompleteManager> list2 = getModules(code2);
		System.out.println(list.toString());
		System.out.println("eq1="+(list.equals(list)));
		System.out.println("eq2="+(list.equals(list2)));

	}

}
