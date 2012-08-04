package ru.concerteza.util.namedregex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Named regex groups implementation from <a href="http://code.google.com/p/named-regexp/">named-regexp project</a>,
 * With almost no changes except package naming. Extended syntax is {@code "^foo(?&lt;bar_group>bar)$"}
 * See {@link Pattern} javadoc for detailed dicumentation of most methods.
 *
 * @see NamedMatchResult
 * @see NamedPattern
 * @see Pattern
 */
public class NamedPattern {
    private static final Pattern NAMED_GROUP_PATTERN = Pattern.compile("\\(\\?<(\\w+)>");

    private Pattern pattern;
    private String namedPattern;
    private List<String> groupNames;

    /**
     * Shortcut factory method
     *
     * @param regex regex string
     * @return named pattern instance
     */
    public static NamedPattern compile(String regex) {
        return new NamedPattern(regex, 0);
    }

    /**
     * Factory method
     *
     * @param regex regex string
     * @param flags regex flags
     * @return named pattern instance
     */
    public static NamedPattern compile(String regex, int flags) {
        return new NamedPattern(regex, flags);
    }

    private NamedPattern(String regex, int i) {
        namedPattern = regex;
        pattern = buildStandardPattern(regex);
        groupNames = extractGroupNames(regex);
    }

    /**
     * @return provided flags
     */
    public int flags() {
        return pattern.flags();
    }

    /**
     * Matches provided char sequence
     *
     * @param input char sequence to match
     * @return named matcher
     */
    public NamedMatcher matcher(CharSequence input) {
        return new NamedMatcher(this, input);
    }

    /**
     * @return non-named pattern
     */
    public Pattern pattern() {
        return pattern;
    }

    /**
     * @return provided regex string with named sections stripped
     */
    public String standardPattern() {
        return pattern.pattern();
    }

    /**
     * @return provided regex string
     */
    public String namedPattern() {
        return namedPattern;
    }

    /**
     * @return list of group names from provided pattern
     */
    public List<String> groupNames() {
        return groupNames;
    }

    /**
     * Splits the given input sequence around matches of this pattern.
     *
     * @param input input char sequence
     * @param limit result threshold
     * @return splitted array
     */
    public String[] split(CharSequence input, int limit) {
        return pattern.split(input, limit);
    }

    /**
     * Splits the given input sequence around matches of this pattern.
     *
     * @param input input char sequence
     * @return splitted array
     */
    public String[] split(CharSequence input) {
        return pattern.split(input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return namedPattern;
    }

    /**
     * Extracts group name from provided pattern
     *
     * @param namedPattern named pattern
     * @return group names list
     */
    static List<String> extractGroupNames(String namedPattern) {
        List<String> groupNames = new ArrayList<String>();
        Matcher matcher = NAMED_GROUP_PATTERN.matcher(namedPattern);
        while (matcher.find()) {
            groupNames.add(matcher.group(1));
        }
        return groupNames;
    }

    /**
     * Builds non-named pattern from provided named pattern string
     *
     * @param namedPattern named patern
     * @return non-named pattern
     */
    static Pattern buildStandardPattern(String namedPattern) {
        return Pattern.compile(NAMED_GROUP_PATTERN.matcher(namedPattern).replaceAll("("));
    }

}
