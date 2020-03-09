package com.saviour.mailman.video;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.saviour.mailman.tool.FFmpegUtil;
import com.saviour.mailman.tool.PictureUtil;
import com.sun.org.apache.xpath.internal.operations.Bool;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xincong yao
 */
@Component
public class SimpleVideoLayer {

    @Autowired
    private FFmpegUtil ffmpegUtil;

    @Autowired
    private PictureUtil pictureUtil;

    public String compose(int fps, int numOfPictures, String workplace, String imagePrefix, Boolean deleteImages){
        generateFirstFrame(fps, numOfPictures, workplace, imagePrefix);
        String path = workplace + "/" + imagePrefix + "-" + fps + "fps.mp4";
        ffmpegUtil.generateVideo(path, fps, imagePrefix);
        if (deleteImages) {
            pictureUtil.deletePictures(workplace, imagePrefix);
        }
        return "OK";
    }

    private String generateFirstFrame(int flag, int numOfPictures, String workplace, String imagePrefix) {
        // with 1 plus head frame
        String strFlag = (numOfPictures + 1) + " frames with " + flag + "fps";
        BufferedImage tempImage;
        try {
            tempImage = ImageIO.read(new File(workplace + File.separator + imagePrefix + "1.jpg"));
        } catch (IOException e) {
            return "Error: image not found, " + workplace + File.separator + imagePrefix + "1.jpg";
        }
        int height = tempImage.getHeight();
        int width = tempImage.getWidth();

        int fontSize = width / 10;

        Font font = new Font("Serif", Font.PLAIN, fontSize);
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D)bi.getGraphics();
        g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0, width, height);
        g2.setPaint(Color.BLACK);
        g2.setFont(font);

        FontRenderContext context = g2.getFontRenderContext();
        Rectangle2D bounds = font.getStringBounds(strFlag, context);
        double x = (width - bounds.getWidth()) / 2;
        double y = (height - bounds.getHeight()) / 2;
        double ascent = -bounds.getY();
        double baseY = y + ascent;
        g2.drawString(strFlag, (int)x, (int)baseY);

        File file = new File(workplace + File.separator + imagePrefix + "0.jpg");
        try {
            ImageIO.write(bi, "jpg", file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "OK";
    }

    public Map<String, Integer> decompose(String workplace, String imagePrefix, String videoName) {
        String path = workplace + '/' + videoName;
        ffmpegUtil.generatePictures(path, imagePrefix);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return detectFirstFrame(workplace, imagePrefix);
    }

    private Map<String, Integer> detectFirstFrame(String workplace, String imagePrefix) {
        Map<String, Integer> table = new HashMap<>();
        table.put("fps", -1);
        table.put("firstFrame", -1);
        table.put("frames", -1);

        ITesseract instance = new Tesseract();

        String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        // Run jar: file:/D:/Serious/Courses/Computer%20Networking/Mailman/target/mailman-0.0.1-SNAPSHOT.jar!/BOOT-INF/classes!/
        path = path.substring(1);

        String[] dir = path.split("mailman-.+-SNAPSHOT\\.jar");
        if (dir.length == 2) {
            path = dir[0].substring(5);
        }
        path = path + "tessdata";
        try {
            path = URLDecoder.decode(path, "UTF-8");
            //System.out.println(path);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        instance.setDatapath(path);
        instance.setLanguage("eng");
        long startTime = System.currentTimeMillis();
        for (int i = 1; i < 60; i++) {
            File file = new File(workplace + File.separator + imagePrefix + i + ".jpg");
            String result = null;
            try {
                result =  instance.doOCR(file).toLowerCase();
            } catch (TesseractException e) {
                e.printStackTrace();
            }

            String[] t = result.split(" frames with ");
            if(t.length > 1){
                t = result.split("[0-9]+ frames with .+fps");
                if(t.length == 1) {
                    result = result.substring(0, result.indexOf(t[0]));
                } else {
                    result = result.substring(t[0].length(), result.indexOf(t[1]));
                }

                String[] values = result.split(" frames with ");

                long endTime = System.currentTimeMillis();
                /*
                 * example: 100 frames with 10fps
                 */
                byte[] frames = values[0].getBytes();
                byte[] fps = values[1].split("fps")[0].getBytes();
                for (int j = 0; j < frames.length; j++) {
                    if(frames[j] == 73 || frames[j] == 108){
                        frames[j] = 49;
                    }
                    if(frames[j] == 79 || frames[j] == 111){
                        frames[j] = 48;
                    }
                    if(frames[j] == 83 || frames[j] == 115){
                        frames[j] = 53;
                    }
                }
                for (int j = 0; j < fps.length; j++) {
                    if(fps[j] == 73 || fps[j] == 108){
                        fps[j] = 49;
                    }
                    if(fps[j] == 79 || fps[j] == 111){
                        fps[j] = 48;
                    }
                    if(fps[j] == 83 || fps[j] == 115){
                        fps[j] = 53;
                    }
                }
                String strFPS = new String(fps);
                String strFrames = new String(frames);

                System.out.println("First frame detecting duration：" + (endTime - startTime) + "ms");
                System.out.println("First frame: " + i);
                System.out.println("Frames: " + strFrames);
                System.out.println("FPS: " + strFPS);
                table.replace("fps", Integer.valueOf(strFPS));
                table.replace("firstFrame", i);
                table.replace("frames", Integer.valueOf(strFrames));
                return table;
            }
        }

        return table;
    }

    public static void main(String[] args) {
        SimpleVideoLayer videoLayer = new SimpleVideoLayer();
        //videoLayer.generateFirstFrame(10, 100, "D:/OTHER/test4cn", "logo");
        //Map<String, Integer> res = videoLayer.detectFirstFrame("D:/OTHER/test4cn", "logo");
        //System.out.println("fps: "  + res.get("fps") + "  "
        //       + "frames: " + res.get("frames") + "  "
        //        + "firstFrame: " + res.get("firstFrame"));
        //String path = "/D:/Serious/Courses/Computer%20Networking/Mailman/target/mailman-0.0.1-SNAPSHOT.jar!/BOOT-INF/classes!/";
        String result = "----\n20 frames with 5fps";
        String[] t = result.split("[0-9]+ frames with ");

        result = result.substring(t[0].length(), result.indexOf(t[1]));
        System.out.println(result);
        for (String s:
             t) {
            System.out.print(s);
        }
    }
}
