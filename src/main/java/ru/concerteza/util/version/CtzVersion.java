package ru.concerteza.util.version;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import ru.concerteza.util.namedregex.NamedMatcher;
import ru.concerteza.util.namedregex.NamedPattern;

import static java.lang.Integer.parseInt;
import static ru.concerteza.util.CtzFormatUtils.format;

/**
 * User: alexey
 * Date: 5/11/11
 */
public class CtzVersion {
    private static final NamedPattern STD_VERSION_PATTERN = NamedPattern.compile(
            "^(?<major_version>\\d+)\\.(?<minor_version>\\d+).*$");

    private final String specificationTitle;
    private final String specificationVersion;
    private final String specificationVendor;
    private final String implementationTitle;
    private final String implementationVersion;
    private final String implementationVendor;
    private final int majorVersion;
    private final int minorVersion;

    public CtzVersion(String specificationTitle, String specificationVersion, String specificationVendor, String implementationTitle, String implementationVersion, String implementationVendor) {
        this.specificationTitle = specificationTitle;
        this.specificationVersion = specificationVersion;
        this.specificationVendor = specificationVendor;
        this.implementationTitle = implementationTitle;
        this.implementationVersion = implementationVersion;
        this.implementationVendor = implementationVendor;
        NamedMatcher matcher = STD_VERSION_PATTERN.matcher(specificationVersion);
        if(matcher.matches()) {
            majorVersion = parseInt(matcher.group("major_version"));
            minorVersion = parseInt(matcher.group("minor_version"));
        } else {
            majorVersion = -1;
            minorVersion = -1;
        }
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

    // git rev-parse --short HEAD
    public String getShortImplementationVersion() {
        if(implementationVersion.length() > 7) return implementationVersion.substring(0, 7);
        else return implementationVersion;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public String standardFormat() {
        return format("{}, {} version: {} build: {}", implementationVendor, specificationTitle, specificationVersion, getShortImplementationVersion());
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
                append("majorVersion", majorVersion).
                append("minorVersion", minorVersion).
                toString();
    }
}
