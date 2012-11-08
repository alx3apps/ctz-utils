package ru.concerteza.util.db.springjdbc.querybuilder;

import org.apache.commons.lang.text.StrSubstitutor;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * User: alexkasko
 * Date: 11/7/12
 */
public class QueryBuilder {
    private static final Pattern CLAUSE_PATTERN = Pattern.compile("[^\\$]\\$\\{(\\w+)\\}");

    private final String template;
    // sorry, no multimap in JDK
    private final Map<String, ExprList> clauses = new HashMap<String, ExprList>();

    public QueryBuilder(String template) {
        if(isBlank(template)) throw new IllegalArgumentException("Provided template is blank");
        this.template = template;
        Matcher m = CLAUSE_PATTERN.matcher(template);
        while (m.find()) {
            String name = m.group(1);
            ExprList existed = clauses.put(name, new ExprList());
            if(null != existed) throw new IllegalArgumentException(
                    "Duplicate clause: [" + name +"] found in template: [" + template +"]");
        }
    }

    public QueryBuilder add(String clauseName, Expression expr) {
        if(isBlank(clauseName)) throw new IllegalArgumentException("Provided clauseName is blank");
        if(null == expr) throw new IllegalArgumentException("Provided expr is null");
        ExprList list = clauses.get(clauseName);
        if(null == list) throw new IllegalArgumentException("Clause: [" + clauseName + "] is not found in template: [" +
                template + "], registered clauses: [" + clauses.keySet() + "], " +
                "(clause name must conform this regex: '[a-zA-Z_0-9]+')");
        list.add(expr);
        return this;
    }

    public String build() {
        Map<String, String> map = new HashMap<String, String>();
        for(Map.Entry<String, ExprList> en : clauses.entrySet()) {
            // todo: check all clauses filled
            map.put(en.getKey(), en.getValue().toString());
        }
        return StrSubstitutor.replace(template, map);
    }
}