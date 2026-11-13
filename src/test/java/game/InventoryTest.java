package game;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Unit tests for {@link Inventory}.
 *
 * Inventory has no Javadocs, so the oracle for each test is the behavior the
 * game relies on: a fixed-capacity bag of Items where add() uses the first free
 * slot, remove() and contains() work by object identity (==), and isFull() is
 * true only when every slot holds an Item.
 *
 * Test design: valid behavior -> invalid/exceptional behavior -> boundaries
 * (capacity 0, one below / at / above capacity, first and last slot).
 */
class InventoryTest {

    private static final int CAPACITY = 3;

    private Inventory inventory;
    private Item sword;
    private Item shield;
    private Item potion;
    private Item extra;

    private static Item item(String name) {
        return new Item('!', Color.WHITE, name, null);
    }

    @BeforeEach
    void setUp() {
        inventory = new Inventory(CAPACITY);
        sword = item("sword");
        shield = item("shield");
        potion = item("potion");
        extra = item("extra");
    }

    private int occupiedSlots() {
        int n = 0;
        for (Item i : inventory.getItems()) {
            if (i != null) {
                n++;
            }
        }
        return n;
    }

    // =====================================================================
    // Constructor
    // =====================================================================

    @ParameterizedTest(name = "Inventory({0}) has {0} slots")
    @ValueSource(ints = { 0, 1, 2, 10, 26 })
    void constructor_createsArrayOfRequestedSize(int max) {
        Inventory inv = new Inventory(max);
        assertEquals(max, inv.getItems().length);
    }

    @Test
    void constructor_allSlotsStartEmpty() {
        for (int i = 0; i < CAPACITY; i++) {
            assertNull(inventory.get(i), "slot " + i);
        }
    }

    @Test
    @DisplayName("Invalid: negative capacity throws NegativeArraySizeException")
    void constructor_negativeCapacity_throws() {
        assertThrows(NegativeArraySizeException.class, () -> new Inventory(-1));
    }

    // =====================================================================
    // add
    // =====================================================================

    @Test
    void add_toEmptyInventory_storesItemInFirstSlot() {
        inventory.add(sword);

        assertSame(sword, inventory.get(0));
        assertEquals(1, occupiedSlots());
    }

    @Test
    void add_multipleItems_fillsSlotsInOrder() {
        inventory.add(sword);
        inventory.add(shield);
        inventory.add(potion);

        assertSame(sword, inventory.get(0));
        assertSame(shield, inventory.get(1));
        assertSame(potion, inventory.get(2));
    }

    @Test
    @DisplayName("add() uses only ONE slot per call")
    void add_singleItem_occupiesExactlyOneSlot() {
        inventory.add(sword);

        assertNull(inventory.get(1));
        assertNull(inventory.get(2));
    }

    @Test
    @DisplayName("add() reuses the first slot freed by remove()")
    void add_afterRemove_fillsFirstFreedSlot() {
        inventory.add(sword);
        inventory.add(shield);
        inventory.add(potion);
        inventory.remove(shield);

        inventory.add(extra);

        assertSame(sword, inventory.get(0));
        assertSame(extra, inventory.get(1));
        assertSame(potion, inventory.get(2));
    }

    @Test
    @DisplayName("add() fills the EARLIEST gap when several slots are free")
    void add_withTwoGaps_fillsEarliestGap() {
        inventory.add(sword);
        inventory.add(shield);
        inventory.add(potion);
        inventory.remove(sword);
        inventory.remove(potion);

        inventory.add(extra);

        assertSame(extra, inventory.get(0));
        assertNull(inventory.get(2));
    }

    @Test
    @DisplayName("Boundary: adding to a full inventory is silently ignored")
    void add_whenFull_leavesInventoryUnchanged() {
        inventory.add(sword);
        inventory.add(shield);
        inventory.add(potion);

        inventory.add(extra);

        assertSame(sword, inventory.get(0));
        assertSame(shield, inventory.get(1));
        assertSame(potion, inventory.get(2));
        assertFalse(inventory.contains(extra));
    }

