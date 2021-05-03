package com.atomatus.util;

import java.io.Closeable;
import java.util.*;

/**
 * Printable text table.
 */
public abstract class TextTable implements Closeable {

    /**
     * Text Table Builder
     */
    public static class Builder {

        private String[] cols;
        private Object[][] rows, table;
        private int maxWidth, width, cellIndex;
        private boolean alignLeft, separator;

        public Builder() {
            alignLeft = true;
        }

        /**
         * Add all columns to current table builder
         * @param cols columns
         * @return current builder
         */
        public Builder columns(String[] cols) {
            this.cols = cols;
            this.width = cols == null ? 0 : cols.length;
            return this;
        }

        /**
         * Add new column to current table builder.
         * @param col new column name
         * @return current builder
         */
        public Builder column(String col) {
            cols = cols == null ?
                    new String[] { col } :
                    ArrayHelper.add(cols, col);
            width = cols.length;
            return this;
        }

        /**
         * Add all rows to current table builder.
         * @param rows rows
         * @return current builder
         */
        public Builder rows(Object[][] rows) {
            if(Objects.requireNonNull(rows).length > 0) {
                int currMaxLen = 0;
                for (Object[] row : rows) {
                    int len = Objects.requireNonNull(row).length;
                    if(currMaxLen == 0) {
                        currMaxLen = len;
                        if(width > 0 && width != currMaxLen){
                            throw new IndexOutOfBoundsException("Rows and columns" +
                                    "have different lenghts!");
                        }
                    } else if(currMaxLen != len) {
                        throw new ArrayIndexOutOfBoundsException("Current matrix (rows) contain " +
                                "arrays (row) with different lengths!");
                    }
                }
            }

            this.rows = rows;
            return this;
        }

        /**
         * Add a new row to current table
         * @param row target row
         * @return current builder
         */
        public Builder row(Object[] row) {
            Objects.requireNonNull(row);
            if(width > 0 && width != row.length) {
                throw new IndexOutOfBoundsException("Rows and columns" +
                        "have different lenghts!");
            }

            if(this.rows == null) {
                this.rows = new Object[1][];
                this.rows[0] = row;
            } else {
                Object[][] aux = new Object[this.rows.length + 1][];
                System.arraycopy(rows, 0, aux, 0, rows.length);
                aux[rows.length] = row;
                this.rows = aux;
            }

            this.cellIndex = 0;
            return this;
        }

        /**
         * Add a new row to current table
         * @param len row width
         * @return current builder
         */
        public Builder row(int len) {
            if(len <= 0){
                throw new IndexOutOfBoundsException();
            } else if(width > 0 && width != len) {
                throw new IndexOutOfBoundsException("Rows and columns" +
                        "have different lenghts!");
            }

            return row(new Object[len]);
        }

        /**
         * Add a new row to current table.
         * @return current builder
         */
        public Builder row() {
            return row(width);
        }

        /**
         * Add cell content to current row.
         * @param cel cell content
         * @return current builder
         */
        public Builder cell(Object cel) {
            if(this.rows == null) {
                throw new NullPointerException("No one row defined yet!");
            } else if(cellIndex == width) {
                throw new NullPointerException("End of row! Set a new row before request it!");
            }

            int index = this.rows.length - 1;
            Object[] row = this.rows[index];
            if(row == null || row.length == 0) {
                row = new Object[width];
                this.rows[index] = row;
            }

            this.rows[index][cellIndex++] = cel;
            return this;
        }

        /**
         * Load full table content
         * @param table content
         * @return current builder
         */
        public Builder from(Object[][] table) {
            this.table = Objects.requireNonNull(table);
            return this;
        }

        /**
         * Max allowed line width.
         * @param maxWidth vany positive value.
         * @return current builder.
         */
        public Builder maxWidth(int maxWidth) {
            if(maxWidth <= 0) throw new IndexOutOfBoundsException();
            this.maxWidth = maxWidth;
            return this;
        }

        /**
         * Align printable cells to left side.
         * @return current builder
         */
        public Builder alignLeft() {
            alignLeft = true;
            return this;
        }

        /**
         * Align printable cells to right side.
         * @return current builder
         */
        public Builder alignRight() {
            alignLeft = false;
            return this;
        }

        /**
         * Print line separator after each row.
         * @return current builder
         */
        public Builder lineSeparator(){
            this.separator = true;
            return this;
        }

        /**
         * Build a simple text table.
         * @return instance of text table.
         */
        public TextTable build() {
            try {
                if(table == null) {
                    table = new Object[this.rows.length + 1][];
                    System.arraycopy(rows, 0, table, 1, rows.length);
                    table[0] = cols;
                }
                return new SimpleTextTable(
                        table,
                        maxWidth,
                        alignLeft,
                        separator);
            } finally {
                this.rows  = null;
                this.cols  = null;
                this.table = null;
                this.width = cellIndex = 0;
            }
        }
    }

