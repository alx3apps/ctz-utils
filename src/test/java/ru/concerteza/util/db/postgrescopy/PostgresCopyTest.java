package ru.concerteza.util.db.postgrescopy;

import com.alexkasko.unsafe.bytearray.ByteArrayTool;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import junit.framework.Assert;
import org.apache.commons.io.EndianUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.concerteza.util.db.partition.Partition;
import ru.concerteza.util.db.partition.PartitionManager;
import ru.concerteza.util.db.partition.PartitionProvider;

import javax.inject.Inject;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collection;

import static com.alexkasko.springjdbc.typedqueries.common.TypedQueriesUtils.STRING_ROW_MAPPER;
import static junit.framework.Assert.assertEquals;
import static org.apache.commons.io.EndianUtils.swapInteger;
import static org.apache.commons.io.EndianUtils.swapLong;
import static org.apache.commons.io.EndianUtils.swapShort;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.toByteArray;
import static ru.concerteza.util.db.postgrescopy.PostgresCopyPersister.EOF_BYTES;
import static ru.concerteza.util.db.postgrescopy.PostgresCopyPersister.HEADER_BYTES;
import static ru.concerteza.util.io.CtzResourceUtils.RESOURCE_LOADER;
import static ru.concerteza.util.string.CtzConstants.UTF8_CHARSET;

