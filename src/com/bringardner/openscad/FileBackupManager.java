package com.bringardner.openscad;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileBackupManager {
	
	

	private void ensureCapacity(File file) {
		if( file.exists()) {
			String backupDir = Configuration.getInstance().getBackupFolder();
			File dir = file.getParentFile();
			if( backupDir != null) {
				dir = new File(backupDir);
				if( !dir.isAbsolute()) {
					dir = new File(file.getParentFile(),backupDir);
				}
			}
			if( !dir.exists()) {
				dir.mkdirs();
			}
			int next = 0;
			int maxBackups =  Configuration.getInstance().getMaxBackup();
			
			for(int idx=1; idx <= maxBackups; idx++) {
				File tmp = new File(dir,getFileName(file.getName(),idx));
				if( !tmp.exists()) {
					next = idx;
					break;
				}
			}
			if( next == 0) {
				File tmp = new File(dir,getFileName(file.getName(),maxBackups));
				tmp.delete();
				next = maxBackups;
			}
			for(int idx=next; idx > 1; idx -- ) {
				File tmp1 = new File(dir,getFileName(file.getName(),idx));
				File tmp2 = new File(dir,getFileName(file.getName(),idx-1));
				tmp2.renameTo(tmp1);
			}
			File tmp2 = new File(dir,getFileName(file.getName(),1));
			file.renameTo(tmp2);
		
		}
	}
	
	private String getFileName(String name, int idx) {
		String ret = name+"-"+idx;
		int dot = name.indexOf('.');
		if( dot > 0 ) {
			ret = name.substring(0, dot)+"-"+idx+name.substring(dot);
		}
		return ret;
	}

	public void save(File tmp, String text) throws IOException {
		ensureCapacity(tmp);
		OutputStream out = null;
		try {
			out = new FileOutputStream(tmp);
			out.write(text.getBytes());
		} finally {
			try {
				out.close();
			} catch (Exception e2) {
			}
		}
	}
	
	
	public static void main(String[] args) throws IOException {
		FileBackupManager mgr = new FileBackupManager();
		System.out.println("name=="+mgr.getFileName("testFilescad", 2));
		File file = new File("T:\\Tmp\\Backups\\TestFile.txt");
		for(int idx=0; idx < ( Configuration.getInstance().getMaxBackup()+2); idx++ ) {
			mgr.save(file, "Sample "+(idx+100));
		}

	}

}
