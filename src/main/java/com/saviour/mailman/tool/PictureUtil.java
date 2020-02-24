package com.saviour.mailman.tool;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
public class PictureUtil {

    public void generatePictures(Boolean[] bits, int num, String root, String prefix) {
        BufferedImage[] images = new BufferedImage[num];

        int counter = 0;
        for (int i = 0; i < num ; i++){
            images[i] = new BufferedImage(
                    Parameter.VIDEOWIDTH.getValue(),
                    Parameter.VIDEOHEIGHT.getValue(),
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = (Graphics2D)images[i].getGraphics();
            g2.setBackground(Color.BLACK);
            for (int y = 0; y < Parameter.VIDEOHEIGHT.getValue(); y++){
                for (int x = 0; x < Parameter.VIDEOWIDTH.getValue(); x++){
                    if(counter >= bits.length){
                        break;
                    }

                    int rgb = (bits[counter] ? 0xffffffff : 0x00000000);
                    images[i].setRGB(x, y, rgb);

                    counter++;
                }
            }

            save(images[i], root, prefix + (i + 1));

        }
    }

    private void save(BufferedImage image, String root, String name){
        File file = new File(root + File.separator + name + ".jpg");
        try {
            ImageIO.write(image, "jpg", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int calGray(int pixel){
        int r = (pixel & 0xff0000) >> 16;
        int g = (pixel & 0xff00) >> 8;
        int b = (pixel & 0xff);
        return (r + g +b) / 3;
    }

    public void deletePictures(String root, String prefix) {
        for(int i = 1; ; i++){
            File image = new File(root + File.separator + prefix + i + ".jpg");
            if(image.exists()){
                image.delete();
                continue;
            }
            return;
        }
    }
}
