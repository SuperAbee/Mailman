package com.saviour.mailman.tool;

import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FFmpegUtil {
    public void generateVideo(String path, int fps) {
        int index = path.lastIndexOf("/");
        String root = path.substring(0, index);

        List<String> commend = new ArrayList<String>();
        commend.add("ffmpeg");
        commend.add("-r");
        commend.add(String.valueOf(fps));
        commend.add("-i");
        commend.add(root + "/tmp%d.jpg");
        commend.add(path);

        ProcessBuilder builder = new ProcessBuilder();
        builder.command(commend);
        try {
            builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generatePictures(String path, String prefix) {
        int index = path.lastIndexOf("/");
        String root = path.substring(0, index);

        List<String> commend = new ArrayList<String>();
        commend.add("ffmpeg");
        commend.add("-i");
        commend.add(path);
        commend.add(root + "/" + prefix + "%d.jpg");

        ProcessBuilder builder = new ProcessBuilder();
        builder.command(commend);
        try {
            builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
