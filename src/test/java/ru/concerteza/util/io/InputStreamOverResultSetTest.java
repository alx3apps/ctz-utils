package ru.concerteza.util.io;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import junit.framework.Assert;
import org.apache.commons.lang.UnhandledException;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.concerteza.util.db.csv.CsvDataSource;
import ru.concerteza.util.db.springjdbc.InputStreamOverResultSet;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.lang.RandomStringUtils.random;
import static org.apache.commons.lang.RandomStringUtils.randomNumeric;
import static org.junit.Assert.assertEquals;
import static ru.concerteza.util.string.CtzConstants.UTF8;

/**
 * User: alexey
 * Date: 8/29/12
 */
public class InputStreamOverResultSetTest {
    private static final int STR_SIZE = 42;
    private static final List<String> DATA = ImmutableList.of(
            random(STR_SIZE).replace("|", "_").replace("\n", "_"),
            random(STR_SIZE * 2).replace("|", "_").replace("\n", "_"),
            random(STR_SIZE * 4).replace("|", "_").replace("\n", "_"),
            random(STR_SIZE * 8).replace("|", "_").replace("\n", "_"),
            random(STR_SIZE).replace("|", "_").replace("\n", "_"),
            random(STR_SIZE * 16).replace("|", "_").replace("\n", "_"));

//    @Test
    public void testDummy() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        String str1 = randomNumeric(4242);
        String str2 = randomNumeric(4242);
        String str3 = randomNumeric(4242);
        dos.writeUTF(str1);
        dos.writeUTF(str2);
        dos.writeUTF(str3);
        dos.flush();
        byte[] data = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        String read1 = dis.readUTF();
        String read2 = dis.readUTF();
        String read3 = dis.readUTF();
        assertEquals(read1, str1);
        assertEquals(read2, str2);
        assertEquals(read3, str3);
    }

    @Test
    public void test() throws UnsupportedEncodingException {
        String source = createData();
        Resource resource = new ByteArrayResource(source.getBytes(UTF8));
        CsvDataSource ds = new CsvDataSource(resource, "|", UTF8);
        NamedParameterJdbcTemplate jt = new NamedParameterJdbcTemplate(ds);
        List<String> read = jt.getJdbcOperations().query("give me the data, please", Extr.INSTANCE);
        assertEquals(6, read.size());
        assertEquals("Data fail", DATA.get(0), read.get(0));
        assertEquals("Data fail", DATA.get(1), read.get(1));
        assertEquals("Data fail", DATA.get(2), read.get(2));
        assertEquals("Data fail", DATA.get(3), read.get(3));
        assertEquals("Data fail", DATA.get(4), read.get(4));
        assertEquals("Data fail", DATA.get(5), read.get(5));
    }

    public String createData() {
        StringBuilder sb = new StringBuilder("foo|bar|baz\n");
        sb.append(DATA.get(0));
        sb.append("|");
        sb.append(DATA.get(1));
        sb.append("|");
        sb.append(DATA.get(2));
        sb.append("\n");
        sb.append(DATA.get(3));
        sb.append("|");
        sb.append(DATA.get(4));
        sb.append("|");
        sb.append(DATA.get(5));
        sb.append("\n");
        return sb.toString();
    }

    private enum Extr implements ResultSetExtractor<List<String>> {
        INSTANCE;
        @Override
        public List<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
            InputStream is = null;
            try {
                is = InputStreamOverResultSet.of(rs);
                DataInputStream ois = new DataInputStream(is);
                List<String> res = Lists.newArrayList();
                for(int i = 0; i < 6; i++) {
                    res.add(ois.readUTF());
                }
                return res;
            } catch(IOException e) {
                throw new RuntimeIOException(e);
            } finally {
                closeQuietly(is);
            }
        }
    }

}
