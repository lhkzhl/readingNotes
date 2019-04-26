import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class Main {

    public static void main(String[] args) {
//        System.out.println("Hello World!");
//        System.out.println("sqrt:" + hlSqrt(1000));
//
//        System.out.println("Hello World!");


//        BigDecimal value = new BigDecimal(2342342233421.60);
//        DecimalFormat format = new DecimalFormat("#0.#######");
//        System.out.println(format.format(value));
//
//        System.out.println(decimalToBinary(8));

//        testBiniarySearchString();

//        testGetAllCompositionSolutions();

//        testMergeSort();


        testPermutate();


    }



    // O(n!)
    public static void permutate(ArrayList<String> restArray, ArrayList<String> result) {
        if ( restArray == null || restArray.size() == 0) {
            System.out.println(result);
            return;
        }
        for (int i = 0; i < restArray.size(); i++) {
            ArrayList<String> newRestArray = (ArrayList<String>) restArray.clone();
            String value = newRestArray.remove(i);
            ArrayList<String> newResult = (ArrayList<String>) result.clone();
            newResult.add(value);
            permutate(newRestArray, newResult);
        }
    }

    public static void testPermutate() {
        ArrayList<String>  list = new ArrayList<String>(){{add("A");add("B");add("C");}};
        permutate(list, new ArrayList<>());
    }


    /**

     * @Description: 十进制转换成二进制
     * @param decimalSource
     * @return String
     */
    public static String decimalToBinary(int decimalSource) {
        BigInteger bi = new BigInteger(String.valueOf(decimalSource)); // 转换成 BigInteger 类型，默认是十进制
        return bi.toString(2); // 参数 2 指定的是转化成二进制
    }

    /**
     * @Description: 二进制转换成十进制
     * @param binarySource
     * @return int
     */
    public static int binaryToDecimal(String binarySource) {
        BigInteger bi = new BigInteger(binarySource, 2);  // 转换为 BigInteger 类型，参数 2 指定的是二进制
        return Integer.parseInt(bi.toString());     // 默认转换成十进制
    }


    /**
     * 求1-n的平方根
     * @param n
     * @return
     */
    public static double hlSqrt(int n) {
        if (n < 1) {
            return -1;
        }
        double low = 1;
        double high  = n;
        double mid = low + (high - low)/2;

        double deltaThreshold = 0.001;
//        注意点，这里的绝对值容易忘
        while (Math.abs(mid * mid - n ) > deltaThreshold) {
            if (mid * mid > n) {
                high = mid;
            } else if (mid *mid < n) {
                low = mid;
            } else {
                return mid;
            }
            mid = low + (high - low)/2;
//            System.out.println(mid);
        }
       return mid;
    }


    //注意点1.low <= high,(1个元素，最容易想) 2.low = mid +1; 下标+/- 1,3.如果用右移操作符，记得括号
    public static int biniarySearch(String[] list,String value) {
        if (list == null) { return -1; }

        int low = 0;
        int high = list.length -1;

        while (low <= high)  {
            int mid = low + ((high - low)>>1);
            if (value == list[mid]) {
                return mid;
            } else if (list[mid].compareTo(value) < 1) {
                low = mid + 1;
            } else {
                high = mid -1;
            }
        }
        return -1;
    }

    public static void testBiniarySearchString() {
        String[] dictionary = {"i", "am", "one", "of", "the", "authors", "in", "geekbang"};

        Arrays.sort(dictionary);
        for (int i = 0; i < dictionary.length; i++) {
            System.out.printf(dictionary[i] + " ");
        }

        String wordToFind = "the2";

        int found = biniarySearch(dictionary, wordToFind);
        if (found > 0) {
            System.out.println(String.format(" 找到了单词 %s,index %d", wordToFind,found));
        } else {
            System.out.println(String.format(" 未能找到单词 %s", wordToFind));
        }
    }


    //有个问题，有重复的数据
    static int[] compositions = {1,2,5,10};
    public static int getAllCompositionSolutions(int totoal, LinkedList<Integer>  list) {
        if (totoal == 0) {
            System.out.println(list);
            return 1;
        } else if (totoal < 0) {
            return 0;
        }

        int sum = 0;
        for (int i = 0; i < compositions.length; i++) {
            LinkedList<Integer> newList = new LinkedList<>(list);
            newList.add(compositions[i]);
              sum += getAllCompositionSolutions(totoal - compositions[i] , newList);
        }
        return sum;
    }

    public static void testGetAllCompositionSolutions() {
        int totalCount =  getAllCompositionSolutions(10, new LinkedList<>());
        System.out.println("总个数："+totalCount);
    }

    public static List<Comparable> mergeSort(List<Comparable> list) {
        System.out.println(list);
        if (list == null) {
            return null;
        }
        if (list.size() <= 1) {
            return list;
        }

        int left = 0;
        int right = list.size();
        int mid = left + ((right - left)>>1);
        System.out.print("left:          ");
        List<Comparable> leftList = mergeSort(list.subList(left, mid));
        List<Comparable> rightList;
        if (mid > right)  {
            rightList  = new LinkedList<>();
        } else {
            System.out.print("right:         ");
            rightList  = mergeSort(list.subList(mid, right));
        }

        int index = leftList.size() < rightList.size() ? leftList.size():rightList.size();
        List<Comparable> result = new LinkedList<>();
        int i = 0,j = 0;
        while (i<leftList.size() && j<rightList.size()) {
            if (leftList.get(i).compareTo(rightList.get(i)) < 0) {
                result.add(leftList.get(i));
                i ++;
            } else {
                result.add(rightList.get(j));
                j++;
            }
        }
        for (; i < leftList.size(); i++) {
            result.add(leftList.get(i));
        }
        for (; j < rightList.size(); j++) {
            result.add(rightList.get(j));
        }
        System.out.println("result:"+result);
        return result;
    }

    public static void testMergeSort() {
//        int[] a = {};

        List<Comparable> list = Arrays.asList(1,5,2,3,4,8,9,6,7,29,43,23);
        System.out.println(mergeSort(list));
    }
}
