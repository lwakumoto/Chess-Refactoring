public class Move {
    private Square fromPiece;

    public Move(Square fromPiece, Square toPiece, String pawnPromotion){
        this.fromPiece = fromPiece;
        this.toPiece = toPiece;
        this.pawnPromotion = pawnPromotion;
    }

    public Square getFromPiece() {
        return fromPiece;
    }

    public void setFromPiece(Square fromPiece) {
        this.fromPiece = fromPiece;
    }

    public Square getToPiece() {
        return toPiece;
    }

    public void setToPiece(Square toPiece) {
        this.toPiece = toPiece;
    }

    public String getPawnPromotion() {
        return pawnPromotion;
    }

    public void setPawnPromotion(String pawnPromotion) {
        this.pawnPromotion = pawnPromotion;
    }

    private Square toPiece;
    private String pawnPromotion;
}
