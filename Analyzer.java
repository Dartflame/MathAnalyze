import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Analyzer {

    String str = "";
    ArrayList<String> list;

    public static void main(String[] args) {
        Analyzer analyzer = new Analyzer();
        analyzer.recurse("sin(2*(-5+1.5*4)+28)", 0); // Expected output: 0.5 6
        System.out.println("hei");
    }

    public void recurse(String expression, int countOperation) {
        expression = expression.replaceAll(" ", "");
        Pattern pattern = Pattern.compile("\\([^\\(\\)]+\\)");
        Matcher matcher = pattern.matcher(expression);
        String temp = "";
        String result = "----";
        if (matcher.find()) {
            matcher.reset();
            while (matcher.find()) {
                str = matcher.group();
                temp = matcher.group();
                str = str.substring(1, str.length() - 1);
                matcher.reset(str);
            }
            if (str.contains("s") || str.contains("t")) {
                function(str);
            }

            countOperation = operator(list, countOperation);
            recurse(expression.replace(temp, str), countOperation);
        } else {
            Pattern pattern2 = Pattern.compile("\\d+[*/^+-]\\d*");
            Matcher matcher2 = pattern2.matcher(expression);

            String calc = expression.toLowerCase();

            if (calc.contains("sin") || calc.contains("cos") || calc.contains("tan")) {
                str = expression;
                result = function(expression);
                countOperation++;
                recurse(str, countOperation);

            } else if (matcher2.find()) {
                str = expression;
                countOperation = operator(list, countOperation);
                recurse(str, countOperation);
                result = str;
            } else {
                result = expression;
                System.out.println("результат : " + result);
                System.out.println("количество операций : " + countOperation);
            }
        }
    }

    public String function(String expression) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH);
        numberFormat.setRoundingMode(RoundingMode.HALF_EVEN);
        DecimalFormat df = (DecimalFormat) numberFormat;
        df.applyPattern("#.##");

        Pattern pattern = Pattern.compile("[a-zA-Z]+[\\-\\d\\.]+");
        Matcher matcher = pattern.matcher(expression);
        String string = "";
        double di = 0;
        double res = 0;
        while (matcher.find()) {
            string = matcher.group();
            di = Double.parseDouble(string.substring(3));
            switch (string.substring(0, 3).toLowerCase()) {
                case "sin":
                    res = Math.sin(Math.toRadians(di));
                    break;
                case "cos":
                    res = Math.cos(Math.toRadians(di));
                    break;
                case "tan":
                    res = Math.tan(Math.toRadians(di));
                    break;
            }

            str = expression.replace(string, df.format(res));
        }
        return df.format(res);
    }

    public int operator(List<String> list, int countOperation) {

        list = reList();
        while (list.contains("^")) {
            countOperation = calculate(list, "^", countOperation);
            list = reList();
        }
        while (list.contains("*")) {
            countOperation = calculate(list, "*", countOperation);
            list = reList();
        }
        while (list.contains("/")) {
            countOperation = calculate(list, "/", countOperation);
            list = reList();
        }
        while (list.contains("+")) {
            countOperation = calculate(list, "+", countOperation);
            list = reList();
        }
        while (list.contains("-")) {
            countOperation = calculate(list, "-", countOperation);
            list = reList();
        }
        return countOperation;
    }

    public int calculate(List<String> list, String operation, int countOperation) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(operation)) {
                double result = 0;
                switch (operation) {
                    case "*":
                        result = Double.parseDouble(list.get(i - 1)) * Double.parseDouble(list.get(i + 1));
                        countOperation++;
                        break;
                    case "/":
                        result = Double.parseDouble(list.get(i - 1)) / Double.parseDouble(list.get(i + 1));
                        countOperation++;
                        break;
                    case "+":
                        result = Double.parseDouble(list.get(i - 1)) + Double.parseDouble(list.get(i + 1));
                        countOperation++;
                        break;
                    case "-":
                        result = Double.parseDouble(list.get(i - 1)) - Double.parseDouble(list.get(i + 1));
                        countOperation++;
                        break;
                    case "^":
                        result = Math.pow(Double.parseDouble(list.get(i - 1)), Double.parseDouble(list.get(i + 1)));
                        countOperation++;
                        break;
                }

                String replacement = list.get(i - 1) + list.get(i) + list.get(i + 1);
                str = str.replace(replacement, String.valueOf(result));
                list = reList();
            }
        }
        return countOperation;
    }

    public List<String> reList() {
        list = new ArrayList<>();
        char[] chars = str.toCharArray();
        //System.out.println(Arrays.toString(chars));
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if ((c >= '0' && c <= '9') || c == '.' || chars[0] == '-' || (chars[i] == '-' && String.valueOf(chars[i - 1]).matches("[\\(\\+\\-\\*\\\\^]"))) {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                for (int j = i + 1; j < chars.length; j++) {
                    char cc = chars[j];
                    if ((cc >= '0' && cc <= '9') || cc == '.') {
                        sb.append(cc);
                        i++;
                        chars[0] = '1';
                    } else
                        break;
                }
                list.add(sb.toString());
            } else
                list.add(String.valueOf(c));
        }
        return list;
    }
}
