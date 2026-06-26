package de.gimik.apps.gpstracker.backend.util;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

import com.sun.jersey.multipart.FormDataBodyPart;

public class ZipUtil {
    public static String compress(List<FormDataBodyPart> files, String directory,String folder,String fileName) {
        try {
        	FileUtils.forceMkdir(new File(directory,folder));
        	String fosPath =directory +"/"+ folder +"/"+fileName;
        	String resultPath = folder +"/"+fileName;
            FileOutputStream   fos = new FileOutputStream(fosPath);
            ZipOutputStream zos = new ZipOutputStream(fos);
            byte[] buffer = new byte[128];
            for (int i = 0; i < files.size(); i++) {
            	InputStream currentFile = files.get(i).getValueAs(InputStream.class);
//                if (!currentFile.isDirectory()) {
                    ZipEntry entry = new ZipEntry(files.get(i).getContentDisposition().getFileName());
                    InputStream fis = currentFile;
                    zos.putNextEntry(entry);
                    int read = 0;
                    while ((read = fis.read(buffer)) != -1) {
                        zos.write(buffer, 0, read);
                    }
                    zos.closeEntry();
                    fis.close();
//                }
            }
            zos.close();
            fos.close();
            return resultPath;
        } catch (FileNotFoundException fnfe) {
        	fnfe.printStackTrace();
            System.out.println("File not found : " + fnfe);
        } catch (IOException ioe) {
            System.out.println("IOException : " + ioe);
        }
        return "";
    }
}
