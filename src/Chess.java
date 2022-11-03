import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chess {

    Piece[][] board = new Piece[8][8];
    boolean playerTurnIsWhite;

    public Chess() {
        setupBlackPieces();

        board[6][0] = Piece.P;
        board[6][1] = Piece.P;
        board[6][2] = Piece.P;
        board[6][3] = Piece.P;
        board[6][4] = Piece.P;
        board[6][5] = Piece.P;
        board[6][6] = Piece.P;
        board[6][7] = Piece.P;
        board[7][0] = Piece.R;
        board[7][1] = Piece.N;
        board[7][2] = Piece.B;
        board[7][3] = Piece.Q;
        board[7][4] = Piece.K;
        board[7][5] = Piece.B;
        board[7][6] = Piece.N;
        board[7][7] = Piece.R;

        playerTurnIsWhite = true;
    }

    private void setupBlackPieces() {
        board[0][0] = Piece.r;
        board[0][1] = Piece.n;
        board[0][2] = Piece.b;
        board[0][3] = Piece.q;
        board[0][4] = Piece.k;
        board[0][5] = Piece.b;
        board[0][6] = Piece.n;
        board[0][7] = Piece.r;
        board[1][0] = Piece.p;
        board[1][1] = Piece.p;
        board[1][2] = Piece.p;
        board[1][3] = Piece.p;
        board[1][4] = Piece.p;
        board[1][5] = Piece.p;
        board[1][6] = Piece.p;
        board[1][7] = Piece.p;
    }

    public void play() throws IOException {
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        while(!gameIsOver()) {
            printBoardToConsole();

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
                movePiece(newMove);
            } else {
                capturePiece(newMove);
            }
            // We are not going to worry about special moves like castling and en passant
        }

        System.out.println("Game over.");
        System.out.println("Thanks for playing!");
    }

    private void movePiece(Move move) {
        int fromFileIndex = move.getFromPiece().getFileIndex();
        int fromRankIndex = move.getFromPiece().getRankIndex();

        int toRankIndex = move.getToPiece().getRankIndex();
        int toFileIndex = move.getToPiece().getFileIndex();

        String pawnPromotionPiece = move.getPawnPromotion();

        Piece fromPiece = board[fromRankIndex][fromFileIndex];

        if(fromPiece == null) {
            System.out.println("Select a square with a piece.");
            return;
        }

        if (correctPlayerNotMovingTheirPiece(fromPiece)) return;

        //Validate Piece Movement
        if(fromPiece.toString().equalsIgnoreCase("n")) {
            if (!((Math.abs(fromFileIndex - toFileIndex) == 2 && Math.abs(fromRankIndex - toRankIndex) == 1) || (Math.abs(fromFileIndex - toFileIndex) == 1 && Math.abs(fromRankIndex - toRankIndex) == 2))) {
                System.out.println("Invalid move for Knight.");
                return;
            }
        } else if(fromPiece.toString().equalsIgnoreCase("r")) {
            if (validateRookMove(toFileIndex, toRankIndex, fromFileIndex, fromRankIndex)) return;
        } else if(fromPiece.toString().equalsIgnoreCase("b")) {
            if (validateBishopMove(toFileIndex, toRankIndex, fromFileIndex, fromRankIndex)) return;
        } else if (fromPiece.toString().equalsIgnoreCase("q")) {
            if (validateQueenMove(fromFileIndex, fromRankIndex, toRankIndex, toFileIndex)) return;
        } else if(fromPiece.toString().equalsIgnoreCase("k")) {
            if (validateKingMove(fromFileIndex, fromRankIndex, toRankIndex, toFileIndex)) return;
        } else if (fromPiece.toString().equalsIgnoreCase("p")) {
            if (validatePawnMove(fromFileIndex, fromRankIndex, toRankIndex, toFileIndex)) return;
        }

        //Handle the promotion of a pawn.
        if(fromPiece.toString().equalsIgnoreCase("p")) {
            if(playerTurnIsWhite && toRankIndex == 0) {
                if(pawnPromotionPiece == null) {
                    System.out.println("Pawn Promotion Piece must be specified for this pawn move.");
                    return;
                }
                if(!pawnPromotionPiece.toUpperCase().equals(pawnPromotionPiece)) {
                    System.out.println("Pawn Promotion Piece must be for White. Input should be uppercase.");
                    return;
                }
                fromPiece = Piece.valueOf(pawnPromotionPiece);
            } else if(!playerTurnIsWhite && toRankIndex == 7) {
                if(pawnPromotionPiece == null) {
                    System.out.println("Pawn Promotion Piece must be specified for this pawn move.");
                    return;
                }
                if(!pawnPromotionPiece.toLowerCase().equals(pawnPromotionPiece)) {
                    System.out.println("Pawn Promotion Piece must be for Black. Input should be lowercase.");
                    return;
                }
                fromPiece = Piece.valueOf(pawnPromotionPiece);
            }
        }

        //If we have gotten here, that means the move is valid and update the board position
        board[toRankIndex][toFileIndex] = fromPiece;
        board[fromRankIndex][fromFileIndex] = null;

        //Change the player's turn
        playerTurnIsWhite = !playerTurnIsWhite;
    }

    private boolean validatePawnMove(int fromFileIndex, int fromRankIndex, int toRankIndex, int toFileIndex) {
        if(fromFileIndex != toFileIndex) {
            System.out.println("Cannot create valid path for Pawn.");
            return true;
        }
        if(playerTurnIsWhite) {
            if(fromRankIndex == 6) {
                int rankDelta = fromRankIndex - toRankIndex;
                if(rankDelta > 2 || rankDelta < 1) {
                    System.out.println("Cannot create valid path for Pawn.");
                    return true;
                } else if (rankDelta == 1) {
                    if(board[toRankIndex][toFileIndex] != null) {
                        System.out.println("Cannot create valid path for Pawn.");
                        return true;
                    }
                } else if (rankDelta == 2) {
                    if(board[toRankIndex][toFileIndex] != null || board[toRankIndex -1][toFileIndex] != null) {
                        System.out.println("Cannot create valid path for Pawn.");
                        return true;
                    }
                }
            } else {
                int rankDelta = fromRankIndex - toRankIndex;
                if(rankDelta != 1) {
                    System.out.println("Cannot create valid path for Pawn.");
                    return true;
                } else {
                    if(board[toRankIndex][toFileIndex] != null) {
                        System.out.println("Cannot create valid path for Pawn.");
                        return true;
                    }
                }
            }
        } else {
            if(fromRankIndex == 1) {
                int rankDelta = fromRankIndex - toRankIndex;
                if(rankDelta < -2 || rankDelta > -1) {
                    System.out.println("Cannot create valid path for Pawn.");
                    return true;
                } else if (rankDelta == -1) {
                    if(board[toRankIndex][toFileIndex] != null) {
                        System.out.println("Cannot create valid path for Pawn.");
                        return true;
                    }
                } else if (rankDelta == -2) {
                    if(board[toRankIndex][toFileIndex] != null || board[toRankIndex +1][toFileIndex] != null) {
                        System.out.println("Cannot create valid path for Pawn.");
                        return true;
                    }
                }
            } else {
                int rankDelta = fromRankIndex - toRankIndex;
                if(rankDelta != -1) {
                    System.out.println("Cannot create valid path for Pawn.");
                    return true;
                } else {
                    if(board[toRankIndex][toFileIndex] != null) {
                        System.out.println("Cannot create valid path for Pawn.");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean validateKingMove(int fromFileIndex, int fromRankIndex, int toRankIndex, int toFileIndex) {
        if(fromFileIndex == toFileIndex && toRankIndex == fromRankIndex) {
            System.out.println("Cannot create valid path for King.");
            return true;
        } else if (Math.abs(fromFileIndex- toFileIndex) > 1) {
            System.out.println("Cannot create valid path for King.");
            return true;
        } else if (Math.abs(fromRankIndex- toRankIndex) > 1) {
            System.out.println("Cannot create valid path for King.");
            return true;
        } else if (board[toRankIndex][toFileIndex] != null) {
            System.out.println("Cannot create valid path for King.");
            return true;
        }
        return false;
    }

    private boolean validateQueenMove(int fromFileIndex, int fromRankIndex, int toRankIndex, int toFileIndex) {
        if(fromFileIndex == toFileIndex && toRankIndex == fromRankIndex) {
            System.out.println("Cannot create valid path for Queen.");
            return true;
        } else if(fromFileIndex == toFileIndex) {
            if(toRankIndex >fromRankIndex) {
                for(int i = fromRankIndex+1; i<= toRankIndex; i++) {
                    if(board[i][fromFileIndex] != null) {
                        System.out.println("Cannot create valid path for Queen.");
                        return true;
                    }
                }
            } else {
                for(int i = fromRankIndex-1; i>= toRankIndex; i--) {
                    if(board[i][fromFileIndex] != null) {
                        System.out.println("Cannot create valid path for Queen.");
                        return true;
                    }
                }
            }
        } else if(fromRankIndex == toRankIndex){
            if(toFileIndex >fromFileIndex) {
                for(int i = fromFileIndex+1; i<= toFileIndex; i++) {
                    if(board[fromRankIndex][i] != null) {
                        System.out.println("Cannot create valid path for Queen.");
                        return true;
                    }
                }
            } else {
                for(int i=fromFileIndex-1; i>=fromFileIndex; i--) {
                    if(board[fromRankIndex][i] != null) {
                        System.out.println("Cannot create valid path for Queen.");
                        return true;
                    }
                }
            }
        } else if(Math.abs(fromFileIndex- toFileIndex) != Math.abs(fromRankIndex- toRankIndex)) {
            System.out.println("Cannot create valid path for Queen.");
            return true;
        } else {
            if (fromFileIndex < toFileIndex && fromRankIndex < toRankIndex) {
                for (int i = 1; i <= toFileIndex - fromFileIndex; i++) {
                    if (board[fromRankIndex + i][fromFileIndex + i] != null) {
                        System.out.println("Cannot create valid path for Queen.");
                        return true;
                    }
                }
            } else if (fromFileIndex < toFileIndex && fromRankIndex > toRankIndex) {
                for (int i = 1; i <= toFileIndex - fromFileIndex; i++) {
                    if (board[fromRankIndex - i][fromFileIndex + i] != null) {
                        System.out.println("Cannot create valid path for Queen.");
                        return true;
                    }
                }
            } else if (fromFileIndex > toFileIndex && fromRankIndex > toRankIndex) {
                for (int i = 1; i <= fromFileIndex - toFileIndex; i++) {
                    if (board[fromRankIndex - i][fromFileIndex - i] != null) {
                        System.out.println("Cannot create valid path for Queen.");
                        return true;
                    }
                }
            } else if (fromFileIndex > toFileIndex && fromRankIndex < toRankIndex) {
                for (int i = 1; i <= fromFileIndex - toFileIndex; i++) {
                    if (board[fromRankIndex + i][fromFileIndex - i] != null) {
                        System.out.println("Cannot create valid path for Queen.");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean validateBishopMove(int toFileIndex, int toRankIndex, int fromFileIndex, int fromRankIndex) {
        if(fromFileIndex == toFileIndex || toRankIndex == fromRankIndex) {
            System.out.println("Cannot create valid path for Bishop.");
            return true;
        } else if(Math.abs(fromFileIndex- toFileIndex) != Math.abs(fromRankIndex- toRankIndex)) {
            System.out.println("Cannot create valid path for Bishop.");
            return true;
        } else {
            if(fromFileIndex < toFileIndex && fromRankIndex < toRankIndex) {
                for(int i = 1; i <= toFileIndex -fromFileIndex; i++) {
                    if(board[fromRankIndex+i][fromFileIndex+i] != null) {
                        System.out.println("Cannot create valid path for Bishop.");
                        return true;
                    }
                }
            } else if(fromFileIndex < toFileIndex && fromRankIndex > toRankIndex) {
                for(int i = 1; i <= toFileIndex -fromFileIndex; i++) {
                    if(board[fromRankIndex-i][fromFileIndex+i] != null) {
                        System.out.println("Cannot create valid path for Bishop.");
                        return true;
                    }
                }
            } else if(fromFileIndex > toFileIndex && fromRankIndex > toRankIndex) {
                for(int i = 1; i <= fromFileIndex- toFileIndex; i++) {
                    if(board[fromRankIndex-i][fromFileIndex-i] != null) {
                        System.out.println("Cannot create valid path for Bishop.");
                        return true;
                    }
                }
            } else if(fromFileIndex > toFileIndex && fromRankIndex < toRankIndex) {
                for(int i = 1; i <= fromFileIndex- toFileIndex; i++) {
                    if(board[fromRankIndex+i][fromFileIndex-i] != null) {
                        System.out.println("Cannot create valid path for Bishop.");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean validateRookMove(int toFileIndex, int toRankIndex, int fromFileIndex, int fromRankIndex) {
        if(fromFileIndex == toFileIndex && toRankIndex == fromRankIndex) {
            System.out.println("Rook must move at least 1 square.");
            return true;
        } else if(fromFileIndex == toFileIndex) {
            if(toRankIndex > fromRankIndex) {
                for(int i = fromRankIndex +1; i<= toRankIndex; i++) {
                    if(board[i][fromFileIndex] != null) {
                        System.out.println("Cannot create valid path for Rook.");
                        return true;
                    }
                }
            } else {
                for(int i = fromRankIndex -1; i>= toRankIndex; i--) {
                    if(board[i][fromFileIndex] != null) {
                        System.out.println("Cannot create valid path for Rook.");
                        return true;
                    }
                }
            }
        } else if(fromRankIndex == toRankIndex){
            if(toFileIndex > fromFileIndex) {
                for(int i = fromFileIndex +1; i<= toFileIndex; i++) {
                    if(board[fromRankIndex][i] != null) {
                        System.out.println("Cannot create valid path for Rook.");
                        return true;
                    }
                }
            } else {
                for(int i = fromFileIndex -1; i>= fromFileIndex; i--) {
                    if(board[fromRankIndex][i] != null) {
                        System.out.println("Cannot create valid path for Rook.");
                        return true;
                    }
                }
            }
        } else {
            System.out.println("Cannot create valid path for Rook.");
            return true;
        }

        return false;
    }

    private boolean correctPlayerNotMovingTheirPiece(Piece fromPiece) {
        //Check that the piece is owned by the correct player.
        if(playerTurnIsWhite) {
            if(fromPiece.toString().toLowerCase() == fromPiece.toString()) {
                System.out.println("Select a square with a white piece.");
                return true;
            }
        } else {
            if(fromPiece.toString().toUpperCase() == fromPiece.toString()) {
                System.out.println("Select a square with a black piece.");
                return true;
            }
        }
        return false;
    }

    // TODO: Homework - Refactor this method to use a single parameter

    private void capturePiece(Move move) {
        int fromRankIndex = move.getFromPiece().getRankIndex();
        int fromFileIndex = move.getFromPiece().getFileIndex();

        int toRankIndex = move.getToPiece().getRankIndex();
        int toFileIndex = move.getToPiece().getFileIndex();


        Piece fromPiece = board[fromRankIndex][fromFileIndex];


        // TODO: Homework - Create capture logic when a piece is capturing another piece
        //           Remember: Pieces can only capture opposing pieces
        //                     Pawns can only capture diagonally in front of them
        //                     We are not worrying about en passant. This is just the simple and basic moves.
        //           Use inspiration from the move method. Think about what can be refactored.
        //                     Extract method is your friend.

        Piece tempHolder = board[toRankIndex][toFileIndex];
        if (tempHolder == null){
            System.out.println("There is no pieces in the place where you are trying to capture!");
            return;
        }
        System.out.println("HTISDFS " + tempHolder.toString());
        boolean fromIsWhite = Character.isUpperCase(fromPiece.toString().charAt(0));
        boolean toIsWhite =   Character.isUpperCase(tempHolder.toString().charAt(0));

        if (fromIsWhite == toIsWhite){// make sure pieces can't capture their own;
            System.out.println("You cannot capture your own pieces!");
            return;
        }

        board[toRankIndex][toFileIndex] = null; // temporarily replace the place at the board with null, so it can be captured if the path is valid.
        //Validate Piece Movement
        if(fromPiece.toString().equalsIgnoreCase("n")) {
            if (!((Math.abs(fromFileIndex - toFileIndex) == 2 && Math.abs(fromRankIndex - toRankIndex) == 1) || (Math.abs(fromFileIndex - toFileIndex) == 1 && Math.abs(fromRankIndex - toRankIndex) == 2))) {
                System.out.println("Invalid move for Knight.");
                return;
            }
        } else if(fromPiece.toString().equalsIgnoreCase("r")) {
            if (validateRookMove(toFileIndex, toRankIndex, fromFileIndex, fromRankIndex)) {
                board[toRankIndex][toFileIndex] = tempHolder;
                return;
            }
        } else if(fromPiece.toString().equalsIgnoreCase("b")) {
            if (validateBishopMove(toFileIndex, toRankIndex, fromFileIndex, fromRankIndex)) {
                board[toRankIndex][toFileIndex] = tempHolder;
                return;
            }
        } else if (fromPiece.toString().equalsIgnoreCase("q")) {
            if (validateQueenMove(fromFileIndex, fromRankIndex, toRankIndex, toFileIndex)) {
                board[toRankIndex][toFileIndex] = tempHolder;
                return;
            }
        } else if(fromPiece.toString().equalsIgnoreCase("k")) {
            if (validateKingMove(fromFileIndex, fromRankIndex, toRankIndex, toFileIndex)) {
                board[toRankIndex][toFileIndex] = tempHolder;
                return;
            }
        } else if (fromPiece.toString().equalsIgnoreCase("p")) {
            if (validatePawnCapture(fromFileIndex, fromRankIndex, toRankIndex, toFileIndex)) {
                board[toRankIndex][toFileIndex] = tempHolder;
                return;
            }
        }

        //Move piece, if the move is allowed.
        board[toRankIndex][toFileIndex] = fromPiece;
        board[fromRankIndex][fromFileIndex] = null;
    }


    private boolean validatePawnCapture(int fromFileIndex, int fromRankIndex, int toRankIndex, int toFileIndex) { // special treatment for pawn, because it captures diagonally
        int direction = (board[fromFileIndex][fromRankIndex].toString().charAt(0) == 'P') ? 1 : -1;
        if((fromFileIndex != toFileIndex - 1 && fromFileIndex != toFileIndex + 1) || ((fromRankIndex != toRankIndex + direction))) {
            System.out.println("Cannot create valid capture path for Pawn.");
            return true;
        }

        return false;
    }

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

    private boolean gameIsOver() {
        return isPositionCheckmate() || isPositionStalemate();
    }

    private boolean isPositionStalemate() {
        return false;
    }

    private boolean isPositionCheckmate() {
        return false;
    }

    private void printBoardToConsole() {
        StringBuilder sb = new StringBuilder();
        int rankNum = 8;
        for (Piece[] rank : board) {
            sb.append(rankNum + " ");
            for(Piece piece : rank) {
                if(piece != null) {
                    sb.append(piece);
                } else {
                    sb.append(" ");
                }
            }
            sb.append("\n");
            rankNum--;
        }
        sb.append("  abcdefgh");
        System.out.println(sb);
    }
}
