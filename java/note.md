# Java

## 设计模式

#### 软件设计原则

1. 开闭原则(扩展开放，修改关闭)
2. 单一职责原则(一个类应该只有一个发生变化的原因)
3. 依赖倒置原则(依赖于抽象接口，不要依赖于具体实现，松耦合)
4. 接口隔离原则(接口隔离性，拆分，解耦)
5. 迪米特法则(最少知道原则,**private,protect**)
6. 里氏替换原则(子类可以扩展但是不改变父类的原来功能)
7. 合成复用原则(使用组合，聚合，而不使用继承达到代码复用的目的)

目的

1. 写出优雅代码
2. 重构项目
3. 经典框架都在用设计模式解决问题

Spring 中用到的设计模式
工厂模式　BeanFactory
装饰器模式　BeanWrapper
代理模式　AopProxy
单例模式　ApplicationContext
委派模式　DispatcherServlet
策略模式　HandlerMapping
适配器模式　HandlerApdapter
模板方法模式　JdbcTemplate
观察者模式　ContextLoaderListener
...

Spring IOC 工厂、单例、装饰器
Spring AOP　代理、观察者
Spring MVC　委派、适配器
Spring JDBC　模板方法

### 工厂模式

简单工厂模式(Simple Factory Pattern):由一人工厂对象决定创建出哪一种产品
属于创建型模式，但不属于GOF，23种设计模式
像java中的Calendar,LoggerFactory

1. 简单工厂
   适用场景
   1.工厂类负责创建的对象较少
   2.客户端只需要传入工厂类的参数就可以，不需要关心创建逻辑
   缺点
   1.工厂类职责过重，增加新产品时，需修改工厂类判断逻辑，违背开闭原则
   2.不易扩展更复杂的产品结构

2. 工厂方法模式
   定义一个创建对象的**接口**，让实现这个接口的类来决定实现实例化哪个类，工厂方法让类的实例化推迟到子类中进行
   **适用于**
   创建对象需要大量重复的代码
   一个类通过子类的创建相应的对象
   **优点**

   不同的产品用不同的工厂来创建，不违背开闭原则，可以扩展，不用修改之前代码
   再增加产品时，增加产品工厂就好了
   **缺点**

   类变多，更复杂，

3. 抽象工厂模式
   创建一系列相关或相互依赖的接口，无须指定具体类
   不符合开闭原则(增加新的维度的产品时，需要改动)，但易于扩展，
   感觉适用于由多个维度决定的产品





像是IOS中的dateForm我就可以来个工厂模式，

根据一个名字生成一个对象



### 单例模式(Singleton Pattern)

－创建型
确保一个类在任何情况下都绝对只有一个实例，并提供一个全局访问点
隐藏其所有的构造方法

#### 四种常见模式

1. 饿汉式单例　在单例类首次加载时就创建单例
   浪费内存空间

2. 懒汉式单例
   如果不加锁，在判空赋值时，打Thread断点，第二个thread断点执行后续代码，可以必现，多线程问题
   加锁之后，从调试也可以看出，加锁的代码只能一个线程去执行，其他线程MONITOR(监听)状态
   优解

   ```
   //volatile解决指令重排序问题
   private volatile static LazySimpleSingleton lazy = null;
   
   //    双重检查锁,不加锁整个函数，有一定性能优化
   public static LazySimpleSingleton getInstance() {
     if (lazy == null) {
         synchronized (LazySimpleSingleton.class) {// 一重　synchronized
             if (lazy == null) { //二重　null
                 lazy = new LazySimpleSingleton();
             }
         }
     }
     return lazy;
   }
   ```

   volatile 暂时不重点展开
   volatile具备两种特性，第一就是保证共享变量对所有线程的可见性。将一个共享变量声明为volatile后，会有以下效应：

   　　　　**1.当写一个volatile变量时，JMM会把该线程对应的本地内存中的变量强制刷新到主内存中去；**

   　　　　**2.这个写会操作会导致其他线程中的缓存无效。**

   最优解,但可能被反射攻击

   ```
   public class LazyInnerClassSingleton {
       private LazyInnerClassSingleton(){}
   
       //    LazyHolder里面的逻辑需要等到外部的方法调用时才执行
   //    巧妙利用了内部类的特性
   //    JVM底层逻辑,完美避免了线程安全问题
       public static LazyInnerClassSingleton getInstance() {
           return LazyHolder.LAZY;
       }
       
       private static class LazyHolder {
           private static final LazyInnerClassSingleton LAZY = new LazyInnerClassSingleton();
       }
   }
   ```

   防止序列化破坏单例,可以看序列化的源码，readResolve的返回结果会替代序列化的结果

   ```
   private Object readResolve() {
       return LazyHolder.LAZY;
   }
   ```

