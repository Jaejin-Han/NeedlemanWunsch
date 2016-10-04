import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;

public class NeedlemanWunschMain {

    public static void main(String[] cmdlineArgs) {
        paramsFileName = cmdlineArgs[0];
        sequenceFileName = cmdlineArgs[1];
        initialize();
        fillNwMatrix();
        traceback();
    }

    // Some boolean switches to control the printing of the matrix and the alignment.
    public static boolean printNWM;
    public static boolean printAlignment;

    // instantiated at runtime
    public static NWCell[][] nwm; // The Needleman-Wunsch score and traceback matrix
    public static HashMap<Character, Integer> charIndexMap; // the mapping from CTAG to indices in the scoreMatrix
    public static int[][] scoreMatrix; // a matrix to hold the scoring system
    public static String paramsFileName;
    public static String sequenceFileName;

    // values defined in input files
    public static String sequenceOne; // the first sequence
    public static String sequenceTwo; // the second sequence
    public static int transversionScore; // values to populate the scoreMatrix
    public static int transitionScore;
    public static int matchScore;
    public static int gapScore;

    // Create empty NW matrix given two Strings
    public static void initialize() {
        charIndexMap = new HashMap<>();
        charIndexMap.put('C', 0);
        charIndexMap.put('T', 1);
        charIndexMap.put('A', 2);
        charIndexMap.put('G', 3);

        handleInputFiles();

        //                                           C                      T                       A                       G
        scoreMatrix = new int[][]{  /* C */     {matchScore,         transitionScore,       transversionScore,       transversionScore},
                                    /* T */     {transitionScore,    matchScore,             transversionScore,      transversionScore},
                                    /* A */     {transversionScore,  transversionScore,      matchScore,             transitionScore},
                                    /* G */     {transversionScore,  transversionScore,      transitionScore,        matchScore}};

        nwm = new NWCell[sequenceOne.length() + 1][sequenceTwo.length() + 1];
    }

    private static void handleInputFiles() {
        try {
            BufferedReader brp = new BufferedReader(new FileReader(paramsFileName));
            BufferedReader brs = new BufferedReader(new FileReader(sequenceFileName));

            String currLine;
            String[] currWords;
            String param;

            // go through params.txt file
            while ((currLine = brp.readLine()) != null) {
                currWords = currLine.split(" ");
                param = currWords[1];
                if (currLine.contains("transversion")) {
                    transversionScore = Integer.valueOf(param);
                } else if (currLine.contains("transition")) {
                    transitionScore = Integer.valueOf(param);
                } else if (currLine.contains("match")) {
                    matchScore = Integer.valueOf(param);
                } else if (currLine.contains("gap")) {
                    gapScore = Integer.valueOf(param);
                } else if (currLine.contains("NWM")) {
                    printNWM = Boolean.valueOf(param);
                } else if (currLine.contains("Alignment")) {
                    printAlignment = Boolean.valueOf(param);
                }
            }

            // go through
            boolean first = true;
            while ((currLine = brs.readLine()) != null) {
                if (currLine.length() > 0 && "CTAG".contains(currLine.substring(0,1))) {
                    if (first) {
                        sequenceOne = currLine;
                        first = !first;
                    } else {
                        sequenceTwo = currLine;
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Fill NW matrix matrix
    public static void fillNwMatrix() {
        for (int i = 0; i < nwm.length; i++) {
            for (int j = 0; j < nwm[0].length; j++) {
                nwm[i][j] = getNWCell(i,j);
            }
        }
        printNWM();
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
            // charAt(i - 1) because the index in nwm for a certain letter is one over since the col/row names start with a gapScore
            max = new NWCell(diag.num + scoreMatrix(sequenceOne.charAt(i-1), sequenceTwo.charAt(j-1)), Direction.DIAG);
        }
        if (up != null && up.num + gapScore > max.num) {
            max = new NWCell(up.num + gapScore, Direction.UP);
        }
        if (left != null && left.num + gapScore > max.num) {
            max = new NWCell(left.num + gapScore, Direction.LEFT);
        }

        if (max.cellPointer == Direction.DONE) {
            max.num = 0;
        }
        return max;
    }

    private static Integer scoreMatrix(char a, char b) {
        // should make sense if you look at the value of the String and matrix used here
        int i = charIndexMap.get(a);
        int j = charIndexMap.get(b);
        return scoreMatrix[i][j];
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
                    stack1.push(sequenceOne.charAt(i-1));
                    stack2.push(sequenceTwo.charAt(j-1));

                    i -= 1;
                    j -= 1;
                    currCell = nwm[i][j];
                    break;
                case UP:
                    stack1.push(sequenceOne.charAt(i-1));
                    stack2.push('-');

                    i -= 1;
                    currCell = nwm[i][j];
                    break;
                case LEFT:
                    stack1.push('-');
                    stack2.push(sequenceTwo.charAt(j-1));

                    j -= 1;
                    currCell = nwm[i][j];
                    break;
            }
        }

        if ( printAlignment ){
            System.out.println();
            for (Character c : stack1) {
                System.out.print(c);
            }
            System.out.println();

            for (Character c : stack2) {
                System.out.print(c);
            }
        }

        System.out.println();
        System.out.println("score: " + score);
    }

    //print the NW matrix
    public static void printNWM() {
        if (!printNWM) {
            return;
        }
        for (int i = 0; i < nwm.length; i++) {
            for (int j = 0; j < nwm[0].length; j++) {
                System.out.print(nwm[i][j].num + ":" + nwm[i][j].cellPointer + "\t\t");
            }
            System.out.println();
        }
    }
}

// A cell in the Needleman-Wunsch score and traceback matrix
class NWCell {
    Integer num;
    Direction cellPointer;

    NWCell(Integer num, Direction cellPointer) {
        this.num = num;
        this.cellPointer = cellPointer;
    }
}

// direction constants for the NWCell class
enum Direction {
    UP, LEFT, DIAG, DONE;
}
