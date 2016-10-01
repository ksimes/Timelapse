package com.stronans.camera;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteStreams;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Composites an image drawn from an external program which dumps the image to the output stream
 * of the running process. Converts that image into a Base64 string.
 * Created by S.King on 11/01/2015.
 */
public class Camera {
    private static final Logger log = Logger.getLogger(com.stronans.camera.Camera.class);

    public static final String SPACE_CHAR = " ";
    public static final String STILL_DEFAULTS = " -t 10";  // Take image after 10 milliseconds.
    public static final String VIDEO_DEFAULTS = "";        // No defaults
    /**
     * The <code>Logger</code> to be used.
     */
    private static final String EXTERNAL_STILL_TOOL = "raspistill";
    private static final String FINAL_STILL_SETTINGS = "-n -o -"; // No preview to screen and output to console (captured by process).
    private static final String FINAL_STILL_SETTINGS_SAVED = "-n -o "; // No preview to screen and output to file.

    private static final String EXTERNAL_VIDEO_TOOL = "raspivid";
    private static final String FINAL_VIDEO_SETTINGS = "-n -o "; // No preview to screen and output to file.

    public static byte[] getImage(String settings) throws IOException {
        byte[] image = new byte[0];

        log.debug("Start getting Camera Image");
        long start = System.currentTimeMillis();

        Joiner joiner = Joiner.on(" ");
        String finalSettings = joiner.join(EXTERNAL_STILL_TOOL, settings, FINAL_STILL_SETTINGS);

        try {
            log.info("Capture image with settings [" + finalSettings + "]");
            Process p = Runtime.getRuntime().exec(finalSettings);
            image = ByteStreams.toByteArray(p.getInputStream());

        } catch (Exception e) {
            log.error("During reading input stream, Camera settings [" + finalSettings + "]", e);
        }

        log.debug("Stop getting Camera Image");
        log.debug("Gathered " + image.length + " bytes");
        log.debug("Duration in ms: " + (System.currentTimeMillis() - start));

        return image;
    }

    public static Optional<String> getEncodedStillImage(String settings) {
        String result = null;

        try {
            byte[] image = getImage(settings);
            result = BaseEncoding.base64().encode(image);
        } catch (Exception exp) {
            log.error("During encoding of image", exp);
            result = null;
        }

        return Optional.fromNullable(result);
    }

    public static boolean getNamedImage(String name, String settings) {
        boolean result = true;

        log.debug("Start getting Camera Image");
        long start = System.currentTimeMillis();

        Joiner joiner = Joiner.on(SPACE_CHAR);
        String finalSettings = joiner.join(EXTERNAL_STILL_TOOL, settings, FINAL_STILL_SETTINGS_SAVED, name);

        try {
            log.info("Capture image with settings [" + finalSettings + "]");
            Process p = Runtime.getRuntime().exec(finalSettings);
            p.waitFor();

        } catch (Exception e) {
            log.error("During reading input stream, Camera settings [" + finalSettings + "]", e);
            result = false;
        }

        log.debug("Stop getting Camera Image");
        log.debug("Duration in ms: " + (System.currentTimeMillis() - start));

        return result;
    }

    public static boolean getNamedVideo(String name, String settings) {
        boolean result = true;

        log.debug("Start getting video Image");
        Joiner joiner = Joiner.on(SPACE_CHAR);
        String finalSettings = joiner.join(EXTERNAL_VIDEO_TOOL, settings, FINAL_VIDEO_SETTINGS, name);

        try {
            log.info("Capture video image with settings [" + finalSettings + "]");

            // Note that this process will wait for the process created to complete before continuing.
            Process p = Runtime.getRuntime().exec(finalSettings);
            p.waitFor();

        } catch (Exception e) {
            log.error("During video capture, Camera settings [" + finalSettings + "]", e);
            result = false;
        }

        log.debug("Stop getting video Image");
        log.info("Completed capture");

        return result;
    }
}
