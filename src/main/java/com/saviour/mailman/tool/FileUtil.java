package com.saviour.mailman.tool;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Component
public class FileUtil {

    public void toBrowser(HttpServletResponse response, String filename){
        if (filename != null) {
            File file = new File(filename);
            if (file.exists()) {
                response.setContentType("application/force-download");
                response.addHeader("Content-Disposition", "attachment;filename=" + filename);
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();

                    int len;
                    while ((len = bis.read(buffer)) != -1) {
                        os.write(buffer, 0, len);
                        os.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
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

    public void toLocal(File file, String path){
        FileInputStream fis = null;
        BufferedInputStream bis = null;

        OutputStream os = null;

        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            byte[] buffer = new byte[1024];

            os = new FileOutputStream(path);

            int len;
            while ((len = bis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
                os.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public File multiPartFile2File(MultipartFile multipartFile){
        File f = new File(multipartFile.getOriginalFilename());
        InputStream in = null;
        OutputStream os = null;
        try {
            in  = multipartFile.getInputStream();
            os = new FileOutputStream(f);

            byte[] buffer = new byte[4096];
            int len;
            while ((len = in.read(buffer)) != -1){
                os.write(buffer,0,len);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return f;
    }

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

    public void delete(String path){
        File file = new File(path);
        if(file.exists()){
            file.delete();
        }
    }
}
