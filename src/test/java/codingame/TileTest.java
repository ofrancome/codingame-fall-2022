package codingame;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class TileTest {

    @Test
    void shouldBeAdjacent() {
        assertThat(new Tile(0, 0).isAdjacent(new Tile(0, 1))).isTrue();
    }

    @Test
    void shouldNotBeAdjacent() {
        assertThat(new Tile(0, 0).isAdjacent(new Tile(1, 1))).isFalse();
        assertThat(new Tile(0, 0).isAdjacent(new Tile(0, 2))).isFalse();
        assertThat(new Tile(0, 0).isAdjacent(new Tile(5, 5))).isFalse();
    }

    @Test
    void distanceShouldBe1() {
        assertThat(new Tile(0, 0).distance(0, 1)).isEqualTo(1);
    }

    @Test
    void distanceShouldBe0() {
        assertThat(new Tile(0, 0).distance(0, 0)).isEqualTo(0);
    }

    @Test
    void distanceShouldBe2() {
        assertThat(new Tile(0, 0).distance(1, 1)).isEqualTo(2);
    }
}