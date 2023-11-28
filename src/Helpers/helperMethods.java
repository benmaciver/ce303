package Helpers;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class helperMethods {
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
    public static <T> boolean areAllElementsNull(T[] array) {
        for (T element : array) {
            if (element != null) {
                return false; // If any element is not null, return false
            }
        }
        return true; // All elements are null
    }
    public static String[] concatenateArrays(String[] array1, String[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;

        // Create a new array with the combined length
        String[] resultArray = new String[length1 + length2];

        // Copy elements from the first array
        System.arraycopy(array1, 0, resultArray, 0, length1);

        // Copy elements from the second array
        System.arraycopy(array2, 0, resultArray, length1, length2);

        return resultArray;
    }

}