    protected Object[][] table;
    protected final int maxWidth;
    protected final boolean isAlignLeft;
    protected final boolean isSeparator;

    protected TextTable(Object[][] table,
                        int maxWidth,
                        boolean alignLeft,
                        boolean separator) {
        this.table       = table;
        this.maxWidth    = maxWidth;
        this.isAlignLeft = alignLeft;
        this.isSeparator = separator;
    }

    /**
     * Print table content.
     */
    public abstract void print();

    /**
     * Clear and dispose text table.
     */
    @Override
    public void close() {
        this.table = null;
    }
}

final class SimpleTextTable extends TextTable {

    private String separator, format;
    private Map<Integer, Integer> columnLengths;
    private Map<Integer, Integer> rowCounts;

    public SimpleTextTable(Object[][] table,
                           int maxWidth,
                           boolean alignLeft,
                           boolean separator) {
        super(table, maxWidth, alignLeft, separator);
        formatTableMaxWidthCell();
    }

    private void formatTableMaxWidthCell() {
        if(maxWidth == 0){
            return;
        }

        rowCounts = new HashMap<>();
        List<Object[]> finalTableList = new ArrayList<>();
        for (int j=0, c = table.length; j < c; j++) {
            Object[] row = table[j];
            // If any cell data is more than max width, then it will need extra row.
            boolean needExtraRow;
            // Count of extra split row.
            int splitRow = 0;
            do {
                needExtraRow = false;
                String[] newRow = new String[row.length];
                for (int i = 0; i < row.length; i++) {
                    String cell = row[i] == null ? "" : row[i].toString();
                    int len = cell.length();

                    // If data is less than max width, use that as it is.
                    if (len < maxWidth) {
                        newRow[i] = splitRow == 0 ? cell : "";
                    } else if ((len > (splitRow * maxWidth))) {
                        // If data is more than max width, then crop data at maxwidth.
                        // Remaining cropped data will be part of next row.
                        int end = Math.min(len, ((splitRow * maxWidth) + maxWidth));
                        newRow[i] = cell.substring((splitRow * maxWidth), end);
                        needExtraRow = true;
                    } else {
                        newRow[i] = "";
                    }
                }

                finalTableList.add(newRow);
                if (needExtraRow) {
                    splitRow++;
                }
            } while (needExtraRow);
            rowCounts.put(j, splitRow + 1);
        }

        Object[][] finalTable = new String[finalTableList.size()][finalTableList.get(0).length];
        for (int i = 0; i < finalTable.length; i++) {
            finalTable[i] = finalTableList.get(i);
        }

        this.table = finalTable;
    }

    private Map<Integer, Integer> getColumnLengths() {
        if(columnLengths == null) {
            columnLengths = new HashMap<>();
            for (Object[] row : table) {
                for (int c = 0, cl = row.length; c < cl; c++) {
                    Object cel = row[c];
                    int len = cel == null ? 0 : cel.toString().length();
                    Integer curr = columnLengths.get(c);
                    if (curr == null || curr < len) {
                        columnLengths.put(c, len);
                    }
                }
            }
        }
        return columnLengths;
    }

    private String getFormatted(){
        if(this.format == null) {
            StringBuilder sb = new StringBuilder();
            String flag = isAlignLeft ? "-" : "";
            for (Map.Entry<Integer, Integer> entry : getColumnLengths().entrySet()) {
                sb.append("| %")
                  .append(flag)
                  .append(entry.getValue())
                  .append("s ");
            }
            format = sb.append("|\n").toString();
        }
        return this.format;
    }

    private String getLineSeparator() {
        if(this.separator == null) {
            StringBuilder ln = new StringBuilder();
            for (Map.Entry<Integer, Integer> entry : getColumnLengths().entrySet()) {
                char[] sep = new char[entry.getValue()];
                Arrays.fill(sep, '-');
                ln.append("+-").append(sep).append('-');
            }
            separator = ln.append("+\n").toString();
        }
        return separator;
    }

    @Override
    public void print() {
        if (table == null || table.length == 0) {
            return;
        }

        System.out.print(getLineSeparator());
        Queue<Integer> rowQueue =
                rowCounts == null ? null :
                        new PriorityQueue<>(rowCounts.values());
        String format = getFormatted();
        for (int i = 0, l = table.length, rc = 0; i < l; i++) {
            rc = rc > 0 ? rc : rowQueue == null ? 1 : rowQueue.remove();
            Object[] row = table[i];
            System.out.printf(format, row);
            if (--rc == 0 && (i == 0 || isSeparator)) {
                System.out.print(getLineSeparator());
            }
        }

        if(table.length > 1 && !isSeparator) {
            System.out.print(getLineSeparator());
        }
    }

    @Override
    public void close() {
        super.close();
        this.format = null;
        this.columnLengths = null;
    }
}
