package com.hugovs.gls.util;

/**
 * String utilities.
 *
 * @author Hugo Sartori
 */
public class StringUtils {

    private static final String HEXES = "0123456789ABCDEF";

    private StringUtils() {
        //no instance
    }

    /**
     * Converts a byte array into a hexadecimal string format.
     *
     * @param byteArray the byte array to be converted to string.
     * @return the string of the byte array on hex format
     */
    public static String from(byte[] byteArray) {
        StringBuilder prt = new StringBuilder("[");
        int i = 0;
        for (byte b : byteArray) {
            prt.append(getHex(new byte[]{b}));
            if (i < byteArray.length - 1)
                prt.append(" ");
            i++;
        }
        prt.append("]");
        return prt.toString();
    }

    /**
     * Get a hexadecimal {@link String} representation of a {@code byte} array.
     *
     * @param raw the {@code byte} array to be converted to a {@link String}.
     * @return the hexadecimal {@link String}.
     */
    private static String getHex( byte [] raw ) {
        if ( raw == null ) {
            return null;
        }
        final StringBuilder hex = new StringBuilder( 2 * raw.length );
        for ( final byte b : raw ) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4))
                    .append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

}
