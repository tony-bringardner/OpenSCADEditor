package com.bringardner.openscad;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncludeFile {
	private static List<String> liraryFolders;
	public static  List<String> getLibraryFolders() {
		if( liraryFolders == null ) {
			synchronized (IncludeFile.class) {
				if( liraryFolders == null ) {
					String processResult="";
					BufferedReader br=null;
					try {
						Process process = new ProcessBuilder(Configuration.getInstance().getExecPath(),	"--info").start();
						br = new BufferedReader(new InputStreamReader(process.getInputStream()));
						String line;

						while ((line = br.readLine()) != null) {
							processResult += line+"\n";
						}

					} catch(Throwable e) {
						processResult += e.toString()+"\n";						
					} finally {
						if( br != null ) {
							try {
								br.close();
							} catch (Exception e2) {
							}
						}
					}
					String lines [] = processResult.split("\n");
					List<String> folders = new ArrayList<>();
					for(int idx=0; idx < lines.length; idx++) {
						if( lines[idx].startsWith("OpenSCAD library path:")) {
							while(++idx < lines.length) {
								String path = lines[idx].trim();
								if( path.isEmpty()) {
									break;
								}
								folders.add(path);
							}
							break;
						}
					}
					if( folders.size()>0) {
						liraryFolders = folders;
					}

					//System.out.println("response="+processResult);
				}
			}
		}
		return liraryFolders;
	}

	private String fileName="Undefined";
	private long lastModified;
	private String code;
	private File file;
	//  The scad file that includes this file
	private File scadParent;


	public IncludeFile(String fileName,File scadParent) {
		this.fileName = fileName;
		this.scadParent = scadParent;
	}

	public String getFileName() {
		return fileName;
	}



	public void setFileName(String fileName) {
		this.fileName = fileName;
	}






	public String getCode() {
		File file = getFile();
		if( code == null || (file != null && file.lastModified() != lastModified) ) {
			synchronized (this) {
				if( code == null || (file != null && file.lastModified() != lastModified) ) {
					if( file != null && file.exists()) {
						try {
							code = Editor.readFile(file);
						} catch (IOException e) {
						}
					}
				}
			}
		}
		return code;
	}



	private File findFile() {
		File ret = null;
		String name = getFileName();
		File tmp = new File(name);
		if( tmp.exists()) {
			ret = tmp;
		} else {
			if( scadParent != null ) {
				tmp = new File(scadParent.getParentFile(),name);
				if( tmp.exists()) {
					ret = tmp;
				}
			}
			if( ret == null ) {
				List<String> libs = getLibraryFolders();
				if( libs != null ) {
					for (String lib : libs) {
						tmp = new File(lib,name);
						if( tmp.exists()) {
							ret = tmp;
							break;
						}				
					}
				}
			}
		}
		if( ret != null ) {
			lastModified = ret.lastModified();
		}

		return ret;
	}





	public void setCode(String code) {
		this.code = code;
	}



	public File getFile() {
		if( file == null ) {
			synchronized (this) {
				file = findFile();
			}
		}
		return file;
	}



	public void setFile(File file) {
		this.file = file;
	}



	public File getScadParent() {
		return scadParent;
	}



	public void setScadParent(File scadParent) {
		this.scadParent = scadParent;
	}



	@Override
	public String toString() {
		return fileName;
	}

	@Override
	public boolean equals(Object obj) {
		boolean ret = false;
		if (obj instanceof IncludeFile) {
			IncludeFile inc = (IncludeFile) obj;
			ret = toString().equals(inc.toString());
		}
		return ret;
	}

	public static Map<String,IncludeFile> findAllIncluded(String code,File currentScad) {
		Map<String,IncludeFile> ret = new HashMap<>();
		ret.putAll(findIncluded("use", code, currentScad));
		ret.putAll(findIncluded("include", code, currentScad));
		return ret;
	}

	public static Map<String,IncludeFile> findIncluded(String type,String code,File currentScad) {
		Map<String,IncludeFile> ret = new HashMap<>();
		int start = code.indexOf(type);
		while(start >= 0 ) {
			start += type.length();
			int idx = code.indexOf('<', start);
			if( idx > 0 ) {
				int end = code.indexOf('>',idx);
				if( end > idx ) {
					String name = code.substring(idx+1, end).trim();
					ret.put(name, new IncludeFile(name,currentScad));
				}
			}
			start = code.indexOf(type,++start);
		}
		return ret;
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {
		List<String> libs = getLibraryFolders();
		System.out.println(libs);
		File scadFile = new File("T:\\Applications\\OpenSCAD\\workspace\\SmalleDriverPeg.scad");
		String code = Editor.readFile(scadFile);
		Map<String, IncludeFile> use = findAllIncluded( code, scadFile);
		Map<String, IncludeFile> use2 = findIncluded("include", code, scadFile);
		System.out.println("use="+use);
		for (IncludeFile inc : use.values()) {
			System.out.println("inc="+inc);
			/*
			code = inc.getCode();
			System.out.println("code="+code);
			System.out.println("eq="+(use.equals(use2)));
			*/
		}
	}

}
