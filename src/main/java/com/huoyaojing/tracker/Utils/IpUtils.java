package com.huoyaojing.tracker.Utils;

/***
 *
 * Util For IP address
 *
 */
public class IpUtils {
    public static long[] IPV4Array = { 0x80000000l, // 1000 0000 0000 0000 0000
            // 0000 0000 0000,//1
            0xC0000000l, // 1100 0000 0000 0000 0000 0000 0000 0000,//2
            0xE0000000l, // 1110 0000 0000 0000 0000 0000 0000 0000,//3
            0xF0000000l, // 1111 0000 0000 0000 0000 0000 0000 0000,//4
            0xF8000000l, // 1111 1000 0000 0000 0000 0000 0000 0000,//5
            0xFC000000l, // 1111 1100 0000 0000 0000 0000 0000 0000,//6
            0xFE000000l, // 1111 1110 0000 0000 0000 0000 0000 0000,//7
            0xFF000000l, // 1111 1111 0000 0000 0000 0000 0000 0000,//8
            0xFF800000l, // 1111 1111 1000 0000 0000 0000 0000 0000,//9
            0xFFC00000l, // 1111 1111 1100 0000 0000 0000 0000 0000,//10
            0xFFE00000l, // 1111 1111 1110 0000 0000 0000 0000 0000,//11
            0xFFF00000l, // 1111 1111 1111 0000 0000 0000 0000 0000,//12
            0xFFF80000l, // 1111 1111 1111 1000 0000 0000 0000 0000,//13
            0xFFFC0000l, // 1111 1111 1111 1100 0000 0000 0000 0000,//14
            0xFFFE0000l, // 1111 1111 1111 1110 0000 0000 0000 0000,//15
            0xFFFF0000l, // 1111 1111 1111 1111 0000 0000 0000 0000,//16
            0xFFFF8000l, // 1111 1111 1111 1111 1000 0000 0000 0000,//17
            0xFFFFC000l, // 1111 1111 1111 1111 1100 0000 0000 0000,//18
            0xFFFFE000l, // 1111 1111 1111 1111 1110 0000 0000 0000,//19
            0xFFFFF000l, // 1111 1111 1111 1111 1111 0000 0000 0000,//20
            0xFFFFF800l, // 1111 1111 1111 1111 1111 1000 0000 0000,//21
            0xFFFFFC00l, // 1111 1111 1111 1111 1111 1100 0000 0000,//22
            0xFFFFFE00l, // 1111 1111 1111 1111 1111 1110 0000 0000,//23
            0xFFFFFF00l, // 1111 1111 1111 1111 1111 1111 0000 0000,//24
            0xFFFFFF80l, // 1111 1111 1111 1111 1111 1111 1000 0000,//25
            0xFFFFFFC0l, // 1111 1111 1111 1111 1111 1111 1100 0000,//26
            0xFFFFFFE0l, // 1111 1111 1111 1111 1111 1111 1110 0000,//27
            0xFFFFFFF0l, // 1111 1111 1111 1111 1111 1111 1111 0000,//28
            0xFFFFFFF8l, // 1111 1111 1111 1111 1111 1111 1111 1000//29
            0xFFFFFFFCl, // 1111 1111 1111 1111 1111 1111 1111 1100//30
            0xFFFFFFFEl, // 1111 1111 1111 1111 1111 1111 1111 1110//31
            0xFFFFFFFFl // 1111 1111 1111 1111 1111 1111 1111 1111//32
    };

