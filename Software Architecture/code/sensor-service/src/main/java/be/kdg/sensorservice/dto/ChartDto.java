package be.kdg.sensorservice.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a Dto for a data table used by the google charts api to generate the linechart.
 */
public class ChartDto {
    private List<Column> cols;
    private List<Row> rows;

    public List<Column> getCols() {
        return cols;
    }

    public List<Row> getRows() {
        return rows;
    }

    /**
     * A Column describes the characteristics of one column in the chart-table.
     */
    class Column {
        private char id;
        private String label;
        private String type;
        private String role;

        Column(char id, String label, String type) {
            this.id = id;
            this.label = label;
            this.type = type;
        }

        public void addRole(String role){
            this.role=role;
        }

        public char getId() {
            return id;
        }

        public String getLabel() {
            return label;
        }

        public String getType() {
            return type;
        }

        public String getRole() {
            return role;
        }
    }

    /**
     * A row is a collection of cells in a chart-table, every column needs one cell per row.
     */
    class Row {
        private List<Cell> c;

        Row(List<Cell> c) {
            this.c = c;
        }

        public List<Cell> getC() {
            return c;
        }
    }

    /**
     * A cell can hold a value of different datatypes
     */
    interface Cell {
    }

    class NumberCell implements Cell {
        private double v;

        public NumberCell(double v) {
            this.v = v;
        }

        public double getV() {
            return v;
        }
    }

    class StringCell implements Cell {
        private String v;

        public StringCell(String v) {
            this.v = v;
        }

        public String getV() {
            return v;
        }
    }

    ChartDto() {
        cols = new ArrayList<>();
        rows = new ArrayList<>();
    }

    public void addRow(List<Cell> cells) {
        rows.add(new Row(cells));
    }
}
