package game;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class CombatRulesTest {

    private static final int MAX_HP = 100;

    @Test
    @DisplayName("T1: healing that lands 1 below the cap is added exactly")
    void healing_justBelowCap_addsAmount() {
        int newHp = CombatRules.calculateNewHp(90, MAX_HP, 9);

        assertEquals(99, newHp);
        assertFalse(CombatRules.isDead(newHp));
    }

    @Test
    @DisplayName("T2: healing past maxHp from full HP is capped at maxHp")
    void healing_pastCapFromFullHp_isCappedAtMaxHp() {
        int newHp = CombatRules.calculateNewHp(100, MAX_HP, 1);

        assertEquals(MAX_HP, newHp);
        assertFalse(CombatRules.isDead(newHp));
    }

    @Test
    @DisplayName("T3: damage from full HP down to 1 leaves the creature alive")
    void damage_fromFullHpToOne_isAlive() {
        int newHp = CombatRules.calculateNewHp(100, MAX_HP, -99);

        assertEquals(1, newHp);
        assertFalse(CombatRules.isDead(newHp), "1 HP is the lowest living value");
    }

    @Test
    @DisplayName("T4: losing the last HP (result exactly 0) is dead")
    void damage_fromOneToZero_isDead() {
        int newHp = CombatRules.calculateNewHp(1, MAX_HP, -1);

        assertEquals(0, newHp);
        assertTrue(CombatRules.isDead(newHp), "0 HP is the first dead value");
    }

    @Test
    @DisplayName("T5: damage reducing below 0 is dead")
    void damage_belowZero_isDead() {
        int newHp = CombatRules.calculateNewHp(10, MAX_HP, -20);
        assertTrue(CombatRules.isDead(newHp));
    }

    @Test
    @DisplayName("T6: zero amount leaves HP unchanged")
    void zeroAmount_leavesHpUnchanged() {
        int newHp = CombatRules.calculateNewHp(50, MAX_HP, 0);

        assertEquals(50, newHp);
        assertFalse(CombatRules.isDead(newHp));
    }
}
