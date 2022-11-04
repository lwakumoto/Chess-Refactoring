import java.util.EnumMap;

public class Board {
    EnumMap<MoveMessage, String> moveMessages = new EnumMap<>(MoveMessage.class);
    Piece[][] board = new Piece[8][8];
    boolean playerTurnIsWhite = true;
    public Board(){
        setupBlackPieces();
        setupWhitePieces();
        initializeMessages();
    }

    private void initializeMessages(){
        moveMessages.put(MoveMessage.INVALID_PATH_BISHOP, "Cannot find path for Bishop");
        moveMessages.put(MoveMessage.INVALID_PATH_KING, "Cannot find path for King");
        moveMessages.put(MoveMessage.INVALID_PATH_KNIGHT, "Cannot find path for Knight");
        moveMessages.put(MoveMessage.INVALID_PATH_PAWN, "Cannot find path for Pawn");
        moveMessages.put(MoveMessage.INVALID_PATH_QUEEN, "Cannot find path for Queen");
        moveMessages.put(MoveMessage.INVALID_PATH_ROOK, "Cannot find path for Rook");
        moveMessages.put(MoveMessage.CAPTURE_OWN_PIECE, "You cannot capture your own piece!");
        moveMessages.put(MoveMessage.CAPTURE_SPACE_UNOCCUPIED, "The space you are trying to capture doesn't have any pieces on it!");
    }


