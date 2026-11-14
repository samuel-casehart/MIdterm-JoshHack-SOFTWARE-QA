package game;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CreatureTest {

    private World world;
    private Creature creature;

    @BeforeEach
    void setUp() {
        Tile[][][] tiles = new Tile[3][3][1];

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                tiles[x][y][0] = Tile.FLOOR;
            }
        }

        world = new World(tiles);

        creature = new Creature(world, '@', Color.GRAY, "josh", 100, 10, 5);

        creature.x = 1;
        creature.y = 1;
        creature.z = 0;
    }

    @ParameterizedTest(name = "[{index}] pre={0}, amount={1} -> hp={2}, dead={3}")
    @DisplayName("modifyHp results in expected HP and death state")
    @CsvSource({
        // we use preamount to setup the creatures HP since not every test will want to have an initial hp of 100.
        // preAmount, amount, expectedHp, expectedDead
        "0,    -30,   70,  false",  //  damage reduces HP
        "-30,   10,   80,  false",  // healing adds HP (hp != maxHp, catches swapped args)
        "-30,   29,   99,  false",  //  maxHp - just below cap, added unchanged
        "0,      1,  100,  false",  //  maxHp - just above cap, held at maxHp
        "-30,   50,  100,  false",  //  maxHp - far above cap, held at maxHp
        "0,    -99,    1,  false",  // isDead - lowest living HP
        "0,   -100,    0,  true",   //  isDead - first dead value
        "0,  -1000,     ,  true",   //  isDead - overkill; no lower bound specified, HP not asserted
        "0,      0,  100,  false",  //  zero amount leaves HP unchanged
    })
    void modifyHp_resultsInExpectedHpAndDeathState(int preAmount, int amount, Integer expectedHp, boolean expectedDead) {
        creature.modifyHp(preAmount, "setup");
        creature.modifyHp(amount, "test");

        if (expectedHp != null)
            assertEquals(expectedHp.intValue(), creature.hp(), "hp after modifyHp");

        assertEquals(expectedDead, CombatRules.isDead(creature.hp()), "death state after modifyHp");
    }


    @ParameterizedTest(name = "[{index}] amount={0} -> corpse left={1}")
    @DisplayName("modifyHp leaves a corpse only when the creature dies")
    @CsvSource({
    // amount, expectedCorpse
    "-99,   false",   // HP 1: alive, so the death block must NOT run
    "-100,  true",    // HP 0: dead, so the death block MUST run
})
void modifyHp_leavesCorpseOnlyWhenDead(int amount, boolean expectedCorpse) {
    creature.modifyHp(amount, "test");

    Item itemAtFeet = world.item(1, 1, 0);

    assertEquals(expectedCorpse, itemAtFeet != null, "corpse present at creature's position");
    if (expectedCorpse)
        assertEquals("josh corpse", itemAtFeet.name());
}
}
