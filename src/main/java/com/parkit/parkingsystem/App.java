package com.parkit.parkingsystem;

import com.parkit.parkingsystem.service.InteractiveShell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App {
    private static final Logger LOG = LogManager.getLogger("App");
    public static void main(final String[] args) {
        LOG.info("Initializing Parking System");
        InteractiveShell.loadInterface();
    }
}
