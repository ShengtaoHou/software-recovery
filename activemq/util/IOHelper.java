// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Stack;
import java.io.File;

public final class IOHelper
{
    protected static final int MAX_DIR_NAME_LENGTH;
    protected static final int MAX_FILE_NAME_LENGTH;
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    
    private IOHelper() {
    }
    
    public static String getDefaultDataDirectory() {
        return getDefaultDirectoryPrefix() + "activemq-data";
    }
    
    public static String getDefaultStoreDirectory() {
        return getDefaultDirectoryPrefix() + "amqstore";
    }
    
    public static String getDefaultDirectoryPrefix() {
        try {
            return System.getProperty("org.apache.activemq.default.directory.prefix", "");
        }
        catch (Exception e) {
            return "";
        }
    }
    
    public static String toFileSystemDirectorySafeName(final String name) {
        return toFileSystemSafeName(name, true, IOHelper.MAX_DIR_NAME_LENGTH);
    }
    
    public static String toFileSystemSafeName(final String name) {
        return toFileSystemSafeName(name, false, IOHelper.MAX_FILE_NAME_LENGTH);
    }
    
    public static String toFileSystemSafeName(final String name, final boolean dirSeparators, final int maxFileLength) {
        final int size = name.length();
        final StringBuffer rc = new StringBuffer(size * 2);
        for (int i = 0; i < size; ++i) {
            final char c = name.charAt(i);
            boolean valid = c >= 'a' && c <= 'z';
            valid = (valid || (c >= 'A' && c <= 'Z'));
            valid = (valid || (c >= '0' && c <= '9'));
            valid = (valid || c == '_' || c == '-' || c == '.' || c == '#' || (dirSeparators && (c == '/' || c == '\\')));
            if (valid) {
                rc.append(c);
            }
            else {
                rc.append('#');
                rc.append(HexSupport.toHexFromInt(c, true));
            }
        }
        String result = rc.toString();
        if (result.length() > maxFileLength) {
            result = result.substring(result.length() - maxFileLength, result.length());
        }
        return result;
    }
    
    public static boolean delete(final File top) {
        boolean result = true;
        final Stack<File> files = new Stack<File>();
        files.push(top);
        while (!files.isEmpty()) {
            final File file = files.pop();
            if (file.isDirectory()) {
                final File[] list = file.listFiles();
                if (list == null || list.length == 0) {
                    result &= file.delete();
                }
                else {
                    files.push(file);
                    for (final File dirFile : list) {
                        if (dirFile.isDirectory()) {
                            files.push(dirFile);
                        }
                        else {
                            result &= dirFile.delete();
                        }
                    }
                }
            }
            else {
                result &= file.delete();
            }
        }
        return result;
    }
    
    public static boolean deleteFile(final File fileToDelete) {
        if (fileToDelete == null || !fileToDelete.exists()) {
            return true;
        }
        boolean result = deleteChildren(fileToDelete);
        result &= fileToDelete.delete();
        return result;
    }
    
    public static boolean deleteChildren(final File parent) {
        if (parent == null || !parent.exists()) {
            return false;
        }
        boolean result = true;
        if (parent.isDirectory()) {
            final File[] files = parent.listFiles();
            if (files == null) {
                result = false;
            }
            else {
                for (int i = 0; i < files.length; ++i) {
                    final File file = files[i];
                    if (!file.getName().equals(".")) {
                        if (!file.getName().equals("..")) {
                            if (file.isDirectory()) {
                                result &= deleteFile(file);
                            }
                            else {
                                result &= file.delete();
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
    
    public static void moveFile(final File src, final File targetDirectory) throws IOException {
        if (!src.renameTo(new File(targetDirectory, src.getName()))) {
            throw new IOException("Failed to move " + src + " to " + targetDirectory);
        }
    }
    
    public static void copyFile(final File src, final File dest) throws IOException {
        copyFile(src, dest, null);
    }
    
    public static void copyFile(final File src, final File dest, final FilenameFilter filter) throws IOException {
        if (!src.getCanonicalPath().equals(dest.getCanonicalPath())) {
            if (src.isDirectory()) {
                mkdirs(dest);
                final List<File> list = getFiles(src, filter);
                for (final File f : list) {
                    if (f.isFile()) {
                        final File target = new File(getCopyParent(src, dest, f), f.getName());
                        copySingleFile(f, target);
                    }
                }
            }
            else if (dest.isDirectory()) {
                mkdirs(dest);
                final File target2 = new File(dest, src.getName());
                copySingleFile(src, target2);
            }
            else {
                copySingleFile(src, dest);
            }
        }
    }
    
    static File getCopyParent(final File from, final File to, final File src) {
        File result = null;
        final File parent = src.getParentFile();
        final String fromPath = from.getAbsolutePath();
        if (parent.getAbsolutePath().equals(fromPath)) {
            result = to;
        }
        else {
            final String parentPath = parent.getAbsolutePath();
            final String path = parentPath.substring(fromPath.length());
            result = new File(to.getAbsolutePath() + File.separator + path);
        }
        return result;
    }
    
    static List<File> getFiles(final File dir, final FilenameFilter filter) {
        final List<File> result = new ArrayList<File>();
        getFiles(dir, result, filter);
        return result;
    }
    
    static void getFiles(final File dir, final List<File> list, final FilenameFilter filter) {
        if (!list.contains(dir)) {
            list.add(dir);
            final String[] fileNames = dir.list(filter);
            for (int i = 0; i < fileNames.length; ++i) {
                final File f = new File(dir, fileNames[i]);
                if (f.isFile()) {
                    list.add(f);
                }
                else {
                    getFiles(dir, list, filter);
                }
            }
        }
    }
    
    public static void copySingleFile(final File src, final File dest) throws IOException {
        final FileInputStream fileSrc = new FileInputStream(src);
        final FileOutputStream fileDest = new FileOutputStream(dest);
        copyInputStream(fileSrc, fileDest);
    }
    
    public static void copyInputStream(final InputStream in, final OutputStream out) throws IOException {
        final byte[] buffer = new byte[4096];
        for (int len = in.read(buffer); len >= 0; len = in.read(buffer)) {
            out.write(buffer, 0, len);
        }
        in.close();
        out.close();
    }
    
    public static int getMaxDirNameLength() {
        return IOHelper.MAX_DIR_NAME_LENGTH;
    }
    
    public static int getMaxFileNameLength() {
        return IOHelper.MAX_FILE_NAME_LENGTH;
    }
    
    public static void mkdirs(final File dir) throws IOException {
        if (dir.exists()) {
            if (!dir.isDirectory()) {
                throw new IOException("Failed to create directory '" + dir + "', regular file already existed with that name");
            }
        }
        else if (!dir.mkdirs()) {
            throw new IOException("Failed to create directory '" + dir + "'");
        }
    }
    
    static {
        MAX_DIR_NAME_LENGTH = Integer.getInteger("MaximumDirNameLength", 200);
        MAX_FILE_NAME_LENGTH = Integer.getInteger("MaximumFileNameLength", 64);
    }
}