/**
 * User: alexkasko
 * Date: 5/5/13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PostgresCopyTest.TestConfig.class)
public class PostgresCopyTest {
    private static final ByteArrayTool BT = ByteArrayTool.get();
    private static final RowMapper<String> STRING_ROW_MAPPER = new SingleColumnRowMapper<String>(String.class);

    @Inject
    private NamedParameterJdbcTemplate jt;
    @Inject
    private javax.sql.DataSource ds;

//    @Test
    public void dummy() {
        //  I'm dummy
    }

//    @Test
    public void testCopy() {
        jt.getJdbcOperations().update("drop table if exists copy_test");
        jt.getJdbcOperations().update("create table copy_test(id bigint, val text)");
        JdbcTemplate sjt = (JdbcTemplate) jt.getJdbcOperations();
        byte[] row1 = new byte[16];
        BT.putLong(row1, 0, 42);
        BT.copy("somedata".getBytes(UTF8_CHARSET), 0, row1, 8, 8);
        byte[] row2 = new byte[16];
        BT.putLong(row2, 0, 43);
        BT.copy("moredata".getBytes(UTF8_CHARSET), 0, row2, 8, 8);
        PostgresCopyPersister pcp = new PostgresCopyPersister(sjt.getDataSource(), new TestProvider(), ImmutableList.of(row1, row2).iterator());
        pcp.persist("copy copy_test(id, val) from stdin binary");
        assertEquals("Rowcount fail", 2, jt.getJdbcOperations().queryForInt("select count(*) from copy_test"));
        assertEquals("Data fail", 42, jt.getJdbcOperations().queryForInt("select min(id) from copy_test"));
        assertEquals("Data fail", "somedata", jt.getJdbcOperations().queryForObject("select val from copy_test where id = 42", STRING_ROW_MAPPER));
        assertEquals("Data fail", 43, jt.getJdbcOperations().queryForInt("select max(id) from copy_test"));
        assertEquals("Data fail", "moredata", jt.getJdbcOperations().queryForObject("select val from copy_test where id = 43", STRING_ROW_MAPPER));
    }

//    @Test
    public void testCopyPartition() {
        jt.getJdbcOperations().update("drop table if exists copy_test_partition_2012010112_2012010113_foo");
        jt.getJdbcOperations().update("drop table if exists copy_test_partition_2012010116_2012010117_foo");
        JdbcTemplate sjt = (JdbcTemplate) jt.getJdbcOperations();
        byte[] row1 = new byte[16];
        BT.putLong(row1, 0, 42);
        BT.putLong(row1, 8, new LocalDateTime(2012, 1, 1, 12, 30).toDate().getTime());
        byte[] row2 = new byte[16];
        BT.putLong(row2, 0, 43);
        BT.putLong(row2, 8, new LocalDateTime(2012, 1, 1, 17, 30).toDate().getTime());
        PartitionManager pm = PartitionManager.builder(new PostgresPartitionProvider(jt)).withTable("copy_test_partition", 2).build();
        ImmutableList<Partition> existed = pm.init();
        assertEquals("Existed data fail", 0, existed.size());
        PostgresPartitionCopyPersister pcp = new PostgresPartitionCopyPersister(sjt.getDataSource(), pm);
        String sql = "copy copy_test_partition_${partition}(id, rec_date) from stdin binary";
        pcp.persist(new TestProviderPartition(), sql, "copy_test_partition", "foo", ImmutableList.of(row1, row2).iterator());
    }

    @Test
    public void testOpenCopyStream() throws SQLException, IOException {
        jt.getJdbcOperations().update("drop table if exists copy_out_test");
        jt.getJdbcOperations().update("create table copy_out_test(id bigint, val int)");
        jt.getJdbcOperations().update("begin transaction");
        jt.getJdbcOperations().update("insert into copy_out_test(id, val) values(41, 42)");
        jt.getJdbcOperations().update("commit");
        jt.getJdbcOperations().update("begin transaction");
        InputStream is = null;
        try {
            byte[] buf = new byte[HEADER_BYTES.length + EOF_BYTES.length + 22];
            is = PostgresCopyUtils.openCopyStream(ds.getConnection(), "copy copy_out_test(id, val) to stdin binary");
            int read = is.read(buf);
            assertEquals(buf.length, read);
            assertEquals((byte) 0xff, buf[buf.length - 2]);
            assertEquals((byte) 0xff, buf[buf.length - 1]);
            long id = swapLong(BT.getLong(buf, HEADER_BYTES.length + 6));
            assertEquals(41, id);
            int val = swapInteger(BT.getInt(buf, HEADER_BYTES.length + 18));
            assertEquals(42, val);
            jt.getJdbcOperations().update("commit");
        } catch (Exception e) {
            jt.getJdbcOperations().update("rollback");
        } finally {
            closeQuietly(is);
        }
    }

    private static class TestProvider implements PostgresCopyProvider {
        @Override
        public int fillCopyBuf(byte[] src, byte[] dest) {
            int pos = 0;
            BT.putShort(dest, pos, swapShort((short) 2));
            pos += 2;
            BT.putInt(dest, pos, swapInteger(8));
            pos += 4;
            BT.putLong(dest, pos, swapLong(BT.getLong(src, 0)));
            pos += 8;
            int len = src.length - 8;
            BT.putInt(dest, pos, swapInteger(len));
            pos += 4;
            BT.copy(src, 8, dest, pos, len);
            pos += len;
            return pos;
        }
    }

    private static class TestProviderPartition implements PostgresPartitionCopyProvider {

        @Override
        public long date(byte[] packet) {
            return BT.getLong(packet, 8);
        }

        @Override
        public int maxSize() {
            return 26;
        }

        @Override
        public int fillCopyBuf(byte[] src, byte[] dest) {
            int pos = 0;
            BT.putShort(dest, pos, swapShort((short) 2));
            pos += 2;
            BT.putInt(dest, pos, swapInteger(8));
            pos += 4;
            BT.putLong(dest, pos, swapLong(BT.getLong(src, 0)));
            pos += 8;
            BT.putInt(dest, pos, swapInteger(8));
            pos += 4;
            BT.putLong(dest, pos, swapLong(BT.getLong(src, 8)));
            pos += 8;
            return pos;
        }
    }

    private static class PostgresPartitionProvider implements PartitionProvider {

        private final NamedParameterJdbcTemplate jt;

        private PostgresPartitionProvider(NamedParameterJdbcTemplate jt) {
            this.jt = jt;
        }

        @Override
        public Collection<String> loadPartitions(String prefix) {
            String sql = "select table_name from information_schema.tables\n" +
                    "    where table_name like :table";
            return jt.query(sql, ImmutableMap.of("table", prefix + "%"), STRING_ROW_MAPPER);
        }

        @Override
        public void createPartition(String prefix, String postfix) {
            if (!"copy_test_partition".equals(prefix)) throw new IllegalArgumentException(prefix);
            String sql = "create table copy_test_partition_" + postfix + " (id bigint, rec_date bigint)";
            jt.getJdbcOperations().update(sql);
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