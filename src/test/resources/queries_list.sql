-- example of SQL requests list parsable with ru.concerteza.util.io.SqlListParser

/* foo-query */
select foo --some comment

    from bar
    -- some comment
    where baz=41
    and boo=42

/* bar-query */
select bar --some comment
    from foo
    -- some comment
    where baz=41
    and boo=42