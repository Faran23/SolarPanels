package io.github.faran23.solarpanels;

import org.jetbrains.annotations.Nullable;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;

public class Utils {

    public static NumberFormat nf = NumberFormat.getInstance();
    public static DecimalFormat dfCommas = new DecimalFormat("0.##");
    public static final DecimalFormat[] dfCommasArray;

    public static String formatNumber(int number) {
        return nf.format(number);
    }

    public static String humanReadableNumberNoUnit(double number, boolean milli) {
        return humanReadableNumber(number, "", milli, null);
    }

    // Taken from Jade to remove any dependency
    public static String humanReadableNumber(double number, String unitSuffix, boolean milli, @Nullable Format formatter) {
        StringBuilder sb = new StringBuilder();
        boolean n = number < 0.0;
        if (n) {
            number = -number;
            sb.append('-');
        }

        if (milli && number >= 1000.0) {
            number /= 1000.0;
            milli = false;
        }

        int exp = 0;
        if (!(number < 1000.0) && (formatter != null || !(number < 10000.0))) {
            exp = (int) Math.log10(number) / 3;
            if (exp > 7) {
                exp = 7;
            }

            if (exp > 0) {
                number /= Math.pow(1000.0, (double) exp);
            }

            if (formatter == null) {
                if (number < 10.0) {
                    formatter = dfCommasArray[0];
                } else if (number < 100.0) {
                    formatter = dfCommasArray[1];
                } else {
                    formatter = dfCommasArray[2];
                }
            }
        } else {
            formatter = dfCommasArray[2];
        }

        if (formatter instanceof NumberFormat numberFormat) {
            sb.append(numberFormat.format(number));
        } else {
            sb.append(((Format) formatter).format(new Object[]{number}));
        }

        if (exp == 0) {
            if (milli && number != 0.0) {
                sb.append('m');
            }
        } else {
            char pre = "kMGTPEZ".charAt(exp - 1);
            sb.append(pre);
        }

        sb.append(unitSuffix);
        return sb.toString();
    }

    static {
        dfCommasArray = new DecimalFormat[]{dfCommas, new DecimalFormat("0.#"), new DecimalFormat("0")};
        DecimalFormat[] var0 = dfCommasArray;
        int var1 = var0.length;

        for (int var2 = 0; var2 < var1; ++var2) {
            DecimalFormat format = var0[var2];
            format.setRoundingMode(RoundingMode.DOWN);
        }
    }

}
