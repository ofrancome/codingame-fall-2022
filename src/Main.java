import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

class Tile {
    final int x;
    final int y;
    final int scrapAmount;
    final int owner;
    final int units;
    final boolean recycler;
    final boolean canBuild;
    final boolean canSpawn;
    final boolean inRangeOfRecycler;

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

//    public String key() {
//        return x+"-"+y;
//    }

}

class Player {

    static final int ME = 1;
    static final int OPP = 0;
    static final int NOONE = -1;
    private static int width;
    private static int height;

    public static void main(String[] args) throws IOException {
        player2();
    }

    private static void player2() throws IOException {
        long startGame = System.nanoTime();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.err.println("Time buffered: " + (System.nanoTime() - startGame) / 1000000 + "ms");
        StringTokenizer wANDh = new StringTokenizer(br.readLine());
        System.err.println("Time readline: " + (System.nanoTime() - startGame) / 1000000 + "ms");
        width = Integer.parseInt(wANDh.nextToken());
        height = Integer.parseInt(wANDh.nextToken());
        System.err.println("Time parse ints: " + (System.nanoTime() - startGame) / 1000000 + "ms");
        // game loop
        while (true) {
            long start = System.nanoTime();
            List<Tile> tiles = new ArrayList<>(width*height);
//            List<Tile> myTiles = new ArrayList<>(width*height);
            List<Tile> oppTiles = new ArrayList<>(width*height);
//            List<Tile> neutralTiles = new ArrayList<>();
            List<Tile> myUnits = new ArrayList<>(width*height);
//            List<Tile> oppUnits = new ArrayList<>();
//            List<Tile> myRecyclers = new ArrayList<>();
//            List<Tile> oppRecyclers = new ArrayList<>();
            StringTokenizer matters = new StringTokenizer(br.readLine());
            int myMatter = Integer.parseInt(matters.nextToken());
            int oppMatter = Integer.parseInt(matters.nextToken());
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    StringTokenizer tileValues = new StringTokenizer(br.readLine());
                    Tile tile = new Tile(
                            x,
                            y,
                            Integer.parseInt(tileValues.nextToken()),
                            Integer.parseInt(tileValues.nextToken()),
                            Integer.parseInt(tileValues.nextToken()),
                            tileValues.nextToken().equals("1"),
                            tileValues.nextToken().equals("1"),
                            tileValues.nextToken().equals("1"),
                            tileValues.nextToken().equals("1"));

                    tiles.add(tile);
                    if (tile.owner == ME) {
//                        myTiles.add(tile);
                        if (tile.units > 0) {
                            myUnits.add(tile);
                        }
//                        else if (tile.recycler) {
//                            myRecyclers.add(tile);
//                        }
                    } else if (tile.owner == OPP) {
                        oppTiles.add(tile);
//                        if (tile.units > 0) {
//                            oppUnits.add(tile);
//                        } else if (tile.recycler) {
//                            oppRecyclers.add(tile);
//                        }
//                    } else {
//                        neutralTiles.add(tile);
                    }
                }
            }
            System.err.println("Time Read: " + (System.nanoTime() - start) / 1000000 + "ms");
            StringBuilder actions = new StringBuilder();
            int myScrap = myMatter;
//            while (myScrap >= 10) {
//                Tile mostRecyclingPotential = myTiles.stream()
//                        .filter(t -> t.canBuild)
//                        .max(Comparator.comparingInt(t -> recyclingPotential(t.x, t.y, tiles)))
//                        .orElse(null);
//                if (mostRecyclingPotential != null) {
//                    actions.add("BUILD " + mostRecyclingPotential.x + " " + mostRecyclingPotential.y);
//                    myScrap -= 10;
//                }
//
////                Tile mostScrap = myTiles.stream()
////                        .filter(t -> t.canSpawn)
////                        .max(Comparator.comparingInt(t -> t.scrapAmount))
////                        .orElse(null);
////                if (mostScrap != null) {
////                    actions.add("SPAWN " + mostScrap.x + " " + mostScrap.y);
////                    myScrap -= 10;
////                }
//            }
            System.err.println("Time Build Spawn: " + (System.nanoTime() - start) / 1000000 + "ms");
            Tile target = null;
            for (Tile tile : myUnits) {
                for (Tile neighbour : neighbours(tile.x, tile.y, tiles)) {
                    if (neighbour.owner == OPP) {
                        if (neighbour.units < tile.units) {
                            target = neighbour;
                            break;
                        }
                    }
                }
                if (target == null) {
                    target = oppTiles.get((int) (Math.random() * oppTiles.size()));
                }
                if (target != null) {
                    int amount = tile.units;
                    actions.append(String.format("MOVE %d %d %d %d %d;", amount, tile.x, tile.y, target.x, target.y));
                }
            }

