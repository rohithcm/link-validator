package com.rohithcm.linkvalidator.enums;

import com.rohithcm.linkvalidator.LinkValidatorMain;

/**
 * Created by rohithcm on 21/01/16.
 */
public enum ValidationDepth {
    ONE(1), TWO(2), THREE(3), INVALID(-1);
    private int level;

    ValidationDepth(final int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public static ValidationDepth getValidationLevel(int level) {
        switch (level) {
            case 1:
                return ONE;
            case 2:
                return TWO;
            case 3:
                return THREE;
            default:
                return INVALID;
        }
    }
}
