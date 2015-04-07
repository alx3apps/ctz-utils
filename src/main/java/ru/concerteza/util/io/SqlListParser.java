package ru.concerteza.util.io;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.core.io.Resource;
import ru.concerteza.util.string.CtzConstants;
import ru.concerteza.util.collection.SingleUseIterable;
import ru.concerteza.util.io.noclose.NoCloseReader;
import ru.concerteza.util.namedregex.NamedMatcher;
import ru.concerteza.util.namedregex.NamedPattern;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static ru.concerteza.util.io.CtzResourceUtils.RESOURCE_LOADER;

/**
 * User: alexey
 * Date: 7/7/12
 */
@Deprecated // use com.alexkasko.springjdbc.typedqueries.common.PlainSqlQueriesParser
public class SqlListParser {
    private static final NamedPattern REQUEST_NAME = NamedPattern.compile("^\\s*/\\*\\s*(?<name>.*?)\\s*\\*/\\s*$");
    private static final NamedPattern COMMENT = NamedPattern.compile("^\\s*--.*$");
    private static final NamedPattern LINE = NamedPattern.compile("^\\s*(?<line>.*?)(?:\\s*--.*)?\\s*$");

    private enum State {STARTED, COLLECTING}

    public static Map<String, String> parseToMap(String resourcePath) {
        return parseToMap(resourcePath, CtzConstants.UTF8);
    }

    public static Map<String, String> parseToMap(String resourcePath, String encoding) {
        Resource resource = RESOURCE_LOADER.getResource(resourcePath);
        return parseToMap(resource, encoding);
    }

    public static Map<String, String> parseToMap(Resource resource, String encoding) {
        InputStream is = null;
        try {
            is = resource.getInputStream();
            return parseToMap(is, encoding);
        } catch(IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    public static Map<String, String> parseToMap(InputStream is, String encoding) {
        return parseToMap(new InputStreamReader(is, Charset.forName(encoding)));
    }

    public static Map<String, String> parseToMap(Reader reader) {
        Map<String, String> res = Maps.newLinkedHashMap();
        LineIterator li = null;
        try {
            li = new LineIterator(NoCloseReader.of(reader));
            State state = State.STARTED;
            String name = null;
            StringBuilder sql = new StringBuilder();
            for(String line : SingleUseIterable.of(li)) {
                if(isBlank(line)) continue;
                if(COMMENT.matcher(line).matches()) continue;
                switch(state) {
                    case STARTED:
                        NamedMatcher startedMatcher = REQUEST_NAME.matcher(line);
                        checkArgument(startedMatcher.matches(), "REQUEST_NAME not found on start, regex: '%s'", REQUEST_NAME);
                        name = startedMatcher.group("name");
                        state = State.COLLECTING;
                        break;
                    case COLLECTING:
                        NamedMatcher nameMatcher = REQUEST_NAME.matcher(line);
                        if(nameMatcher.matches()) {
                            checkArgument(sql.length() > 0, "No SQL found for request name: '%s'", name);
                            String existed = res.put(name, sql.toString());
                            checkArgument(null == existed, "Duplicate SQL query name: '%s'", name);
                            sql = new StringBuilder();
                            name = nameMatcher.group("name");
                        } else {
                            NamedMatcher matcher = LINE.matcher(line);
                            matcher.matches();
                            String sqlLine = matcher.group("line");
                            if(sql.length() > 0) sql.append(" ");
                            sql.append(sqlLine);
                        }
                        break;
                    default: throw new IllegalStateException(state.name());
                }
            }
            // tail
            String existed = res.put(name, sql.toString());
            checkArgument(null == existed, "Duplicate SQL query name: '%s'", name);
        } finally {
            LineIterator.closeQuietly(li);
        }
        return res;
    }
}
