### 面试题

1. 一个NSObject对象占用多少内存？

   1. 我们平时编写的代码oc代码，底层都是c/c++实现的

      ```
      oc -> c\c++ -> 汇编语言　-> 机器语言
      ```

      oc的对象、类主要是基于c\c++的**结构体**实现的

      ```
      重写oc代码为c++代码
      clang -rewrite-objc main.m -o main.cpp  
      //指定平台及架构
      xcrun -sdk iphoneos clang -arch arm64 -rewrite-objc main.m -o main-arm64.cpp
      ```

      ```
      int main(int argc, const char * argv[]) {
          /* @autoreleasepool */ { __AtAutoreleasePool __autoreleasepool; 
      
              NSLog((NSString *)&__NSConstantStringImpl__var_folders_1p_ggtwy57x5v91l5zz1yqbb_pc0000gn_T_main_29c53f_mi_0);
      
              Person *p = ((Person *(*)(id, SEL))(void *)objc_msgSend)((id)((Person *(*)(id, SEL))(void *)objc_msgSend)((id)objc_getClass("Person"), sel_registerName("alloc")), sel_registerName("init"));
          }
          return 0;
      }
      
      //Class　指定　64位8个字节
      typedef struct objc_class *Class;
      struct NSObject_IMPL {
      	Class isa;　//只有一个成员变量
      };
      struct Person_IMPL {
      	struct NSObject_IMPL NSObject_IVARS;
      };
      
      struct objc_class {
          Class _Nonnull isa ;
      };
      
      Person *p = [[Person alloc] init];
      NSLog(@"%zd",class_getInstanceSize([Person class])); // 8
      NSLog(@"%zd",malloc_size((__bridge const void*)p));	 // 16
      
      
      ```

      

      

   2. 分配了16个字节，但利用的只有8个，即成员变量的字节数

2. 对象的isa指针指向哪里？

3. oc的类信息存放在哪里？





item2快捷键设置