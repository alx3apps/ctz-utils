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
public interface NamedMatchResult extends MatchResult {

    public List<String> orderedGroups();

    public Map<String, String> namedGroups();

    public String group(String groupName);

    public int start(String groupName);

    public int end(String groupName);

}
