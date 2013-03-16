package ru.concerteza.util.namedregex;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Named regex groups implementation from <a href="http://code.google.com/p/named-regexp/">named-regexp project</a>,
 * no changes except package naming. Extended syntax is {@code "^foo(?&lt;bar_group>bar)$"}.
 * See {@link Matcher} javadoc for detailed dicumentation of most methods.
 *
 * @see NamedMatchResult
 * @see NamedPattern
 * @see Matcher
 */
@Deprecated // use com.github.tony19:named-regexp
public class NamedMatcher implements NamedMatchResult {

    private Matcher matcher;
    private NamedPattern parentPattern;

    /**
     * Inner use constructor
     */
    NamedMatcher() {
    }

    /**
     * Inner use constructor
     *
     * @param parentPattern pattern
     * @param matcher matcher result
     */
    NamedMatcher(NamedPattern parentPattern, MatchResult matcher) {
        this.parentPattern = parentPattern;
        this.matcher = (Matcher) matcher;
    }

    /**
     * Inner use constructor
     *
     * @param parentPattern pattern
     * @param input input char sequence
     */
    NamedMatcher(NamedPattern parentPattern, CharSequence input) {
        this.parentPattern = parentPattern;
        this.matcher = parentPattern.pattern().matcher(input);
    }

    /**
     * @return non-named pattern
     */
    public Pattern standardPattern() {
        return matcher.pattern();
    }

    /**
     * @return named pattern
     */
    public NamedPattern namedPattern() {
        return parentPattern;
    }

    /**
     * Changes the Pattern that this Matcher uses to
     * find matches with.
     *
     * @param newPattern new pattern
     * @return matcher itself
     */
    public NamedMatcher usePattern(NamedPattern newPattern) {
        this.parentPattern = newPattern;
        matcher.usePattern(newPattern.pattern());
        return this;
    }

    /**
     * Resets this matcher.
     *
     * @return matcher itself
     */
    public NamedMatcher reset() {
        matcher.reset();
        return this;
    }

    /**
     * Resets this matcher with a new input sequence
     *
     * @param input input char sequence
     * @return matcher itself
     */
    public NamedMatcher reset(CharSequence input) {
        matcher.reset(input);
        return this;
    }

    /**
     * Attempts to match the entire region against the pattern
     *
     * @return whether matching was successful
     */
    public boolean matches() {
        return matcher.matches();
    }

    /**
     * Returns the match state of this matcher as a {@link NamedMatchResult}.
     *
     * @return named match result
     */
    public NamedMatchResult toMatchResult() {
        return new NamedMatcher(this.parentPattern, matcher.toMatchResult());
    }

    /**
     * Attempts to find the next subsequence of the input sequence that matches the pattern.
     *
     * @return whether find was successful
     */
    public boolean find() {
        return matcher.find();
    }

    /**
     * Attempts to find the next subsequence of the input sequence that matches the pattern.
     *
     * @param start start position
     * @return whether find was successful
     */
    public boolean find(int start) {
        return matcher.find(start);
    }

    /**
     * Attempts to match the input sequence, starting at the beginning of the region, against the pattern
     *
     * @return whether looking at was successful
     */
    public boolean lookingAt() {
        return matcher.lookingAt();
    }

    /**
     * non-terminal append-and-replace step
     *
     * @param sb buffer
     * @param replacement replacement string
     * @return matcher itself
     */
    public NamedMatcher appendReplacement(StringBuffer sb, String replacement) {
        matcher.appendReplacement(sb, replacement);
        return this;
    }

    /**
     * terminal append-and-replace step
     *
     * @param sb buffer
     * @return matcher itself
     */
    public StringBuffer appendTail(StringBuffer sb) {
        return matcher.appendTail(sb);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String group() {
        return matcher.group();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String group(int group) {
        return matcher.group(group);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int groupCount() {
        return matcher.groupCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> orderedGroups() {
        ArrayList<String> groups = new ArrayList<String>();
        for (int i = 1; i <= groupCount(); i++) {
            groups.add(group(i));
        }
        return groups;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String group(String groupName) {
        return group(groupIndex(groupName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> namedGroups() {
        Map<String, String> result = new LinkedHashMap<String, String>();

        for (int i = 1; i <= groupCount(); i++) {
            String groupName = parentPattern.groupNames().get(i - 1);
            String groupValue = matcher.group(i);
            result.put(groupName, groupValue);
        }

        return result;
    }

    /**
     * @param groupName group name
     * @return index of this group
     */
    private int groupIndex(String groupName) {
        return parentPattern.groupNames().indexOf(groupName) + 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int start() {
        return matcher.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int start(int group) {
        return matcher.start(group);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int start(String groupName) {
        return start(groupIndex(groupName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int end() {
        return matcher.end();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int end(int group) {
        return matcher.end(group);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int end(String groupName) {
        return end(groupIndex(groupName));
    }

    /**
     * Sets the limits of this matcher's region
     *
     * @param start start index
     * @param end end index
     * @return matcher itself
     */
    public NamedMatcher region(int start, int end) {
        matcher.region(start, end);
        return this;
    }

    /**
     * end index (exclusive) of this matcher's region
     *
     * @return end index
     */
    public int regionEnd() {
        return matcher.regionEnd();
    }

    /**
     * start index of this matcher's region
     *
     * @return start index
     */
    public int regionStart() {
        return matcher.regionStart();
    }

    /**
     * Returns true if the end of input was hit by the search engine in
     * the last match operation performed by this matcher
     *
     * @return whether end was hit
     */
    public boolean hitEnd() {
        return matcher.hitEnd();
    }

    /**
     * returns true if more input could change a positive match into a
     * negative one.
     *
     * @return true if more input could change a positive match into a
     *          negative one
     */
    public boolean requireEnd() {
        return matcher.requireEnd();
    }

    /**
     * Queries the anchoring of region bounds for this matcher
     *
     * @return whether this matcher is using anchoring bounds
     */
    public boolean hasAnchoringBounds() {
        return matcher.hasAnchoringBounds();
    }

    /**
     * transparency of region bounds for this matcher
     *
     * @return whether this matcher is using transparent bounds
     */
    public boolean hasTransparentBounds() {
        return matcher.hasTransparentBounds();
    }

    /**
     * Replaces every subsequence of the input sequence that matches the
     * pattern
     *
     * @param replacement replace string
     * @return string after replacement
     */
    public String replaceAll(String replacement) {
        return matcher.replaceAll(replacement);
    }

    public String replaceFirst(String replacement) {
        return matcher.replaceFirst(replacement);
    }

    /**
     * Enables or disables anchoring bound
     *
     * @param b new value
     * @return matcher itself
     */
    public NamedMatcher useAnchoringBounds(boolean b) {
        matcher.useAnchoringBounds(b);
        return this;
    }

    /**
     * Enables or disables transparent bounds
     *
     * @param b new value
     * @return matcher itself
     */
    public NamedMatcher useTransparentBounds(boolean b) {
        matcher.useTransparentBounds(b);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return matcher.equals(obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return matcher.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return matcher.toString();
    }

}
