import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    public boolean isAdjacent(Tile target) {
        return Math.abs(x - target.x) + Math.abs(y - target.y) == 1;
    }
}

class Player {

    static final int ME = 1;
    static final int OPP = 0;
    static final int NOONE = -1;
    private static int width;
    private static int height;

    private static int turn = 0;

    private static final List<List<Integer>> neigbhoursList = new ArrayList<>();
    private static int ecoRecycl;
    private static int ecoTurn;

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

//        int ecoRecycl = 3;
//        int ecoTurn = 3;
        System.err.println("Init: " + (System.nanoTime() - start) / 1000000);
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
            int roundScrapTiles = 0;
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
                    if (tile.scrapAmount > 0) {
                        roundScrapTiles++;
                    }
                }
            }
            System.err.println("Read Turn: " + (System.nanoTime() - startTurn) / 1000000);
            List<String> actions = new ArrayList<>();
            if (turn == 1) {
                ecoRecycl = (roundScrapTiles / 40);
                ecoTurn = width;
                System.err.println("ecoRecycl = " + ecoRecycl);
                System.err.println("ecoTurn = " + ecoTurn);
                Tile topMost = myTiles.stream().min(Comparator.comparingInt(t -> t.y)).get();
                Tile bottomMost = myTiles.stream().max(Comparator.comparingInt(t -> t.y)).get();

                int direction = 1;
                for (Tile tile : myUnits) {
                    direction = tile.x < width/2 ? 1 : -1;
                    if (tile.y == topMost.y) {
                        if (tile.y > 1 && tiles.get(getIndexFromCoord(tile.x, tile.y - 1)).scrapAmount > 0) {
                            actions.add(String.format("MOVE %d %d %d %d %d", tile.units, tile.x, tile.y, tile.x, tile.y - 1));
                        } else {
                            actions.add(String.format("MOVE %d %d %d %d %d", tile.units, tile.x, tile.y, tile.x, tile.y + direction));
                        }
                    } else if (tile.y == bottomMost.y) {
                        if (tile.y < height - 1 && tiles.get(getIndexFromCoord(tile.x, tile.y + 1)).scrapAmount > 0) {
                            actions.add(String.format("MOVE %d %d %d %d %d", tile.units, tile.x, tile.y, tile.x, tile.y + 1));
                        } else {
                            actions.add(String.format("MOVE %d %d %d %d %d", tile.units, tile.x, tile.y, tile.x, tile.y + direction));
                        }

                    } else if (tile.x == topMost.x + direction) {
                        actions.add(String.format("MOVE %d %d %d %d %d", tile.units, tile.x, tile.y, tile.x + direction, tile.y));
                    } else {
                        actions.add(String.format("MOVE %d %d %d %d %d", tile.units, tile.x, tile.y, tile.x + direction, tile.y));
                    }
                }
                actions.add(String.format("SPAWN 1 %d %d", topMost.x + direction, topMost.y + 1));
            } else {
                startTurn = System.nanoTime();

                int myScrap = myMatter;
                boolean didSmth = true;
                int ecoRecyclSize = myRecyclers.size();
                while (myScrap >= 10 && didSmth) {
                    didSmth = false;
                    List<Tile> opportunities = buildOpportunity(myTiles, tiles);
                    if (!opportunities.isEmpty()) {
                        Tile opportunity = opportunities.get(0);
                        actions.add("BUILD " + opportunity.x + " " + opportunity.y);
                        opportunity.recycler = true;
                        opportunity.canBuild = false;
                        opportunity.canSpawn = false;
                        myScrap -= 10;
                        didSmth = true;
                        ecoRecyclSize++;
                    } else {
                        if (ecoRecyclSize < ecoRecycl && turn < ecoTurn) {
                            List<Tile> recycleTiles = myTiles.stream().filter(t -> t.canBuild && destroysOnlyHisTile(t.x, t.y, tiles)).collect(Collectors.toList());
                            recycleTiles.sort(Comparator.comparingInt((Tile t) -> t.scrapAmount).reversed());
                            if (!recycleTiles.isEmpty() && recyclingPotential(recycleTiles.get(0).x, recycleTiles.get(0).y, tiles) > 15) {
                                Tile recycle = recycleTiles.get(0);
                                recycle.recycler = true;
                                recycle.canBuild = false;
                                recycle.canSpawn = false;
                                actions.add("BUILD " + recycle.x + " " + recycle.y);
                                myScrap -= 10;
                                didSmth = true;
                                ecoRecyclSize++;
                            }
                        }
                    }
                    if (myScrap >= 10) {
//                    System.err.println("Trying to Spawn");
                        Tile spawnTile = null;
                        int amount = 1;
                        myTiles.sort(Comparator.comparingInt(t -> distance(t.x, t.y, width / 2, height / 2)));
                        for (Tile myTile : myTiles) {
                            if (myTile.canSpawn
                                    && myTile.scrapAmount > 1
                                    && neighbours(myTile.x, myTile.y, tiles).stream()
                                        .allMatch(t2 -> t2.owner == OPP)) {
                                spawnTile = myTile;
                                amount = myScrap / 10;
//                            System.err.println("Spawned at " + myTile.x + " " + myTile.y);
                                break;
                            }
                        }
                        if (spawnTile == null) {
                            for (Tile myTile : myTiles) {
                                if (myTile.canSpawn
                                        && myTile.scrapAmount > 1
                                        && neighbours(myTile.x, myTile.y, tiles).stream()
                                            .anyMatch(t2 -> (t2.owner == OPP && t2.scrapAmount > 0))) {
                                    spawnTile = myTile;
                                    amount = myScrap / 10;
//                                System.err.println("Spawned at " + myTile.x + " " + myTile.y);
                                    break;
                                }
                            }
                            if (spawnTile == null) {
                                for (Tile myTile : myTiles) {
                                    if (myTile.canSpawn
                                            && myTile.scrapAmount > 1
                                            && neighbours(myTile.x, myTile.y, tiles).stream()
                                                .anyMatch(t2 -> (t2.owner == NOONE && t2.scrapAmount > 0))) {
                                        spawnTile = myTile;
                                        myTile.canSpawn = false;
    //                                System.err.println("Spawned at " + myTile.x + " " + myTile.y);
                                        break;
                                    }
                                }
                            }
                        }
                        if (spawnTile != null) {
                            actions.add("SPAWN " + amount + " " + spawnTile.x + " " + spawnTile.y);
                            myScrap -= 10 * amount;
                            didSmth = true;
                        }
                    }
                }
                System.err.println("Build/Spawn: " + (System.nanoTime() - startTurn) / 1000000 + "ms - myScrap = " + myScrap);

                startTurn = System.nanoTime();
                int oppIndex = 0;
                for (Tile tile : myUnits) {
                    while (tile.units > 0) {

                        Tile target = null;
                        List<Tile> sortedNeighbours = neighbours(tile.x, tile.y, tiles);
                        sortedNeighbours.sort(Comparator.comparingInt(t -> distance(t.x, t.y, width / 2, t.y)));
                        for (Tile neighbour : sortedNeighbours) {
                            if (neighbour.owner == OPP && neighbour.scrapAmount > 0 && !neighbour.inRangeOfRecycler) {
//                                System.err.println("Taken - tile " + tile.x + " - " + tile.y);
                                target = neighbour;
                                break;
                            }
                        }
                        if (target == null) {
//                        System.err.println("3- tile " + tile.x + " - " + tile.y);
                            for (Tile neighbour : sortedNeighbours) {
                                if (neighbour.owner == NOONE && neighbour.scrapAmount > 0 && !neighbour.inRangeOfRecycler) {
//                                System.err.println("4- tile " + tile.x + " - " + tile.y);
                                    if (neighbours(neighbour.x, neighbour.y, tiles).stream()
                                                .anyMatch(t -> t.owner == OPP && t.units > tile.units)) {
                                        target = neighbour;
                                        break;
                                    }
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
                                if (neighbours(target.x, target.y, tiles).stream().anyMatch(oppUnits::contains)) {
                                    amount = tile.units;
                                } else {
                                    amount = 1;
                                }
                            } else {
                                amount = tile.units;
                            }
                            tile.units -= amount;
                            if (tile.isAdjacent(target)) {
                                target.owner = ME;
                            }
                            actions.add(String.format("MOVE %d %d %d %d %d", amount, tile.x, tile.y, target.x, target.y));
                        }
                    }
                }

                // To debug: System.err.println("Debug messages...");
            }
            System.err.println("Move: " + (System.nanoTime() - startTurn) / 1000000);
            if (actions.isEmpty()) {
                System.out.println("WAIT");
            } else {
                System.out.println(String.join(";", actions));
            }
        }
    }

    private static boolean destroysOnlyHisTile(int x, int y, List<Tile> tiles) {
        Tile candidate = tiles.get(getIndexFromCoord(x, y));
        return neighbours(x, y, tiles).stream().allMatch(t -> t.scrapAmount > candidate.scrapAmount || t.scrapAmount == 0);
    }

    private static boolean hasNo2libertyNeighbours(int x, int y, List<Tile> tiles) {
        return neighbours(x, y, tiles).stream().anyMatch(t2 -> liberty(t2.x, t2.y, tiles) <= 2);
    }

    private static int liberty(int x, int y, List<Tile> tiles) {
        return neighbours(x, y, tiles).stream().mapToInt(t2 -> t2.scrapAmount >= 1 ? 1 : 0).sum();
    }

    private static List<Tile> buildOpportunity(List<Tile> myTiles, List<Tile> tiles) {
        Stream<Tile> stream = getReversedStreamOnOddTurnsAndNormalStreamOnEvenTurns(myTiles);
        List<Tile> tilesToBuild = stream.filter(t -> t.canBuild && neighbours(t.x, t.y, tiles).stream().allMatch(t2 -> t2.owner == OPP && !t2.recycler))
                .sorted(Comparator.comparingInt((Tile t) -> recyclingPotential(t.x, t.y, tiles)).reversed()).collect(Collectors.toList());
        if (!tilesToBuild.isEmpty()) {
            return tilesToBuild;
        }
        stream = getReversedStreamOnOddTurnsAndNormalStreamOnEvenTurns(myTiles);
        tilesToBuild.addAll(stream.filter(t -> t.canBuild && neighbours(t.x, t.y, tiles).stream().anyMatch(t2 -> t2.owner == OPP && !t2.recycler)).collect(Collectors.toList()));
        tilesToBuild.sort(Comparator.comparingInt((Tile t) -> recyclingPotential(t.x, t.y, tiles)).reversed());
        if (!tilesToBuild.isEmpty()) {
            return tilesToBuild;
        }
        return Collections.emptyList();
    }

    private static Stream<Tile> getReversedStreamOnOddTurnsAndNormalStreamOnEvenTurns(List<Tile> myTiles) {
        return turn % 2 == 0 ? myTiles.stream() : IntStream.rangeClosed(0, myTiles.size() - 1).mapToObj(i -> myTiles.get(myTiles.size() - 1 - i));
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
        final int scrap = tiles.get(getIndexFromCoord(x,y)).scrapAmount;
        return scrap + neighbours(x, y, tiles).stream().mapToInt(tile -> Math.min(tile.scrapAmount, scrap)).sum();
    }

    static int distance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }
}