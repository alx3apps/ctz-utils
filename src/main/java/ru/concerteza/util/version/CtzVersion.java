package ru.concerteza.util.version;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static ru.concerteza.util.CtzFormatUtils.format;

/**
 * Application version representation, parsed from MANIFEST.MF file by {@link CtzVersionUtils}
 *
 * @author alexey,
 * Date: 5/11/11
 * @see CtzVersionUtils
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

    /**
     * Main constructor
     * @param specificationTitle {@code Specification-Title} manifest field
     * @param specificationVersion {@code Specification-Version} manifest field
     * @param specificationVendor {@code Specification-Vendor} manifest field
     * @param implementationTitle {@code Implementation-Title} manifest field
     * @param implementationVersion {@code Implementation-Version} manifest field
     * @param implementationVendor {@code Implementation-Vendor} manifest field
     * @param gitBranch {@code X-Git-Branch} manifest field
     * @param gitTag {@code X-Git-Tag} manifest field
     * @param gitCommitsCount {@code X-Git-Commits-Count} manifest field
     */
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

    /**
     * @return {@code Specification-Title} manifest field
     */
    public String getSpecificationTitle() {
        return specificationTitle;
    }

    /**
     * @return {@code Specification-Version} manifest field
     */
    public String getSpecificationVersion() {
        return specificationVersion;
    }

    /**
     * @return {@code Specification-Vendor} manifest field
     */
    public String getSpecificationVendor() {
        return specificationVendor;
    }

    /**
     * @return {@code Implementation-Title} manifest field
     */
    public String getImplementationTitle() {
        return implementationTitle;
    }

    /**
     * @return {@code Implementation-Version} manifest field
     */
    public String getImplementationVersion() {
        return implementationVersion;
    }

    /**
     * @return {@code Implementation-Vendor} manifest field
     */
    public String getImplementationVendor() {
        return implementationVendor;
    }

    /**
     * @return {@code X-Git-Branch} manifest field
     */
    public String getGitBranch() {
        return gitBranch;
    }

    /**
     * @return {@code X-Git-Tag} manifest field
     */
    public String getGitTag() {
        return gitTag;
    }

    /**
     * @return {@code X-Git-Commits-Count} manifest field
     */
    public int getGitCommitsCount() {
        return gitCommitsCount;
    }

    /**
     * Revison number in form returning by {@code git rev-parse --short HEAD}
     * @return revision ID stripped to 7 symbols
     */
    private String getRevision() {
        if(implementationVersion.length() > 7) return implementationVersion.substring(0, 7);
        else return implementationVersion;
    }

    /**
     * Standard buildnumber, consists of {@code tag_or_branch.commits_count.revision_id}
     * @return buildnumber string
     */
    public String createBuildnumber() {
        final String prefix;
        if(isNotEmpty(gitBranch)) prefix = gitBranch + "-dev";
        else if(isNotEmpty(gitTag)) prefix = gitTag;
        else prefix = "UNTAGGED";
        return format("{}.{}.{}", prefix, gitCommitsCount, getRevision());
    }

    /**
     * Standard version, consists of {@code implementation_vendor, specfication_title, buildnumber}
     * @return version string
     */
    public String standardFormat() {
        return format("{}, {}, {}", implementationVendor, specificationTitle, createBuildnumber());
    }

    /**
     * {@code toString} implementation
     * @return string representation using <a href="http://commons.apache.org/lang/api-2.4/org/apache/commons/lang/builder/ToStringStyle.html#SHORT_PREFIX_STYLE">SHORT_PREFIX_STYLE</a>
     */
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