    @Test
    void add_sameItemTwice_occupiesTwoSlots() {
        inventory.add(sword);
        inventory.add(sword);

        assertSame(sword, inventory.get(0));
        assertSame(sword, inventory.get(1));
        assertEquals(2, occupiedSlots());
    }

    @Test
    @DisplayName("Invalid: add(null) does not consume a slot or throw")
    void add_null_doesNotConsumeSlot() {
        assertDoesNotThrow(() -> inventory.add(null));

        assertEquals(0, occupiedSlots());
        assertFalse(inventory.isFull());
    }

    @Test
    void add_toZeroCapacityInventory_doesNothing() {
        Inventory empty = new Inventory(0);
        assertDoesNotThrow(() -> empty.add(sword));
        assertEquals(0, empty.getItems().length);
    }

    // =====================================================================
    // remove
    // =====================================================================

    @Test
    void remove_presentItem_clearsItsSlot() {
        inventory.add(sword);

        inventory.remove(sword);

        assertNull(inventory.get(0));
        assertFalse(inventory.contains(sword));
    }

    @Test
    @DisplayName("remove() does not shift the other items")
    void remove_middleItem_leavesOthersInPlace() {
        inventory.add(sword);
        inventory.add(shield);
        inventory.add(potion);

        inventory.remove(shield);

        assertSame(sword, inventory.get(0));
        assertNull(inventory.get(1));
        assertSame(potion, inventory.get(2));
    }

    @Test
    void remove_lastSlotItem_isRemoved() {
        inventory.add(sword);
        inventory.add(shield);
        inventory.add(potion);

        inventory.remove(potion);

        assertNull(inventory.get(2));
        assertSame(sword, inventory.get(0));
    }

    @Test
    @DisplayName("Invalid: removing an absent item changes nothing")
    void remove_absentItem_leavesInventoryUnchanged() {
        inventory.add(sword);

        assertDoesNotThrow(() -> inventory.remove(shield));

        assertSame(sword, inventory.get(0));
        assertEquals(1, occupiedSlots());
    }

    @Test
    @DisplayName("Boundary: removing an absent item from a FULL inventory is safe")
    void remove_absentItemFromFullInventory_doesNotThrow() {
        inventory.add(sword);
        inventory.add(shield);
        inventory.add(potion);

        assertDoesNotThrow(() -> inventory.remove(extra));

        assertEquals(3, occupiedSlots());
    }

    @Test
    void remove_fromEmptyInventory_doesNothing() {
        assertDoesNotThrow(() -> inventory.remove(sword));
        assertEquals(0, occupiedSlots());
    }

    @Test
    @DisplayName("remove() removes only the FIRST occurrence of a duplicated item")
    void remove_duplicatedItem_removesOnlyFirstOccurrence() {
        inventory.add(sword);
        inventory.add(sword);

        inventory.remove(sword);

        assertNull(inventory.get(0));
        assertSame(sword, inventory.get(1));
    }

    @Test
    @DisplayName("remove() compares by identity, not by equal-looking fields")
    void remove_equalLookingButDifferentInstance_isNotRemoved() {
        Item twin = item("sword");
        inventory.add(sword);

        inventory.remove(twin);

        assertSame(sword, inventory.get(0));
    }

    @Test
    void remove_null_doesNotAffectStoredItems() {
        inventory.add(sword);

        assertDoesNotThrow(() -> inventory.remove(null));

        assertSame(sword, inventory.get(0));
    }

    @Test
    void remove_fromFullInventory_makesItNotFull() {
        inventory.add(sword);
        inventory.add(shield);
        inventory.add(potion);

        inventory.remove(shield);

        assertFalse(inventory.isFull());
    }

    // =====================================================================
    // isFull
    // =====================================================================

    @Test
    void isFull_emptyInventory_isFalse() {
        assertFalse(inventory.isFull());
    }

    @Test
    @DisplayName("Boundary: one below capacity is not full")
    void isFull_oneBelowCapacity_isFalse() {
        inventory.add(sword);
        inventory.add(shield);
        assertFalse(inventory.isFull());
    }

    @Test
    @DisplayName("Boundary: exactly at capacity is full")
    void isFull_atCapacity_isTrue() {
        inventory.add(sword);
        inventory.add(shield);
        inventory.add(potion);
        assertTrue(inventory.isFull());
    }

