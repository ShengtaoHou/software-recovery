// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.scheduler;

import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;

public class JobSupport
{
    public static String getDateTime(final long value) {
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Date date = new Date(value);
        return dateFormat.format(date);
    }
    
    public static long getDataTime(final String value) throws Exception {
        final DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Date date = dfm.parse(value);
        return date.getTime();
    }
}
