package ru.concerteza.util.version;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.UnhandledException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.jar.Manifest;

import static com.google.common.base.Preconditions.checkState;
import static ru.concerteza.util.CtzFormatUtils.format;


/**
 * User: alexey
 * Date: 5/11/11
 */
// todo check overhead for big projects
public class CtzVersionUtils {
    private static final String MANIFEST_PATH = "classpath*:/META-INF/MANIFEST.MF";

    private static final String SPC_TITLE = "Specification-Title";
    private static final String SPC_VERSION = "Specification-Version";
    private static final String SPC_VENDOR = "Specification-Vendor";
    private static final String IMP_TITLE = "Implementation-Title";
    private static final String IMP_VERSION = "Implementation-Version";
    private static final String IMP_VENDOR = "Implementation-Vendor";


    public static CtzVersion readVersionFromManifest(String implementationTitle) {
        Map<String, String> mf = loadManifest(implementationTitle);
        List<String> requiredFields = ImmutableList.of(SPC_TITLE, SPC_VERSION, SPC_VENDOR, IMP_TITLE, IMP_VERSION, IMP_VENDOR);
        for (String fi : requiredFields) {
            checkState(mf.containsKey(fi), "Required field: %s not found in manifest: %s", fi, mf);
        }
        return new CtzVersion(mf.get(SPC_TITLE), mf.get(SPC_VERSION), mf.get(SPC_VENDOR),
                mf.get(IMP_TITLE), mf.get(IMP_VERSION), mf.get(IMP_VENDOR));
    }

    private static Map<String, String> loadManifest(String impTitle) {
        try {
            PathMatchingResourcePatternResolver pathResolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = pathResolver.getResources(MANIFEST_PATH);
            for (Resource re : resources) {
                Map<String, String> attrs = loadManifestAttrs(re);
                if (attrs.containsKey(IMP_TITLE) && impTitle.equals(attrs.get(IMP_TITLE))) {
                    return attrs;
                }
            }
            throw new RuntimeException(format(
                    "Cannot find manifest with {} = {} in resources, size: {}", IMP_TITLE, impTitle, resources.length));
        } catch (IOException e) {
            // classpath only operations, don't want checked exception
            throw new UnhandledException(e);
        }
    }

    private static Map<String, String> loadManifestAttrs(Resource re) throws IOException {
        InputStream stream = null;
        try {
            stream = re.getInputStream();
            Manifest mf = new Manifest(stream);
            ImmutableMap.Builder<String, String> builder = new ImmutableMap.Builder<String, String>();
            for(Map.Entry<Object, Object> en : mf.getMainAttributes().entrySet()) {
                builder.put(en.getKey().toString(), en.getValue().toString());
            }
            return builder.build();
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

}
