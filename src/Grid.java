import java.util.HashSet;
import java.util.Set;

public class Grid {
    private int[][] grid;

    private static final Set<Integer> POSSIBLE_VALUES = Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9);

    public Grid(int[][] grid) {
        this.grid = grid.clone();
    }

    private Set<Integer> getRow(int row) {
        Set<Integer> remainingValues = new HashSet<>(POSSIBLE_VALUES);
        Set<Integer> rowValues = new HashSet<>();

        for (int i = 0; i < 9; i++) {
            if (this.grid[row][i] != 0) {
                rowValues.add(this.grid[row][i]);
            }
        }

        remainingValues.removeAll(rowValues);

        return remainingValues;
    }

    private Set<Integer> getColumn(int column) {
        Set<Integer> remainingValues = new HashSet<>(POSSIBLE_VALUES);
        Set<Integer> columnValues = new HashSet<>();

        for (int i = 0; i < 9; i++) {
            columnValues.add(this.grid[i][column]);
        }

        remainingValues.removeAll(columnValues);

        return remainingValues;
    }

    private Set<Integer> getBlock(int blockRow, int blockColumn) {
        Set<Integer> remainingValues = new HashSet<>(POSSIBLE_VALUES);
        Set<Integer> blockValues = new HashSet<>();

        for (int i = blockRow * 3; i < blockRow * 3 + 3; i++) {
            for (int j = blockColumn * 3; j < blockColumn * 3 + 3; j++) {
                blockValues.add(this.grid[i][j]);
            }
        }

        remainingValues.removeAll(blockValues);

        return remainingValues;
    }

    private boolean predictCell(int row, int column) {
        Set<Integer> rowPossibleValues = this.getRow(row);
        Set<Integer> columnPossibleValues = this.getColumn(column);
        Set<Integer> blockPossibleValues = this.getBlock(row / 3, column / 3);

        Set<Integer> possibleValues = new HashSet<>(rowPossibleValues);
        possibleValues.retainAll(columnPossibleValues);
        possibleValues.retainAll(blockPossibleValues);

        if (possibleValues.size() == 1) {
            int value = possibleValues.iterator().next();
            this.grid[row][column] = value;

            System.out.println("Predicted value " + value + " for cell (" + row + ", " + column + ").");
            return true;
        }

        return false;
    }

    public void solve() {
        boolean predicted;

        do {
            predicted = false;

            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (this.grid[i][j] == 0) {
                        predicted = predictCell(i, j);
                    }
                }
            }
        } while (predicted);
    }
}
