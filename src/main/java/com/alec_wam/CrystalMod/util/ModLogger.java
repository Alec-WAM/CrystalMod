package com.alec_wam.CrystalMod.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alec_wam.CrystalMod.CrystalMod;



public class ModLogger {
    
    private static Logger logger = LogManager.getLogger(CrystalMod.MODID);

    public static void log(Level level, String format, Object... data)
    {
        logger.log(level, format, data);
    }

    public static void error(String format, Object... data)
    {
        log(Level.ERROR, format, data);
    }

    public static void warning(String format, Object... data)
    {
        log(Level.WARN, format, data);
    }

    public static void info(String format, Object... data)
    {
        log(Level.INFO, format, data);
    }

    public static void debug(String format, Object... data)
    {
        log(Level.DEBUG, format, data);
    }

    public static void trace(String format, Object... data)
    {
        log(Level.TRACE, format, data);
    }

    public static Logger getLogger()
    {
        return logger;
    }
    
}
