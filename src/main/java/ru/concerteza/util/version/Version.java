package ru.concerteza.util.version;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * User: alexey
 * Date: 5/11/11
 */
public class Version {
    private final String specificationTitle;
    private final String specificationVersion;
    private final String specificationVendor;
    private final String implementationTitle;
    private final String implementationVersion;
    private final String implementationVendor;

    public Version(String specificationTitle, String specificationVersion, String specificationVendor, String implementationTitle, String implementationVersion, String implementationVendor) {
        this.specificationTitle = specificationTitle;
        this.specificationVersion = specificationVersion;
        this.specificationVendor = specificationVendor;
        this.implementationTitle = implementationTitle;
        this.implementationVersion = implementationVersion;
        this.implementationVendor = implementationVendor;
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

    public String getShortImplementationVersion() {
        if(implementationVersion.length() > 7) return implementationVersion.substring(0, 7);
        else return implementationVersion;
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
                toString();
    }
}
