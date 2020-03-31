package com.saviour.mailman.decoder;

import com.saviour.mailman.tool.*;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author xincong yao
 */
@Component
public class SimpleVideoDecoder {

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private PictureUtil pictureUtil;

    @Autowired
    private FFmpegUtil ffmpegUtil;

    @Autowired
    private FormatTransfer transfer;

    protected File file;


    /**
     * The root path work place
     */
    protected String root;

    protected static final String VIDEONAME = "tmp.mp4";
    protected static final String PICTUREPREFIX = "tmp";
    protected static final int ORIGINALFPS = 2;

    public void load(File file, String path) {
        this.file = file;
        int index = path.lastIndexOf("/");
        this.root = path.substring(0, index);
    }

    public byte[] decode() throws Exception {
        /*
         * step1: save video to local
         *
         * tips: the separator must be '/',
         *       cause I have used String.subString() method,
         *       and the File.separator() is '\'
         */
        String tmpVideoPath = root + "/" + VIDEONAME;
        fileUtil.toLocal(file, tmpVideoPath);

        /*
         * step2: video -> picture
         */
        ffmpegUtil.generatePictures(tmpVideoPath, PICTUREPREFIX);

        /*
         * step3: parse header
         */
        BufferedImage firstFrame = null;
        try {
            Thread.sleep(500);
            firstFrame = ImageIO.read(new File(root + File.separator + PICTUREPREFIX +"1.jpg"));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        int fps = transfer.bit2Int(extractFPS(firstFrame));
        int bodyLength = transfer.bit2Int(extractLength(firstFrame));

        /*
         * step4: delete temperate pictures
         */
        pictureUtil.deletePictures(root, PICTUREPREFIX);

        /*
         * step5: re-choose picture -> byte,
         * dangerous ！fps might be wrong!
         */
        if(fps > 100){
            return null;
        }
        ffmpegUtil.generatePictures(tmpVideoPath, PICTUREPREFIX);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Boolean[] bits = getBitsFromPictures(root, PICTUREPREFIX);
        byte[] message = SVCP(bits, bodyLength);

        /*
         * step6: delete temperate files and pictures
         */
        pictureUtil.deletePictures(root, PICTUREPREFIX);
        fileUtil.delete(tmpVideoPath);
        return message;
    }

    private byte[] SVCP(Boolean[] bits, int length) {
        /*
         * to be re-write, magic value sucks.
         */
        byte[] res = new byte[length / 8];
        int counter = 0;
        for(int i = (4 + 4 + 4); i < bits.length / 8; i++){
            for(int j = 0; j < 8; j++){
                if(bits[i * 8 + j]){
                    res[i - 12] += Math.pow(2, j);
                }
                counter++;
            }
            if(counter >= length){
                break;
            }
        }
        return res;
    }

    protected Boolean[] getBitsFromPictures(String root, String prefix) {
        Boolean[] res = null;
        File tempFile;
        for(int i = 0; ; i++){
            BufferedImage image;
            try {
                tempFile = new File(root + File.separator + prefix + (i + 1) + ".jpg");
                image = ImageIO.read(tempFile);
            } catch (IOException e) {
                System.out.println(i + " valid pictures found.");
                return res;
            }
            if(tempFile.exists()){
                if(res == null){
                    res = new Boolean[Parameter.VIDEOHEIGHT.getValue() * Parameter.VIDEOHEIGHT.getValue()];;
                }
                else{
                    Boolean[] temp = res;
                    res = new Boolean[temp.length + Parameter.VIDEOHEIGHT.getValue() * Parameter.VIDEOHEIGHT.getValue()];
                    System.arraycopy(temp, 0, res, 0, temp.length);
                }
                for (int y = 0; y < Parameter.VIDEOHEIGHT.getValue(); y++){
                    for (int x = 0; x < Parameter.VIDEOWIDTH.getValue(); x++){
                        res[i * (Parameter.VIDEOHEIGHT.getValue() * Parameter.VIDEOHEIGHT.getValue())
                                + y * Parameter.VIDEOHEIGHT.getValue()
                                + x] = (pictureUtil.calGray(image.getRGB(x, y)) > 128);
                    }
                }
                continue;
            }
            break;
        }
        return res;
    }

    private Boolean[] extractLength(BufferedImage image) {
        /**
         * 32 the bit length of length(int)
         */
        Boolean[] length = new Boolean[32];
        int counter = 0;
        for (int y = 0; y < Parameter.VIDEOHEIGHT.getValue(); y++){
            for (int x = 0; x < Parameter.VIDEOWIDTH.getValue(); x++){
                if(counter >= 64 && counter < 96){
                    /*
                     * get gray value to judge bits
                     */
                    int gray = pictureUtil.calGray(image.getRGB(x, y));
                    length[counter - 64] = (gray > 128);
                }
                counter++;
            }
            if(counter > 96){
                break;
            }
        }
        return length;
    }

    public Boolean[] extractFPS(BufferedImage image){
        /**
         * 32 the bit length of fps(int)
         */
        Boolean[] bitsOfFPS = new Boolean[32];
        int counter = 0;
        for (int y = 0; y < Parameter.VIDEOHEIGHT.getValue(); y++){
            for (int x = 0; x < Parameter.VIDEOWIDTH.getValue(); x++){
                if(counter >= 32 && counter < 64){
                    /*
                     * get gray value to judge bits
                     */
                    int gray = pictureUtil.calGray(image.getRGB(x, y));
                    bitsOfFPS[counter - 32] = (gray > 128);
                }
                counter++;
            }
            if(counter > 64){
                break;
            }
        }
        return bitsOfFPS;
    }

    public byte[] decode4test(String path) throws Exception {
        return null;
    }
}
