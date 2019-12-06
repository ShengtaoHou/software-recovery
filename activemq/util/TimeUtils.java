// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class TimeUtils
{
    private TimeUtils() {
    }
    
    public static String printDuration(double uptime) {
        final NumberFormat fmtI = new DecimalFormat("###,###", new DecimalFormatSymbols(Locale.ENGLISH));
        final NumberFormat fmtD = new DecimalFormat("###,##0.000", new DecimalFormatSymbols(Locale.ENGLISH));
        uptime /= 1000.0;
        if (uptime < 60.0) {
            return fmtD.format(uptime) + " seconds";
        }
        uptime /= 60.0;
        if (uptime < 60.0) {
            final long minutes = (long)uptime;
            final String s = fmtI.format(minutes) + ((minutes > 1L) ? " minutes" : " minute");
            return s;
        }
        uptime /= 60.0;
        if (uptime < 24.0) {
            final long hours = (long)uptime;
            final long minutes2 = (long)((uptime - hours) * 60.0);
            String s2 = fmtI.format(hours) + ((hours > 1L) ? " hours" : " hour");
            if (minutes2 != 0L) {
                s2 = s2 + " " + fmtI.format(minutes2) + ((minutes2 > 1L) ? " minutes" : " minute");
            }
            return s2;
        }
        uptime /= 24.0;
        final long days = (long)uptime;
        final long hours2 = (long)((uptime - days) * 24.0);
        String s2 = fmtI.format(days) + ((days > 1L) ? " days" : " day");
        if (hours2 != 0L) {
            s2 = s2 + " " + fmtI.format(hours2) + ((hours2 > 1L) ? " hours" : " hour");
        }
        return s2;
    }
}
