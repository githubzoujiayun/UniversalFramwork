package com.universal.framwork.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

public class FileUtil
{
	public static File saveCache(Bitmap bitmap,String dir,String key)
	{
		File file=new File(dir,key);
		try {
		  if(!file.exists())
		  {
		    file.createNewFile();
		  }
			FileOutputStream fos=new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
	}
	
	//通过url的hashcode生成key
	public static String keyGenerator(String imageUri) {
    return String.valueOf(imageUri.hashCode());
  }

}
