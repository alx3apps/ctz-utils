package ru.concerteza.util.io;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.io.FileUtils.ONE_MB;

/**
 * User: alexey
 * Date: 4/18/11
 */

// specialized methods here, for simple methods use FileUtils
public class CtzCopyCheckLMUtils {

    private static final long FIFTY_MB = ONE_MB * 50;

    // only adjusted method, others copied from FileUtils
    /**
     * Internal copy file method.
     *
     * @param srcFile          the validated source file, must not be <code>null</code>
     * @param destFile         the validated destination file, must not be <code>null</code>
     * @param preserveFileDate whether to preserve the file date
     * @throws IOException if an error occurs
     */
    private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
        if (destFile.exists()) {
            if(destFile.isDirectory()) throw new IOException("Destination '" + destFile + "' exists but is a directory");
            // check LastModified
            if ((destFile.length() == srcFile.length()) && (destFile.lastModified() == srcFile.lastModified())) {
                return;
            }
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel input = null;
        FileChannel output = null;
        try {
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);
            input = fis.getChannel();
            output = fos.getChannel();
            long size = input.size();
            long pos = 0;
            long count = 0;
            while (pos < size) {
                count = (size - pos) > FIFTY_MB ? FIFTY_MB : (size - pos);
                pos += output.transferFrom(input, pos, count);
            }
        } finally {
            IOUtils.closeQuietly(output);
            IOUtils.closeQuietly(fos);
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(fis);
        }

