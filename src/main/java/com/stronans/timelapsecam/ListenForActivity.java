package com.stronans.timelapsecam;

/*
 * **********************************************************************
 * ORGANIZATION  :  Cathcart Software
 * PROJECT       :  PIR Catcam client
 * FILENAME      :  ListenForActivity.java
 *
 * This file is part of the CatCam project. More information about
 * this project can be found here:  TBC
 * **********************************************************************
 */

import com.google.common.base.Joiner;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.wiringpi.Gpio;
import com.stronans.ProgramProperties;
import com.stronans.camera.Camera;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 *
 */
public class ListenForActivity {
    /**
     * The <code>Logger</code> to be used.
     */
    private static final Logger log = Logger.getLogger(ListenForActivity.class);
    private static final ProgramProperties properties = ProgramProperties.getInstance("timelapse.properties");
    private static final String PROGRAM_ROOT = "com.stronans.timelapse.";

    private static final String CAMERS_SETTINGS = PROGRAM_ROOT + "camera.settings";
    private static final String NAME_STUB = PROGRAM_ROOT + "filename.stub";
    private static final String STORE_PATH = PROGRAM_ROOT + "store.path";
    private static final String FILE_EXTENSION = PROGRAM_ROOT + "file.extension";

    /**
     * Handles the loading of the log4j configuration. properties file must be
     * on the classpath.
     *
     * @throws RuntimeException
     */
    private static void initLogging() throws RuntimeException {
        try {
            Properties properties = new Properties();
            properties.load(ListenForActivity.class.getClassLoader().getResourceAsStream("log4j.properties"));
            PropertyConfigurator.configure(properties);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to load logging properties for System");
        }
    }

    private static String getName(String root, String extension) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date now = new Date();

        return root + sdfDate.format(now) + extension;
    }

    public static void main(String args[]) throws InterruptedException {
        String fileName;
        String fileExtension;
        String cameraProperties;

        try {
            initLogging();
        } catch (RuntimeException ex) {
            System.out.println("Error setting up log4j logging");
            System.out.println("Application will continue but without any logging.");
        }

        log.info("Timelapse camera started");

            Joiner joiner = Joiner.on(" ");
            cameraProperties = joiner.join(properties.getString(CAMERS_SETTINGS), Camera.STILL_DEFAULTS);

            joiner = Joiner.on("/");
            fileName = joiner.join(properties.getString(STORE_PATH), properties.getString(NAME_STUB));

            fileExtension = properties.getString(FILE_EXTENSION);

            log.info("Ready for capture");

            // Capture
            Camera.getNamedImage(getName(fileName, fileExtension), cameraProperties);

        }
}
