## 程序员的数学基础课

### 二进制计数法

十进制计数是使用 10 作为基数，那么二进制就是使用 2 作为基数，类比过来，二进制的数位就是 2^n 的形式。如果需要将这个数字转化为人们易于理解的十进制，我们就可以这样来计算：
![](https://static001.geekbang.org/resource/image/c6/c0/c6ae1772d7bf369aa9939fc00ca7b5c0.jpg)

```
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
```

计算机使用二进制和现代计算机系统的硬件实现有关。组成计算机系统的逻辑电路通常只有两个状态，即开关的接通与断开。 断开的状态我们用“0”来表示，接通的状态用“1”来表示。由于每位数据只有断开与接通两种状态，所以即便系统受到一定程度的干扰时，它仍然能够可靠地分辨出数字是“0”还是“1”。因此，在具体的系统实现中，二进制的数据表达具有抗干扰能力强、可靠性高的优点。

#### 二进制的位操作

1. 左移右移
   二进制 110101 向左移一位，就是在末尾添加一位 0，因此 110101 就变成了 1101010。请注意，这里讨论的是数字没有溢出的情况。
   所谓**数字溢出**，就是二进制数的位数超过了系统所指定的位数。目前主流的系统都支持至少 32 位的整型数字，而 1101010 远未超过 32 位，所以不会溢出。如果进行左移操作的二进制已经超出了 32 位，左移后数字就会溢出，需要将溢出的位数去除。
   **二进制左移一位，其实就是将数字翻倍。**
   **二进制右移一位，就是将数字除以 2 并求整数商的操作。**
   **左移位是 <<, java/python 中逻辑右移>>>和算术右移>>**　　

   逻辑右移 1 位，左边补 0 即可。算术右移时保持符号位不变，除符号位之外的右移一位并补符号位 1。补的 1 仍然在符号位之后。
   **c/c++中逻辑右移和算数右移共享同一个运算符 >>如果运算数类型是 unsigned，则采用逻辑右移；而是 signed，则采用算数右移。如果你针对 unsigned 类型的数据使用算数右移，或者针对 signed 类型的数据使用逻辑右移，那么你首先需要进行类型的转换。**

2. 按位“或”`|`、“与” `&` 、“异或”`^`

### 余数　

余数总是在一个固定的范围内，取余操作本身就是个哈希函数
**同余定理**。简单来说，就是两个整数 a 和 b，如果它们除以正整数 m 得到的余数相等，我们就可以说 a 和 b 对于模 m 同余。**同余定理其实就是用来分类的**

**哈希**简单来说，它就是**将任意长度的输入，通过哈希算法，压缩为某一固定长度的输出。**

如何快速读写 100 万条数据记录？(没有能够容纳 100 万条记录的连续地址空间)

![](https://static001.geekbang.org/resource/image/b3/58/b32e791f822044f579b80ad2cfe48c58.jpg)

其中x 表示等待被转换的数值，而 size 表示有限存储空间的大小，mod 表示取余操作。**通过余数，你就能将任何数值，转换为有限范围内的一个数值，然后根据这个新的数值，来确定将数据存放在何处。**

假设有两条记录，它们的记录标号分别是 1 和 101。我们把这些模 100 之后余数都是 1 的，存放到第 1 个可用空间里。以此类推，将余数为 2 的 2、102、202 等，存放到第 2 个可用空间，将 100、200、300 等存放到第 100 个可用空间里。 这样，我们就可以根据求余的快速数字变化，对数据进行分组，并把它们存放到不同的地址空间里。而求余操作本身非常简单，因此几乎不会增加寻址时间。

还可以进行优化`f(x)= (x + max) mod size`,max为随机数，增加数列的随机程序

### 迭代法

**迭代法，简单来说，其实就是不断地用旧的变量值，递推计算新的变量值。**迭代法的思想，很容易通过计算机语言中的循环语言来实现

**应用场景**

1. **求数值的精确或者近似解**。典型的方法包括二分法（Bisection method）和牛顿迭代法（Newton’s method）。
   迭代可以帮助我们进行无穷次地逼近，求得方程的精确或者近似解。
2. **在一定范围内查找目标值。**典型的方法包括二分查找。
3. **机器学习算法中的迭代**。相关的算法或者模型有很多，比如 K- 均值算法（K-means clustering）、PageRank 的马尔科夫链（Markov chain）、梯度下降法（Gradient descent）等等。迭代法之所以在机器学习中有广泛的应用，是因为**很多时候机器学习的过程，就是根据已知的数据和一定的假设，求一个局部最优解**。而迭代法可以帮助学习算法逐步搜索，直至发现这种解。



#### 应用举例

1. **比如说，我们想计算某个给定正整数 n（n>1）的平方根，如果不使用编程语言自带的函数，你会如何来实现呢？**
   ![](https://static001.geekbang.org/resource/image/89/7d/89c9c38113624288091cd65ff3d8957d.jpg)**注意绝对值**

    ```
    /**
     * 求1-n的平方根
     * @param n
     * @return
     */
    public static double hlSqrt(int n) {
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
    ```

   还可以这么做

    ```
     public static double getSqureRoot(int n, double deltaThreshold, int maxTry) {
   
     }
    ```

    1.精确度由外面传入，避免耗费大量时间，
    2.传入最大尝试次数，出于良好习惯，避免死循环

2. 二分查找，针对有序数组(int,string,只要有序都可以)

   ```
   //注意点1.low <= high,(1个元素，最容易想) 2.low = mid +1; 下标+/- 1,3.如果用右移操作符，记得括号,4.value没有判空，做为传入参数比较好
   public static int biniarySearch(String[] list,String value) {
       if (list == null) { return -1; }
   
       int low = 0;
       int high = list.length -1;
   
       while (low <= high)  {
           int mid = low + ((high - low)>>1);
           if (value == list[mid]) {
               return mid;
               //value没有判空，做为传入参数比较好
           } else if (list[mid].compareTo(value) < 1) {
               low = mid + 1;
           } else {
               high = mid -1;
           }
       }
       return -1;
   }
   ```

   

### 数学归纳法

数学归纳法的一般步骤是这样的：
**1.证明基本情况（通常是 n=1n=1 的时候）是否成立；**
**2.假设 n=k−1n=k−1 成立，再证明 n=kn=k 也是成立的（kk 为任意大于 11 的自然数）。**

和使用迭代法的计算相比，数学归纳法最大的特点就在于“归纳”二字。它已经总结出了规律。只要我们能够证明这个规律是正确的，就没有必要进行逐步的推算，可以节省很多时间和资源。

数学归纳法在理论上证明了命题是否成立，而无需迭代那样反复计算，因此可以帮助我们节约大量的资源，并大幅地提升系统的性能。
 数学归纳法实现的运行时间几乎为 0。不过，数学归纳法需要我们能做出合理的命题假设，然后才能进行证明。虽然很多时候要做这点比较难，确实也没什么捷径。你就是要多做题，多去看别人是怎么解题的，自己去积累经验。 最后，我通过函数的递归调用，模拟了数学归纳法的证明过程。如果你细心的话，会发现递归的函数值返回实现了从 k=1开始到 k=n的迭代



### 递归

既然递归的函数值返回过程和基于循环的迭代法一致，我们直接用迭代法不就好了，为什么还要用递归的数学思想和编程方法呢？这是因为，在某些场景下，**递归的解法比基于循环的迭代法更容易实现**。



1、2、5、10元组成总共为10元的组合有多少种？下面有重复

```
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
    		//
        LinkedList<Integer> newList = new LinkedList<>(list);
        newList.add(compositions[i]);
          sum += getAllCompositionSolutions(totoal - compositions[i] , newList);
    }
    return sum;
}
```



**归并排序(merge sort)**
核心：**就是把两个有序的数列合并起来，形成一个更大的有序数列。**









[libwep.framework下载](https://developers.google.com/speed/webp/download?hl=zh-CN)



