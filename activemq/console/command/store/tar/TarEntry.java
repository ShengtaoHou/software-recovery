// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.command.store.tar;

import java.util.Locale;
import java.util.Date;
import java.io.File;

public class TarEntry implements TarConstants
{
    private StringBuffer name;
    private int mode;
    private int userId;
    private int groupId;
    private long size;
    private long modTime;
    private byte linkFlag;
    private StringBuffer linkName;
    private StringBuffer magic;
    private StringBuffer userName;
    private StringBuffer groupName;
    private int devMajor;
    private int devMinor;
    private File file;
    public static final int MAX_NAMELEN = 31;
    public static final int DEFAULT_DIR_MODE = 16877;
    public static final int DEFAULT_FILE_MODE = 33188;
    public static final int MILLIS_PER_SECOND = 1000;
    
    private TarEntry() {
        this.magic = new StringBuffer("ustar");
        this.name = new StringBuffer();
        this.linkName = new StringBuffer();
        String user = System.getProperty("user.name", "");
        if (user.length() > 31) {
            user = user.substring(0, 31);
        }
        this.userId = 0;
        this.groupId = 0;
        this.userName = new StringBuffer(user);
        this.groupName = new StringBuffer("");
        this.file = null;
    }
    
    public TarEntry(final String name) {
        this(name, false);
    }
    
    public TarEntry(String name, final boolean preserveLeadingSlashes) {
        this();
        name = normalizeFileName(name, preserveLeadingSlashes);
        final boolean isDir = name.endsWith("/");
        this.devMajor = 0;
        this.devMinor = 0;
        this.name = new StringBuffer(name);
        this.mode = (isDir ? 16877 : 33188);
        this.linkFlag = (byte)(isDir ? 53 : 48);
        this.userId = 0;
        this.groupId = 0;
        this.size = 0L;
        this.modTime = new Date().getTime() / 1000L;
        this.linkName = new StringBuffer("");
        this.userName = new StringBuffer("");
        this.groupName = new StringBuffer("");
        this.devMajor = 0;
        this.devMinor = 0;
    }
    
    public TarEntry(final String name, final byte linkFlag) {
        this(name);
        this.linkFlag = linkFlag;
        if (linkFlag == 76) {
            this.magic = new StringBuffer("ustar  ");
        }
    }
    
    public TarEntry(final File file) {
        this();
        this.file = file;
        final String fileName = normalizeFileName(file.getPath(), false);
        this.linkName = new StringBuffer("");
        this.name = new StringBuffer(fileName);
        if (file.isDirectory()) {
            this.mode = 16877;
            this.linkFlag = 53;
            final int nameLength = this.name.length();
            if (nameLength == 0 || this.name.charAt(nameLength - 1) != '/') {
                this.name.append("/");
            }
            this.size = 0L;
        }
        else {
            this.mode = 33188;
            this.linkFlag = 48;
            this.size = file.length();
        }
        this.modTime = file.lastModified() / 1000L;
        this.devMajor = 0;
        this.devMinor = 0;
    }
    
    public TarEntry(final byte[] headerBuf) {
        this();
        this.parseTarHeader(headerBuf);
    }
    
    public boolean equals(final TarEntry it) {
        return this.getName().equals(it.getName());
    }
    
    @Override
    public boolean equals(final Object it) {
        return it != null && this.getClass() == it.getClass() && this.equals((TarEntry)it);
    }
    
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
    
    public boolean isDescendent(final TarEntry desc) {
        return desc.getName().startsWith(this.getName());
    }
    
    public String getName() {
        return this.name.toString();
    }
    
    public void setName(final String name) {
        this.name = new StringBuffer(normalizeFileName(name, false));
    }
    
    public void setMode(final int mode) {
        this.mode = mode;
    }
    
    public String getLinkName() {
        return this.linkName.toString();
    }
    
    public int getUserId() {
        return this.userId;
    }
    
    public void setUserId(final int userId) {
        this.userId = userId;
    }
    
    public int getGroupId() {
        return this.groupId;
    }
    
    public void setGroupId(final int groupId) {
        this.groupId = groupId;
    }
    
    public String getUserName() {
        return this.userName.toString();
    }
    
    public void setUserName(final String userName) {
        this.userName = new StringBuffer(userName);
    }
    
    public String getGroupName() {
        return this.groupName.toString();
    }
    
    public void setGroupName(final String groupName) {
        this.groupName = new StringBuffer(groupName);
    }
    
    public void setIds(final int userId, final int groupId) {
        this.setUserId(userId);
        this.setGroupId(groupId);
    }
    
    public void setNames(final String userName, final String groupName) {
        this.setUserName(userName);
        this.setGroupName(groupName);
    }
    
    public void setModTime(final long time) {
        this.modTime = time / 1000L;
    }
    
