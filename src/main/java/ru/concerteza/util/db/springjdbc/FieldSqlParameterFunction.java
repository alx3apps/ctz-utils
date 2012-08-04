package ru.concerteza.util.db.springjdbc;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import ru.concerteza.util.reflect.CtzReflectionUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
* User: alexey
* Date: 7/29/12
*/
// todo extend, test
public class FieldSqlParameterFunction<T> implements Function<T, SqlParameterSource> {
    private final Map<String, Field> fields;

    public FieldSqlParameterFunction(Class<T> clazz) {
        checkNotNull(clazz, "Provided class is null");
        List<Field> list = CtzReflectionUtils.collectFields(clazz, NotStaticPredicate.INSTANCE);
        this.fields = Maps.uniqueIndex(list, FieldNameFun.INSTANCE);
    }

    public static <T> FieldSqlParameterFunction<T> forClass(Class<T> clazz) {
        return new FieldSqlParameterFunction<T>(clazz);
    }

    @Override
    public SqlParameterSource apply(@Nullable T input) {
        return new Params(input);
    }

    private class Params implements SqlParameterSource {
        private final T t;

        private Params(T t) {
            checkNotNull(t, "Provided object is null");
            this.t = t;
        }

        @Override
        public boolean hasValue(String paramName) {
            return fields.containsKey(paramName);
        }

        @Override
        public Object getValue(String paramName) throws IllegalArgumentException {
            try {
                Field fi = fields.get(paramName);
                checkArgument(null != fi, "Key not found: '%s', existed keys: '%s'", paramName, fields.keySet());
                if(!fi.isAccessible()) fi.setAccessible(true);
                return fi.get(t);
            } catch(IllegalAccessException e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        public int getSqlType(String paramName) {
            Field fi = fields.get(paramName);
            checkArgument(null != fi, "Key not found: '%s', existed keys: '%s'", paramName, fields.keySet());
            return StatementCreatorUtils.javaTypeToSqlParameterType(fi.getType());
        }

        @Override
        public String getTypeName(String paramName) {
            return null;
        }
    }

    private enum NotStaticPredicate implements Predicate<Field> {
        INSTANCE;
        @Override
        public boolean apply(Field input) {
            return !Modifier.isStatic(input.getModifiers());
        }
    }

    private enum FieldNameFun implements Function<Field, String> {
        INSTANCE;
        @Override
        public String apply(Field input) {
            return input.getName();
        }
    }
}
