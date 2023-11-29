package Helpers;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class helperMethods {
    //Returns an arraY list of every instance a regex match is found in inputText
    public static ArrayList<String> findRegexMatches(String regex, String inputText)
    {
        ArrayList<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputText);

        while (matcher.find()) {
            String match = matcher.group();
            result.add(match);
        }
        return result;
    }
    //returns if all elements in an array are null
    public static <T> boolean areAllElementsNull(T[] array) {
        for (T element : array) {
            if (element != null) {
                return false;
            }
        }
        return true;
    }
    //adds two arrays together
    public static String[] concatenateArrays(String[] array1, String[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;


        String[] resultArray = new String[length1 + length2];


        System.arraycopy(array1, 0, resultArray, 0, length1);


        System.arraycopy(array2, 0, resultArray, length1, length2);

        return resultArray;
    }

}
