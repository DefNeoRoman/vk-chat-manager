package config;

import java.util.*;

@SuppressWarnings("serial")
public class MyProperties extends Properties {

    public Enumeration<Object> keys() {

        Enumeration<Object> keysEnum = super.keys();

        Vector<Object> keyList = new Vector<Object>();

        while (keysEnum.hasMoreElements()) {
            keyList.add(keysEnum.nextElement());
        }

        keyList.sort(Comparator.comparing(Object::toString));

        return keyList.elements();
    }
}
