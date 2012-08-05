-- some comment

/* foo-query */
select foo --inline comment
    from bar
    where 41 > 42 --inline comment

/* bar-query */
select bar
    from foo
    where 42 < 41