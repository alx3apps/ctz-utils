package ru.concerteza.util.handlers;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.springframework.http.HttpMethod;
import ru.concerteza.util.namedregex.NamedMatcher;
import ru.concerteza.util.namedregex.NamedPattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * User: alexey
 * Date: 4/4/12
 */
@Deprecated// use com.alexkasko.rest:com.alexkasko.rest
public class HandlersDispatcher {
    private final Multimap<HttpMethod, Entry<?>> mapping;
    private final HandlersErrorReporter errorReporter;

    private HandlersDispatcher(Multimap<HttpMethod, Entry<?>> mapping, HandlersErrorReporter errorReporter) {
        this.mapping = mapping;
        this.errorReporter = errorReporter;
    }

    public static Builder builder(HandlersErrorReporter errorReporter) {
        return new Builder(errorReporter);
    }

    @SuppressWarnings("unchecked")
    public void dispatch(HttpServletRequest req, HttpServletResponse resp) {
        try {
            HttpMethod method = HttpMethod.valueOf(req.getMethod());
            for(Entry en : mapping.get(method)) {
                NamedMatcher matcher = en.matcher(req.getPathInfo());
                if(matcher.matches()) {
                    en.ra.process(en.clazz, matcher.namedGroups(), req, resp);
                    return;
                }
            }
            errorReporter.report404(req, resp);
        } catch(Exception e) {
            errorReporter.reportException(req, resp, e);
        }
    }

    private static class Entry<T> {
        private final HttpMethod method;
        private final NamedPattern pattern;
        private final HandlersProcessor<T> ra;
        private final Class<? extends T> clazz;

        private Entry(HttpMethod method, String pattern, HandlersProcessor<T> ra, Class<? extends T> clazz) {
            checkArgument(isNotBlank(pattern), "Provided pattern is blank");
            checkNotNull(method, "Provided http method is null");
            checkNotNull(clazz, "Provided handler class is null");
            this.method = method;
            this.pattern = NamedPattern.compile(pattern);
            this.ra = ra;
            this.clazz = clazz;
        }

        private NamedMatcher matcher(String input) {
            return pattern.matcher(input);
        }
    }

    public static class Builder {
        private final HandlersErrorReporter errorReporter;
        private final ImmutableList.Builder<Entry<?>> handlers = ImmutableList.builder();


        public Builder(HandlersErrorReporter errorReporter) {
            this.errorReporter = errorReporter;
        }

        public <T> Builder add(HttpMethod method, String pattern, HandlersProcessor<T> ra, Class<? extends T> clazz) {
            handlers.add(new Entry<T>(method, pattern, ra, clazz));
            return this;
        }

        public HandlersDispatcher build() {
            Multimap<HttpMethod, Entry<?>> mapping = Multimaps.index(handlers.build(), EntryKeyFun.INSTANCE);
            return new HandlersDispatcher(mapping, errorReporter);
        }
    }

    private enum EntryKeyFun implements Function<Entry, HttpMethod> {
        INSTANCE;
        @Override
        public HttpMethod apply(Entry input) {
            return input.method;
        }
    }
}
