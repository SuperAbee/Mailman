package com.saviour.mailman.encoder;

import com.saviour.mailman.tool.FFmpegUtil;
import com.saviour.mailman.tool.FormatTransfer;
import com.saviour.mailman.tool.Parameter;
import com.saviour.mailman.tool.PictureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

import static java.lang.Thread.sleep;

/**
 * @author xincong yao
 * The encoder accept byte to be encoded, video path and video length as a input.
 * Generate a video as a output and save it to the specific path.
 */
@Component
public class SimpleVideoEncoder {

    @Autowired
    private PictureUtil pictureUtil;

    @Autowired
    private FFmpegUtil ffmpegUtil;

    @Autowired
    private FormatTransfer transfer;

    /**
     * bytes to be encoded
     */
    protected byte[] srcByte;

    /**
     * path to save encoded video
     */
    protected String path;

    /**
     * root of path
     */
    protected String root;

    /**
     * unit: millisecond
     */
    protected Integer lengthOfVideo;

    /**
     * frame per second
     */
    protected Integer fps;

    /**
     * the prefix of temperate pictures
     */
    private static final String PICTUREPREFIX = "tmp";


    public void load(byte[] srcByte, Integer lengthOfVideo, String path){
        this.srcByte = srcByte;
        this.lengthOfVideo = lengthOfVideo;
        this.path = path;
        int index = path.lastIndexOf("/");
        this.root = path.substring(0, index);
        this.fps = (int) Math.ceil((8 * (4 + 4 + 4 + srcByte.length))/
                (Parameter.VIDEOHEIGHT.getValue() * Parameter.VIDEOWIDTH.getValue() * Math.ceil(lengthOfVideo/1000.0)));
    }

    public String encode() throws Exception {
        /**
         * The size of bytes bigger than 10MB, switch to fast mode
         */
        if(srcByte.length >= 1024 * 1024 * 10){
            return fastEncode();
        }

        /**
         * step1: bytes -> bits
         */
        Boolean[] bits = SVCP();

        /**
         * step2: bits -> generate pictures
         */
        int pictureNumber = calculatePictureNumber();
        pictureUtil.generatePictures(bits, pictureNumber, root, PICTUREPREFIX);

        /**
         * step3: pictures -> video
         */
        ffmpegUtil.generateVideo(path, fps);

        /**
         * step4: remove pictures
         */
        try {
            /**
             * This thread sleep 10ms per pictureNumber to delete pictures,
             * so that ffmpeg has enough time to combine videos.
             */
            sleep(10 * pictureNumber);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pictureUtil.deletePictures(root, PICTUREPREFIX);

        return "OK";
    }

    protected String fastEncode(){
        return "OK";
    }

    /**
     * implement the protocol of encoder
     */
    protected Boolean[] SVCP(){
        /**
         * Simple Video Code Protocol:
         * 4B           : start flag
         * 4B           : fps
         * 4B           : body length
         * bytes.length : body
         */

        Boolean[] bitsOfStartFlag = transfer.int2Bit(Parameter.STARTFLAG.getValue());
        Boolean[] bitsOfBody = transfer.byte2Bit(srcByte);
        Boolean[] bitsOfBodyLength = transfer.int2Bit(bitsOfBody.length);

        /**
         * ceil, otherwise the duration of video is too short
         */
        Boolean[] bitsOfFPS = transfer.int2Bit(fps);

        /**
         * contact all bits
         */
        Boolean[] result = new Boolean[bitsOfStartFlag.length
                + bitsOfFPS.length
                + bitsOfBodyLength.length
                + bitsOfBody.length];
        System.arraycopy(bitsOfStartFlag, 0, result, 0, bitsOfStartFlag.length);
        System.arraycopy(bitsOfFPS, 0, result, bitsOfFPS.length, bitsOfFPS.length);
        System.arraycopy(bitsOfBodyLength, 0, result, bitsOfStartFlag.length + bitsOfFPS.length, bitsOfBodyLength.length);
        System.arraycopy(bitsOfBody, 0, result, bitsOfStartFlag.length + bitsOfFPS.length + bitsOfBodyLength.length, bitsOfBody.length);

        return result;
    }

    protected int calculatePictureNumber(){
        return (int) (fps * Math.ceil(lengthOfVideo/1000.0));
    }
}
