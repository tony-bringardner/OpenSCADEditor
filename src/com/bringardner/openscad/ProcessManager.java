package com.bringardner.openscad;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class ProcessManager {

	private Process process;
	private BufferedReader br;
	private boolean restartOnClose=true;
	private File previewFile;
	private Thread thread;
	private String processResult="";

	public ProcessManager() {
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
						try {
							process = new ProcessBuilder(Configuration.getInstance().getExecPath(),
									getPreviewFile().getAbsolutePath()).start();
							br = new BufferedReader(new InputStreamReader(process.getInputStream()));
							String line;

							while ((line = br.readLine()) != null) {
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


	public void setProcess(Process process) {
		this.process = process;
	}


	public BufferedReader getBr() {
		return br;
	}


	public void setBr(BufferedReader br) {
		this.br = br;
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
