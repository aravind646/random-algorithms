/**
 * Created by Aravind Selvan on 10/17/14.
 */
public class StringToLong {
    public static void main(String[] args) {
        String inputString;
        try {
            if (args.length != 1) {
                throw new Exception("Please provide an input string");
            } else {
                inputString = args[0];
                convertToLong(inputString.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static long convertToLong(String inputString) {
        int stringLength = inputString.length();
        boolean endsWithL = Character.toLowerCase(inputString.charAt(stringLength - 1)) == 'l';
        boolean isNegative = inputString.startsWith("-");
        int low = isNegative ? 1 : 0;
        int high = endsWithL ? stringLength - 1 : stringLength;
        long result = 0;
        int index = low;
        while(index < high) {
            result = result * 10 + Character.digit(inputString.charAt(index++), 10);
        }

        return isNegative ? -result : result;

    }
}
