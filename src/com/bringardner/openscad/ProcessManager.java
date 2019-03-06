package com.bringardner.openscad;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ProcessManager {

	private Process process;
	private boolean restartOnClose=true;
	private File previewFile;
	private Thread thread;
	private String processResult="";
	private List<String> args;
	
	
	public ProcessManager() {
	}

	
	
	public List<String> getArgs() {
		return args;
	}

	public void setArgs(String... command) {
        this.args = new ArrayList<>(command.length);
        for (String arg : command)
            this.args.add(arg);
    }

	public void setArgs(List<String> args) {
		this.args = args;
	}



	public String getProcessResult() {
		return processResult;
	}

	public boolean isAlive() {
		return process != null && process.isAlive();
	}
	
	public void start() {
		if( !isAlive() ) {
			thread = new Thread(new Runnable() {

				@Override
				public void run() {
					do {
						BufferedReader br=null;
						try {
							if( args == null || args.size()==0) {
								process = new ProcessBuilder(Configuration.getInstance().getExecPath(),	getPreviewFile().getAbsolutePath()).start();
							} else {
								List<String> myargs = new ArrayList<String>();
								myargs.add(Configuration.getInstance().getExecPath());
								myargs.addAll(args);
								process = new ProcessBuilder(myargs).start();
							}
							br = new BufferedReader(new InputStreamReader(process.getInputStream()));
							BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
							String line;

							
							while ((line = br.readLine()) != null) {
								processResult += line+"\n";
							}
							while ((line = err.readLine()) != null) {
								processResult += line+"\n";
							}
						} catch(Throwable e) {
							processResult += e.toString()+"\n";
							if( br != null ) {
								try {
									br.close();
								} catch (Exception e2) {
								}
							}
						}
					} while(restartOnClose);
				}
			});
			thread.start();
		}
	}
	
	public void stop() {
		if( process != null) {
			if(process.isAlive()) {
				restartOnClose = false;
				process.destroyForcibly();
			}
		}
	}
	
	public Process getProcess() {
		return process;
	}

	public boolean isRestartOnClose() {
		return restartOnClose;
	}


	public void setRestartOnClose(boolean restartOnClose) {
		this.restartOnClose = restartOnClose;
	}


	public File getPreviewFile() {
		if( previewFile == null ) {
			synchronized (this) {
				if( previewFile == null ) {
					try {
						previewFile = File.createTempFile("Editor", ".scad");
						OutputStream out = new FileOutputStream(previewFile);
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return previewFile;
	}


	public void setPreviewFile(File previewFile) {
		this.previewFile = previewFile;
	}


	public void updatePreviewFile(String code) throws IOException {
		OutputStream out = null;
		try {
			out = new FileOutputStream(getPreviewFile());
			out.write(code.getBytes());
		} finally {
			try {
				out.close();
			} catch (Exception e2) {
			}
		}
	}
}
