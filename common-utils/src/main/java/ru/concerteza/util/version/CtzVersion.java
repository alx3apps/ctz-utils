package ru.concerteza.util.version;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import ru.concerteza.util.namedregex.NamedMatcher;
import ru.concerteza.util.namedregex.NamedPattern;

import static java.lang.Integer.parseInt;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static ru.concerteza.util.CtzFormatUtils.format;

/**
 * User: alexey
 * Date: 5/11/11
 */
public class CtzVersion {

    private final String specificationTitle;
    private final String specificationVersion;
    private final String specificationVendor;
    private final String implementationTitle;
    private final String implementationVersion;
    private final String implementationVendor;
    private final String gitBranch;
    private final String gitTag;
    private final int gitCommitsCount;

    public CtzVersion(String specificationTitle, String specificationVersion, String specificationVendor, String implementationTitle, String implementationVersion, String implementationVendor, String gitBranch, String gitTag, String gitCommitsCount) {
        this.specificationTitle = specificationTitle;
        this.specificationVersion = specificationVersion;
        this.specificationVendor = specificationVendor;
        this.implementationTitle = implementationTitle;
        this.implementationVersion = implementationVersion;
        this.implementationVendor = implementationVendor;
        this.gitBranch = gitBranch;
        this.gitTag = gitTag;
        this.gitCommitsCount = Integer.parseInt(gitCommitsCount);
    }

    public String getSpecificationTitle() {
        return specificationTitle;
    }

    public String getSpecificationVersion() {
        return specificationVersion;
    }

    public String getSpecificationVendor() {
        return specificationVendor;
    }

    public String getImplementationTitle() {
        return implementationTitle;
    }

    public String getImplementationVersion() {
        return implementationVersion;
    }

    public String getImplementationVendor() {
        return implementationVendor;
    }

    public String getGitBranch() {
        return gitBranch;
    }

    public String getGitTag() {
        return gitTag;
    }

    public int getGitCommitsCount() {
        return gitCommitsCount;
    }

    // git rev-parse --short HEAD
    private String getRevision() {
        if(implementationVersion.length() > 7) return implementationVersion.substring(0, 7);
        else return implementationVersion;
    }

    public String createBuildnumber() {
        final String prefix;
        if(isNotEmpty(gitBranch)) prefix = gitBranch + "-dev";
        else if(isNotEmpty(gitTag)) prefix = gitTag;
        else prefix = "UNTAGGED";
        return format("{}.{}.{}", prefix, gitCommitsCount, getRevision());
    }

    public String standardFormat() {
        return format("{}, {}, {}", implementationVendor, specificationTitle, createBuildnumber());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                append("specificationTitle", specificationTitle).
                append("specificationVersion", specificationVersion).
                append("specificationVendor", specificationVendor).
                append("implementationTitle", implementationTitle).
                append("implementationVersion", implementationVersion).
                append("implementationVendor", implementationVendor).
                append("gitBranch", gitBranch).
                append("gitTag", gitTag).
                append("gitCommitsCount", gitCommitsCount).
                toString();
    }
}
