/* Glazed Lists                                                 (c) 2003-2005 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/
package ca.odell.glazedlists;

// for being a JUnit test case
import junit.framework.*;
// standard collections
import java.util.*;

/**
 * Verifies that {@link SelectionList} works as expected.
 *
 * @author <a href="mailto:kevin@swank.ca">Kevin Maltby</a>
 */
public class SelectionListTest extends TestCase {

    /** to generate some random values */
    private Random dice = new Random(167);

    /** the selection list */
    private SelectionList source = null;

    /** the list of selected elements */
    private EventList selectedList = null;

    /** the list of deselected elements */
    private EventList deselectedList = null;

    /**
     * Prepare for the test.
     */
    public void setUp() {
        source = new SelectionList(new BasicEventList());
        selectedList = source.getSelected();
        deselectedList = source.getDeselected();
        source.addListEventListener(new ConsistencyTestList(source, "SelectionList: ", false));
        selectedList.addListEventListener(new ConsistencyTestList(selectedList, "selected: ", false));
        deselectedList.addListEventListener(new ConsistencyTestList(deselectedList, "deselected: ", false));
    }

    /**
     * Clean up after the test.
     */
    public void tearDown() {
        source.dispose();
        source = null;
        selectedList = null;
        deselectedList = null;
    }

    /**
     * Tests selecting all elements.
     */
    public void testSelectAll() {
        source.add(0, new Integer(15));
        source.add(1, new Integer(155));
        source.add(2, new Integer(1555));
        source.add(0, new Integer(1));

        // select on a completely deselected list
        source.selectAll();
        assertEquals(source.size(), selectedList.size());
        assertEquals(0, deselectedList.size());

        // select on an already selected list
        source.selectAll();
        assertEquals(source.size(), selectedList.size());
        assertEquals(0, deselectedList.size());
    }

    /**
     * Test deselecting all elements
     */
    public void testDeselectAll() {
        source.add(0, new Integer(15));
        source.add(1, new Integer(155));
        source.add(2, new Integer(1555));
        source.add(0, new Integer(1));

        // deselect on an already deselected list
        source.deselectAll();
        assertEquals(0, selectedList.size());
        assertEquals(source.size(), deselectedList.size());

        // deselect on a completely selected list
        source.selectAll();
        source.deselectAll();
        assertEquals(0, selectedList.size());
        assertEquals(source.size(), deselectedList.size());
    }

    /**
     * Tests that adding to the source affects the lists in the expected way
     * for the default selection mode, which is MULTIPLE_INTERVAL_SELECTION_DEFENSIVE.
     */
    public void testDefaultSelectionMode() {
        source.add(0, new Integer(15));
        assertEquals(0, selectedList.size());
        assertEquals(source.size(), deselectedList.size());
        source.select(0);

        source.add(1, new Integer(155));
        source.add(2, new Integer(1555));
        source.add(0, new Integer(1));

        assertEquals(1, selectedList.size());
        assertEquals(new Integer(15), selectedList.get(0));
        assertEquals(3, deselectedList.size());
        assertEquals(new Integer(1), deselectedList.get(0));
        assertEquals(new Integer(155), deselectedList.get(1));
        assertEquals(new Integer(1555), deselectedList.get(2));
    }

    /**
     * Tests that adding to the source affects the lists in the expected way
     * for the MULTIPLE_INTERVAL_SELECTION mode.
     */
    public void testMultipleIntervalSelectionMode() {
        source.setSelectionMode(SelectionList.MULTIPLE_INTERVAL_SELECTION);
        source.add(0, new Integer(15));
        assertEquals(0, selectedList.size());
        assertEquals(source.size(), deselectedList.size());
        source.select(0);

        source.add(1, new Integer(155));
        source.add(2, new Integer(1555));
        source.add(0, new Integer(1));

        assertEquals(2, selectedList.size());
        assertEquals(new Integer(1), selectedList.get(0));
        assertEquals(new Integer(15), selectedList.get(1));
        assertEquals(2, deselectedList.size());
        assertEquals(new Integer(155), deselectedList.get(0));
        assertEquals(new Integer(1555), deselectedList.get(1));
    }

    /**
     * Test setting selection by index.
     */
    public void testSettingSelectionByIndex() {
        for(int i = 0; i < 20; i++) {
            source.add(new Integer(i));
        }

        for(int i = 0;i < 100; i++) {
            int selectionIndex = dice.nextInt(20);
            source.setSelection(selectionIndex);
            assertEquals(1, selectedList.size());
            assertEquals(source.get(selectionIndex), selectedList.get(0));
            assertEquals(true, source.isSelected(selectionIndex));
            assertEquals(19, deselectedList.size());
        }
    }

