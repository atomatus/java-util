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

        private static final int TYPE_NULL      = -1;
        private static final int TYPE_MONETARY  =  0;
        private static final int TYPE_NUMERIC   =  1;
        private static final int TYPE_TEXT      =  2;
        private static final int TYPE_URL       =  3;

        //region attr
        private String[] cols, labels;
        private Object[][] rows, table;
        private Boolean hasCols;
        private int maxWidth, width, cellIndex;
        private boolean indexLabels, alignLeft, separator, noWrap;
        //endregion

        /**
         * Construct table.
         */
        public Builder() {
            alignLeft = true;
        }

        //region builder options
        /**
         * Add all columns to current table builder
         * @param cols columns
         * @return current builder
         */
        public Builder columns(String[] cols) {
            this.cols = cols;
            this.width = cols == null ? 0 : cols.length;
            this.hasCols = width > 0;
            return this;
        }

        /**
         * Add new column to current table builder.
         * @param col new column name
         * @return current builder
         */
        public Builder column(String col) {
            this.cols = this.cols == null ?
                    new String[] { col } :
                    ArrayHelper.add(this.cols, col);
            this.width = this.cols.length;
            this.hasCols = true;
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
         * Add labels to display like first column of table.
         * @param labels labels values, identify each row value by column
         * @return current builder.
         */
        public Builder label(String[] labels) {
            if(Objects.requireNonNull(labels).length == 0) {
                throw new IndexOutOfBoundsException();
            }

            this.labels = labels;
            this.indexLabels = false;
            return this;
        }

        /**
         * Enable to display index labels like first column of table.
         * @return current builder.
         */
        public Builder label() {
            indexLabels = true;
            labels = null;
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
         * Enable no wrap line, so when max width set and cell
         * value is largest then it, will be replaced
         * remaining text to "..." (ellipsis).
         * @return current builder.
         */
        public Builder noWrap(){
            this.noWrap = true;
            return this;
        }

        /**
         * Mark current table as containg columns in first line.
         * @return current builder.
         */
        public Builder hasColumns(){
            hasCols = true;
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
        //endregion

        //region validate
        private Double[] validateTableColumnSimilarity(Object[][] table) {
            int len = table.length;
            Double[] sim = new Double[len]; //similarity
            for(int i=0; i < len; i++) {
                String str;
                Object cell;
                Object[] row = table[i];
                Integer[] cTypes = new Integer[row.length];
                for(int j=0, k=row.length; j < k; j++) {
                    cTypes[j] = (cell = row[j]) == null ? TYPE_NULL :
                            RegExp.isValidNumberOnly(str = cell.toString().trim()) ? TYPE_NUMERIC :
                            RegExp.isValidMonetary(str) ? TYPE_MONETARY :
                            RegExp.isValidURL(str) ? TYPE_URL : TYPE_TEXT;
                }
                Integer[] dist = ArrayHelper.distinct(cTypes);
                sim[i] = 1d / dist.length;
            }

            return sim;
        }
        //endregion

        //region getOrMount
        private Object[][] getOrMountTable(){
            if(table == null) {
                int colOffset = cols != null ? 1 : 0;
                int rowsCount = rows != null ? rows.length : 0;

                table = new Object[rowsCount + colOffset][];

                if(rowsCount > 0) {
                    System.arraycopy(rows, 0, table, colOffset, rows.length);
                }

                if(colOffset > 0) {
                    table[0] = cols;
                    hasCols  = true;
                }
            }

            return table;
        }

        private boolean getOrValidateHasCols(){
            if(hasCols == null) {
                Object[][] table = getOrMountTable();
                Double[] sim = validateTableColumnSimilarity(table);
                hasCols = sim.length > 1 && sim[0] == 1d &&
                    ArrayHelper.reduce(sim,
                        (acc, curr) -> curr == 1d ? acc++ : acc,
                        0d) <= (sim.length * .5d);
            }

            return hasCols;
        }

        private String[] getOrMountLabels(){
            if(indexLabels && labels == null) {
                Object[][] table = getOrMountTable();
                int len = table.length;
                if(len > 1) {
                    labels = new String[len];
                    for (int i = 0, c = 0; i < len; i++) {
                        labels[i] = (i == 0 && getOrValidateHasCols()) ? "" : Integer.toString(c++);
                    }
                }
            }
            return labels;
        }
        //endregion

        /**
         * Build a simple text table.
         * @return instance of text table.
         */
        public TextTable build() {
            try {

                return new SimpleTextTable(
                        getOrMountTable(),
                        getOrMountLabels(),
                        maxWidth,
                        getOrValidateHasCols(),
                        alignLeft,
                        separator,
                        noWrap);
            } finally {
                this.rows    = null;
                this.cols    = null;
                this.labels  = null;
                this.table   = null;
                this.hasCols = null;
                this.width   = cellIndex = 0;
            }
        }
    }

    /**
     * Target table.
     */
    protected Object[][] table;

    /**
     * Identify that table contains columns as first line.
     */
    protected final boolean hasCols;

    /**
     * Identify that content must be align to left side.
     */
    protected final boolean isAlignLeft;

    /**
     * Identify that content must by print with separate line.
     */
    protected final boolean isSeparator;

    /**
     * Constructos text table.
     * @param table matrix table.
     * @param hasCols identify table has columns in first line
     * @param alignLeft align content to left.
     * @param separator print separator line for each line.
     */
    protected TextTable(Object[][] table,
                        boolean hasCols,
                        boolean alignLeft,
                        boolean separator) {
        this.table       = table;
        this.hasCols     = hasCols;
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
                           String[] labels,
                           int maxWidth,
                           boolean hasCols,
                           boolean alignLeft,
                           boolean separator,
                           boolean noWrap) {
        super(table, hasCols, alignLeft, separator);
        formatTableAddLabels(labels);
        formatTableMaxWidthCell(maxWidth, noWrap);
        formatTableMaxWidthCellNoWrap(maxWidth, noWrap);
    }

    private void formatTableAddLabels(String[] labels) {
        if(labels != null && table != null && table.length > 0) {
            if(table.length < labels.length) {
                String[] aux = labels;
                labels = new String[table.length];
                System.arraycopy(aux, 0, labels, 0, labels.length);
            } else if(table.length > labels.length) {
                String[] aux = labels;
                labels = new String[table.length];
                System.arraycopy(aux, 0, labels, 0, aux.length);
                for(int i=aux.length, l=labels.length; i < l; i++) {
                    labels[i] = "";
                }
            }

            for(int i=0, l=table.length; i < l; i++) {
                Object[] aux = table[i];
                table[i] = new Object[aux.length + 1];
                System.arraycopy(aux, 0, table[i], 1, aux.length);
                table[i][0] = labels[i];
            }
        }
    }

    private void formatTableMaxWidthCell(int maxWidth, boolean noWrap) {
        if(table == null || maxWidth == 0 || noWrap){
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

    private void formatTableMaxWidthCellNoWrap(int maxWidth, boolean noWrap) {
        if(table == null || maxWidth == 0 || !noWrap) {
            return;
        }

        String ellipsis = "...";
        int diff = maxWidth + ellipsis.length();
        for(int i=0, l=table.length; i < l; i++) {
            for (int j = 0, k = table[i] != null ? table[i].length : 0; j < k; j++) {
                Object cell = table[i][j];
                if(cell instanceof String) {
                    String str = (String) cell;

                    if(str.length() < diff){
                        continue;
                    }

                    str = str.substring(0, diff) + ellipsis;
                    table[i][j] = str;
                } else if(cell instanceof StringBuilder) {
                    StringBuilder sb = (StringBuilder) cell;

                    if(sb.length() < diff) {
                        continue;
                    }

                    sb.delete(diff, sb.length()).append(ellipsis);
                } else if(cell instanceof StringBuffer) {
                    StringBuffer sb = (StringBuffer) cell;

                    if(sb.length() < diff) {
                        continue;
                    }

                    sb.delete(diff, sb.length()).append(ellipsis);
                } else {
                    String str = cell.toString();
                    if(str.length() > diff) {
                        str = str.substring(0, diff) + ellipsis;
                        table[i][j] = str;
                    }
                }
            }
        }

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
        this.rowCounts = null;
    }
}
