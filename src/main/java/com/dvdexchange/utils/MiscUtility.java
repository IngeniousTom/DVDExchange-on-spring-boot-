package com.dvdexchange.utils;


import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

public class MiscUtility {

    public static String removeJSONExtraSymbols(String initialString) {
        /*Метод служит для удаления лишних символов, которые появляются при преобразовании
        исходных данных в Формат json*/
        StringBuilder sb = new StringBuilder(initialString.trim());

        if(sb.charAt(0) == '"'){
            sb.deleteCharAt(0);
        }
        if (sb.charAt(sb.length()-1) == '"'){
            sb.deleteCharAt(sb.length()-1);
        }
        int indexOfScreenSymbol = 0;
        while (indexOfScreenSymbol > -1) {
            indexOfScreenSymbol = sb.indexOf("\\\"");
            if (indexOfScreenSymbol > -1) {
                sb.deleteCharAt(indexOfScreenSymbol);
            }
        }
        return sb.toString();
    }
}
