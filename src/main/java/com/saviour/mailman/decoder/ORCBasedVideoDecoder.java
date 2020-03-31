package com.saviour.mailman.decoder;

import com.saviour.mailman.tool.FFmpegUtil;
import com.saviour.mailman.tool.FileUtil;
import com.saviour.mailman.tool.PictureUtil;
import com.saviour.mailman.tool.QRCodeUtil;
import com.saviour.mailman.video.SimpleVideoLayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("orcBasedVideoDecoder")
public class ORCBasedVideoDecoder extends SimpleVideoDecoder {
    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private PictureUtil pictureUtil;

    @Autowired
    private SimpleVideoLayer videoLayer;

    @Autowired
    private QRCodeUtil qrCodeUtil;

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
        Map<String, Integer> result = videoLayer.decompose(root, PICTUREPREFIX, VIDEONAME);
        int fps = result.get("fps");
        int startFrame = result.get("firstFrame");
        int num = result.get("frames");

        /*
         * step3: picture -> byte,
         */
        int duration = 30 / fps;
        byte[] message = qrCodeUtil.getBytesFromPictures(root, PICTUREPREFIX, startFrame, duration, num);

        /*
         * step4: delete temperate files and pictures
         */
        pictureUtil.deletePictures(root, PICTUREPREFIX);
        fileUtil.delete(tmpVideoPath);
        return message;
    }

    @Override
    public byte[] decode4test(String video) throws Exception {

        int flag = video.lastIndexOf("/");
        String videoRoot = video.substring(0, flag);
        String videoName = video.substring(flag + 1);
        String picPrefix = video.split("/")[video.split("/").length - 1] + PICTUREPREFIX;

        /*
         * step2: video -> picture
         */
        Map<String, Integer> result = videoLayer.decompose(videoRoot, picPrefix, videoName);
        int fps = result.get("fps");
        int startFrame = result.get("firstFrame");
        int num = result.get("frames");

        /*
         * step3: picture -> byte,
         */
        int duration = 30 / fps;
        byte[] message = qrCodeUtil.getBytesFromPictures(videoRoot, picPrefix, startFrame, duration, num);

        /*
         * step4: delete temperate files and pictures
         */
        pictureUtil.deletePictures(videoRoot, picPrefix);
        return message;
    }
}
