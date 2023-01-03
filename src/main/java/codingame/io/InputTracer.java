package codingame.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class InputTracer {

    private final Scanner input;
    private final List<String> readLines;

    public InputTracer(Scanner input) {
        this.input = input;
        readLines = new ArrayList<>();
    }

    public String[] nextLine(){
        String line = input.nextLine();
        readLines.add(line);
        return line.split(" ");
    }

    public int[] nextLineAsInts(){
        return Arrays.stream(this.nextLine())
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    public String nextLineAsSingleString(){
        String line = input.nextLine();
        readLines.add(line);
        return line;
    }

    public int nextLineAsSingleInt(){
        String line = input.nextLine();
        readLines.add(line);
        return Integer.parseInt(line);
    }

    public int nextInt(){
        int i = input.nextInt();
        readLines.add(String.valueOf(i));
        return i;
    }

    public String trace(){
        return String.join("\\n", readLines);
    }
}