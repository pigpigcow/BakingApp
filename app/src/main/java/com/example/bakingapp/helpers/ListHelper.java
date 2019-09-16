package com.example.bakingapp.helpers;

import java.util.List;

public class ListHelper {
    public static <T> void addObjectToLIstIfNotNull(List<T> list, T element) {
        if(element != null && list != null) {
            list.add(element);
        }
    }
}
