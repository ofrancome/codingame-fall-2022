import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

class Tile {
    final int x, y, scrapAmount, owner, units;
    final boolean recycler, canBuild, canSpawn, inRangeOfRecycler;

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
}

class Player {

    static final int ME = 1;
    static final int OPP = 0;
    static final int NOONE = -1;
    private static int width;
    private static int height;

    private static final Random random = new Random();

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        width = in.nextInt();
        height = in.nextInt();

        int turn = 0;
        // game loop
        while (true) {
            turn++;
            List<Tile> tiles = new ArrayList<>();
            List<Tile> myTiles = new ArrayList<>();
            List<Tile> oppTiles = new ArrayList<>();
            List<Tile> neutralTiles = new ArrayList<>();
            List<Tile> myUnits = new ArrayList<>();
            List<Tile> oppUnits = new ArrayList<>();
            List<Tile> myRecyclers = new ArrayList<>();
            List<Tile> oppRecyclers = new ArrayList<>();

            int myMatter = in.nextInt();
            int oppMatter = in.nextInt();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Tile tile = new Tile(
                            x,
                            y,
                            in.nextInt(),
                            in.nextInt(),
                            in.nextInt(),
                            in.nextInt() == 1,
                            in.nextInt() == 1,
                            in.nextInt() == 1,
                            in.nextInt() == 1);

                    tiles.add(tile);
                    if (tile.owner == ME) {
                        myTiles.add(tile);
                        if (tile.units > 0) {
                            myUnits.add(tile);
                        } else if (tile.recycler) {
                            myRecyclers.add(tile);
                        }
                    } else if (tile.owner == OPP) {
                        oppTiles.add(tile);
                        if (tile.units > 0) {
                            oppUnits.add(tile);
                        } else if (tile.recycler) {
                            oppRecyclers.add(tile);
                        }
                    } else {
                        neutralTiles.add(tile);
                    }
                }
            }

            List<String> actions = new ArrayList<>();

            int myScrap = myMatter;
            while (myScrap >= 10) {
                boolean buildWorth = (random.nextInt() % 200 - turn) > 150;
                if (turn == 1 || buildWorth) {
                    Tile mostRecyclingPotential = myTiles.stream()
                            .filter(t -> t.canBuild)
                            .max(Comparator.comparingInt(t -> recyclingPotential(t.x, t.y, tiles)))
                            .orElse(null);
                    if (mostRecyclingPotential != null) {
                        actions.add("BUILD " + mostRecyclingPotential.x + " " + mostRecyclingPotential.y);
                        myScrap -= 10;
                    }
                } else {
                    Tile mostScrap = myTiles.stream()
                            .filter(t -> t.canSpawn)
                            .max(Comparator.comparingInt(t -> t.scrapAmount))
                            .orElse(null);
                    if (mostScrap != null) {
                        actions.add("SPAWN 1 " + mostScrap.x + " " + mostScrap.y);
                        myScrap -= 10;
                    }
                }
            }

            int oppIndex = 0;
            for (Tile tile : myUnits) {
                Tile target = null;
                for (Tile neighbour : neighbours(tile.x, tile.y, tiles)) {
                    if (neighbour.owner == OPP && neighbour.scrapAmount > 1) {
                        if (neighbour.units < tile.units) {
                            target = neighbour;
                            break;
                        }
                    }
                }
                for (Tile neighbour : neighbours(tile.x, tile.y, tiles)) {
                    if (neighbour.owner == NOONE && neighbour.scrapAmount > 1) {
                        if (neighbour.units < tile.units) {
                            target = neighbour;
                            break;
                        }
                    }
                }
                if (target == null) {
                    target = oppTiles.get(oppIndex);
                    oppIndex = (oppIndex + 1) % oppTiles.size();
                }
                if (target != null) {
                    int amount = tile.units;
                    actions.add(String.format("MOVE %d %d %d %d %d", amount, tile.x, tile.y, target.x, target.y));
                }
            }

            // To debug: System.err.println("Debug messages...");
            if (actions.isEmpty()) {
                System.out.println("WAIT");
            } else {
                System.out.println(String.join(";", actions));
            }
        }
    }

    static List<Tile> neighbours(int x, int y, List<Tile> tiles) {
        List<Tile> neighbours = new ArrayList<>(4);
        if (x-1 > 0) {
            neighbours.add(tiles.get(getIndexFromCoord(x - 1, y)));
        }
        if (y-1 > 0) {
            neighbours.add(tiles.get(getIndexFromCoord(x, y - 1)));
        }
        if (height < x + 1) {
            neighbours.add(tiles.get(getIndexFromCoord(x + 1, y)));
        }
        if (width < y + 1) {
            neighbours.add(tiles.get(getIndexFromCoord(x, y + 1)));
        }
        return neighbours;
    }

    static int getIndexFromCoord(int x, int y) {
        return y * width + x;
    }

    static int recyclingPotential(int x, int y, List<Tile> tiles) {
        int scrap = tiles.get(getIndexFromCoord(x,y)).scrapAmount;
        scrap += neighbours(x, y, tiles).stream().mapToInt(tile -> tile.scrapAmount).sum();
        return scrap;
    }
}