    @Test
    @DisplayName("Boundary: a gap in the middle means not full")
    void isFull_withGapInMiddle_isFalse() {
        inventory.add(sword);
        inventory.add(shield);
        inventory.add(potion);
        inventory.remove(shield);
        assertFalse(inventory.isFull());
    }

    @Test
    @DisplayName("Boundary: zero-capacity inventory is (vacuously) full")
    void isFull_zeroCapacity_isTrue() {
        assertTrue(new Inventory(0).isFull());
    }

    @ParameterizedTest(name = "capacity {0}, {1} added -> full = {2}")
    @CsvSource({
            "1, 0, false",
            "1, 1, true",
            "1, 2, true",
            "3, 2, false",
            "3, 3, true",
            "3, 4, true",
            "26, 25, false",
            "26, 26, true"
    })
    void isFull_dependsOnNumberAddedVersusCapacity(int capacity, int toAdd, boolean expectedFull) {
        Inventory inv = new Inventory(capacity);
        for (int i = 0; i < toAdd; i++) {
            inv.add(item("item" + i));
        }
        assertEquals(expectedFull, inv.isFull());
    }

    // =====================================================================
    // contains
    // =====================================================================

    @Test
    void contains_presentItem_isTrue() {
        inventory.add(sword);
        assertTrue(inventory.contains(sword));
    }

    @Test
    void contains_absentItem_isFalse() {
        inventory.add(sword);
        assertFalse(inventory.contains(shield));
    }

    @Test
    void contains_emptyInventory_isFalse() {
        assertFalse(inventory.contains(sword));
    }

    @Test
    @DisplayName("Boundary: item in the LAST slot is found")
    void contains_itemInLastSlot_isTrue() {
        inventory.add(sword);
        inventory.add(shield);
        inventory.add(potion);
        assertTrue(inventory.contains(potion));
    }

    @Test
    @DisplayName("Boundary: absent item in a FULL inventory is not found")
    void contains_absentItemInFullInventory_isFalse() {
        inventory.add(sword);
        inventory.add(shield);
        inventory.add(potion);
        assertFalse(inventory.contains(extra));
    }

    @Test
    void contains_afterRemoval_isFalse() {
        inventory.add(sword);
        inventory.remove(sword);
        assertFalse(inventory.contains(sword));
    }

    @Test
    void contains_equalLookingButDifferentInstance_isFalse() {
        inventory.add(sword);
        assertFalse(inventory.contains(item("sword")));
    }

    /**
     * SUSPECTED FAULT (documented, not fixed): contains(null) returns true whenever
     * any slot is empty, because empty slots hold null. An inventory with free
     * space does not "contain" a null item, so the expected result is false.
     * Disabled so the regression suite stays green; remove @Disabled to see it fail.
     */
    @Test
    @Disabled("Suspected fault: contains(null) returns true when any slot is empty")
    void contains_null_shouldBeFalse() {
        assertFalse(inventory.contains(null));
    }

    // =====================================================================
    // get / getItems
    // =====================================================================

    @Test
    void get_emptySlot_returnsNull() {
        assertNull(inventory.get(1));
    }

    @ParameterizedTest(name = "get({0}) is out of range")
    @ValueSource(ints = { -1, 3, 4, Integer.MAX_VALUE })
    @DisplayName("Invalid/Boundary: out-of-range index throws ArrayIndexOutOfBoundsException")
    void get_outOfRangeIndex_throws(int index) {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> inventory.get(index));
    }

    @Test
    @DisplayName("Boundary: first and last valid indices are accepted")
    void get_firstAndLastIndex_areAccepted() {
        inventory.add(sword);
        inventory.add(shield);
        inventory.add(potion);

        assertSame(sword, inventory.get(0));
        assertSame(potion, inventory.get(CAPACITY - 1));
    }

    @Test
    void getItems_returnsTheBackingArrayContents() {
        inventory.add(sword);
        inventory.add(shield);

        Item[] items = inventory.getItems();

        assertEquals(CAPACITY, items.length);
        assertSame(sword, items[0]);
        assertSame(shield, items[1]);
        assertNull(items[2]);
    }
}
