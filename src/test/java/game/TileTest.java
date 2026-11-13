package game;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TileTest {

    @ParameterizedTest(name = "{0}: isGround = {1}, isDiggable = {2}")
    @CsvSource({
            "FLOOR,       true,  false",
            "WALL,        false, true",
            "BOUNDS,      false, false",
            "STAIRS_DOWN, true,  false",
            "STAIRS_UP,   true,  false",
            "UNKNOWN,     true,  false"
    })
    void groundAndDiggable_perTileType(Tile tile, boolean ground, boolean diggable) {
        assertEquals(ground, tile.isGround());
        assertEquals(diggable, tile.isDiggable());
    }

    @Test
    void glyphs_ofSpecialTiles() {
        assertEquals('>', Tile.STAIRS_DOWN.glyph());
        assertEquals('<', Tile.STAIRS_UP.glyph());
        assertEquals('x', Tile.BOUNDS.glyph());
        assertEquals(' ', Tile.UNKNOWN.glyph());
    }

    @Test
    void everyTile_hasColorAndDescription() {
        for (Tile t : Tile.values()) {
            assertNotNull(t.color(), t.name());
            assertNotNull(t.details(), t.name());
            assertFalse(t.details().isEmpty(), t.name());
        }
    }

    @Test
    void details_unknownTile() {
        assertEquals("(unknown)", Tile.UNKNOWN.details());
    }
}
