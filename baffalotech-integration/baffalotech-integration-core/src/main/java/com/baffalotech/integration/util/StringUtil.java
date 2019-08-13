package com.baffalotech.integration.util;

/**
 * Created by acer01 on 2018/7/31/031.
 */
public class StringUtil {
	
	/* ------------------------------------------------------------ */
    public static final char[] lowercases = {
          '\000','\001','\002','\003','\004','\005','\006','\007',
          '\010','\011','\012','\013','\014','\015','\016','\017',
          '\020','\021','\022','\023','\024','\025','\026','\027',
          '\030','\031','\032','\033','\034','\035','\036','\037',
          '\040','\041','\042','\043','\044','\045','\046','\047',
          '\050','\051','\052','\053','\054','\055','\056','\057',
          '\060','\061','\062','\063','\064','\065','\066','\067',
          '\070','\071','\072','\073','\074','\075','\076','\077',
          '\100','\141','\142','\143','\144','\145','\146','\147',
          '\150','\151','\152','\153','\154','\155','\156','\157',
          '\160','\161','\162','\163','\164','\165','\166','\167',
          '\170','\171','\172','\133','\134','\135','\136','\137',
          '\140','\141','\142','\143','\144','\145','\146','\147',
          '\150','\151','\152','\153','\154','\155','\156','\157',
          '\160','\161','\162','\163','\164','\165','\166','\167',
          '\170','\171','\172','\173','\174','\175','\176','\177' };

    /* ------------------------------------------------------------ */

    public static boolean isNotEmpty(CharSequence str){
        return !isEmpty(str);
    }

    public static boolean isEmpty(Object str) {
        return str == null || "".equals(str);
    }

    public static String firstUpperCase(String str){
        if(str == null || str.isEmpty() || Character.isUpperCase(str.charAt(0))){
            return str;
        }

        char[] cs= str.toCharArray();
        cs[0] -= 32;
        return new String(cs);
    }

    public static String firstLowerCase(String str){
        if(str == null || str.isEmpty() || Character.isLowerCase(str.charAt(0))){
            return str;
        }

        char[] cs= str.toCharArray();
        cs[0] =  Character.toLowerCase(cs[0]);
        return new String(cs);
    }

    public static String capitalize(String str) {
        return changeFirstCharacterCase(str, true);
    }

    private static String changeFirstCharacterCase(String str, boolean capitalize) {
        if(str == null || str.isEmpty()) {
            return str;
        } else {
            char baseChar = str.charAt(0);
            char updatedChar;
            if(capitalize) {
                updatedChar = Character.toUpperCase(baseChar);
            } else {
                updatedChar = Character.toLowerCase(baseChar);
            }

            if(baseChar == updatedChar) {
                return str;
            } else {
                char[] chars = str.toCharArray();
                chars[0] = updatedChar;
                return new String(chars, 0, chars.length);
            }
        }
    }
    
    /* ------------------------------------------------------------ */
    /** Append substring to StringBuilder 
     * @param buf StringBuilder to append to
     * @param s String to append from
     * @param offset The offset of the substring
     * @param length The length of the substring
     */
    public static void append(StringBuilder buf,
                              String s,
                              int offset,
                              int length)
    {
        synchronized(buf)
        {
            int end=offset+length;
            for (int i=offset; i<end;i++)
            {
                if (i>=s.length())
                    break;
                buf.append(s.charAt(i));
            }
        }
    }

    
    /* ------------------------------------------------------------ */
    /**
     * append hex digit
     * @param buf the buffer to append to
     * @param b the byte to append
     * @param base the base of the hex output (almost always 16).
     * 
     */
    public static void append(StringBuilder buf,byte b,int base)
    {
        int bi=0xff&b;
        int c='0'+(bi/base)%base;
        if (c>'9')
            c= 'a'+(c-'0'-10);
        buf.append((char)c);
        c='0'+bi%base;
        if (c>'9')
            c= 'a'+(c-'0'-10);
        buf.append((char)c);
    }

    /* ------------------------------------------------------------ */
    
    /**
     * Convert String to an integer. Parses up to the first non-numeric character. If no number is found an IllegalArgumentException is thrown
     * 
     * @param string A String containing an integer.
     * @param from The index to start parsing from
     * @return an int
     */
    public static int toInt(String string,int from)
    {
        int val = 0;
        boolean started = false;
        boolean minus = false;

        for (int i = from; i < string.length(); i++)
        {
            char b = string.charAt(i);
            if (b <= ' ')
            {
                if (started)
                    break;
            }
            else if (b >= '0' && b <= '9')
            {
                val = val * 10 + (b - '0');
                started = true;
            }
            else if (b == '-' && !started)
            {
                minus = true;
            }
            else
                break;
        }

        if (started)
            return minus?(-val):val;
        throw new NumberFormatException(string);
    }

}