    public static long[] IPV6Array = { 0x8000000000000000l, // 1
            0xC000000000000000l, // 2
            0xE000000000000000l, // 3
            0xF000000000000000l, // 4
            0xF800000000000000l, // 5
            0xFC00000000000000l, // 6
            0xFE00000000000000l, // 7
            0xFF00000000000000l, // 8
            0xFF80000000000000l, // 9
            0xFFC0000000000000l, // 10
            0xFFE0000000000000l, // 11
            0xFFF0000000000000l, // 12
            0xFFF8000000000000l, // 13
            0xFFFC000000000000l, // 14
            0xFFFE000000000000l, // 15
            0xFFFF000000000000l, // 16
            0xFFFF800000000000l, // 17
            0xFFFFC00000000000l, // 18
            0xFFFFE00000000000l, // 19
            0xFFFFF00000000000l, // 20
            0xFFFFF80000000000l, // 21
            0xFFFFFC0000000000l, // 22
            0xFFFFFE0000000000l, // 23
            0xFFFFFF0000000000l, // 24
            0xFFFFFF8000000000l, // 25
            0xFFFFFFC000000000l, // 26
            0xFFFFFFE000000000l, // 27
            0xFFFFFFF000000000l, // 28
            0xFFFFFFF800000000l, // 29
            0xFFFFFFFC00000000l, // 30
            0xFFFFFFFE00000000l, // 31
            0xFFFFFFFF00000000l, // 32
            0xFFFFFFFF80000000l, // 33
            0xFFFFFFFFC0000000l, // 34
            0xFFFFFFFFE0000000l, // 35
            0xFFFFFFFFF0000000l, // 36
            0xFFFFFFFFF8000000l, // 37
            0xFFFFFFFFFC000000l, // 38
            0xFFFFFFFFFE000000l, // 39
            0xFFFFFFFFFF000000l, // 40
            0xFFFFFFFFFF800000l, // 41
            0xFFFFFFFFFFC00000l, // 42
            0xFFFFFFFFFFE00000l, // 43
            0xFFFFFFFFFFF00000l, // 44
            0xFFFFFFFFFFF80000l, // 45
            0xFFFFFFFFFFFC0000l, // 46
            0xFFFFFFFFFFFE0000l, // 47
            0xFFFFFFFFFFFF0000l, // 48
            0xFFFFFFFFFFFF8000l, // 49
            0xFFFFFFFFFFFFC000l, // 50
            0xFFFFFFFFFFFFE000l, // 51
            0xFFFFFFFFFFFFF000l, // 52
            0xFFFFFFFFFFFFF800l, // 53
            0xFFFFFFFFFFFFFC00l, // 54
            0xFFFFFFFFFFFFFE00l, // 55
            0xFFFFFFFFFFFFFF00l, // 56
            0xFFFFFFFFFFFFFF80l, // 57
            0xFFFFFFFFFFFFFFC0l, // 58
            0xFFFFFFFFFFFFFFE0l, // 59
            0xFFFFFFFFFFFFFFF0l, // 60
            0xFFFFFFFFFFFFFFF8l, // 61
            0xFFFFFFFFFFFFFFFCl, // 62
            0xFFFFFFFFFFFFFFFEl, // 63
            0xFFFFFFFFFFFFFFFFl // 64
    };

    /**
     * parse a IPv4/v6 string to long[]
     * @param ipInfo
     * @param ipLength
     * @return long[] for a IPv4/v6 address
     */
    public static long[] parsingIp(String ipInfo, int ipLength) {
        long startHighAddr = 0, endHighAddr = 0, startLowAddr = 0, endLowAddr = 0;
        long[] ipInfoLong = iptolong(ipInfo);
        if (ipLength != 0) {
            if (ipInfo.indexOf(":") != -1) {
                if (ipLength < 65) {
                    ipLength = ipLength -1;
                    startHighAddr = ipInfoLong[0] & IPV6Array[ipLength];
                    startLowAddr = ipInfoLong[1];

                    long inversion = (~IPV6Array[ipLength]) & 0xFFFFFFFFFFFFFFFFL;
                    endHighAddr = ipInfoLong[0] | inversion;
                    endLowAddr = startLowAddr;
                } else {
                    ipLength = ipLength - 65;
                    startHighAddr = ipInfoLong[0];
                    startLowAddr = ipInfoLong[1] & IPV6Array[ipLength];

                    long inversion = (~IPV6Array[ipLength]) & 0xFFFFFFFFFFFFFFFFL;
                    endLowAddr = ipInfoLong[0] | inversion;
                    endHighAddr = startHighAddr;
                }

            } else {
                ipLength = ipLength - 1;
                startLowAddr = ipInfoLong[0] & IPV4Array[ipLength];
                long inversion = (~IPV4Array[ipLength]) & 0xFFFF;
                endLowAddr = ipInfoLong[0] | inversion;
            }

        } else {
            if (ipInfo.indexOf(":") != -1) {
                startHighAddr = ipInfoLong[0];
                startLowAddr = ipInfoLong[1];
            } else {
                startLowAddr = ipInfoLong[0];
            }
        }

        long[] iplongs = { startHighAddr, endHighAddr, startLowAddr, endLowAddr };
        return iplongs;
    }

    /**
     * parse two IPv4/v6 strings to long[]
     * @param startAddress
     * @param endAddress
     * @param startIpLength
     * @param endIpLength
     * @return long[] for a IPv4/v6 address
     */
    public static long[] parsingIp(String startAddress, String endAddress,
                                   int startIpLength, int endIpLength) {
        long startHighAddr = 0, endHighAddr = 0, startLowAddr = 0, endLowAddr = 0;
        long[] ipStartInfoLong = iptolong(startAddress);
        long[] ipEndInfoLong = iptolong(endAddress);

        if (startIpLength != 0 && endIpLength != 0) {
            if (startAddress.indexOf(":") != -1
                    && endAddress.indexOf(":") != -1) {
                if (endIpLength < 65) {
                    startHighAddr = ipStartInfoLong[0]
                            & IPV6Array[startIpLength];
                    startLowAddr = ipStartInfoLong[1];

                    endHighAddr = ipEndInfoLong[0] & IPV6Array[endIpLength];
                    endLowAddr = ipEndInfoLong[1];
                } else {
                    startHighAddr = ipStartInfoLong[0];
                    startLowAddr = ipStartInfoLong[1]
                            & IPV6Array[startIpLength];

                    endLowAddr = ipEndInfoLong[0] & IPV6Array[endIpLength];
                    endHighAddr = ipEndInfoLong[1];
                }

            } else {
                startLowAddr = ipStartInfoLong[0] & IPV4Array[startIpLength];
                endLowAddr = ipStartInfoLong[0] & IPV4Array[endIpLength];
            }

        } else {
            if (startAddress.indexOf(":") != -1
                    && endAddress.indexOf(":") != -1) {
                startHighAddr = ipStartInfoLong[0];
                startLowAddr = ipStartInfoLong[1];

                endHighAddr = ipEndInfoLong[0];
                endLowAddr = ipEndInfoLong[1];
            } else {
                startHighAddr = 0;
                startLowAddr = ipStartInfoLong[0];

                endHighAddr = 0;
                endLowAddr = ipEndInfoLong[0];
            }

        }
        long[] iplongs = { startHighAddr, endHighAddr, startLowAddr, endLowAddr };
        return iplongs;
    }