3. 注册式单例
   将每一个实例都缓存到统一的容器中，使用唯一的标识获取实例

   1. EnumSingleton 从jdk层面，已为枚举不被序列化和反射保驾护航
   2. ContainerSingleton 需要自己加锁，通过map实现

4. ThreedLocal单例　
   伪线程安全，可以保证线程内部的全局唯一
   可以使用ThreadLocal来实现多数据源动态切换，

线程调试

1. 断点thread模式，(断点右击选择thread)

2. 选择线程运行
   ![image-20190412213918646](/Users/hailong/Library/Application Support/typora-user-images/image-20190412213918646.png)




#### 注意点

1. 私有化构造器
2. 线程安全
3. 延迟加载
4. 防止序列化和反序列化破坏单例对象　`readResolve`解决
5. 防御反射攻击单例　构造方法抛异常解决

**缺点：没有接口，扩展困难，如果要扩展单例对象，只有修改代码，没有其他途径**

总结，4种模式，5个特点

### 原型模式(Prototype Patten)

－创建型模式
原型实现指定创建对象的种类，并且通过拷贝这些原型创建新的对象
调用者不需要知道任何调用细节，不调用构造函数

适用场景

1. 类初始化消耗资源较多
2. new产生的一个对象需要非常繁琐的过程(数据准备，权限访问等)
3. 构造函数较复杂
4. 循环中生产大量对象时

实现：
1.简单工厂将setter、getter封装到某个方法，
2.jdk提供的Cloneabler接口

深、浅拷贝



### 代理模式

－结构型

指为其他对象提供一种代理，以控制对这个对象的访问
代理对象在客服端和目标对象之间起到中介作用
目的，保护目标对象，增强目标对象

#### 静态代理

聚合，代理拥有目标对象，实现对目标方法的增强



#### 动态代理

1. jdk实现　`InvocationHandler` 
   有点像oc中的`NSKVONotifying_Person`，会生成一个中间类

   ```
   public final class Proxy0 extends Proxy
       implements Person
   {
       public Proxy0(InvocationHandler invocationhandler)
       {
           super(invocationhandler);
       }
   
       public final void findLover()
       {m
           try
           {
               super.h.invoke(this, m3, null);
               return;
           }
           catch(Error _ex) { }
           catch(Throwable throwable)
           {
               throw new UndeclaredThrowableException(throwable);
           }
       }
   
       private static Method m1;
       private static Method m3;
       private static Method m2;
       private static Method m0;
   
       static 
       {
           try
           {
               m1 = Class.forName("java.lang.Object").getMethod("equals", new Class[] {
                   Class.forName("java.lang.Object")
               });
               m3 = Class.forName("com.zhl.pattern.Proxy.Person").getMethod("findLover", new Class[0]);
               m2 = Class.forName("java.lang.Object").getMethod("toString", new Class[0]);
               m0 = Class.forName("java.lang.Object").getMethod("hashCode", new Class[0]);
           }
           catch(NoSuchMethodException nosuchmethodexception)
           {
               throw new NoSuchMethodError(nosuchmethodexception.getMessage());
           }
           catch(ClassNotFoundException classnotfoundexception)
           {
               throw new NoClassDefFoundError(classnotfoundexception.getMessage());
           }
       }
   }
   ```

2. 自己实现HLProxy, 需要接口，扫描接口中方法

   1. 拿到被代理类的引用，获取所有接口中的所有方法
   2. 重新生成一个新的类，实现被代理类的所有接口方法
   3. 动态生成java代码，把增强逻辑加入到新的代码中去
   4. 编译生成新的java代码的class文件
   5. 加载并重新运行新的class得到全新类

3. cglib 动态生成一个继承目标类的子类
   

   ```
   //            //JDK是采用读取接口的信息
   //            //CGLib覆盖父类方法
   //            //目的：都是生成一个新的类，去实现增强代码逻辑的功能
   //
   //            //JDK Proxy 对于用户而言，必须要有一个接口实现，目标类相对来说复杂
   //            //CGLib 可以代理任意一个普通的类，没有任何要求
   //
   //            //CGLib 生成代理逻辑更复杂，效率,调用效率更高，生成一个包含了所有的逻辑的FastClass，不再需要反射调用
   //            //JDK Proxy生成代理的逻辑简单，执行效率相对要低，每次都要反射动态调用
   //
   //            //CGLib 有个坑，CGLib不能代理final的方法
   ```















idea快捷键

cmd+N　setter and getter 

Ctrl + i  implement method 