    /**
     * Test adding to selection by index.
     */
    public void testSelectingByIndex() {
        for(int i = 0; i < 20; i++) {
            source.add(new Integer(i));
        }

        int oldSize = 0;
        for(int i = 0;i < 100; i++) {
            int selectionIndex = dice.nextInt(20);
            boolean wasSelected = source.isSelected(selectionIndex);
            source.select(selectionIndex);
            if(!wasSelected) oldSize++;
            assertEquals(oldSize, selectedList.size());
            assertEquals(20 - oldSize, deselectedList.size());
            assertEquals(true, source.isSelected(selectionIndex));

            if(selectedList.size() == 20) {
                source.deselectAll();
                oldSize = 0;
            }
        }
    }

    /**
     * Test deselecting by index
     */
    public void testDeselectingByIndex() {
        for(int i = 0; i < 20; i++) {
            source.add(new Integer(i));
        }
        source.selectAll();

        int oldSize = 0;
        for(int i = 0;i < 100; i++) {
            int selectionIndex = dice.nextInt(20);
            boolean wasSelected = source.isSelected(selectionIndex);
            source.deselect(selectionIndex);
            if(wasSelected) oldSize++;
            assertEquals(oldSize, deselectedList.size());
            assertEquals(20 - oldSize, selectedList.size());
            assertEquals(false, source.isSelected(selectionIndex));

            if(deselectedList.size() == 20) {
                source.selectAll();
                oldSize = 0;
            }
        }
    }

    /**
     * Test setting selection with ranges
     */
    public void testSettingSelectionRange() {
        for(int i = 0; i < 20; i++) {
            source.add(new Integer(i));
        }

        // select an initial range
        source.setSelection(5, 14);
        assertEquals(10, selectedList.size());
        assertEquals(new Integer(5), selectedList.get(0));
        assertEquals(new Integer(14), selectedList.get(9));
        assertEquals(10, deselectedList.size());

        // select a unique range
        source.setSelection(15, 16);
        assertEquals(2, selectedList.size());
        assertEquals(new Integer(15), selectedList.get(0));
        assertEquals(new Integer(16), selectedList.get(1));
        assertEquals(18, deselectedList.size());

        // select a range with some overlap
        source.setSelection(10, 19);
        assertEquals(10, selectedList.size());
        assertEquals(new Integer(10), selectedList.get(0));
        assertEquals(new Integer(19), selectedList.get(9));
        assertEquals(10, deselectedList.size());

        // select an overlapping range
        source.setSelection(10, 15);
        assertEquals(6, selectedList.size());
        assertEquals(new Integer(10), selectedList.get(0));
        assertEquals(new Integer(15), selectedList.get(5));
        assertEquals(14, deselectedList.size());
    }

    /**
     * Test appending to selection with ranges
     */
    public void testAppendingSelectionRange() {
        for(int i = 0; i < 20; i++) {
            source.add(new Integer(i));
        }

        // select an initial range
        source.select(5, 14);
        assertEquals(10, selectedList.size());
        assertEquals(new Integer(5), selectedList.get(0));
        assertEquals(new Integer(14), selectedList.get(9));
        assertEquals(10, deselectedList.size());

        // select a mutually exclusive range
        source.select(15, 16);
        assertEquals(12, selectedList.size());
        assertEquals(new Integer(5), selectedList.get(0));
        assertEquals(new Integer(16), selectedList.get(11));
        assertEquals(8, deselectedList.size());

        // select a range with some overlap
        source.select(10, 19);
        assertEquals(15, selectedList.size());
        assertEquals(new Integer(5), selectedList.get(0));
        assertEquals(new Integer(19), selectedList.get(14));
        assertEquals(5, deselectedList.size());

        // select an entirely overlapping range
        source.select(10, 15);
        assertEquals(15, selectedList.size());
        assertEquals(new Integer(5), selectedList.get(0));
        assertEquals(new Integer(19), selectedList.get(14));
        assertEquals(5, deselectedList.size());
    }

