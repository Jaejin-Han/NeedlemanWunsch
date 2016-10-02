public class Main {

    public static void main(String[] args) {
        //TODO: get user input for argStrings

        createNwMatrix(argString1, argString2);
        fillNwMatrix();
    }

    public static final int GAP_SCORE = -2;
    public static final String SUBST_MATRIX_COL_AND_ROW_NAMES = "CTAG";
    public static final int[][] SUBSTITUTION_MATRIX =  {{2,1,-1,-1},
                                                        {1,2,-1,-1},
                                                        {-1,-1,2,1},
                                                        {-1,-1,1,2}};
    public static String argString1;
    public static String argString2;
    public static NamedInt[][] nwm; // Needleman-Wunsch score and traceback matrix

    /**
     * Create empty traceback/score matrix given two Strings
     */
    public static void createNwMatrix(String a, String b) {
        nwm = new NamedInt[a.length() + 1][b.length() + 1];
        nwm[0][0] = new NamedInt(0, "done");
    }

    /**
     * Fill traceback/score matrix matrix
     */
    public static void fillNwMatrix() {
        for (int i = 0; i < nwm.length; i++) {
            for (int j = 0; j < nwm[0].length; j++) {
                nwm[i][j] = getCellScoreAndTracebackDirection(i,j);
            }
        }
    }

    /**
     * Calculate the score and traceback direction for the cell using a recurrence relation
     */
    public static NamedInt getCellScoreAndTracebackDirection(int i ,int j) {
        NamedInt top = (i - 1 < 0)? null: nwm[i-1][j];
        NamedInt left = (j - 1 < 0)? null: nwm[i][j-1];
        NamedInt topleft = (j - 1 < 0 || i - 1 < 0)? null: nwm[i-1][j-1];
        NamedInt max = new NamedInt(Integer.MIN_VALUE, "");

        if (topleft != null) {
            max = new NamedInt(topleft.num + scoreMatrix(i, j), "topleft");
        }
        if (top != null && top.num + GAP_SCORE > max.num) {
            max = new NamedInt(top.num + GAP_SCORE, "top");
        }
        if (left != null && left.num + GAP_SCORE > max.num) {
            max = new NamedInt(left.num + GAP_SCORE, "left");
        }
        return max;
    }

    private static Integer scoreMatrix(int i, int j) {
        //TODO
        return null;
    }

    /**
     * Perform traceback
     */
    public static void traceback() {

    }
}

class NamedInt {
    Integer num;
    String name;

    NamedInt(Integer num, String name) {
        this.num = num;
        this.name = name;
    }
}
