package com.example.totalshell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;

public class Util {

	public static String getSDPath() {
		File dir = null;
		boolean sdExists = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		if (sdExists) {
			dir = Environment.getExternalStorageDirectory();
		}
		return dir.getAbsolutePath();
	}

	public static boolean copyFile(Context t, String des, String src) {
//		ProgressBar bar=(ProgressBar)(((Activity)t).findViewById(R.id.progress));
//		bar.setVisibility(View.VISIBLE);
		
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			int byteread = 0;
//			long copied = 0;
			File oldfile = new File(src);
//			long total = oldfile.length();
			if (oldfile.exists()) {
				fis = new FileInputStream(src);
				fos = new FileOutputStream(des);
				byte[] buffer = new byte[4096];
				while ((byteread = fis.read(buffer)) != -1) {
//					copied += byteread;
//					bar.setProgress((int)(100f * copied / total));
					fos.write(buffer, 0, byteread);
				}
				fis.close();
				fos.close();
			}
			File f = new File(des);
			if (f == null || !f.exists() || !f.isFile() || !f.canRead()) {
				;
			} else {
//				bar.setVisibility(View.GONE);
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
//		bar.setVisibility(View.GONE);
		return false;
	}
}
