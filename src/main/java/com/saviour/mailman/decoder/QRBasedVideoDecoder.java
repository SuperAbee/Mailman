package com.saviour.mailman.decoder;

import com.saviour.mailman.tool.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;


/**
 * @author xincong yao
 */
@Component("qrBasedVideoDecoder")
public class QRBasedVideoDecoder extends SimpleVideoDecoder {
    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private PictureUtil pictureUtil;

    @Autowired
    private FFmpegUtil ffmpegUtil;

    @Autowired
    private QRCodeUtil qrCodeUtil;

    /**
     * the same with QRBasedVideoEncoder
     */
    private static final int FPS = 10;

    @Override
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
        Thread.sleep(2000);

        /*
         * step3: picture -> byte,
         */
        int duration = 30 / FPS;
        int startFrame = locateStartFrame(root, PICTUREPREFIX);
        int num = extractPicNum(startFrame, PICTUREPREFIX);
        byte[] message = qrCodeUtil.getBytesFromPictures(root, PICTUREPREFIX, startFrame, duration, num);

        /*
         * step4: delete temperate files and pictures
         */
        pictureUtil.deletePictures(root, PICTUREPREFIX);
        fileUtil.delete(tmpVideoPath);
        return message;
    }

    private int locateStartFrame(String root, String prefix) throws Exception {
        File tempFile;
        for(int i = 0; ; i++){
            tempFile = new File(root + File.separator + prefix + (i + 1) + ".jpg");
            String message = null;
            if(tempFile.exists()){
                try{
                    message = QRCodeUtil.decode(tempFile);
                } catch (Exception e){
                }
                if(message != null && message.substring(0, 3).equals("111")){
                    System.out.println("StartFrame " + (i + 1) + " with message: " + message);
                    return i + 1;
                }
                continue;
            }
            break;
        }
        return -1;
    }

    private int extractPicNum(int startFrame, String prefix) throws Exception {
        File tempFile = new File(root + File.separator + prefix + startFrame + ".jpg");
        String message = QRCodeUtil.decode(tempFile);
        int num = Integer.parseInt(message.substring(3, 5));
        System.out.println(num + " effective pictures expected.");
        return num;
    }
}
