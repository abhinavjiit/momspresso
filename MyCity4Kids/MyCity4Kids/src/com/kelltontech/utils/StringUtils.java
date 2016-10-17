package com.kelltontech.utils;

import java.util.regex.Pattern;

/**
 * Utility class useful when dealing with string objects. This class is a
 * collection of static functions it is not allowed to create instances of this
 * class
 */
public abstract class StringUtils {

    private static final String LOG_TAG = "StringUtils";

    public static final String EMAIL_REGEX = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";

    /**
     * @param pStr String object to be tested.
     * @returns true if the given string is null or empty or contains spaces
     * only.
     */
    public static boolean isNullOrEmpty(final String pStr) {
        return pStr == null || pStr.trim().length() == 0;
    }

    public static boolean isNullOrEmptyOrZero(final String pStr) {
        return pStr == null || pStr.trim().length() == 0 || (pStr != null && pStr.equals("0"));
    }

    /**
     * @param pEmail
     * @return true if pEmail matches with {@link StringUtils#EMAIL_REGEX},
     * false otherwise
     */
    public static boolean isValidEmail(String pEmail) {
        Pattern validRegexPattern = Pattern.compile(EMAIL_REGEX);
        return validRegexPattern.matcher(pEmail).matches();
    }

    /**
     * @param pStr
     * @param startIndex
     * @param pEndIndex
     * @return int value, parsed from a substring of pStr
     */

    public static final Pattern MOBILE_NUMBER_PATTERN = Pattern
            .compile("^((\\+91?)|0|)?[0-9]{10}$");

    public static boolean checkMobileNumber(String mobileNumber) {
        return MOBILE_NUMBER_PATTERN.matcher(mobileNumber).matches();
    }

    public static int parseInt(String pStr, int startIndex, int pEndIndex) {
        if (pStr == null || pStr.length() < pEndIndex) {
            return 0;
        }
        try {
            return Integer.parseInt(pStr.substring(startIndex, pEndIndex));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * This method checks and ensure http/https protocol in URL
     *
     * @param url
     * @return formattedUrl
     */
    public static String getFormattedURL(String url) {
        if (url.indexOf("http://") == 0 || url.indexOf("https://") == 0) {
            return url;
        } else if (url.indexOf("://") == 0) {
            return "http" + url;
        } else if (url.indexOf("//") == 0) {
            return "http:" + url;
        } else {
            return "http://" + url;
        }
    }

    public static String firstLetterToUpperCase(String pWord) {
        pWord = pWord == null ? "" : pWord;
        String output = "";
        for (int i = 0; i < pWord.length(); i++) {
            if (i == 0) {
                output += Character.toUpperCase(pWord.charAt(i));
            } else {
                output += Character.toLowerCase(pWord.charAt(i));
            }
        }
        return output;
    }

//	public static String replaceSpecialCharacters(String str) {
//		// http://www.utexas.edu/learn/html/spchar.html
//		// above url is used for html special characters.
//
//		if (str == null)
//			return null;
//		if (str.trim().equals(""))
//			return "";
//
//		String[] htmlSpecialcharArray = { "&ndash;", "&mdash;", "&iexcl;",
//				"&iquest;", "&quot;", "&ldquo;", "&rdquo;", "&lsquo;",
//				"&rsquo;", "&laquo;", "&raquo;", "&nbsp;", "&amp;", "&cent;",
//				"&copy;", "&divide;", "&gt;", "&lt;", "&micro;", "&middot;",
//				"&para;", "&plusmn;", "&euro;", "&pound;", "&reg;", "&sect;",
//				"&trade;", "&yen;", "&aacute;", "&Aacute;", "&agrave;",
//				"&Agrave;", "&acirc;", "&Acirc;", "&aring;", "&Aring;",
//				"&atilde;", "&Atilde;", "&auml;", "&Auml;", "&aelig;",
//				"&AElig;", "&ccedil;", "&Ccedil;", "&eacute;", "&Eacute;",
//				"&egrave;", "&Egrave;", "&ecirc;", "&Ecirc;", "&euml;",
//				"&Euml;", "&iacute;", "&Iacute;", "&igrave;", "&Igrave;",
//				"&icirc;", "&Icirc;", "&iuml;", "&Iuml;", "&ntilde;",
//				"&Ntilde;", "&oacute;", "&Oacute;", "&ograve;", "&Ograve;",
//				"&ocirc;", "&Ocirc;", "&oslash;", "&Oslash;", "&otilde;",
//				"&Otilde;", "&ouml;", "&Ouml;", "&szlig;", "&uacute;",
//				"&Uacute;", "&ugrave;", "&Ugrave;", "&ucirc;", "&Ucirc;",
//				"&uuml;", "&Uuml;", "&yuml;", "&sbquo;&not;", "&Atilde;",
//				"&uml;", "&brvbar;", "&bdquo;", "&sup1;", "&ordf;", "&#039;" };
//
//		String[] stringSpecialcharArray = { "ñ", "ó", "°", "ø", "\"", "ì",
//				"ì", "ë", "í", "´", "ª", " ", "And", "¢", "©", "˜",
//				">", "<", "µ", "∑", "∂", "±", "Ä", "£", "Æ", "ß",
//				"ô", "•", "·", "¡", "‡", "¿", "‚", "¬", "Â", "≈",
//				"„", "√", "‰", "ƒ", "Ê", "∆", "�?", "«", "È",
//				"…", "Ë", "»", "�?", " ", "Î", "À", "Ì", "Õ", "�?",
//				"Ã", "Ó", "Œ", "Ô", "œ", "Ò", "—", "Û", "�?", "Ú",
//				"“", "Ù", "‘", "¯", "ÿ", "ı", "’", "ˆ", "÷", "ﬂ",
//				"˙", "�?�", "˘", "Ÿ", "˚", "€", "¸", "‹", "ˇ", "¨",
//				"~", "U", "b", "q", "s", "o", "'" };
//
//		// replce special charaters one by one
//		int len = htmlSpecialcharArray.length;
//		for (int index = 0; index < len; index++) {
//			str = replaceOld(str, htmlSpecialcharArray[index],
//					stringSpecialcharArray[index]);
//		}
//		return str;
//	}

    public static String stripSpecialCharacters(String str) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (Character.isLetterOrDigit(ch) || Character.isSpaceChar(ch))
                sb.append(ch);
        }
        return sb.toString();
    }

    public static String replaceOld(final String aInput,
                                    final String aOldPattern, final String aNewPattern) {
        if (aOldPattern == null || aOldPattern.equals("")) {
            // throw new
            // IllegalArgumentException("Old pattern must have content.");
            return "";
        }
        final StringBuffer result = new StringBuffer();
        // startIdx and idxOld delimit various chunks of aInput; these
        // chunks always end where aOldPattern begins
        int startIdx = 0;
        int idxOld = 0;
        while ((idxOld = aInput.indexOf(aOldPattern, startIdx)) >= 0) {
            // grab a part of aInput which does not include aOldPattern
            result.append(aInput.substring(startIdx, idxOld));
            // add aNewPattern to take place of aOldPattern
            result.append(aNewPattern);
            // reset the startIdx to just after the current match, to see
            // if there are any further matches
            startIdx = idxOld + aOldPattern.length();
        }
        // the final chunk will go to the end of aInput
        result.append(aInput.substring(startIdx));
        return result.toString();
    }

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
}