    public void setModTime(final Date time) {
        this.modTime = time.getTime() / 1000L;
    }
    
    public Date getModTime() {
        return new Date(this.modTime * 1000L);
    }
    
    public File getFile() {
        return this.file;
    }
    
    public int getMode() {
        return this.mode;
    }
    
    public long getSize() {
        return this.size;
    }
    
    public void setSize(final long size) {
        this.size = size;
    }
    
    public boolean isGNULongNameEntry() {
        return this.linkFlag == 76 && this.name.toString().equals("././@LongLink");
    }
    
    public boolean isDirectory() {
        if (this.file != null) {
            return this.file.isDirectory();
        }
        return this.linkFlag == 53 || this.getName().endsWith("/");
    }
    
    public TarEntry[] getDirectoryEntries() {
        if (this.file == null || !this.file.isDirectory()) {
            return new TarEntry[0];
        }
        final String[] list = this.file.list();
        final TarEntry[] result = new TarEntry[list.length];
        for (int i = 0; i < list.length; ++i) {
            result[i] = new TarEntry(new File(this.file, list[i]));
        }
        return result;
    }
    
    public void writeEntryHeader(final byte[] outbuf) {
        int offset = 0;
        offset = TarUtils.getNameBytes(this.name, outbuf, offset, 100);
        offset = TarUtils.getOctalBytes(this.mode, outbuf, offset, 8);
        offset = TarUtils.getOctalBytes(this.userId, outbuf, offset, 8);
        offset = TarUtils.getOctalBytes(this.groupId, outbuf, offset, 8);
        offset = TarUtils.getLongOctalBytes(this.size, outbuf, offset, 12);
        final int csOffset;
        offset = (csOffset = TarUtils.getLongOctalBytes(this.modTime, outbuf, offset, 12));
        for (int c = 0; c < 8; ++c) {
            outbuf[offset++] = 32;
        }
        outbuf[offset++] = this.linkFlag;
        for (offset = TarUtils.getNameBytes(this.linkName, outbuf, offset, 100), offset = TarUtils.getNameBytes(this.magic, outbuf, offset, 8), offset = TarUtils.getNameBytes(this.userName, outbuf, offset, 32), offset = TarUtils.getNameBytes(this.groupName, outbuf, offset, 32), offset = TarUtils.getOctalBytes(this.devMajor, outbuf, offset, 8), offset = TarUtils.getOctalBytes(this.devMinor, outbuf, offset, 8); offset < outbuf.length; outbuf[offset++] = 0) {}
        final long chk = TarUtils.computeCheckSum(outbuf);
        TarUtils.getCheckSumOctalBytes(chk, outbuf, csOffset, 8);
    }
    
    public void parseTarHeader(final byte[] header) {
        int offset = 0;
        this.name = TarUtils.parseName(header, offset, 100);
        offset += 100;
        this.mode = (int)TarUtils.parseOctal(header, offset, 8);
        offset += 8;
        this.userId = (int)TarUtils.parseOctal(header, offset, 8);
        offset += 8;
        this.groupId = (int)TarUtils.parseOctal(header, offset, 8);
        offset += 8;
        this.size = TarUtils.parseOctal(header, offset, 12);
        offset += 12;
        this.modTime = TarUtils.parseOctal(header, offset, 12);
        offset += 12;
        offset += 8;
        this.linkFlag = header[offset++];
        this.linkName = TarUtils.parseName(header, offset, 100);
        offset += 100;
        this.magic = TarUtils.parseName(header, offset, 8);
        offset += 8;
        this.userName = TarUtils.parseName(header, offset, 32);
        offset += 32;
        this.groupName = TarUtils.parseName(header, offset, 32);
        offset += 32;
        this.devMajor = (int)TarUtils.parseOctal(header, offset, 8);
        offset += 8;
        this.devMinor = (int)TarUtils.parseOctal(header, offset, 8);
    }
    
    private static String normalizeFileName(String fileName, final boolean preserveLeadingSlashes) {
        final String osname = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        if (osname != null) {
            if (osname.startsWith("windows")) {
                if (fileName.length() > 2) {
                    final char ch1 = fileName.charAt(0);
                    final char ch2 = fileName.charAt(1);
                    if (ch2 == ':' && ((ch1 >= 'a' && ch1 <= 'z') || (ch1 >= 'A' && ch1 <= 'Z'))) {
                        fileName = fileName.substring(2);
                    }
                }
            }
            else if (osname.indexOf("netware") > -1) {
                final int colon = fileName.indexOf(58);
                if (colon != -1) {
                    fileName = fileName.substring(colon + 1);
                }
            }
        }
        for (fileName = fileName.replace(File.separatorChar, '/'); !preserveLeadingSlashes && fileName.startsWith("/"); fileName = fileName.substring(1)) {}
        return fileName;
    }
}
