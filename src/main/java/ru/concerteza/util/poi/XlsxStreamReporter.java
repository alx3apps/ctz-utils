package ru.concerteza.util.poi;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.UnhandledException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.SheetUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import ru.concerteza.util.collection.SingleUseIterable;
import ru.concerteza.util.io.RuntimeIOException;

import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.AttributedString;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.poi.ss.usermodel.Cell.*;
import static ru.concerteza.util.reflect.CtzReflectionUtils.findGetter;
import static ru.concerteza.util.reflect.CtzReflectionUtils.invokeMethod;
import static ru.concerteza.util.reflect.CtzReflectionUtils.isAssignableBoxed;
import static ru.concerteza.util.string.CtzFormatUtils.format;

/**
 * Wrapper over Apache POI SXSSF library for streaming generation of simple (and large) XLSX files.
 * Every row is read from provided object through getters. Column types are based on getter return types.
 * Only {@code String}, {@code boolean}, {@code Number}, {@code LocalDate} and {@code LocalDateTime} are supported.
 * Result XLSX has one named sheet with bold headers. Column width may be auto-sized
 * over content and headers or over headers only.
 * Instances of this class are immutable and thread-safe.
 *
 * @author alexey
 * Date: 10/8/12
 */
public class XlsxStreamReporter<T> {
    // see org.apache.poi.ss.util.SheetUtil
    private static final char DEFAULT_CHAR = '0';
    private static final FontRenderContext FONT_RENDER_CONTEXT = new FontRenderContext(null, true, true);

    private enum CellType {STRING, NUMERIC, BOOLEAN, DATE, DATETIME}
    private final String sheetName;
    private final ImmutableList<Column> cols;
    private final String dateFormatString;
    private final String dateTimeFormatString;
    private final String fontName;
    private final short fontSize;
    private final boolean useContentWidth;

    private XlsxStreamReporter(String sheetName, ImmutableList<Column> cols, String fontName, short fontSize, String dateFormatString, String dateTimeFormatString, boolean useContentWidth) {
        this.sheetName = sheetName;
        this.cols = cols;
        this.dateFormatString = dateFormatString;
        this.dateTimeFormatString = dateTimeFormatString;
        this.fontName = fontName;
        this.fontSize = fontSize;
        this.useContentWidth = useContentWidth;
    }

    /**
     * Creates builder for this reporter
     *
     * @param sheetName name of sheet
     * @param clazz row class
     * @param <T> type of row class
     * @return reporter instance
     */
    public static <T> Builder<T> builder(String sheetName, Class<T> clazz) {
        return new Builder<T>(sheetName, clazz);
    }

