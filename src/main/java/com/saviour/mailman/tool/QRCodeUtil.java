package com.saviour.mailman.tool;


import java.awt.*;

import java.awt.geom.RoundRectangle2D;

import java.awt.image.BufferedImage;

import java.io.File;

import java.io.IOException;
import java.io.OutputStream;

import java.util.Hashtable;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;

import com.google.zxing.BinaryBitmap;

import com.google.zxing.DecodeHintType;

import com.google.zxing.EncodeHintType;

import com.google.zxing.MultiFormatReader;

import com.google.zxing.MultiFormatWriter;

import com.google.zxing.Result;

import com.google.zxing.common.BitMatrix;

import com.google.zxing.common.HybridBinarizer;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.saviour.mailman.encoder.QRBasedVideoEncoder;
import org.springframework.stereotype.Component;


@Component
public class QRCodeUtil {

    private static final String CHARSET = "utf-8";

    private static final String FORMAT_NAME = "JPG";

    // 二维码尺寸

    private static final int QRCODE_SIZE = 500;

    // LOGO宽度

    private static final int WIDTH = 60;

    // LOGO高度

    private static final int HEIGHT = 60;


    private static BufferedImage createImage(String content, String imgPath, boolean needCompress) throws Exception {

        Hashtable hints = new Hashtable();

        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);

        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE,

                hints);

        int width = bitMatrix.getWidth();

        int height = bitMatrix.getHeight();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {

            for (int y = 0; y < height; y++) {

                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);

            }

        }

        if (imgPath == null || "".equals(imgPath)) {

            return image;

        }

        return image;

    }


    private static void insertImage(BufferedImage source, String imgPath, boolean needCompress) throws Exception {

        File file = new File(imgPath);

        if (!file.exists()) {

            System.err.println("" + imgPath + "   该文件不存在！");

            return;

        }

        Image src = ImageIO.read(new File(imgPath));

        int width = src.getWidth(null);

        int height = src.getHeight(null);

        if (needCompress) { // 压缩LOGO

            if (width > WIDTH) {

                width = WIDTH;

            }

            if (height > HEIGHT) {

                height = HEIGHT;

            }

            Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);

            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            Graphics g = tag.getGraphics();

            g.drawImage(image, 0, 0, null); // 绘制缩小后的图

            g.dispose();

            src = image;

        }

        // 插入LOGO

        Graphics2D graph = source.createGraphics();

        int x = (QRCODE_SIZE - width) / 2;

        int y = (QRCODE_SIZE - height) / 2;

        graph.drawImage(src, x, y, width, height, null);

        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);

        graph.setStroke(new BasicStroke(3f));

        graph.draw(shape);

        graph.dispose();

    }


    public static void encode(String content, String imgPath, String destPath, boolean needCompress) throws Exception {

        BufferedImage image = QRCodeUtil.createImage(content, imgPath, needCompress);

        mkdirs(destPath);

        // String file = new Random().nextInt(99999999)+".jpg";

        // ImageIO.write(image, FORMAT_NAME, new File(destPath+"/"+file));

        ImageIO.write(image, FORMAT_NAME, new File(destPath));

    }


    public static BufferedImage encode(String content, String destPath, boolean needCompress) throws Exception {

        BufferedImage image = QRCodeUtil.createImage(content, destPath, needCompress);

        mkdirs(destPath);

        // String file = new Random().nextInt(99999999)+".jpg";

        // ImageIO.write(image, FORMAT_NAME, new File(destPath+"/"+file));

        ImageIO.write(image, FORMAT_NAME, new File(destPath));
        return image;

    }


    public static void mkdirs(String destPath) {

        File file = new File(destPath);

        // 当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)

        if (!file.exists() && !file.isDirectory()) {

            file.mkdirs();

        }

    }


    public static void encode(String content, String imgPath, String destPath) throws Exception {

        QRCodeUtil.encode(content, imgPath, destPath, false);

    }


    public static void encode(String content, String destPath) throws Exception {

        QRCodeUtil.encode(content, null, destPath, false);

    }


    public static void encode(String content, String imgPath, OutputStream output, boolean needCompress)
            throws Exception {

        BufferedImage image = QRCodeUtil.createImage(content, imgPath, needCompress);

        ImageIO.write(image, FORMAT_NAME, output);

    }


    public static void encode(String content, OutputStream output) throws Exception {

        QRCodeUtil.encode(content, null, output, false);

    }


    public static String decode(File file) throws Exception {

        BufferedImage image;

        image = ImageIO.read(file);

        if (image == null) {

            return null;

        }

        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);

        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Result result;

        Hashtable hints = new Hashtable();

        hints.put(DecodeHintType.CHARACTER_SET, CHARSET);

        result = new MultiFormatReader().decode(bitmap, hints);

        String resultStr = result.getText();

        return resultStr;

    }


    public static String decode(String path) throws Exception {

        return QRCodeUtil.decode(new File(path));

    }

    public void generatePictures(String message, int maxLen, String root, String prefix) throws Exception {
        /*
        protocol is implemented here.
         */
        int numOfPictures =  ((message.length() + 1) / maxLen + 1);

        for (int i = 0; i < numOfPictures - 1; i++) {
            encode(message.substring(i * maxLen, (i + 1) * maxLen),
                    root + File.separator + prefix + (i + 1) + ".jpg",
                    true);
        }
        if(message.length() % maxLen != 0){
            encode(message.substring(maxLen * (numOfPictures - 1)),
                    root + File.separator + prefix + numOfPictures + ".jpg");
        }
    }

    public byte[] getBytesFromPictures(String root, String prefix, int startFrame, int duration, int num) throws Exception {
        File tempFile = null;
        String res = "";

        int loseFrames = 0;
        for(int i = startFrame + duration, counter = 1; ; i+=duration, counter++){
            if(counter > num - 1){
                break;
            }
            tempFile = new File(root + File.separator + prefix + i + ".jpg");
            if(tempFile.exists()){
                String temp = null;

                resize(tempFile);
                tempFile = new File(root + File.separator + prefix + i + ".jpg");

                try {
                    temp = decode(tempFile);
                    System.out.println("Frame " + i + " with message: " + temp);
                } catch (Exception e){
                    byte[] t = new byte[QRBasedVideoEncoder.maxLength];
                    for (int j = 0; j < t.length; j++) {
                        t[j] = '0';
                    }
                    temp = new String(t);
                    loseFrames++;
                    System.out.println("Frame " + i + " lose");
                }
                res = res + temp;
                continue;
            }
            break;
        }
        System.out.println(loseFrames + " frames lose.");
        return res.getBytes();
    }

    public void resize(File tempFile) throws IOException {
        BufferedImage img = ImageIO.read(tempFile);
        int w = img.getWidth();
        int h = img.getHeight();
        int m = w / 500;
        int hdv = h - w > 0 ? (h - w) : 0;
        int wdv = w - h > 0 ? (w - h) : 0;

        img = img.getSubimage(wdv / 2, hdv / 2, w - wdv, h - hdv);
        w = w - wdv;
        h = h - hdv;

        BufferedImage dimg = new BufferedImage(w / m, h / m, img.getType());
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, w / m, h / m,
                0, 0, w, h, null);
        g.dispose();
        ImageIO.write(dimg, "jpg", tempFile);
    }

    public static void main(String[] args) throws Exception {
        File file = new File("D:/OTHER/test4cn/Mailman/bug1.jpg");
        String res = QRCodeUtil.decode(file);
        System.out.println(res);
    }
}

