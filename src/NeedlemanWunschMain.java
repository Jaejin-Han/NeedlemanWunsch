import java.util.ArrayDeque;

public class NeedlemanWunschMain {

    public static void main(String[] args) {
        argString1 = args[0];
        argString2 = args[1];

        initialize(argString1, argString2);
        fillNwMatrix();
        printNWM();
        traceback();

    }

    // some permanent constants
    public static final int GAP_SCORE = -2;
    public static final String SCORE_MATRIX_COL_AND_ROW_NAMES = "CTAG";
    public static final int[][] SCORE_MATRIX =  {{2,1,-1,-1}, // the top left 4 entries and bottom right 4 entries are
                                                 {1,2,-1,-1}, // positive values whereas the other 8 are negative. This
                                                 {-1,-1,2,1}, // indicates that transversion gives a score of -1 and
                                                 {-1,-1,1,2}}; // transition gives 1. Matching gives 2.

    // some constants to be defined at runtime
    public static String argString1;
    public static String argString2;
    public static NWCell[][] nwm; // The Needleman-Wunsch score and traceback matrix


    // Create empty NW matrix given two Strings
    public static void initialize(String a, String b) {
        nwm = new NWCell[a.length() + 1][b.length() + 1];
    }

    // Fill NW matrix matrix
    public static void fillNwMatrix() {
        for (int i = 0; i < nwm.length; i++) {
            for (int j = 0; j < nwm[0].length; j++) {
                nwm[i][j] = getNWCell(i,j);
            }
        }
    }

    // Calculate the score and traceback direction for the cell using a recurrence relation
    public static NWCell getNWCell(int i , int j) {
        // get the three relevant adjacent cells to the current cell according to the NW algo
        NWCell up = (i - 1 < 0)? null: nwm[i-1][j];
        NWCell left = (j - 1 < 0)? null: nwm[i][j-1];
        NWCell diag = (j - 1 < 0 || i - 1 < 0)? null: nwm[i-1][j-1];
        NWCell max = new NWCell(Integer.MIN_VALUE, Direction.DONE);

        // determine the max of the three cells and assign direction and score to the current cell accordingly
        if (diag != null) {
            // charAt(i - 1) because the index in nwm for a certain letter is one over since the col/row names start with a gap
            max = new NWCell(diag.num + scoreMatrix(argString1.charAt(i-1), argString2.charAt(j-1)), Direction.DIAG);
        }
        if (up != null && up.num + GAP_SCORE > max.num) {
            max = new NWCell(up.num + GAP_SCORE, Direction.UP);
        }
        if (left != null && left.num + GAP_SCORE > max.num) {
            max = new NWCell(left.num + GAP_SCORE, Direction.LEFT);
        }

        if (max.cellPointer == Direction.DONE) {
            max.num = 0;
        }
        return max;
    }

    private static Integer scoreMatrix(char a, char b) {
        // should make sense if you look at the value of the String and matrix used here
        int i = SCORE_MATRIX_COL_AND_ROW_NAMES.indexOf(a);
        int j = SCORE_MATRIX_COL_AND_ROW_NAMES.indexOf(b);
        return SCORE_MATRIX[i][j];
    }

    /**
     * Perform traceback
     */
    public static void traceback() {
        ArrayDeque<Character> stack1 = new ArrayDeque<>();
        ArrayDeque<Character> stack2 = new ArrayDeque<>();

        int i = nwm.length - 1;
        int j = nwm[0].length - 1;
        NWCell currCell = nwm[i][j]; //bottom right cell
        int score = currCell.num;

        while (currCell.cellPointer != Direction.DONE) {
            switch (currCell.cellPointer) {
                case DIAG:
                    stack1.push(argString1.charAt(i-1));
                    stack2.push(argString2.charAt(j-1));

                    i -= 1;
                    j -= 1;
                    currCell = nwm[i][j];
                    break;
                case UP:
                    stack1.push(argString1.charAt(i-1));
                    stack2.push('-');

                    i -= 1;
                    currCell = nwm[i][j];
                    break;
                case LEFT:
                    stack1.push('-');
                    stack2.push(argString2.charAt(j-1));

                    j -= 1;
                    currCell = nwm[i][j];
                    break;
            }
        }

        System.out.println();
        for (Character c : stack1) {
            System.out.print(c);
        } System.out.println();

        for (Character c : stack2) {
            System.out.print(c);
        } System.out.println();

        System.out.println("score: " + score);
    }

    //print the nwm matrix
    public static void printNWM() {
        for (int i = 0; i < nwm.length; i++) {
            for (int j = 0; j < nwm[0].length; j++) {
                System.out.print(nwm[i][j].num + ":" + nwm[i][j].cellPointer + "\t\t");
            }
            System.out.println();
        }
    }
}

/**
 * A cell in the Needleman-Wunsch score and traceback matrix
 */
class NWCell {
    Integer num;
    Direction cellPointer;

    NWCell(Integer num, Direction whereto) {
        this.num = num;
        this.cellPointer = whereto;
    }
}

/**
 * some useful constants for the NWCell class
 */
enum Direction {
    UP, LEFT, DIAG, DONE;
}
