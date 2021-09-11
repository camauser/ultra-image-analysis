package ca.camauser.imageanalysis.filling;

import java.util.Objects;

public class Pixel {
    private final int row;
    private final int column;

    public Pixel(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pixel pixel = (Pixel) o;
        return row == pixel.row && column == pixel.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}