            if (actions.length() == 0) {
                System.out.println("WAIT");
            } else {
                System.out.print(actions.substring(0, actions.length() - 1));
            }
            System.err.println("Time Move: " + (System.nanoTime() - start) / 1000000 + "ms");
        }
    }


//    private static void player1() {
//        Scanner in = new Scanner(System.in);
//        int width = in.nextInt();
//        int height = in.nextInt();
//
//        // game loop
//        while (true) {
//            long start = System.nanoTime();
//            Map<String, Tile> tiles = new HashMap<>();
//            Map<String, Tile> myTiles = new HashMap<>();
//            Map<String, Tile> oppTiles = new HashMap<>();
//            Map<String, Tile> neutralTiles = new HashMap<>();
//            Map<String, Tile> myUnits = new HashMap<>();
//            Map<String, Tile> oppUnits = new HashMap<>();
//            Map<String, Tile> myRecyclers = new HashMap<>();
//            Map<String, Tile> oppRecyclers = new HashMap<>();
//
//            int myMatter = in.nextInt();
//            int oppMatter = in.nextInt();
//            for (int y = 0; y < height; y++) {
//                for (int x = 0; x < width; x++) {
//                    Tile tile = new Tile(
//                            x,
//                            y,
//                            in.nextInt(),
//                            in.nextInt(),
//                            in.nextInt(),
//                            in.nextInt() == 1,
//                            in.nextInt() == 1,
//                            in.nextInt() == 1,
//                            in.nextInt() == 1);
//
//                    tiles.put(tile.key(), tile);
//                    if (tile.owner == ME) {
//                        myTiles.put(tile.key(), tile);
//                        if (tile.units > 0) {
//                            myUnits.put(tile.key(), tile);
//                        } else if (tile.recycler) {
//                            myRecyclers.put(tile.key(), tile);
//                        }
//                    } else if (tile.owner == OPP) {
//                        oppTiles.put(tile.key(), tile);
//                        if (tile.units > 0) {
//                            oppUnits.put(tile.key(), tile);
//                        } else if (tile.recycler) {
//                            oppRecyclers.put(tile.key(), tile);
//                        }
//                    } else {
//                        neutralTiles.put(tile.key(), tile);
//                    }
//                }
//            }
//            System.err.println("Time Read: " + (System.nanoTime() - start) / 1000000 + "ms");
//            List<String> actions = new ArrayList<>();
//            int myScrap = myMatter;
//            while (myScrap >= 10) {
//                boolean shouldBuild = Math.random() < 0.5;
//                if (shouldBuild) {
//                    Tile mostRecyclingPotential = myTiles.values().stream()
//                            .filter(t -> t.canBuild)
//                            .max(Comparator.comparingInt(t -> recyclingPotential(t.x, t.y, tiles)))
//                            .orElse(null);
//                    if (mostRecyclingPotential != null) {
//                        actions.add("BUILD " + mostRecyclingPotential.x + " " + mostRecyclingPotential.y);
//                        myScrap -= 10;
//                        continue;
//                    }
//                }
//                Tile mostScrap = myTiles.values().stream()
//                        .filter(t -> t.canSpawn)
//                        .max(Comparator.comparingInt(t -> t.scrapAmount))
//                        .orElse(null);
//                if (mostScrap != null) {
//                    actions.add("SPAWN " + mostScrap.x + " " + mostScrap.y);
//                    myScrap -= 10;
//                }
//            }
//            System.err.println("Time Build Spawn: " + (System.nanoTime() - start) / 1000000 + "ms");
//            Tile target = null;
//            for (Tile tile : myUnits.values()) {
//                for(Tile neighbour : neighbours(tile.x, tile.y, tiles, width, height)) {
//                    if (neighbour.owner == OPP) {
//                        if (neighbour.units < tile.units) {
//                            target = neighbour;
//                            break;
//                        }
//                    }
//                }
//                if (target == null) {
//                    target = oppTiles.values().stream().min(Comparator.comparingInt(t -> distance(tile.x, tile.y, t.x, t.y))).get();
//                }
//                int amount = tile.units;
//                actions.add(String.format("MOVE %d %d %d %d %d", amount, tile.x, tile.y, target.x, target.y));
//            }
//
//            System.err.println("Time: " + (System.nanoTime() - start) / 1000000 + "ms");
//            if (actions.isEmpty()) {
//                System.out.println("WAIT");
//            } else {
//                System.out.print(String.join(";", actions));
//            }
//        }
//    }


//    private static int distance(int x, int y, int x1, int y1) {
//        return Math.abs(x - x1) + Math.abs(y - y1);
//    }

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

//    static int recyclingPotential(int x, int y, List<Tile> tiles) {
//        int scrap = tiles.get(getIndexFromCoord(x,y)).scrapAmount;
//        scrap += neighbours(x, y, tiles).stream().mapToInt(tile -> tile.scrapAmount).sum();
//        return scrap;
//    }
}