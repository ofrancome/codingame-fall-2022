package codingame;


import codingame.io.InputTracer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Main game class
 **/
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
    private static GameBoard gameboard;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        readGameInput(in);
        while (true) {
            gameTurn(in, System.out, System.err);
            turn++;
        }
    }

    static void gameTurn(Scanner in, PrintStream out, PrintStream err){
        long startTurn = System.nanoTime();
        readTurnInput(in);
        List<String> actions = new ArrayList<>();
        if (turn == 1) {
            ecoRecycl = (gameboard.getRoundScrapTiles() / 40);
            ecoTurn = width;
            err.println("ecoRecycl = " + ecoRecycl);
            err.println("ecoTurn = " + ecoTurn);
            Tile topMost = gameboard.getMyTiles().stream().min(Comparator.comparingInt(t -> t.y)).get();
            Tile bottomMost = gameboard.getMyTiles().stream().max(Comparator.comparingInt(t -> t.y)).get();

            int direction = 1;
            for (Tile tile : gameboard.getMyUnits()) {
                direction = tile.x < width/2 ? 1 : -1;
                if (tile.y == topMost.y) {
                    if (tile.y > 1 && gameboard.getTiles().get(getIndexFromCoord(tile.x, tile.y - 1)).scrapAmount > 0) {
                        actions.add(String.format("MOVE %d %d %d %d %d", tile.units, tile.x, tile.y, tile.x, tile.y - 1));
                    } else {
                        actions.add(String.format("MOVE %d %d %d %d %d", tile.units, tile.x, tile.y, tile.x, tile.y + direction));
                    }
                } else if (tile.y == bottomMost.y) {
                    if (tile.y < height - 1 && gameboard.getTiles().get(getIndexFromCoord(tile.x, tile.y + 1)).scrapAmount > 0) {
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

            int myScrap = gameboard.getMyMatter();
            boolean didSmth = true;
            int ecoRecyclSize = gameboard.getMyRecyclers().size();
            while (myScrap >= 10 && didSmth) {
                didSmth = false;
                List<Tile> opportunities = buildOpportunity();
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
                        List<Tile> recycleTiles = gameboard.getMyTiles().stream().filter(t -> t.canBuild && destroysOnlyHisTile(t.x, t.y)).collect(Collectors.toList());
                        recycleTiles.sort(Comparator.comparingInt((Tile t) -> t.scrapAmount).reversed());
                        if (!recycleTiles.isEmpty() && recyclingPotential(recycleTiles.get(0).x, recycleTiles.get(0).y) > 15) {
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
                    Tile spawnTile = null;
                    int amount = 1;
                    gameboard.getMyTiles().sort(Comparator.comparingInt(t -> t.distance(width / 2, height / 2)));
                    for (Tile myTile : gameboard.getMyTiles()) {
                        if (myTile.canSpawn
                                && ((myTile.scrapAmount == 1 && !myTile.inRangeOfRecycler) || myTile.scrapAmount > 1)
                                && neighbours(myTile.x, myTile.y).stream()
                                .allMatch(t2 -> t2.owner == OPP && !t2.inRangeOfRecycler)) {
                            spawnTile = myTile;
                            amount = Math.max(amount, myScrap / 20);
                            break;
                        }
                    }
                    if (spawnTile == null) {
                        for (Tile myTile : gameboard.getMyTiles()) {
                            if (myTile.canSpawn
                                    && ((myTile.scrapAmount == 1 && !myTile.inRangeOfRecycler) || myTile.scrapAmount > 1)
                                    && neighbours(myTile.x, myTile.y).stream()
                                    .anyMatch(t2 -> (t2.owner == OPP && t2.scrapAmount > 0 && !t2.inRangeOfRecycler))) {
                                spawnTile = myTile;
                                amount = Math.max(amount, myScrap / 20);
                                break;
                            }
                        }
                        if (spawnTile == null) {
                            for (Tile myTile : gameboard.getMyTiles()) {
                                if (myTile.canSpawn
                                        && ((myTile.scrapAmount == 1 && !myTile.inRangeOfRecycler) || myTile.scrapAmount > 1)
                                        && neighbours(myTile.x, myTile.y).stream()
                                        .anyMatch(t2 -> (t2.owner == NOONE && t2.scrapAmount > 0))) {
                                    spawnTile = myTile;
                                    myTile.canSpawn = false;
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
            err.println("Build/Spawn: " + (System.nanoTime() - startTurn) / 1000000 + "ms - myScrap = " + myScrap);

            startTurn = System.nanoTime();
            int oppIndex = 0;

            boolean hasReachTop = false;
            boolean hasReachBottom = false;
            for (int i = 0; i < width; ++i) {
                if (gameboard.getTiles().get(getIndexFromCoord(i, 0)).owner == ME) {
                    hasReachTop = true;
                    break;
                }
            }

            for (int i = 0; i < width; ++i) {
                if (gameboard.getTiles().get(getIndexFromCoord(i, height - 1)).owner == ME) {
                    hasReachBottom = true;
                    break;
                }
            }

            if (!hasReachTop) {
                Tile topMost = gameboard.getMyTiles().stream().min(Comparator.comparingInt(t -> t.y)).get();
                actions.add(String.format("MOVE %d %d %d %d %d", 1, topMost.x, topMost.y, topMost.x, 0));
                topMost.units -= 1;
            }
            if (!hasReachBottom) {
                Tile bottomMost = gameboard.getMyTiles().stream().max(Comparator.comparingInt(t -> t.y)).get();
                actions.add(String.format("MOVE %d %d %d %d %d", 1, bottomMost.x, bottomMost.y, bottomMost.x, height-1));
                bottomMost.units -= 1;
            }

            for (Tile tile : gameboard.getMyUnits()) {
                while (tile.units > 0) {

                    Tile target = null;
                    List<Tile> sortedNeighbours = neighbours(tile.x, tile.y);
                    sortedNeighbours.sort(Comparator.comparingInt(t -> t.distance(width / 2, t.y)));
                    for (Tile neighbour : sortedNeighbours) {
                        if (neighbour.owner == OPP && neighbour.scrapAmount > 0 && !neighbour.inRangeOfRecycler) {
                            target = neighbour;
                            break;
                        }
                    }
                    if (target == null) {
                        for (Tile neighbour : sortedNeighbours) {
                            if (neighbour.owner == NOONE && neighbour.scrapAmount > 0 && !neighbour.inRangeOfRecycler) {
                                if (neighbours(neighbour.x, neighbour.y).stream()
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
                        for (Tile neighbour : sortedNeighbours) {
                            if (neighbour.owner == NOONE && neighbour.scrapAmount > 1) {
                                if (neighbours(neighbour.x, neighbour.y).stream()
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
                        target = gameboard.getOppTiles().get(oppIndex);
                        oppIndex = (oppIndex + 1) % gameboard.getOppTiles().size();
                    }
                    if (target != null) {
                        int amount;
                        if (target.owner == NOONE) {
                            if (neighbours(target.x, target.y).stream().anyMatch(gameboard.getOppUnits()::contains)) {
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

        }
        err.println("Move: " + (System.nanoTime() - startTurn) / 1000000);
        if (actions.isEmpty()) {
            out.println("WAIT");
        } else {
            out.println(String.join(";", actions));
        }
    }

    static void readGameInput(Scanner input){
        InputTracer in = new InputTracer(input);

        // Read here the game inputs...
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
    }

    static void readTurnInput(Scanner input){
        InputTracer in = new InputTracer(input);

        // Read here the turn inputs...
        List<Tile> tiles = new ArrayList<>();
        List<Tile> myTiles = new ArrayList<>();
        List<Tile> oppTiles = new ArrayList<>();
        List<Tile> neutralTiles = new ArrayList<>();
        List<Tile> myUnits = new ArrayList<>();
        List<Tile> oppUnits = new ArrayList<>();
        List<Tile> myRecyclers = new ArrayList<>();
        List<Tile> oppRecyclers = new ArrayList<>();
        int roundScrapTiles;
        int myMatter;
        int oppMatter;
        myMatter = in.nextInt();
        oppMatter = in.nextInt();
        roundScrapTiles = 0;
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
        gameboard = new GameBoard(tiles, myTiles, oppTiles, neutralTiles, myUnits, oppUnits, myRecyclers, oppRecyclers,  roundScrapTiles, myMatter, oppMatter);
    }

    static int getIndexFromCoord(int x, int y) {
        return y * width + x;
    }

    private static List<Tile> buildOpportunity() {
        Stream<Tile> stream = getReversedMyTilesStreamOnOddTurnsAndNormalStreamOnEvenTurns();
        List<Tile> tilesToBuild = stream.filter(t ->
                        t.canBuild &&
                                (t.scrapAmount > 1 ||(t.scrapAmount == 1 && !t.inRangeOfRecycler)) &&
                                neighbours(t.x, t.y).stream().allMatch(t2 -> t2.owner == OPP && !t2.recycler) &&
                                neighbours(t.x, t.y).stream().anyMatch(t2 -> t2.owner == OPP && t2.units > 0))
                .sorted(Comparator.comparingInt((Tile t) -> recyclingPotential(t.x, t.y)).reversed()).collect(Collectors.toList());
        if (!tilesToBuild.isEmpty()) {
            return tilesToBuild;
        }
        stream = getReversedMyTilesStreamOnOddTurnsAndNormalStreamOnEvenTurns();
        tilesToBuild.addAll(stream.filter(t -> t.canBuild &&
                (t.scrapAmount > 1 ||(t.scrapAmount == 1 && !t.inRangeOfRecycler)) &&
                neighbours(t.x, t.y).stream().anyMatch(t2 -> t2.owner == OPP && !t2.recycler) &&
                neighbours(t.x, t.y).stream().anyMatch(t2 -> t2.owner == OPP && t2.units > 0))
                .collect(Collectors.toList()));
        tilesToBuild.sort(Comparator.comparingInt((Tile t) -> recyclingPotential(t.x, t.y)).reversed());
        if (!tilesToBuild.isEmpty()) {
            return tilesToBuild;
        }
        stream = getReversedMyTilesStreamOnOddTurnsAndNormalStreamOnEvenTurns();
        tilesToBuild.addAll(stream.filter(t -> t.canBuild &&
                (t.scrapAmount > 1 ||(t.scrapAmount == 1 && !t.inRangeOfRecycler)) && neighbours(t.x, t.y).stream().anyMatch(t2 -> t2.owner == OPP && !t2.recycler)).collect(Collectors.toList()));
        tilesToBuild.sort(Comparator.comparingInt((Tile t) -> recyclingPotential(t.x, t.y)).reversed());
        if (!tilesToBuild.isEmpty()) {
            return tilesToBuild;
        }
        return Collections.emptyList();
    }

    private static Stream<Tile> getReversedMyTilesStreamOnOddTurnsAndNormalStreamOnEvenTurns() {
        return turn % 2 == 0 ? gameboard.getMyTiles().stream() : IntStream.rangeClosed(0, gameboard.getMyTiles().size() - 1).mapToObj(i -> gameboard.getMyTiles().get(gameboard.getMyTiles().size() - 1 - i));
    }

    static int recyclingPotential(int x, int y) {
        final int scrap = gameboard.getTiles().get(getIndexFromCoord(x,y)).scrapAmount;
        return scrap + neighbours(x, y).stream().mapToInt(tile -> Math.min(tile.scrapAmount, scrap)).sum();
    }

    static List<Tile> neighbours(int x, int y) {
        List<Tile> neighbours = new ArrayList<>(4);
        for (Integer i : neigbhoursList.get(getIndexFromCoord(x, y))) {
            neighbours.add(gameboard.getTiles().get(i));
        }
        return neighbours;
    }

    private static boolean destroysOnlyHisTile(int x, int y) {
        Tile candidate = gameboard.getTiles().get(getIndexFromCoord(x, y));
        return neighbours(x, y).stream().allMatch(t -> t.scrapAmount > candidate.scrapAmount || t.scrapAmount == 0);
    }
}