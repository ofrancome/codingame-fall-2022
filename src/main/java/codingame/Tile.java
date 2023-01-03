package codingame;

class Tile {
    int x;
    int y;
    int scrapAmount;
    int owner;
    int units;
    boolean recycler;
    boolean canBuild;
    boolean canSpawn;
    boolean inRangeOfRecycler;

    public Tile(int x, int y, int scrapAmount, int owner, int units, boolean recycler, boolean canBuild, boolean canSpawn,
                boolean inRangeOfRecycler) {
        this.x = x;
        this.y = y;
        this.scrapAmount = scrapAmount;
        this.owner = owner;
        this.units = units;
        this.recycler = recycler;
        this.canBuild = canBuild;
        this.canSpawn = canSpawn;
        this.inRangeOfRecycler = inRangeOfRecycler;
    }

    public Tile(int x, int y) {
        this(x, y, 0, 0, 0, false, false, false, false);
    }

    public boolean isAdjacent(Tile target) {
        return Math.abs(x - target.x) + Math.abs(y - target.y) == 1;
    }

    public int distance(int x, int y) {
        return Math.abs(this.x - x) + Math.abs(this.y - y);
    }
}
