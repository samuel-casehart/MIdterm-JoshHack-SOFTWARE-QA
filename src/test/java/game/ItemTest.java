package game;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ItemTest {

    private Item item;

    @BeforeEach
    void setUp() {
        item = new Item('/', Color.RED, "dagger", "shiny blade");
    }

    @Test
    void constructor_storesFields() {
        assertEquals('/', item.glyph());
        assertEquals(Color.RED, item.color());
        assertEquals("dagger", item.name());
        assertEquals("shiny blade", item.appearance());
    }

    @Test
    void constructor_nullAppearance_usesName() {
        assertEquals("dagger", new Item('/', Color.RED, "dagger", null).appearance());
    }

    @Test
    void constructor_defaults() {
        assertEquals(0, item.foodValue());
        assertEquals(0, item.attackValue());
        assertEquals(0, item.defenseValue());
        assertEquals(0, item.rangedAttackValue());
        assertEquals(1, item.thrownAttackValue(), "thrown attack defaults to 1");
        assertNull(item.quaffEffect());
        assertNotNull(item.writtenSpells());
        assertTrue(item.writtenSpells().isEmpty());
    }

    @Test
    void modifyValues_addToCurrentValueAndAllowNegativeChanges() {
        item.modifyFoodValue(5);
        item.modifyFoodValue(-2);
        item.modifyAttackValue(4);
        item.modifyDefenseValue(3);
        item.modifyThrownAttackValue(2);
        item.modifyRangedAttackValue(6);

        assertEquals(3, item.foodValue());
        assertEquals(4, item.attackValue());
        assertEquals(3, item.defenseValue());
        assertEquals(3, item.thrownAttackValue());
        assertEquals(6, item.rangedAttackValue());
    }

    @Test
    void setQuaffEffect_storesEffect() {
        Effect effect = new Effect(5);
        item.setQuaffEffect(effect);
        assertSame(effect, item.quaffEffect());
    }

    @Test
    void addWrittenSpell_appendsSpellWithGivenValues() {
        item.addWrittenSpell("blink", 4, new Effect(3));

        assertEquals(1, item.writtenSpells().size());
        Spell spell = item.writtenSpells().get(0);
        assertEquals("blink", spell.name());
        assertEquals(4, spell.manaCost());
    }

    // ---------- details() ----------

    @Test
    void details_defaultItem_isEmpty() {
        assertEquals("", item.details());
    }

    @Test
    void details_attack() {
        item.modifyAttackValue(3);
        assertEquals("  attack:3", item.details());
    }

    @Test
    void details_thrownOnlyWhenNotDefault() {
        item.modifyThrownAttackValue(2);
        assertEquals("  thrown:3", item.details());
    }

    @Test
    void details_rangedOnlyWhenPositive() {
        item.modifyRangedAttackValue(2);
        assertEquals("  ranged:2", item.details());
    }

    @Test
    void details_negativeRanged_isNotShown() {
        item.modifyRangedAttackValue(-1);
        assertEquals("", item.details());
    }

    @Test
    void details_defense_includingNegative() {
        item.modifyDefenseValue(-2);
        assertEquals("  defense:-2", item.details());
    }

    @Test
    void details_food() {
        item.modifyFoodValue(40);
        assertEquals("  food:40", item.details());
    }

    @Test
    void details_allValues_inFixedOrder() {
        item.modifyAttackValue(1);
        item.modifyThrownAttackValue(1);
        item.modifyRangedAttackValue(1);
        item.modifyDefenseValue(1);
        item.modifyFoodValue(1);
        assertEquals("  attack:1  thrown:2  ranged:1  defense:1  food:1", item.details());
    }
}
