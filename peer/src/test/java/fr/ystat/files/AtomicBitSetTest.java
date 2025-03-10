package fr.ystat.files;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AtomicBitSetTest {

    @Test
    public void testAtomicBitSetAndNotOperation(){

        int bitSetsLength = 10;
        AtomicBitSet trueSet = new AtomicBitSet(bitSetsLength);
        trueSet.fill();

        AtomicBitSet falseSet = new AtomicBitSet(bitSetsLength);
        falseSet.empty();

        AtomicBitSet shouldBeTrueSet = trueSet.andNot(falseSet);

        for (int i = 0; i < bitSetsLength; i++) {
            assertTrue(shouldBeTrueSet.get(i));
        }

        AtomicBitSet shouldBeFalseSet = falseSet.andNot(trueSet);
        for (int i = 0; i < bitSetsLength; i++) {
            assertFalse(shouldBeFalseSet.get(i));
        }

        AtomicBitSet shouldBeFalseSet2 = falseSet.andNot(falseSet);
        for (int i = 0; i < bitSetsLength; i++) {
            assertFalse(shouldBeFalseSet2.get(i));
        }

        AtomicBitSet shouldBeFalseSet3 = falseSet.andNot(trueSet);
        for (int i = 0; i < bitSetsLength; i++) {
            assertFalse(shouldBeFalseSet3.get(i));
        }
    }

    @Test
    public void testAtomicBitSetUpdate(){
        int bitSetsLength = 10;
        AtomicBitSet trueSet = new AtomicBitSet(bitSetsLength);
        trueSet.fill();

        AtomicBitSet falseSetThatWillReachTruth = new AtomicBitSet(bitSetsLength);
        falseSetThatWillReachTruth.empty();

        falseSetThatWillReachTruth.update(trueSet);

        for (int i = 0; i < bitSetsLength; i++) {
            assertTrue(falseSetThatWillReachTruth.get(i));
            assertEquals(trueSet.get(i), falseSetThatWillReachTruth.get(i));
        }

    }

    @Test
    public void testAtomicBitSetIsFilled(){
        int bitSetsLength = 10;
        AtomicBitSet trueSet = new AtomicBitSet(bitSetsLength);
        trueSet.fill();
        assertTrue(trueSet.isFilled());

        AtomicBitSet set2 = new AtomicBitSet(bitSetsLength);
        for (int i = 0; i < 10; i++) {
            assertFalse(set2.get(i));
            set2.set(i);
        }
        assertTrue(set2.isFilled());
    }

}
