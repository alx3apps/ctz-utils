package ru.concerteza.util.namedregex;

import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;

// http://code.google.com/p/named-regexp/
public interface NamedMatchResult extends MatchResult {

    public List<String> orderedGroups();

    public Map<String, String> namedGroups();

    public String group(String groupName);

    public int start(String groupName);

    public int end(String groupName);

}
