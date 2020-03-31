package com.saviour.mailman.web;

import com.saviour.mailman.tool.FFmpegUtil;
import com.saviour.mailman.tool.PictureUtil;
import com.saviour.mailman.video.SimpleVideoLayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Map;

@RestController
public class VideoLayerController {

    @Autowired
    private SimpleVideoLayer videoLayer;

    /**
     *
     * @param fps frames per second.
     * @param numOfPictures number of total frames.
     * @param workplace images' parent folder and the generated video's parent folder.
     * @param imagePrefix images index start with '1'.
     * @param deleteImages whether delete the images or not.
     * @return status
     */
    @RequestMapping("/video/compose")
    public String compose(int fps, int numOfPictures,
                                String workplace, String imagePrefix,
                                Boolean deleteImages) {

        //String result = videoLayer.compose(fps, numOfPictures, workplace, imagePrefix, deleteImages);

        return null;
    }

    /**
     *
     * @param workplace the generated images' parent folder.
     * @param imagePrefix the prefix of images to be generated.
     * @param videoName name of video, which to be generated.
     * @return fps, frames, first frame.
     */
    @RequestMapping("/video/decompose")
    public String decompose(String workplace, String imagePrefix, String videoName) throws IOException {

        Map<String, Integer> result = videoLayer.decompose(workplace, imagePrefix, videoName);

        return "First Frame: " + result.get("firstFrame") + "; Frames: " + result.get("frames") +
                "; FPS: " + result.get("fps");
    }
}
