public enum MoveMessage {
    INVALID_PATH_KNIGHT,
    INVALID_PATH_ROOK,
    INVALID_PATH_BISHOP,
    INVALID_PATH_QUEEN,
    INVALID_PATH_KING,
    INVALID_PATH_PAWN,
    CAPTURE_OWN_PIECE,
    CAPTURE_SPACE_UNOCCUPIED
}
/*
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
 */