    /**
     * Writes all data from iterator to output stream as XLSX file
     *
     * @param data row data
     * @param out output stream
     */
    public void write(Iterator<T> data, OutputStream out) {
        // workbook
        Workbook wb = new SXSSFWorkbook();
        Sheet sh = wb.createSheet();
        wb.setSheetName(0, sheetName);
        // styles
        CellStyle headStyle = createStyle(wb, true);
        CellStyle mainStyle = createStyle(wb, false);
        CellStyle dateStyle = createStyle(wb, false);
        dateStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat(dateFormatString));
        CellStyle dateTimeStyle = createStyle(wb, false);
        dateTimeStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat(dateTimeFormatString));
        DataFormatter df = new DataFormatter();
        int[] maxColumnWidths = new int[cols.size()];
        Font mainFont = wb.getFontAt(mainStyle.getFontIndex());
        // headers
        Row headRow = sh.createRow(0);
        for(int i = 0; i < cols.size(); i++) {
            Column col = cols.get(i);
            Cell cell = headRow.createCell(i, CELL_TYPE_STRING);
            cell.setCellStyle(headStyle);
            cell.setCellValue(col.description);
            if(useContentWidth) maxColumnWidths[i] = cellWidth(cell, wb.getFontAt(headStyle.getFontIndex()), df);
        }
        // column widths based on headers, this won't work on whole sheet because of streaming
        for(int i = 0; i < cols.size(); i++) {
            sh.autoSizeColumn(i);
        }
        // data
        int rownum = 1;
        for (T t : SingleUseIterable.of(data)) {
            Row row = sh.createRow(rownum++);
            row.setRowStyle(mainStyle);
            for(int i = 0; i < cols.size(); i++) {
                Column col = cols.get(i);
                Cell cell = createCell(t, col.getter, row, i, col.type, mainStyle, dateStyle, dateTimeStyle);
                if(useContentWidth) {
                    int width = cellWidth(cell, mainFont, df);
                    if(maxColumnWidths[i] < width) maxColumnWidths[i] = width;
                }
            }
        }
        // column widths based on content
        if(useContentWidth) {
            for(int i = 0; i < cols.size(); i++) {
                sh.autoSizeColumn(maxColumnWidths[i]);
            }
        }
        // write
        writeWorkbook(wb, out);
    }

    private void writeWorkbook(Workbook wb, OutputStream out) {
        try {
            wb.write(out);
        } catch(IOException e) {
            throw new XlsxStreamReporterException(e);
        }
    }

    private CellStyle createStyle(Workbook wb, boolean head) {
        Font fo = wb.createFont();
        fo.setFontName(fontName);
        fo.setFontHeightInPoints(fontSize);
        if(head) fo.setBoldweight(Font.BOLDWEIGHT_BOLD);
        CellStyle st = wb.createCellStyle();
        st.setFont(fo);
        return st;
    }

    private Cell createCell(Object rowObject, Method getter, Row row, int column, CellType type, CellStyle mainStyle, CellStyle dateFormat, CellStyle dateTimeFormat) {
        Object optValue = invokeMethod(rowObject, getter);
        final Object value;
        if(optValue instanceof Optional) {
            Optional<?> opt = (Optional<?>) optValue;
            if(!opt.isPresent()) {
                Cell cell = row.createCell(column, CELL_TYPE_BLANK);
                cell.setCellStyle(mainStyle);
                return cell;
            } else {
                value = opt.get();
            }
        } else value = optValue;
        final Cell cell;
        if(CellType.NUMERIC.equals(type)) {
            cell = row.createCell(column, CELL_TYPE_NUMERIC);
            Number nval = (Number) value;
            cell.setCellStyle(mainStyle);
            cell.setCellValue(nval.doubleValue());
        } else if(CellType.BOOLEAN.equals(type)) {
            cell = row.createCell(column, CELL_TYPE_BOOLEAN);
            boolean bval = (Boolean) value;
            cell.setCellStyle(mainStyle);
            cell.setCellValue(bval);
        } else if(CellType.DATE.equals(type)) {
            cell = row.createCell(column, CELL_TYPE_NUMERIC);
            cell.setCellStyle(dateFormat);
            LocalDate ldval = (LocalDate) value;
            cell.setCellValue(ldval.toDate());
        } else if(CellType.DATETIME.equals(type)) {
            cell = row.createCell(column, CELL_TYPE_NUMERIC);
            cell.setCellStyle(dateTimeFormat);
            LocalDateTime ldtval = (LocalDateTime) value;
            cell.setCellValue(ldtval.toDate());
        } else {
            cell = row.createCell(column, CELL_TYPE_STRING);
            cell.setCellStyle(mainStyle);
            String sval = (String) value;
            cell.setCellValue(sval);
        }
        return cell;
    }

    // see org.apache.poi.ss.util.SheetUtil
    private static int cellWidth(Cell cell, Font font, DataFormatter df) {
        AttributedString str = new AttributedString(String.valueOf(DEFAULT_CHAR));
        copyAttributes(font, str, 0, 1);
        TextLayout layout = new TextLayout(str.getIterator(), FONT_RENDER_CONTEXT);
        int defaultCharWidth = (int) layout.getAdvance();
        return (int) SheetUtil.getCellWidth(cell, defaultCharWidth, df, false);
    }

    // see org.apache.poi.ss.util.SheetUtil
    private static void copyAttributes(Font font, AttributedString str, int startIdx, int endIdx) {
        str.addAttribute(TextAttribute.FAMILY, font.getFontName(), startIdx, endIdx);
        str.addAttribute(TextAttribute.SIZE, (float) font.getFontHeightInPoints());
        if(font.getBoldweight() == Font.BOLDWEIGHT_BOLD) str.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, startIdx, endIdx);
        if(font.getItalic()) str.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE, startIdx, endIdx);
        if(font.getUnderline() == Font.U_SINGLE) str.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, startIdx, endIdx);
    }


    /**
     * Builder class for reporter
     *
     * @param <T> row object type
     */
    public static class Builder<T> {
        private final String sheetName;
        private final Class<T> rowClass;
        private final ImmutableList.Builder<Column> cols = ImmutableList.builder();

        private String dateFormat = "yyyy-MM-dd";
        private String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
        private String fontName = XSSFFont.DEFAULT_FONT_NAME;
        private short fontSize = XSSFFont.DEFAULT_FONT_SIZE;
        private boolean useContentWidth = false;

        /**
         * @param sheetName name of sheet
         * @param rowClass row class
         */
        public Builder(String sheetName, Class<T> rowClass) {
            checkNotNull(rowClass, "Provided class is null");
            checkArgument(isNotBlank(sheetName), "Provided sheetName is blank");
            this.sheetName = sheetName;
            this.rowClass = rowClass;
        }

        /**
         * Registers column
         *
         * @param name row object bean property name
         * @param description column header
         * @return builder itself
         */
        public Builder addColumn(String name, String description) {
            checkArgument(isNotBlank(name), "Provided name is blank");
            checkArgument(isNotBlank(description), "Provided description is blank");
            Method getter = findGetter(rowClass, name);
            Class<?> rt = getter.getReturnType();
            final CellType type;
            if(isAssignableBoxed(Number.class, rt)) type = CellType.NUMERIC;
            else if(isAssignableBoxed(Boolean.class, rt)) type = CellType.BOOLEAN;
            else if(LocalDate.class.isAssignableFrom(rt)) type = CellType.DATE;
            else if(LocalDateTime.class.isAssignableFrom(rt)) type = CellType.DATETIME;
            else type = CellType.STRING;
            cols.add(new Column(description, type, getter));
            return this;
        }

        /**
         * Register nullable column with {@code Optional} support
         *
         * @param name row object bean property name
         * @param clazz value class
         * @param description column header
         * @return builder itself
         */
        public Builder addColumn(String name, Class<?> clazz, String description) {
            checkArgument(isNotBlank(name), "Provided name is blank");
            checkArgument(null != clazz, "Provided class is null");
            checkArgument(isNotBlank(description), "Provided description is blank");
            Method getter = findGetter(rowClass, name);
            final CellType type;
            if(isAssignableBoxed(Number.class, clazz)) type = CellType.NUMERIC;
            else if(isAssignableBoxed(Boolean.class, clazz)) type = CellType.BOOLEAN;
            else if(LocalDate.class.isAssignableFrom(clazz)) type = CellType.DATE;
            else if(LocalDateTime.class.isAssignableFrom(clazz)) type = CellType.DATETIME;
            else type = CellType.STRING;
            cols.add(new Column(description, type, getter));
            return this;
        }


        /**
         * @param dateFormat format for {@code LocalDate} properties, default: {@code "yyyy-MM-dd"}
         * @return builder itself
         */
        public Builder setDateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
            return this;
        }

        /**
         * @param dateTimeFormat format for {@code LocalDateTime} properties, default: {@code "yyyy-MM-dd HH:mm:ss"}
         * @return builder itself
         */
        public Builder setDateTimeFormat(String dateTimeFormat) {
            this.dateTimeFormat = dateTimeFormat;
            return this;
        }

        /**
         * @param fontName font name, default: {@code "Calibri"}
         * @return builder itself
         */
        public Builder setFontName(String fontName) {
            this.fontName = fontName;
            return this;
        }

        /**
         * @param fontSize in pt, default: {@code 11}
         * @return builder itself
         */
        public Builder setFontSize(int fontSize) {
            this.fontSize = (short) fontSize;
            return this;
        }

        /**
         * With this option enabled every cell will be checked for width after creation
         * and columns widths will be adjusted for max cell size. By default only header
         * row is checked for column widths.
         *
         * @return builder itself
         */
        public Builder enableContentWidth() {
            this.useContentWidth = true;
            return this;
        }

        /**
         * @return reporter instance
         */
        public XlsxStreamReporter<T> build() {
            return new XlsxStreamReporter<T>(sheetName, cols.build(), fontName, fontSize, dateFormat, dateTimeFormat, useContentWidth);
        }
    }

    private static class Column {
        private final String description;
        private final CellType type;
        private final Method getter;

        private Column(String description, CellType type, Method getter) {
            this.description = description;
            this.type = type;
            this.getter = getter;
        }
    }
}