        if (srcFile.length() != destFile.length()) {
            throw new IOException("Failed to copy full contents from '" +
                    srcFile + "' to '" + destFile + "'");
        }
        if (preserveFileDate) {
            destFile.setLastModified(srcFile.lastModified());
        }
    }


    //-----------------------------------------------------------------------

    /**
     * Copies a file to a directory preserving the file date.
     * <p/>
     * This method copies the contents of the specified source file
     * to a file of the same name in the specified destination directory.
     * The destination directory is created if it does not exist.
     * If the destination file exists, then this method will overwrite it.
     * <p/>
     * <strong>Note:</strong> This method tries to preserve the file's last
     * modified date/times using {@link File#setLastModified(long)}, however
     * it is not guaranteed that the operation will succeed.
     * If the modification operation fails, no indication is provided.
     *
     * @param srcFile an existing file to copy, must not be <code>null</code>
     * @param destDir the directory to place the copy in, must not be <code>null</code>
     * @throws NullPointerException if source or destination is null
     * @throws IOException          if source or destination is invalid
     * @throws IOException          if an IO error occurs during copying
     * @see #copyFile(File, File, boolean)
     */
    public static void copyFileToDirectory(File srcFile, File destDir) throws IOException {
        copyFileToDirectory(srcFile, destDir, true);
    }

    /**
     * Copies a file to a directory optionally preserving the file date.
     * <p/>
     * This method copies the contents of the specified source file
     * to a file of the same name in the specified destination directory.
     * The destination directory is created if it does not exist.
     * If the destination file exists, then this method will overwrite it.
     * <p/>
     * <strong>Note:</strong> Setting <code>preserveFileDate</code> to
     * <code>true</code> tries to preserve the file's last modified
     * date/times using {@link File#setLastModified(long)}, however it is
     * not guaranteed that the operation will succeed.
     * If the modification operation fails, no indication is provided.
     *
     * @param srcFile          an existing file to copy, must not be <code>null</code>
     * @param destDir          the directory to place the copy in, must not be <code>null</code>
     * @param preserveFileDate true if the file date of the copy
     *                         should be the same as the original
     * @throws NullPointerException if source or destination is <code>null</code>
     * @throws IOException          if source or destination is invalid
     * @throws IOException          if an IO error occurs during copying
     * @see #copyFile(File, File, boolean)
     * @since Commons IO 1.3
     */
    public static void copyFileToDirectory(File srcFile, File destDir, boolean preserveFileDate) throws IOException {
        if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (destDir.exists() && destDir.isDirectory() == false) {
            throw new IllegalArgumentException("Destination '" + destDir + "' is not a directory");
        }
        File destFile = new File(destDir, srcFile.getName());
        copyFile(srcFile, destFile, preserveFileDate);
    }

    /**
     * Copies a file to a new location preserving the file date.
     * <p/>
     * This method copies the contents of the specified source file to the
     * specified destination file. The directory holding the destination file is
     * created if it does not exist. If the destination file exists, then this
     * method will overwrite it.
     * <p/>
     * <strong>Note:</strong> This method tries to preserve the file's last
     * modified date/times using {@link File#setLastModified(long)}, however
     * it is not guaranteed that the operation will succeed.
     * If the modification operation fails, no indication is provided.
     *
     * @param srcFile  an existing file to copy, must not be <code>null</code>
     * @param destFile the new file, must not be <code>null</code>
     * @throws NullPointerException if source or destination is <code>null</code>
     * @throws IOException          if source or destination is invalid
     * @throws IOException          if an IO error occurs during copying
     * @see #copyFileToDirectory(File, File)
     */
    public static void copyFile(File srcFile, File destFile) throws IOException {
        copyFile(srcFile, destFile, true);
    }

    /**
     * Copies a file to a new location.
     * <p/>
     * This method copies the contents of the specified source file
     * to the specified destination file.
     * The directory holding the destination file is created if it does not exist.
     * If the destination file exists, then this method will overwrite it.
     * <p/>
     * <strong>Note:</strong> Setting <code>preserveFileDate</code> to
     * <code>true</code> tries to preserve the file's last modified
     * date/times using {@link File#setLastModified(long)}, however it is
     * not guaranteed that the operation will succeed.
     * If the modification operation fails, no indication is provided.
     *
     * @param srcFile          an existing file to copy, must not be <code>null</code>
     * @param destFile         the new file, must not be <code>null</code>
     * @param preserveFileDate true if the file date of the copy
     *                         should be the same as the original
     * @throws NullPointerException if source or destination is <code>null</code>
     * @throws IOException          if source or destination is invalid
     * @throws IOException          if an IO error occurs during copying
     * @see #copyFileToDirectory(File, File, boolean)
     */
    public static void copyFile(File srcFile, File destFile,
                                boolean preserveFileDate) throws IOException {
        if (srcFile == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destFile == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (srcFile.exists() == false) {
            throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
        }
        if (srcFile.isDirectory()) {
            throw new IOException("Source '" + srcFile + "' exists but is a directory");
        }
        if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
            throw new IOException("Source '" + srcFile + "' and destination '" + destFile + "' are the same");
        }
        if (destFile.getParentFile() != null && destFile.getParentFile().exists() == false) {
            if (destFile.getParentFile().mkdirs() == false) {
                throw new IOException("Destination '" + destFile + "' directory cannot be created");
            }
        }
        if (destFile.exists() && destFile.canWrite() == false) {
            throw new IOException("Destination '" + destFile + "' exists but is read-only");
        }
        doCopyFile(srcFile, destFile, preserveFileDate);
    }

    //-----------------------------------------------------------------------

    /**
     * Copies a directory to within another directory preserving the file dates.
     * <p/>
     * This method copies the source directory and all its contents to a
     * directory of the same name in the specified destination directory.
     * <p/>
     * The destination directory is created if it does not exist.
     * If the destination directory did exist, then this method merges
     * the source with the destination, with the source taking precedence.
     * <p/>
     * <strong>Note:</strong> This method tries to preserve the files' last
     * modified date/times using {@link File#setLastModified(long)}, however
     * it is not guaranteed that those operations will succeed.
     * If the modification operation fails, no indication is provided.
     *
     * @param srcDir  an existing directory to copy, must not be <code>null</code>
     * @param destDir the directory to place the copy in, must not be <code>null</code>
     * @throws NullPointerException if source or destination is <code>null</code>
     * @throws IOException          if source or destination is invalid
     * @throws IOException          if an IO error occurs during copying
     * @since Commons IO 1.2
     */
    public static void copyDirectoryToDirectory(File srcDir, File destDir) throws IOException {
        if (srcDir == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (srcDir.exists() && srcDir.isDirectory() == false) {
            throw new IllegalArgumentException("Source '" + destDir + "' is not a directory");
        }
        if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (destDir.exists() && destDir.isDirectory() == false) {
            throw new IllegalArgumentException("Destination '" + destDir + "' is not a directory");
        }
        copyDirectory(srcDir, new File(destDir, srcDir.getName()), true);
    }

    /**
     * Copies a whole directory to a new location preserving the file dates.
     * <p/>
     * This method copies the specified directory and all its child
     * directories and files to the specified destination.
     * The destination is the new location and name of the directory.
     * <p/>
     * The destination directory is created if it does not exist.
     * If the destination directory did exist, then this method merges
     * the source with the destination, with the source taking precedence.
     * <p/>
     * <strong>Note:</strong> This method tries to preserve the files' last
     * modified date/times using {@link File#setLastModified(long)}, however
     * it is not guaranteed that those operations will succeed.
     * If the modification operation fails, no indication is provided.
     *
     * @param srcDir  an existing directory to copy, must not be <code>null</code>
     * @param destDir the new directory, must not be <code>null</code>
     * @throws NullPointerException if source or destination is <code>null</code>
     * @throws IOException          if source or destination is invalid
     * @throws IOException          if an IO error occurs during copying
     * @since Commons IO 1.1
     */
    public static void copyDirectory(File srcDir, File destDir) throws IOException {
        copyDirectory(srcDir, destDir, true);
    }

    /**
     * Copies a whole directory to a new location.
     * <p/>
     * This method copies the contents of the specified source directory
     * to within the specified destination directory.
     * <p/>
     * The destination directory is created if it does not exist.
     * If the destination directory did exist, then this method merges
     * the source with the destination, with the source taking precedence.
     * <p/>
     * <strong>Note:</strong> Setting <code>preserveFileDate</code> to
     * <code>true</code> tries to preserve the files' last modified
     * date/times using {@link File#setLastModified(long)}, however it is
     * not guaranteed that those operations will succeed.
     * If the modification operation fails, no indication is provided.
     *
     * @param srcDir           an existing directory to copy, must not be <code>null</code>
     * @param destDir          the new directory, must not be <code>null</code>
     * @param preserveFileDate true if the file date of the copy
     *                         should be the same as the original
     * @throws NullPointerException if source or destination is <code>null</code>
     * @throws IOException          if source or destination is invalid
     * @throws IOException          if an IO error occurs during copying
     * @since Commons IO 1.1
     */
    public static void copyDirectory(File srcDir, File destDir,
                                     boolean preserveFileDate) throws IOException {
        copyDirectory(srcDir, destDir, null, preserveFileDate);
    }

    /**
     * Copies a filtered directory to a new location preserving the file dates.
     * <p/>
     * This method copies the contents of the specified source directory
     * to within the specified destination directory.
     * <p/>
     * The destination directory is created if it does not exist.
     * If the destination directory did exist, then this method merges
     * the source with the destination, with the source taking precedence.
     * <p/>
     * <strong>Note:</strong> This method tries to preserve the files' last
     * modified date/times using {@link File#setLastModified(long)}, however
     * it is not guaranteed that those operations will succeed.
     * If the modification operation fails, no indication is provided.
     * <p/>
     * <h4>Example: Copy directories only</h4>
     * <pre>
     *  // only copy the directory structure
     *  FileUtils.copyDirectory(srcDir, destDir, DirectoryFileFilter.DIRECTORY);
     *  </pre>
     *
     * <h4>Example: Copy directories and txt files</h4>
     * <pre>
     *  // Create a filter for ".txt" files
     *  IOFileFilter txtSuffixFilter = FileFilterUtils.suffixFileFilter(".txt");
     *  IOFileFilter txtFiles = FileFilterUtils.andFileFilter(FileFileFilter.FILE, txtSuffixFilter);
     *
     *  // Create a filter for either directories or ".txt" files
     *  FileFilter filter = FileFilterUtils.orFileFilter(DirectoryFileFilter.DIRECTORY, txtFiles);
     *
     *  // Copy using the filter
     *  FileUtils.copyDirectory(srcDir, destDir, filter);
     *  </pre>
     *
     * @param srcDir  an existing directory to copy, must not be <code>null</code>
     * @param destDir the new directory, must not be <code>null</code>
     * @param filter  the filter to apply, null means copy all directories and files
     *                should be the same as the original
     * @throws NullPointerException if source or destination is <code>null</code>
     * @throws IOException          if source or destination is invalid
     * @throws IOException          if an IO error occurs during copying
     * @since Commons IO 1.4
     */
    public static void copyDirectory(File srcDir, File destDir,
                                     FileFilter filter) throws IOException {
        copyDirectory(srcDir, destDir, filter, true);
    }

    /**
     * Copies a filtered directory to a new location.
     * <p/>
     * This method copies the contents of the specified source directory
     * to within the specified destination directory.
     * <p/>
     * The destination directory is created if it does not exist.
     * If the destination directory did exist, then this method merges
     * the source with the destination, with the source taking precedence.
     * <p/>
     * <strong>Note:</strong> Setting <code>preserveFileDate</code> to
     * <code>true</code> tries to preserve the files' last modified
     * date/times using {@link File#setLastModified(long)}, however it is
     * not guaranteed that those operations will succeed.
     * If the modification operation fails, no indication is provided.
     * <p/>
     * <h4>Example: Copy directories only</h4>
     * <pre>
     *  // only copy the directory structure
     *  FileUtils.copyDirectory(srcDir, destDir, DirectoryFileFilter.DIRECTORY, false);
     *  </pre>
     *
     * <h4>Example: Copy directories and txt files</h4>
     * <pre>
     *  // Create a filter for ".txt" files
     *  IOFileFilter txtSuffixFilter = FileFilterUtils.suffixFileFilter(".txt");
     *  IOFileFilter txtFiles = FileFilterUtils.andFileFilter(FileFileFilter.FILE, txtSuffixFilter);
     *
     *  // Create a filter for either directories or ".txt" files
     *  FileFilter filter = FileFilterUtils.orFileFilter(DirectoryFileFilter.DIRECTORY, txtFiles);
     *
     *  // Copy using the filter
     *  FileUtils.copyDirectory(srcDir, destDir, filter, false);
     *  </pre>
     *
     * @param srcDir           an existing directory to copy, must not be <code>null</code>
     * @param destDir          the new directory, must not be <code>null</code>
     * @param filter           the filter to apply, null means copy all directories and files
     * @param preserveFileDate true if the file date of the copy
     *                         should be the same as the original
     * @throws NullPointerException if source or destination is <code>null</code>
     * @throws IOException          if source or destination is invalid
     * @throws IOException          if an IO error occurs during copying
     * @since Commons IO 1.4
     */
    public static void copyDirectory(File srcDir, File destDir,
                                     FileFilter filter, boolean preserveFileDate) throws IOException {
        if (srcDir == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (srcDir.exists() == false) {
            throw new FileNotFoundException("Source '" + srcDir + "' does not exist");
        }
        if (srcDir.isDirectory() == false) {
            throw new IOException("Source '" + srcDir + "' exists but is not a directory");
        }
        if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath())) {
            throw new IOException("Source '" + srcDir + "' and destination '" + destDir + "' are the same");
        }

        // Cater for destination being directory within the source directory (see IO-141)
        List<String> exclusionList = null;
        if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath())) {
            File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
            if (srcFiles != null && srcFiles.length > 0) {
                exclusionList = new ArrayList<String>(srcFiles.length);
                for (File srcFile : srcFiles) {
                    File copiedFile = new File(destDir, srcFile.getName());
                    exclusionList.add(copiedFile.getCanonicalPath());
                }
            }
        }
        doCopyDirectory(srcDir, destDir, filter, preserveFileDate, exclusionList);
    }

    /**
     * Internal copy directory method.
     *
     * @param srcDir           the validated source directory, must not be <code>null</code>
     * @param destDir          the validated destination directory, must not be <code>null</code>
     * @param filter           the filter to apply, null means copy all directories and files
     * @param preserveFileDate whether to preserve the file date
     * @param exclusionList    List of files and directories to exclude from the copy, may be null
     * @throws IOException if an error occurs
     * @since Commons IO 1.1
     */
    private static void doCopyDirectory(File srcDir, File destDir, FileFilter filter,
                                        boolean preserveFileDate, List<String> exclusionList) throws IOException {
        // recurse
        File[] files = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
        if (files == null) {  // null if security restricted
            throw new IOException("Failed to list contents of " + srcDir);
        }
        if (destDir.exists()) {
            if (destDir.isDirectory() == false) {
                throw new IOException("Destination '" + destDir + "' exists but is not a directory");
            }
        } else {
            if (destDir.mkdirs() == false) {
                throw new IOException("Destination '" + destDir + "' directory cannot be created");
            }
        }
        if (destDir.canWrite() == false) {
            throw new IOException("Destination '" + destDir + "' cannot be written to");
        }
        for (File file : files) {
            File copiedFile = new File(destDir, file.getName());
            if (exclusionList == null || !exclusionList.contains(file.getCanonicalPath())) {
                if (file.isDirectory()) {
                    doCopyDirectory(file, copiedFile, filter, preserveFileDate, exclusionList);
                } else {
                    doCopyFile(file, copiedFile, preserveFileDate);
                }
            }
        }

        // Do this last, as the above has probably affected directory metadata
        if (preserveFileDate) {
            destDir.setLastModified(srcDir.lastModified());
        }
    }
}
