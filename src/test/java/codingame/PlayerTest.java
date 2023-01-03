package codingame;

import codingame.io.MockInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

public class PlayerTest {

    private static final String GAME_INPUT_SAMPLE = "Write here a game input sample";
    private static final String TURN_INPUT_SAMPLE = "Write here a turn input sample";

    private ByteArrayOutputStream testOut;
    private ByteArrayOutputStream testErr;

    @BeforeEach
    public void setUpOutput() {
        testOut = new ByteArrayOutputStream();
        testErr = new ByteArrayOutputStream();
    }

    @Test
    public void simulateGameTurn(){
        // given: turn input data
        Scanner input = MockInput.mock(TURN_INPUT_SAMPLE);
        // when: playing a game turn
        Player.gameTurn(input, new PrintStream(testOut), new PrintStream(testErr));
        // then: ...
        // execute this test in debug mode to debug a game turn !
    }

    @Test
    public void readGameInput(){
        // given: initial data to parse and store
        Scanner input = MockInput.mock(GAME_INPUT_SAMPLE);

        // when: reading the input
        Player.readGameInput(input);

        // then: ...
        // write assertions here
    }

    @Test
    public void readTurnInput(){
        // given: initial data to parse and store
        Scanner input = MockInput.mock(TURN_INPUT_SAMPLE);

        // when: reading the input
        Player.readTurnInput(input);

        // then: ...
        // write assertions here
    }

    private void assertOutputIs(String expected){
        assertThat(testOut.toString()).isEqualTo(expected+System.lineSeparator());
    }

}
