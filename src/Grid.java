import java.util.*;

public class Grid {
    private int[][] grid;

    private HashSet<Integer>[][] knowledgeBase;

    private static final Set<Integer> POSSIBLE_VALUES = Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9);

    private ArrayList<String> log;

    private int blankCellsCount;

    public Grid(int[][] grid) {
        this.log = new ArrayList<>();

        this.blankCellsCount = 9 * 9;

        this.grid = grid.clone();

        this.knowledgeBase = new HashSet[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j] == 0) {
                    knowledgeBase[i][j] = getPossibleValues(i, j);
                } else {
                    blankCellsCount--;

                    knowledgeBase[i][j] = new HashSet<>();
                    knowledgeBase[i][j].add(grid[i][j]);
                }

//                if (grid[i][j] != 0) {
//                    blankCellsCount--;
//                }
            }
        }
    }

    public void printGrid() {
        for (int i = 0; i < 9; i++) {
            if (i % 3 == 0 && i != 0) {
                System.out.println("------+-------+------");
            }
            for (int j = 0; j < 9; j++) {
                if (j % 3 == 0 && j != 0) {
                    System.out.print("| ");
                }

                if (grid[i][j] == 0) {
                    System.out.print("  ");
                } else {
                    System.out.print(grid[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

    public ArrayList<String> getLog() {
        return log;
    }

    private HashSet<Integer> getRow(int row) {
        HashSet<Integer> remainingValues = new HashSet<>(POSSIBLE_VALUES);
        HashSet<Integer> rowValues = new HashSet<>();

        for (int i = 0; i < 9; i++) {
            if (this.grid[row][i] != 0) {
                rowValues.add(this.grid[row][i]);
            }
        }

        remainingValues.removeAll(rowValues);

        return remainingValues;
    }

    private HashSet<Integer> getColumn(int column) {
        HashSet<Integer> remainingValues = new HashSet<>(POSSIBLE_VALUES);
        HashSet<Integer> columnValues = new HashSet<>();

        for (int i = 0; i < 9; i++) {
            columnValues.add(this.grid[i][column]);
        }

        remainingValues.removeAll(columnValues);

        return remainingValues;
    }

    private HashSet<Integer> getBlock(int blockRow, int blockColumn) {
        HashSet<Integer> remainingValues = new HashSet<>(POSSIBLE_VALUES);
        HashSet<Integer> blockValues = new HashSet<>();

        for (int i = blockRow * 3; i < blockRow * 3 + 3; i++) {
            for (int j = blockColumn * 3; j < blockColumn * 3 + 3; j++) {
                blockValues.add(this.grid[i][j]);
            }
        }

        remainingValues.removeAll(blockValues);

        return remainingValues;
    }

    private HashSet<Integer> getPossibleValues(int row, int col) {
        HashSet<Integer> possibleValues = new HashSet<>();

        if (grid[row][col] != 0) {
            possibleValues.add(grid[row][col]);
            return possibleValues;
        }

        HashSet<Integer> rowPossibleValues = this.getRow(row);
        HashSet<Integer> columnPossibleValues = this.getColumn(col);
        HashSet<Integer> blockPossibleValues = this.getBlock(row / 3, col / 3);

        possibleValues.addAll(rowPossibleValues);
        possibleValues.retainAll(columnPossibleValues);
        possibleValues.retainAll(blockPossibleValues);

        return possibleValues;
    }

    private void updateKnowledgeBaseFor(int row, int col) {
        knowledgeBase[row][col] = new HashSet<>();
        knowledgeBase[row][col].add(grid[row][col]);

        for (int i = 0; i < 9; i++) {
            if (i != col) {
                knowledgeBase[row][i].remove(grid[row][col]);
            }

            if (i != row) {
                knowledgeBase[i][col].remove(grid[row][col]);
            }
        }

        int blockRow = (row / 3) * 3;
        int blockCol = (col / 3) * 3;

        for (int i = blockRow; i < blockRow + 3; i++) {
            for (int j = blockCol; j < blockCol + 3; j++) {
                if (!(i == row && j == col)) {
                    knowledgeBase[i][j].remove(grid[row][col]);
                }
            }
        }
    }

    private void setCell(int row, int col, int value) {
        grid[row][col] = value;
        blankCellsCount--;

        log.add(value + " -> (" + row + ", " + col + ")");

        updateKnowledgeBaseFor(row, col);
    }

    private boolean checkDuplicates(int type, int row, int col) {
        Map<HashSet<Integer>, Integer> uniqueSetsCount = new HashMap<>();

        if (type == 0) {        // Row

            for (int i = 0; i < 9; i++) {
                uniqueSetsCount.put(knowledgeBase[row][i], uniqueSetsCount.getOrDefault(knowledgeBase[row][i], 0) + 1);
            }

        } else if (type == 1) { // Col

            for (int i = 0; i < 9; i++) {
                uniqueSetsCount.put(knowledgeBase[i][col], uniqueSetsCount.getOrDefault(knowledgeBase[i][col], 0) + 1);
            }

        } else {            // Block

            int blockRow = (row / 3) * 3;
            int blockCol = (col / 3) * 3;

            for (int i = blockRow; i < blockRow + 3; i++) {
                for (int j = blockCol; j < blockCol + 3; j++) {
                    uniqueSetsCount.put(knowledgeBase[i][j], uniqueSetsCount.getOrDefault(knowledgeBase[i][j], 0) + 1);
                }
            }
        }

        ArrayList<HashSet<Integer>> duplicates = new ArrayList<>();

        for (Map.Entry<HashSet<Integer>, Integer> entry : uniqueSetsCount.entrySet()) {
            if ((entry.getKey().size() > 1) && (entry.getValue() == entry.getKey().size())) {
                duplicates.add(entry.getKey());
            }
        }

        for (HashSet<Integer> duplicate : duplicates) {
            if (type == 0) {
                for (int i = 0; i < 9; i++) {
                    if (!knowledgeBase[row][i].equals(duplicate)) {
                        knowledgeBase[row][i].removeAll(duplicate);
                    }
                }
            } else if (type == 1) {
                for (int i = 0; i < 9; i++) {
                    if (!knowledgeBase[i][col].equals(duplicate)) {
                        knowledgeBase[i][col].removeAll(duplicate);
                    }
                }
            } else {
                int blockRow = (row / 3) * 3;
                int blockCol = (col / 3) * 3;

                for (int i = blockRow; i < blockRow + 3; i++) {
                    for (int j = blockCol; j < blockCol + 3; j++) {
                        if (!knowledgeBase[i][j].equals(duplicate)) {
                            knowledgeBase[i][j].removeAll(duplicate);
                        }
                    }
                }
            }
        }

        return !duplicates.isEmpty();
    }

    private boolean checkUniqueness(int row, int col) {
        HashSet<Integer> currentValues = new HashSet<>(knowledgeBase[row][col]);
        HashSet<Integer> otherValues = new HashSet<>();

        for (int i = 0; i < 9; i++) {
            if (i != col) {
                otherValues.addAll(knowledgeBase[row][i]);
            }

            if (i != row) {
                otherValues.addAll(knowledgeBase[i][col]);
            }
        }

        int blockRow = (row / 3) * 3;
        int blockCol = (col / 3) * 3;

        for (int i = blockRow; i < blockRow + 3; i++) {
            for (int j = blockCol; j < blockCol + 3; j++) {
                if (!(i == row && j == col)) {
                    otherValues.addAll(knowledgeBase[i][j]);
                }
            }
        }

        currentValues.removeAll(otherValues);

        if (currentValues.size() > 1) {
            for (int i = 0; i < 9; i++) {
                if (i != col) {
                    knowledgeBase[row][i].removeAll(currentValues);
                }

                if (i != row) {
                    knowledgeBase[i][col].removeAll(currentValues);
                }
            }

            for (int i = blockRow; i < blockRow + 3; i++) {
                for (int j = blockCol; j < blockCol + 3; j++) {
                    if (!(i == row && j == col)) {
                        knowledgeBase[i][j].removeAll(currentValues);
                    }
                }
            }

            this.knowledgeBase[row][col] = currentValues;

            return true;
        } else {
            return false;
        }
    }

    private boolean inferBy(int type, int row, int col) {
        checkDuplicates(type, row, col);

        HashSet<Integer> cellValues = new HashSet<>(knowledgeBase[row][col]);

        if (cellValues.size() == 1) {          // Infer By Cell
            setCell(row, col, cellValues.iterator().next());
            return true;
        } else {

            if (type == 0) {                // Infer By Row

                for (int i = 0; i < 9; i++) {
                    if (i != col) {
                        HashSet<Integer> values = new HashSet<>(knowledgeBase[row][i]);
                        cellValues.removeAll(values);

                        if (cellValues.isEmpty()) {
                            break;
                        }
                    }
                }

                if (cellValues.size() == 1) {
                    setCell(row, col, cellValues.iterator().next());
                    return true;
                }

            } else if (type == 1) {                // Infer By Col

                for (int i = 0; i < 9; i++) {
                    if (i != row) {
                        HashSet<Integer> values = new HashSet<>(knowledgeBase[i][col]);
                        cellValues.removeAll(values);

                        if (cellValues.isEmpty()) {
                            break;
                        }
                    }
                }

                if (cellValues.size() == 1) {
                    setCell(row, col, cellValues.iterator().next());
                    return true;
                }

            } else {                               // Infer By Block

                int blockRow = (row / 3) * 3;
                int blockCol = (col / 3) * 3;

                outerLoop:
                for (int i = blockRow; i < blockRow + 3; i++) {
                    for (int j = blockCol; j < blockCol + 3; j++) {
                        if (!(i == row && j == col)) {
                            HashSet<Integer> values = new HashSet<>(knowledgeBase[i][j]);
                            cellValues.removeAll(values);

                            if (cellValues.isEmpty()) {
                                break outerLoop;
                            }
                        }
                    }
                }

                if (cellValues.size() == 1) {
                    setCell(row, col, cellValues.iterator().next());
                    return true;
                }
            }
        }

        return false;
    }

    public void solve() {
        boolean predicted;
        int turnsCount = 0;

        while(blankCellsCount > 0) {
            predicted = false;
            turnsCount++;

            outerLoop:
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (this.grid[i][j] == 0) {
                        checkUniqueness(i, j);
                        for (int k = 0; k < 3; k++) {
//                            predicted = checkDuplicates(k, i, j) || predicted;
                            if (inferBy(k, i, j)){
                                break;
                            }

//                            if (predicted) {
//                                break outerLoop;
//                            }
                        }
                    }
                }
            }
        }
    }
}