    /**
     * Test deselecting with ranges
     */
    public void testDeselectionByRange() {
        for(int i = 0; i < 20; i++) {
            source.add(new Integer(i));
        }

        source.selectAll();

        // deselect an initial range
        source.deselect(5, 14);
        assertEquals(10, deselectedList.size());
        assertEquals(new Integer(5), deselectedList.get(0));
        assertEquals(new Integer(14), deselectedList.get(9));
        assertEquals(10, selectedList.size());

        // deselect a mutually exclusive range
        source.deselect(15, 16);
        assertEquals(12, deselectedList.size());
        assertEquals(new Integer(5), deselectedList.get(0));
        assertEquals(new Integer(16), deselectedList.get(11));
        assertEquals(8, selectedList.size());

        // deselect a range with some overlap
        source.deselect(10, 19);
        assertEquals(15, deselectedList.size());
        assertEquals(new Integer(5), deselectedList.get(0));
        assertEquals(new Integer(19), deselectedList.get(14));
        assertEquals(5, selectedList.size());

        // deselect an entirely overlapping range
        source.deselect(10, 15);
        assertEquals(15, deselectedList.size());
        assertEquals(new Integer(5), deselectedList.get(0));
        assertEquals(new Integer(19), deselectedList.get(14));
        assertEquals(5, selectedList.size());
    }

    /**
     * Test setting selection with an index array.
     */
    public void testSettingSelectionByArray() {
        int[] testArray1 = {0, 1, 5, 6, 8, 9, 14, 15, 18, 19};
        int[] testArray2 = {5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
        int[] testArray3 = {0, 1, 2, 3, 4, 15, 16, 17, 18, 19};
        for(int i = 0; i < 20; i++) {
            source.add(new Integer(i));
        }

        // select with array 1
        source.setSelection(testArray1);
        assertEquals(10, selectedList.size());
        assertEquals(10, deselectedList.size());

        // select with array 2
        source.setSelection(testArray2);
        assertEquals(10, selectedList.size());
        assertEquals(10, deselectedList.size());

        // select with array 3
        source.setSelection(testArray3);
        assertEquals(10, selectedList.size());
        assertEquals(10, deselectedList.size());
    }

    /**
     * Test appending to selection with an index array.
     */
    public void testAddingSelectionByArray() {
        int[] allUnselected = {0, 1, 5, 6, 8, 9, 14, 15, 18, 19};
        int[] totallyOverlapping = {5, 6, 9, 14, 19};
        int[] partialOverlap = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int[] remainingElements = {11, 12, 13, 16, 17};
        for(int i = 0; i < 20; i++) {
            source.add(new Integer(i));
        }

        // select with array 1
        source.select(allUnselected);
        assertEquals(10, selectedList.size());
        assertEquals(10, deselectedList.size());

        // select with array 2
        source.select(totallyOverlapping);
        assertEquals(10, selectedList.size());
        assertEquals(10, deselectedList.size());

        // select with array 3
        source.select(partialOverlap);
        assertEquals(15, selectedList.size());
        assertEquals(5, deselectedList.size());

        // select with array 4
        source.select(remainingElements);
        assertEquals(20, selectedList.size());
        assertEquals(0, deselectedList.size());
    }

    /**
     * Test deselecting with an index array.
     */
    public void testDeselectingByArray() {
        int[] allDeselected = {0, 1, 5, 6, 8, 9, 14, 15, 18, 19};
        int[] totallyOverlapping = {5, 6, 9, 14, 19};
        int[] partialOverlap = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int[] remainingElements = {11, 12, 13, 16, 17};
        for(int i = 0; i < 20; i++) {
            source.add(new Integer(i));
        }

        source.selectAll();

        // deselect with array 1
        source.deselect(allDeselected);
        assertEquals(10, selectedList.size());
        assertEquals(10, deselectedList.size());

        // deselect with array 2
        source.deselect(totallyOverlapping);
        assertEquals(10, selectedList.size());
        assertEquals(10, deselectedList.size());

        // deselect with array 3
        source.deselect(partialOverlap);
        assertEquals(5, selectedList.size());
        assertEquals(15, deselectedList.size());

        // deselect with array 4
        source.deselect(remainingElements);
        assertEquals(0, selectedList.size());
        assertEquals(20, deselectedList.size());
    }

    /**
     * Tests inverting selection
     */
    public void testSelectionInversion() {
        for(int i = 0; i < 20; i++) {
            source.add(new Integer(i));
        }

        // select all the even values
        for(int i = 0; i < 20; i += 2) {
            source.select(i);
        }

        // invert once
        source.invertSelection();
        assertEquals(10, selectedList.size());
        assertEquals(10, deselectedList.size());
        for(int i = 1; i < 20; i += 2) {
            assertEquals(true, source.isSelected(i));
        }
        for(int i = 0; i < 20; i += 2) {
            assertEquals(false, source.isSelected(i));
        }

        // invert again
        source.invertSelection();
        assertEquals(10, selectedList.size());
        assertEquals(10, deselectedList.size());
        for(int i = 1; i < 20; i += 2) {
            assertEquals(false, source.isSelected(i));
        }
        for(int i = 0; i < 20; i += 2) {
            assertEquals(true, source.isSelected(i));
        }

    }
}