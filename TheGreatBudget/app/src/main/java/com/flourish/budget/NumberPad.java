package com.flourish.budget;

class NumberPad {

    private static final int INCOME_LIMIT = 6;

    private StringBuilder mNumber;

    NumberPad() {
        this.mNumber = new StringBuilder();
    }

    /**
     * converts {@link NumberPad} content to double
     * @return {@link NumberPad} content as a double
     */
    double toDouble() {
        String number = mNumber.toString();
        if (number.isEmpty() || number.equals(".")) {
            return 0;
        }
        return Double.parseDouble(number);
    }

    /**
     * check for decimal point
     * @return true if {@link NumberPad} has a decimal point, false otherwise
     */
    boolean hasDecimal() {
        return mNumber.toString().contains(".");
    }

    /**
     * add number string to the back of {@link NumberPad}
     * @param number assumed to be '0-9' or '.'
     */
    void push(String number) {
        if (number.equals(".")) {
            mNumber.append(number);
        } else if (mNumber.toString().contains(".")) {
            int index = mNumber.indexOf(".");
            if (mNumber.length() <= index + 2) {
                if (mNumber.length() == index + 2) {
                    if (!number.equals("0")) {
                        mNumber.append(number);
                    }
                } else {
                    mNumber.append(number);
                }

            }
        } else {
            if (mNumber.length() < INCOME_LIMIT) {
                mNumber.append(number);
            }
        }
    }

    /**
     * pop last char from {@link NumberPad}
     */
    void pop() {
        String number = mNumber.toString();
        if (!number.isEmpty()) {
            if (number.contains(".") && number.length() - 1 == number.indexOf(".")) {
                if (number.length() == 1) {
                    number = number.substring(0, number.length() - 1);
                } else {
                    number = number.substring(0, number.length() - 2);
                }
            } else {
                number = number.substring(0, number.length() - 1);
            }
        }
        mNumber = new StringBuilder(number);
    }

    /**
     * clear {@link NumberPad}
     */
    void clear() {
        mNumber = new StringBuilder();
    }
}
