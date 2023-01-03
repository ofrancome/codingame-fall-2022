package codingame;

import java.util.List;

public class GameBoard {

    private List<Tile> tiles;
    private List<Tile> myTiles;
    private List<Tile> oppTiles;
    private List<Tile> neutralTiles;
    private List<Tile> myUnits;
    private List<Tile> oppUnits;
    private List<Tile> myRecyclers;
    private List<Tile> oppRecyclers;
    private int roundScrapTiles;
    private int myMatter;
    private int oppMatter;

    public GameBoard (List<Tile> tiles, List<Tile> myTiles, List<Tile> oppTiles, List<Tile> neutralTiles,
                      List<Tile> myUnits, List<Tile> oppUnits, List<Tile> myRecyclers, List<Tile> oppRecyclers,
                      int roundScrapTiles, int myMatter, int oppMatter) {
        this.tiles = tiles;
        this.myTiles = myTiles;
        this.oppTiles = oppTiles;
        this.neutralTiles = neutralTiles;
        this.myUnits = myUnits;
        this.oppUnits = oppUnits;
        this.myRecyclers = myRecyclers;
        this.oppRecyclers = oppRecyclers;
        this.roundScrapTiles = roundScrapTiles;
        this.myMatter = myMatter;
        this.oppMatter = oppMatter;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public List<Tile> getMyTiles() {
        return myTiles;
    }

    public List<Tile> getOppTiles() {
        return oppTiles;
    }

    public List<Tile> getNeutralTiles() {
        return neutralTiles;
    }

    public List<Tile> getMyUnits() {
        return myUnits;
    }

    public List<Tile> getOppUnits() {
        return oppUnits;
    }

    public List<Tile> getMyRecyclers() {
        return myRecyclers;
    }

    public List<Tile> getOppRecyclers() {
        return oppRecyclers;
    }

    public int getRoundScrapTiles() {
        return roundScrapTiles;
    }

    public int getMyMatter() {
        return myMatter;
    }

    public int getOppMatter() {
        return oppMatter;
    }
}
