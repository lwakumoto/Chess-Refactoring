import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.EnumMap;

public class Chess {

    Piece[][] board = new Piece[8][8];
    boolean playerTurnIsWhite;
    EnumMap<MoveMessage, String> moveMessages = new EnumMap<>(MoveMessage.class);
    Board gameBoard = new Board();

    public Chess() {

//        initializeMessages();
//        initializeBoard();

        playerTurnIsWhite = true;
    }


    public void play() throws IOException {
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        while(!gameBoard.gameIsOver()) {
            gameBoard.printBoardToConsole();

            // TODO: Homework - Is this really the chess game's responsibility to do the move conversion?
            //                       Refactor this code to make it the responsibility of a different class.
            // We'll be doing a simpler notation for our chess game. Notation will be a 5 or 6 character length. Form
            //       will take the shape of {a-h}{1-8}(\-x}{a-h}{1-8}{rnbqRNBQ}.
            //       First character is the from file
            //       Second character is the from rank
            //       Third character is - for move, x for capture
            //       Forth character is the to file
            //       Fifth character is the to rank
            //       Sixth character is the promotion of a pawn to a piece type. This is optional
            String move = inputReader.readLine();
            Pattern movePattern = Pattern.compile("^[a-h][1-8][-x][a-h][1-8][rnbqRNBQ]{0,1}");
            Matcher moveMatcher = movePattern.matcher(move);
            if(!moveMatcher.find()) {
                //Move is invalid;
                System.out.println("Move is invalid. Please input a valid move.");
                continue;
            }

            Square fromSquare = new Square();
            fromSquare.setFileIndex(calcFileIndex(move.charAt(0)));
            fromSquare.setRankIndex(calcRankIndex(Integer.valueOf(move.substring(1,2))));
            boolean capture = (move.charAt(2) == 'x');
            Square toSquare = new Square();
            toSquare.setFileIndex(calcFileIndex(move.charAt(3)));
            toSquare.setRankIndex(calcRankIndex(Integer.valueOf(move.substring(4,5))));

            //Move move = new Move(fromSquare, toSquare, pawnPromotionPiece);
            String pawnPromotionPiece = null;
            if(move.length() == 6) {
                pawnPromotionPiece = move.substring(5,6);
            }
            Move newMove = new Move(fromSquare, toSquare, pawnPromotionPiece);

            if(!capture) {
                gameBoard.movePiece(newMove);
            } else {
                gameBoard.capturePiece(newMove);
            }
            // We are not going to worry about special moves like castling and en passant
        }

        System.out.println("Game over.");
        System.out.println("Thanks for playing!");
    }

    // TODO: Homework - Refactor this method to use a single parameter


    private static int calcFileIndex(Character file) {
        // Files are associated as follows: a->7, b->6, c->5, d->4, e->3, f->2, g->1, h->0
        switch(file) {
            case 'a' :
                return 0;
            case 'b' :
                return 1;
            case 'c' :
                return 2;
            case 'd' :
                return 3;
            case 'e' :
                return 4;
            case 'f' :
                return 5;
            case 'g' :
                return 6;
            case 'h' :
                return 7;
            default :
                throw new IllegalArgumentException("File Character '" + file + "' is invalid.");
        }
    }

    private static int calcRankIndex(int rankNumber) {
        // Ranks are associated as follows: 1->7, 2->6, 3->5, 4->4, 5->3, 6->2, 7->1, 8->0
        switch(rankNumber) {
            case 1 :
                return 7;
            case 2 :
                return 6;
            case 3 :
                return 5;
            case 4 :
                return 4;
            case 5 :
                return 3;
            case 6 :
                return 2;
            case 7 :
                return 1;
            case 8 :
                return 0;
            default:
                throw new IllegalArgumentException("Rank Value '" + rankNumber + "' is invalid.");

        }
    }


}
