package codingame.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TimerTest {

    @Test
    public void getMilliseconds() throws InterruptedException {
        Timer.reset();
        Thread.sleep(100);
        assertThat(Timer.getMilliseconds()).isGreaterThan(100);
    }

}