    private void setupWhitePieces(){
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

    private void printMoveMsg(MoveMessage message){
        System.out.println(moveMessages.get(message));
    }


    public void movePiece(Move move) {
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
                    printMoveMsg(MoveMessage.INVALID_PATH_PAWN);
                    return true;
                } else if (rankDelta == 1) {
                    if(board[toRankIndex][toFileIndex] != null) {
                        printMoveMsg(MoveMessage.INVALID_PATH_PAWN);
                        return true;
                    }
                } else if (rankDelta == 2) {
                    if(board[toRankIndex][toFileIndex] != null || board[toRankIndex -1][toFileIndex] != null) {
                        printMoveMsg(MoveMessage.INVALID_PATH_PAWN);
                        return true;
                    }
                }
            } else {
                int rankDelta = fromRankIndex - toRankIndex;
                if(rankDelta != 1) {
                    printMoveMsg(MoveMessage.INVALID_PATH_PAWN);
                    return true;
                } else {
                    if(board[toRankIndex][toFileIndex] != null) {
                        printMoveMsg(MoveMessage.INVALID_PATH_PAWN);
                        return true;
                    }
                }
            }
        } else {
            if(fromRankIndex == 1) {
                int rankDelta = fromRankIndex - toRankIndex;
                if(rankDelta < -2 || rankDelta > -1) {
                    printMoveMsg(MoveMessage.INVALID_PATH_PAWN);
                    return true;
                } else if (rankDelta == -1) {
                    if(board[toRankIndex][toFileIndex] != null) {
                        printMoveMsg(MoveMessage.INVALID_PATH_PAWN);
                        return true;
                    }
                } else if (rankDelta == -2) {
                    if(board[toRankIndex][toFileIndex] != null || board[toRankIndex +1][toFileIndex] != null) {
                        printMoveMsg(MoveMessage.INVALID_PATH_PAWN);
                        return true;
                    }
                }
            } else {
                int rankDelta = fromRankIndex - toRankIndex;
                if(rankDelta != -1) {
                    printMoveMsg(MoveMessage.INVALID_PATH_PAWN);
                    return true;
                } else {
                    if(board[toRankIndex][toFileIndex] != null) {
                        printMoveMsg(MoveMessage.INVALID_PATH_PAWN);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean validateKingMove(int fromFileIndex, int fromRankIndex, int toRankIndex, int toFileIndex) {
        if(fromFileIndex == toFileIndex && toRankIndex == fromRankIndex) {
            printMoveMsg(MoveMessage.INVALID_PATH_KING);
            return true;
        } else if (Math.abs(fromFileIndex- toFileIndex) > 1) {
            printMoveMsg(MoveMessage.INVALID_PATH_KING);
            return true;
        } else if (Math.abs(fromRankIndex- toRankIndex) > 1) {
            printMoveMsg(MoveMessage.INVALID_PATH_KING);
            return true;
        } else if (board[toRankIndex][toFileIndex] != null) {
            printMoveMsg(MoveMessage.INVALID_PATH_KING);
            return true;
        }
        return false;
    }

    private boolean validateQueenMove(int fromFileIndex, int fromRankIndex, int toRankIndex, int toFileIndex) {
        if(fromFileIndex == toFileIndex && toRankIndex == fromRankIndex) {
            printMoveMsg(MoveMessage.INVALID_PATH_QUEEN);
            return true;
        } else if(fromFileIndex == toFileIndex) {
            if(toRankIndex >fromRankIndex) {
                for(int i = fromRankIndex+1; i<= toRankIndex; i++) {
                    if(board[i][fromFileIndex] != null) {
                        printMoveMsg(MoveMessage.INVALID_PATH_QUEEN);
                        return true;
                    }
                }
            } else {
                for(int i = fromRankIndex-1; i>= toRankIndex; i--) {
                    if(board[i][fromFileIndex] != null) {
                        printMoveMsg(MoveMessage.INVALID_PATH_QUEEN);
                        return true;
                    }
                }
            }
        } else if(fromRankIndex == toRankIndex){
            if(toFileIndex >fromFileIndex) {
                for(int i = fromFileIndex+1; i<= toFileIndex; i++) {
                    if(board[fromRankIndex][i] != null) {
                        printMoveMsg(MoveMessage.INVALID_PATH_QUEEN);
                        return true;
                    }
                }
            } else {
                for(int i=fromFileIndex-1; i>=fromFileIndex; i--) {
                    if(board[fromRankIndex][i] != null) {
                        printMoveMsg(MoveMessage.INVALID_PATH_QUEEN);
                        return true;
                    }
                }
            }
        } else if(Math.abs(fromFileIndex- toFileIndex) != Math.abs(fromRankIndex- toRankIndex)) {
            printMoveMsg(MoveMessage.INVALID_PATH_QUEEN);
            return true;
        } else {
            if (fromFileIndex < toFileIndex && fromRankIndex < toRankIndex) {
                for (int i = 1; i <= toFileIndex - fromFileIndex; i++) {
                    if (board[fromRankIndex + i][fromFileIndex + i] != null) {
                        printMoveMsg(MoveMessage.INVALID_PATH_QUEEN);
                        return true;
                    }
                }
            } else if (fromFileIndex < toFileIndex && fromRankIndex > toRankIndex) {
                for (int i = 1; i <= toFileIndex - fromFileIndex; i++) {
                    if (board[fromRankIndex - i][fromFileIndex + i] != null) {
                        printMoveMsg(MoveMessage.INVALID_PATH_QUEEN);
                        return true;
                    }
                }
            } else if (fromFileIndex > toFileIndex && fromRankIndex > toRankIndex) {
                for (int i = 1; i <= fromFileIndex - toFileIndex; i++) {
                    if (board[fromRankIndex - i][fromFileIndex - i] != null) {
                        printMoveMsg(MoveMessage.INVALID_PATH_QUEEN);
                        return true;
                    }
                }
            } else if (fromFileIndex > toFileIndex && fromRankIndex < toRankIndex) {
                for (int i = 1; i <= fromFileIndex - toFileIndex; i++) {
                    if (board[fromRankIndex + i][fromFileIndex - i] != null) {
                        printMoveMsg(MoveMessage.INVALID_PATH_QUEEN);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean validateBishopMove(int toFileIndex, int toRankIndex, int fromFileIndex, int fromRankIndex) {
        if(fromFileIndex == toFileIndex || toRankIndex == fromRankIndex) {
            printMoveMsg(MoveMessage.INVALID_PATH_BISHOP);
            return true;
        } else if(Math.abs(fromFileIndex- toFileIndex) != Math.abs(fromRankIndex- toRankIndex)) {
            printMoveMsg(MoveMessage.INVALID_PATH_BISHOP);
            return true;
        } else {
            if(fromFileIndex < toFileIndex && fromRankIndex < toRankIndex) {
                for(int i = 1; i <= toFileIndex -fromFileIndex; i++) {
                    if(board[fromRankIndex+i][fromFileIndex+i] != null) {
                        printMoveMsg(MoveMessage.INVALID_PATH_BISHOP);
                        return true;
                    }
                }
            } else if(fromFileIndex < toFileIndex && fromRankIndex > toRankIndex) {
                for(int i = 1; i <= toFileIndex -fromFileIndex; i++) {
                    if(board[fromRankIndex-i][fromFileIndex+i] != null) {
                        printMoveMsg(MoveMessage.INVALID_PATH_BISHOP);
                        return true;
                    }
                }
            } else if(fromFileIndex > toFileIndex && fromRankIndex > toRankIndex) {
                for(int i = 1; i <= fromFileIndex- toFileIndex; i++) {
                    if(board[fromRankIndex-i][fromFileIndex-i] != null) {
                        printMoveMsg(MoveMessage.INVALID_PATH_BISHOP);
                        return true;
                    }
                }
            } else if(fromFileIndex > toFileIndex && fromRankIndex < toRankIndex) {
                for(int i = 1; i <= fromFileIndex- toFileIndex; i++) {
                    if(board[fromRankIndex+i][fromFileIndex-i] != null) {
                        printMoveMsg(MoveMessage.INVALID_PATH_BISHOP);
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
                        printMoveMsg(MoveMessage.INVALID_PATH_ROOK);
                        return true;
                    }
                }
            } else {
                for(int i = fromRankIndex -1; i>= toRankIndex; i--) {
                    if(board[i][fromFileIndex] != null) {
                        printMoveMsg(MoveMessage.INVALID_PATH_ROOK);
                        return true;
                    }
                }
            }
        } else if(fromRankIndex == toRankIndex){
            if(toFileIndex > fromFileIndex) {
                for(int i = fromFileIndex +1; i<= toFileIndex; i++) {
                    if(board[fromRankIndex][i] != null) {
                        printMoveMsg(MoveMessage.INVALID_PATH_ROOK);
                        return true;
                    }
                }
            } else {
                for(int i = fromFileIndex -1; i>= fromFileIndex; i--) {
                    if(board[fromRankIndex][i] != null) {
                        printMoveMsg(MoveMessage.INVALID_PATH_ROOK);
                        return true;
                    }
                }
            }
        } else {
            printMoveMsg(MoveMessage.INVALID_PATH_ROOK);
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


    public void capturePiece(Move move) {
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
        if (fromPiece != null){
            boolean fromIsWhite = Character.isUpperCase(fromPiece.toString().charAt(0));
            boolean toIsWhite =   Character.isUpperCase(tempHolder.toString().charAt(0));

            if (fromIsWhite == toIsWhite){// make sure pieces can't capture their own;
                System.out.println("You cannot capture your own pieces!");
                return;
            }

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
        int direction = (board[fromFileIndex][fromRankIndex].toString().toLowerCase().equals(board[fromFileIndex][fromRankIndex].toString()) ) ? 1 : -1;
       System.out.println(direction);
        if((fromFileIndex != toFileIndex - 1 && fromFileIndex != toFileIndex + 1) || ((fromRankIndex != toRankIndex - direction))) {
            System.out.println("Cannot create valid capture path for Pawn.");
            return true;
        }

        return false;
    }

    public void printBoardToConsole() {
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


    public boolean gameIsOver() {
        return isPositionCheckmate() || isPositionStalemate();
    }

    private boolean isPositionStalemate() {
        return false;
    }

    private boolean isPositionCheckmate() {
        return false;
    }


}
