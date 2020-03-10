package com.saviour.mailman.test;

import com.saviour.mailman.tool.FFmpegUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class TestFrameSwitch {

    public static void main(String[] args) {
        //TestFrameSwitch.generateWithWhite();
        FFmpegUtil fFmpegUtil = new FFmpegUtil();
        //fFmpegUtil.generateVideo("D:/OTHER/test4cn/Mailman/test-w.mp4",30, "image-w");

        fFmpegUtil.generatePictures("D:/OTHER/test4cn/Mailman/test-w-captured.mp4", "image-w");
    }

    public static String generateWithWhite() {
        for (int i = 0; i < 120; i++) {
            int height = 300;
            int width = 300;

            int fontSize = width / 10;

            Font font = new Font("Serif", Font.PLAIN, fontSize);
            Random random = new Random();

            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = (Graphics2D) bi.getGraphics();
            g2.setBackground(Color.WHITE);
            g2.clearRect(0, 0, width, height);
            g2.setPaint(Color.BLACK);
            g2.setFont(font);

            FontRenderContext context = g2.getFontRenderContext();
            Rectangle2D bounds = font.getStringBounds(String.valueOf(i), context);
            double x = (width - bounds.getWidth()) / 2;
            double y = (height - bounds.getHeight()) / 2;
            double ascent = -bounds.getY();
            double baseY = y + ascent;

            if (i % 2 == 0) {
                g2.drawString(String.valueOf(i / 2), (int) x + random.nextInt() % 100, (int) baseY + random.nextInt() % 100);
            }

            File file = new File("D:/OTHER/test4cn/Mailman/image-w" + i + ".jpg");
            try {
                ImageIO.write(bi, "jpg", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return "OK";
    }

    public static String generate(){
        for (int i = 0; i < 120; i++) {
            int height = 300;
            int width = 300;

            int fontSize = width / 10;

            Font font = new Font("Serif", Font.PLAIN, fontSize);
            Random random = new Random();

            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = (Graphics2D)bi.getGraphics();
            g2.setBackground(Color.WHITE);
            g2.clearRect(0, 0, width, height);
            g2.setPaint(Color.BLACK);
            g2.setFont(font);

            FontRenderContext context = g2.getFontRenderContext();
            Rectangle2D bounds = font.getStringBounds(String.valueOf(i), context);
            double x = (width - bounds.getWidth()) / 2;
            double y = (height - bounds.getHeight()) / 2;
            double ascent = -bounds.getY();
            double baseY = y + ascent;
            g2.drawString(String.valueOf(i), (int)x + random.nextInt() % 100, (int)baseY + random.nextInt() % 100);

            File file = new File("D:/OTHER/test4cn/Mailman/image" + i + ".jpg");
            try {
                ImageIO.write(bi, "jpg", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return "OK";
    }
}
