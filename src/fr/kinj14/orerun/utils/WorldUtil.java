package fr.kinj14.orerun.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class WorldUtil {
	public void copyWorld(File src, File destination) throws IOException {
		if(src.isDirectory()) {
			if(!destination.exists()) {
				destination.mkdir();
			}
			
			String[] srcFiles = src.list();
			
			for(String file : srcFiles) {
				File srcFile = new File(src, file);
				File destinationFile = new File(destination, file);
				
				copyWorld(srcFile, destinationFile);
			}
		} else {
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(destination);
			
			byte[] b = new byte[1204];
			int length = in.read();
			
			while(length > 0) {
				out.write(b, 0, length);
			}
			
			in.close();
			out.close();
		}
	}
	
	public void deleteWorld(File worldFile) {
		// File Exist
		if(worldFile.exists()) {
			File Files[] = worldFile.listFiles();
			
			for(File file : Files) {
				if(file.isDirectory()) {
					deleteWorld(file);
				} else {
					file.delete();
				}
			}
		}
		
	}
}
