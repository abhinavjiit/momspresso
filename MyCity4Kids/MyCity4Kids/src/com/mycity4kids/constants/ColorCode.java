package com.mycity4kids.constants;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by khushboo.goyal on 17-06-2015.
 */
public class ColorCode {

    private HashMap<String, String> colorMap = new HashMap<>();

    public ColorCode() {

//        colorMap.put("1", "#ff8a65");
//        colorMap.put("2", "#1CBD52");
//        colorMap.put("3", "#ff1744");
//        colorMap.put("4", "#FC09EC");
//        colorMap.put("5", "#ab47bc");
//        colorMap.put("6", "#7e57c2");
//        colorMap.put("7", "#3949ab");
//        colorMap.put("8", "#42a5f5");
//        colorMap.put("9", "#00acc1");
//        colorMap.put("10", "#26a69a");

        colorMap.put("1", "#ff8a65");
        colorMap.put("2", "#ef5350");
        colorMap.put("3", "#ff1744");
        colorMap.put("4", "#d81b60");
        colorMap.put("5", "#ab47bc");
        colorMap.put("6", "#7e57c2");
        colorMap.put("7", "#3949ab");
        colorMap.put("8", "#42a5f5");
        colorMap.put("9", "#00acc1");
        colorMap.put("10", "#26a69a");
    }


    public String getValue(String key) {
        return colorMap.get(key);
    }

    public String getKey(String value) {

        Iterator myVeryOwnIterator = colorMap.keySet().iterator();
        while (myVeryOwnIterator.hasNext()) {
            String key = (String) myVeryOwnIterator.next();
            String val = (String) colorMap.get(key);
            if (val.equals(value)) {
                return key;
            }

        }
        return "1";

    }
}
