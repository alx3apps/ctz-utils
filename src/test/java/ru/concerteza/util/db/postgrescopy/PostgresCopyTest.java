package ru.concerteza.util.db.postgrescopy;

import com.alexkasko.unsafe.bytearray.ByteArrayTool;
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.inject.Inject;

import static junit.framework.Assert.assertEquals;
import static org.apache.commons.io.EndianUtils.swapInteger;
import static org.apache.commons.io.EndianUtils.swapLong;
import static org.apache.commons.io.EndianUtils.swapShort;
import static ru.concerteza.util.io.CtzResourceUtils.RESOURCE_LOADER;
import static ru.concerteza.util.string.CtzConstants.UTF8_CHARSET;

/**
 * User: alexkasko
 * Date: 5/5/13
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = PostgresCopyTest.TestConfig.class)
public class PostgresCopyTest {
    private static final ByteArrayTool bat = ByteArrayTool.get();
    private static final RowMapper<String> STRING_ROW_MAPPER = new SingleColumnRowMapper<String>(String.class);

    @Inject
    private NamedParameterJdbcTemplate jt;

    @Test
    public void dummy() {
        //  I'm dummy
    }

//    @Test
    public void testCopy() {
        jt.getJdbcOperations().update("drop table if exists copy_test");
        jt.getJdbcOperations().update("create table copy_test(id bigint, val text)");
        JdbcTemplate sjt = (JdbcTemplate) jt.getJdbcOperations();
        PostgresCopyPersister pcp = new PostgresCopyPersister(sjt.getDataSource());
        byte[] row1 = new byte[16];
        bat.putLong(row1, 0, 42);
        bat.copy("somedata".getBytes(UTF8_CHARSET), 0, row1, 8, 8);
        byte[] row2 = new byte[16];
        bat.putLong(row2, 0, 43);
        bat.copy("moredata".getBytes(UTF8_CHARSET), 0, row2, 8, 8);
        PostgresCopyStream st = new PostgresCopyStream(ImmutableList.of(row1, row2).iterator(), new TestProvider());
        pcp.persist("copy copy_test(id, val) from stdin binary", st);
        assertEquals("Rowcount fail", 2, jt.getJdbcOperations().queryForInt("select count(*) from copy_test"));
        assertEquals("Data fail", 42, jt.getJdbcOperations().queryForInt("select min(id) from copy_test"));
        assertEquals("Data fail", "somedata", jt.getJdbcOperations().queryForObject("select val from copy_test where id = 42", STRING_ROW_MAPPER));
        assertEquals("Data fail", 43, jt.getJdbcOperations().queryForInt("select max(id) from copy_test"));
        assertEquals("Data fail", "moredata", jt.getJdbcOperations().queryForObject("select val from copy_test where id = 43", STRING_ROW_MAPPER));
    }

    private static class TestProvider implements PostgresCopyProvider {
        @Override
        public int fillCopyBuf(byte[] src, byte[] dest) {
            int pos = 0;
            bat.putShort(dest, pos, swapShort((short) 2));
            pos += 2;
            bat.putInt(dest, pos, swapInteger(8));
            pos += 4;
            bat.putLong(dest, pos, swapLong(bat.getLong(src, 0)));
            pos += 8;
            int len = src.length - 8;
            bat.putInt(dest, pos, swapInteger(len));
            pos += 4;
            bat.copy(src, 8, dest, pos, len);
            pos += len;
            return pos;
        }
    }

    @Configuration
    static class TestConfig {
        @Value("${ctzutils.postgrescopy.hostname}") private String hostname;
        @Value("${ctzutils.postgrescopy.port}") private int port;
        @Value("${ctzutils.postgrescopy.database}") private String database;
        @Value("${ctzutils.postgrescopy.user}") private String user;
        @Value("${ctzutils.postgrescopy.password}") private String password;

        @Bean
        static PropertySourcesPlaceholderConfigurer properties() {
            PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
            Resource[] resources = new Resource[]{RESOURCE_LOADER.getResource("classpath:/postgres-copy-test.properties")};
            pspc.setLocations(resources);
            return pspc;
        }

        @Bean
        public javax.sql.DataSource dataSource() {
            DriverManagerDataSource ds = new DriverManagerDataSource();
            ds.setDriverClassName("org.postgresql.Driver");
            ds.setUrl("jdbc:postgresql://" + hostname + ":" + port + "/" + database);
            ds.setUsername(user);
            ds.setPassword(password);
            return ds;
        }

        @Bean
        public NamedParameterJdbcTemplate jt() {
            return new NamedParameterJdbcTemplate(dataSource());
        }


    }

}
