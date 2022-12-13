import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

class Tile {
    int x, y, scrapAmount, owner, units;
    boolean recycler, canBuild, canSpawn, inRangeOfRecycler;

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

    private static List<List<Integer>> neigbhoursList = new ArrayList<>();

    public static void main(String args[]) {
        long start = System.nanoTime();
        Scanner in = new Scanner(System.in);
        width = in.nextInt();
        height = in.nextInt();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                List<Integer> neighbours = new ArrayList<>();
                if (j-1 >= 0) {
                    neighbours.add(getIndexFromCoord(j - 1, i));
                }
                if (i-1 >= 0) {
                    neighbours.add(getIndexFromCoord(j, i - 1));
                }
                if (width > j + 1) {
                    neighbours.add(getIndexFromCoord(j + 1, i));
                }
                if (height > i + 1) {
                    neighbours.add(getIndexFromCoord(j, i + 1));
                }
                neigbhoursList.add(neighbours);
            }
        }
        System.err.println("Init: " + (System.nanoTime() - start) / 1000000);
        int turn = 0;
        // game loop
        while (true) {
            long startTurn = System.nanoTime();
            turn++;
            List<Tile> tiles = new ArrayList<>(width * height);
            List<Tile> myTiles = new ArrayList<>(width * height);
            List<Tile> oppTiles = new ArrayList<>(width * height);
            List<Tile> neutralTiles = new ArrayList<>(width * height);
            List<Tile> myUnits = new ArrayList<>(width * height);
            List<Tile> oppUnits = new ArrayList<>(width * height);
            List<Tile> myRecyclers = new ArrayList<>(width * height);
            List<Tile> oppRecyclers = new ArrayList<>(width * height);

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
            System.err.println("Read Turn: " + (System.nanoTime() - startTurn) / 1000000);
            startTurn = System.nanoTime();
            List<String> actions = new ArrayList<>();

            int myScrap = myMatter;
            boolean didSmth = true;
            while (myScrap >= 10 && didSmth) {
                didSmth = false;
                List<Tile> opportunities = buildOpportunity(myTiles, tiles);
                if (!opportunities.isEmpty()) {
                    Tile opportunity = opportunities.get(0);
                    actions.add("BUILD " + opportunity.x + " " + opportunity.y);
                    myScrap -= 10;
                }
                if (myScrap >= 10) {
//                    System.err.println("Trying to Spawn");
                    Tile spawnTile = null;
                    for (Tile myTile : myTiles) {
                        if (myTile.canSpawn && myTile.scrapAmount > 1 && myTile.units == 0 && neighbours(myTile.x, myTile.y, tiles).stream().anyMatch(t2 -> t2.owner == OPP)) {
                            spawnTile = myTile;
                            myTile.canSpawn = false;
//                            System.err.println("Spawned at " + myTile.x + " " + myTile.y);
                            break;
                        }
                    }
                    if (spawnTile == null) {
                        for (Tile myTile : myTiles) {
                            if (myTile.canSpawn && myTile.scrapAmount > 1 && myTile.units == 0 && neighbours(myTile.x, myTile.y, tiles).stream().anyMatch(t2 -> (t2.owner == NOONE && t2.scrapAmount > 0))) {
                                spawnTile = myTile;
                                myTile.canSpawn = false;
//                                System.err.println("Spawned at " + myTile.x + " " + myTile.y);
                                break;
                            }
                        }
                    }
                    if (spawnTile != null) {
                        actions.add("SPAWN 1 " + spawnTile.x + " " + spawnTile.y);
                        myScrap -= 10;
                        didSmth = true;
                    }
                }
            }
            System.err.println("Build/Spawn: " + (System.nanoTime() - startTurn) / 1000000);

            startTurn = System.nanoTime();
            int oppIndex = 0;
            for (Tile tile : myUnits) {
                while (tile.units > 0) {
//                    System.err.println("1 - tile " + tile.x + " - " + tile.y);
                    Tile target = null;
                    List<Tile> sortedNeighbours = neighbours(tile.x, tile.y, tiles);
                    sortedNeighbours.sort(Comparator.comparingInt(t -> distance(t.x, t.y, width / 2, height / 2)));
                    for (Tile neighbour : sortedNeighbours) {
                        if (neighbour.owner == OPP && neighbour.scrapAmount > 0 && !neighbour.inRangeOfRecycler) {
//                            System.err.println("2- tile " + tile.x + " - " + tile.y);
                            target = neighbour;
                            break;
                        }
                    }
                    if (target == null) {
//                        System.err.println("3- tile " + tile.x + " - " + tile.y);
                        for (Tile neighbour : sortedNeighbours) {
                            if (neighbour.owner == NOONE && neighbour.scrapAmount > 0 && !neighbour.inRangeOfRecycler) {
//                                System.err.println("4- tile " + tile.x + " - " + tile.y);
                                target = neighbour;
                                break;
                            }
                        }
                    }
                    if (target == null) {
//                        System.err.println("5- tile " + tile.x + " - " + tile.y);
                        target = oppTiles.get(oppIndex);
                        oppIndex = (oppIndex + 1) % oppTiles.size();
                    }
                    if (target != null) {
                        int amount;
                        if (target.owner == NOONE) {
                            amount = 1;
                        } else {
                            amount = tile.units;
                        }
                        tile.units -= amount;
                        target.owner = ME;
                        actions.add(String.format("MOVE %d %d %d %d %d", amount, tile.x, tile.y, target.x, target.y));
                    }
                }
            }

            // To debug: System.err.println("Debug messages...");

            System.err.println("Move: " + (System.nanoTime() - startTurn) / 1000000);
            if (actions.isEmpty()) {
                System.out.println("WAIT");
            } else {
                System.out.println(String.join(";", actions));
            }
        }
    }

    private static List<Tile> buildOpportunity(List<Tile> myTiles, List<Tile> tiles) {
        List<Tile> tilesToBuild = myTiles.stream().filter(t -> t.canBuild && neighbours(t.x, t.y, tiles).stream().allMatch(t2 -> t2.owner == OPP)).collect(Collectors.toList());
        if (!tilesToBuild.isEmpty()) {
            return tilesToBuild;
        }
        tilesToBuild.addAll(myTiles.stream().filter(t -> t.canBuild && neighbours(t.x, t.y, tiles).stream().anyMatch(t2 -> t2.owner == OPP)).collect(Collectors.toList()));
        if (!tilesToBuild.isEmpty()) {
            return tilesToBuild;
        }
        return Collections.emptyList();
    }

    static List<Tile> neighbours(int x, int y, List<Tile> tiles) {
        List<Tile> neighbours = new ArrayList<>(4);
        for (Integer i : neigbhoursList.get(getIndexFromCoord(x, y))) {
            neighbours.add(tiles.get(i));
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

    static int distance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }
}