package ru.concerteza.util.namedregex;

import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;

/**
 * Named regex groups implementation from <a href="http://code.google.com/p/named-regexp/">named-regexp project</a>,
 * no changes except package naming. Extended syntax is {@code "^foo(?&lt;bar_group>bar)$"}
 * @see NamedMatchResult
 * @see NamedPattern
 * @see MatchResult
 */
@Deprecated // use com.github.tony19:named-regexp
public interface NamedMatchResult extends MatchResult {

    /**
     * @return ordered list of matched groups
     */
    public List<String> orderedGroups();

    /**
     * @return map of named groups
     */
    public Map<String, String> namedGroups();

    /**
     * Returns group by name
     *
     * @param groupName name
     * @return group
     */
    public String group(String groupName);

    /**
     * @param groupName name
     * @return start index of the group
     */
    public int start(String groupName);

    /**
     * @param groupName name
     * @return end index of the group
     */
    public int end(String groupName);

}
