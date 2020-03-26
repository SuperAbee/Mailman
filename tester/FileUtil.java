package com.company;

import java.io.*;

public class FileUtil {

    public String write(byte[] result, String path) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(path);
            os.write(result);
            return "OK";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "ERROR";
    }
    public byte[] read(File file){
        byte[] result = null;

        FileInputStream fis = null;
        BufferedInputStream bis = null;

        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            byte[] buffer = new byte[4096];

            int len;
            while ((len = bis.read(buffer)) != -1) {
                if(result == null){
                    result = new byte[len];
                    System.arraycopy(buffer, 0, result, 0, len);
                }
                else{
                    int originalLength = result.length;
                    byte[] originalBytes = result;
                    result = new byte[originalLength + len];
                    System.arraycopy(originalBytes, 0, result, 0, originalLength);
                    System.arraycopy(buffer, 0, result, originalLength, len);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