    /**
     * Long converted to type IpV4
     *
     * @param longip
     * @return ipv4String
     */

    public static String longtoipV4(long longip) {
        return String.format("%d.%d.%d.%d", longip >>> 24,
                (longip & 0x00ffffff) >>> 16, (longip & 0x0000ffff) >>> 8,
                longip & 0x000000ff);
    }

    /**
     * Long converted to type IpV6
     *
     * @param highBits
     * @param lowBits
     * @return ipv6String
     */
    public static String ipV6ToString(long highBits, long lowBits) {
        short[] shorts = new short[8];
        String[] strings = new String[shorts.length];

        for (int i = 0; i < 8; i++) {
            if (i >= 0 && i < 4)
                strings[i] = String
                        .format("%x",
                                (short) (((highBits << i * 16) >>> 16 * (8 - 1)) & 0xFFFF));
            else
                strings[i] = String
                        .format("%x",
                                (short) (((lowBits << i * 16) >>> 16 * (8 - 1)) & 0xFFFF));

        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < strings.length; i++) {
            result.append(strings[i]);
            if (i < strings.length - 1)
                result.append(":");
        }
        return result.toString();
    }

    /**
     * The abbreviated IPv6 converted into a standard wording
     *
     * @param string
     * @return ipv6String
     */
    public static String expandShortNotation(String string) {
        if (!string.contains("::")) {
            return string;
        } else if (string.equals("::")) {
            return generateZeroes(8);
        } else {
            final int numberOfColons = countOccurrences(string, ':');
            if (string.startsWith("::"))
                return string.replace("::", generateZeroes((7 + 2)
                        - numberOfColons));
            else if (string.endsWith("::"))
                return string.replace("::", ":"
                        + generateZeroes((7 + 2) - numberOfColons));
            else
                return string.replace("::", ":"
                        + generateZeroes((7 + 2 - 1) - numberOfColons));
        }
    }

    /**
     * Generated IPv6 address 0
     *
     * @param number
     * @return ipv6String
     */
    public static String generateZeroes(int number) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < number; i++) {
            builder.append("0:");
        }

        return builder.toString();
    }

    /**
     * The record ipv6 address: Number of
     *
     * @param haystack
     * @param needle
     * @return count
     */
    public static int countOccurrences(String haystack, char needle) {
        int count = 0;
        for (int i = 0; i < haystack.length(); i++) {
            if (haystack.charAt(i) == needle) {
                count++;
            }
        }
        return count;
    }

    /**
     * Ip converted to type long
     *
     * @param ipStr
     * @return ipLongArr[]
     */
    public static long[] iptolong(String ipStr) {
        long[] ipLongArr = new long[2];
        if (ipStr.indexOf(".") >= 0) {
            String[] ip = ipStr.split("\\.");
            ipLongArr[0] = (Long.parseLong(ip[0]) << 24)
                    + (Long.parseLong(ip[1]) << 16)
                    + (Long.parseLong(ip[2]) << 8) + Long.parseLong(ip[3]);
            ipLongArr[1] = 0;
        } else {
            return Ipv6ToLong(ipStr);
        }
        return ipLongArr;
    }

    /**
     * string of IpV6 converted to long
     * @param longip
     * @return long[]
     */
    public static long[] Ipv6ToLong(String longip) {
        String[] strings = expandShortNotation(longip).split(":");
        long[] longs = new long[strings.length];

        long high = 0L;
        long low = 0L;
        for (int i = 0; i < strings.length; i++) {

            if (i >= 0 && i < 4)
                high |= (Long.parseLong(strings[i], 16) << ((longs.length - i - 1) * 16));
            else
                low |= (Long.parseLong(strings[i], 16) << ((longs.length - i - 1) * 16));
        }
        longs[0] = high;
        longs[1] = low;
        return longs;
    }

}