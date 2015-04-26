package ru.concerteza.util.net.diriterator;

import org.apache.commons.lang3.builder.ToStringBuilder;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * User: alexkasko
 * Date: 12/6/14
 */
public class DirIteratorPaths {

    private final String dir;
    private final String successDir;
    private final String errorDir;

    public DirIteratorPaths(String dir, String successDir, String errorDir) {
        checkArgument(isNotBlank(dir), "Specified dir is blank");
        checkArgument(isNotBlank(successDir), "Specified successDir is blank");
        checkArgument(isNotBlank(errorDir), "Specified errorDir is blank");
        this.dir = ensureSlash(dir);
        this.successDir = ensureSlash(successDir);
        this.errorDir = ensureSlash(errorDir);
    }

    public String getDir() {
        return dir;
    }

    public String getSuccessDir() {
        return successDir;
    }

    public String getErrorDir() {
        return errorDir;
    }

    private String ensureSlash(String path) {
        return path.endsWith("/") ? path : path + "/";
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE).
                append("dir", dir).
                append("successDir", successDir).
                append("errorDir", errorDir).
                toString();
    }
}
