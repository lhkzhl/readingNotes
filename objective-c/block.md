

## block

block本质也是一个oc对象，内部也有isa指针
block是封装了函数调用及函数调用环境的oc对象

```
int g_weight = 30;
int main(int argc, const char * argv[]) {
    @autoreleasepool {
        int age = 10;
        static int height = 20;
        void (^block)(int a) = ^(int a){
            height = a;
            NSLog(@"%d--%d-%d",age,height,g_weight);
        };
        block(30);
｝
```

上面代码转cpp后为下面代码

```
				int age = 10;
        static int height = 20;
        void (*block)(int a) = ((void (*)(int))&__main_block_impl_0((void *)__main_block_func_0, &__main_block_desc_0_DATA, &height, age));
        ((void (*)(__block_impl *, int))((__block_impl *)block)->FuncPtr)((__block_impl *)block, 30);
```



```
//block_impl 基础结构，类似基类
struct __block_impl {
  void *isa;
  int Flags;
  int Reserved;
  void *FuncPtr;
};

struct __main_block_impl_0 {
  struct __block_impl impl;
  struct __main_block_desc_0* Desc;
  int *height;
  int age;
  __main_block_impl_0(void *fp, struct __main_block_desc_0 *desc, int *_height, int _age, int flags=0) : height(_height), age(_age) {
    impl.isa = &_NSConcreteStackBlock;
    impl.Flags = flags;
    impl.FuncPtr = fp;
    Desc = desc;
  }
};

//block调用函数会传入block自己及外部传入的参数，这样可以拿到block捕获外界的值和传入的参数
static void __main_block_func_0(struct __main_block_impl_0 *__cself, int a) {
  int *height = __cself->height; // bound by copy
  int age = __cself->age; // bound by copy

            (*height) = a;
            NSLog((NSString *)&__NSConstantStringImpl__var_folders_1p_ggtwy57x5v91l5zz1yqbb_pc0000gn_T_main_543aca_mii_3,age,(*height),g_weight);
        }
static struct __main_block_desc_0 {
  size_t reserved;
  size_t Block_size;
} __main_block_desc_0_DATA = { 0, sizeof(struct __main_block_impl_0)};

```

#### block变量捕获

| **变量类型** | **捕获到**block**内部** | **访问方式** |        |
| :----------- | ----------------------- | ------------ | ------ |
| 局部变量     | auto                    | √            | 值传递 |
| 局部变量 |static       						| √           | 指针传递 |
| 全局变量     | * | ×                       | 直接访问     |

像block内部调用self，self为局部变量，会捕获，类型通常为　`obj *`

block结构如下图

![image-20190421101641205](/Users/hailong/Library/Application Support/typora-user-images/image-20190421101641205.png)

#### Block类型

block有3种类型，可以通过调用class方法或者isa指针查看具体类型，最终都是继承自NSBlock类型
__NSGlobalBlock__ （ _NSConcreteGlobalBlock ）
__NSStackBlock__ （ _NSConcreteStackBlock ）
__NSMallocBlock__ （ _NSConcreteMallocBlock ）

![image-20190421102456253](/Users/hailong/Library/Application Support/typora-user-images/image-20190421102456253.png)



| **block**类型     | **环境**                   |
| ----------------- | -------------------------- |
| __NSGlobalBlock__ | 没有访问auto变量           |
| __NSStackBlock__  | 访问了auto变量             |
| __NSMallocBlock__ | __NSStackBlock__调用了copy |

调用block 调用copy的结果
![image-20190421102700018](/Users/hailong/Library/Application Support/typora-user-images/image-20190421102700018.png)



栈block不安全问题,以下代码需要放在mrc下，arc会自动调用copy，让栈block变成堆block

```
void (^g_block)();
void test() {
    int age = 10;
    g_block = ^{
        NSLog(@"block ----%d",age);
    };
}

int main(int argc, const char * argv[]) {
    @autoreleasepool {
        test();
        g_block();
	}
}
//打印结果：block -----272632536
// test函数调用完毕，age内存上的数据会变成垃圾数据
// 调用copy变成堆block会得到想要结果
g_block = [^{
        NSLog(@"block ----%d",age);
    } copy];　　
```

#### block的copy

在**ARC**环境下，编译器会根据情况自动将栈上的block复制到堆上，比如以下情况
**block作为函数返回值时**
**将block赋值给__strong指针时** 即`block = ^{};`
**block作为Cocoa API中方法名含有usingBlock的方法参数时**
**block作为GCD API的方法参数时**

**注意**

自己写的作为参数如果不用copy其实为**栈block**，一般没什么问题

**MRC**下block属性的建议写法
@property (**copy**, nonatomic) void (^block)(void);

ARC下block属性的建议写法
@property (**strong**, nonatomic) void (^block)(void);
@property (**copy**, nonatomic) void (^block)(void);

#### 对象类型的auto变量

```
当block内部访问了对象类型的auto变量时
如果block是在栈上，将不会对auto变量产生强引用

如果block被拷贝到堆上
会调用block内部的copy函数
copy函数内部会调用_Block_object_assign函数
_Block_object_assign函数会根据auto变量的修饰符（__strong、__weak、__unsafe_unretained）做出相应的操作，形成强引用（retain）或者弱引用

如果block从堆上移除
会调用block内部的dispose函数
dispose函数内部会调用_Block_object_dispose函数
_Block_object_dispose函数会自动释放引用的auto变量（release）

```

```
static struct __main_block_desc_0 {
  size_t reserved;
  size_t Block_size;
  void (*copy)(struct __main_block_impl_0*, struct __main_block_impl_0*);
  void (*dispose)(struct __main_block_impl_0*);
} __main_block_desc_0_DATA = { 0, sizeof(struct __main_block_impl_0),

static void __main_block_copy_0(struct __main_block_impl_0*dst, struct __main_block_impl_0*src) {_Block_object_assign((void*)&dst->weakObj, (void*)src->weakObj, 3/*BLOCK_FIELD_IS_OBJECT*/);}

static void __main_block_dispose_0(struct __main_block_impl_0*src) {_Block_object_dispose((void*)src->weakObj, 3/*BLOCK_FIELD_IS_OBJECT*/);}

```



__weak转换代码问题

```
在使用clang转换OC为C++代码时，可能会遇到以下问题
cannot create __weak reference in file using manual reference

解决方案：支持ARC、指定运行时系统版本，比如
xcrun -sdk iphoneos clan -arch arm64 -rewrite-objc -fobjc-arc -fobjc-runtime=ios-8.0.0 main.m

```



#### __block修饰符

`__block`可以用于解决block内部无法修改auto变量值的问题
`__block`不能修饰全局变量、静态变量（static）
编译器会将__block变量包装成一个对象

```
__block int age = 10;
^{
  NSLog(@"%d",age);
}()
NSLog(@"%d",age); //其实是使用的封装过的对象age.__forwarding->age
```

```
        __attribute__((__blocks__(byref))) __Block_byref_age_0 age = {(void*)0,(__Block_byref_age_0 *)&age, 0, sizeof(__Block_byref_age_0), 10};
        ((void (*)())&__main_block_impl_0((void *)__main_block_func_0, &__main_block_desc_0_DATA, (__Block_byref_age_0 *)&age, 570425344))();
        NSLog((NSString *)&__NSConstantStringImpl__var_folders_1p_ggtwy57x5v91l5zz1yqbb_pc0000gn_T_main_415512_mii_5,(age.__forwarding->age));
        
struct __Block_byref_age_0 {
  void *__isa;
__Block_byref_age_0 *__forwarding;//指向自己
 int __flags;
 int __size;
 int age;
};
struct __main_block_impl_0 {
  struct __block_impl impl;
  struct __main_block_desc_0* Desc;
  __Block_byref_age_0 *age; // by ref
  __main_block_impl_0(void *fp, struct __main_block_desc_0 *desc, __Block_byref_age_0 *_age, int flags=0) : age(_age->__forwarding) {
    impl.isa = &_NSConcreteStackBlock;
    impl.Flags = flags;
    impl.FuncPtr = fp;
    Desc = desc;
  }
};
```

#### __forwarding 指针

![image-20190421231725188](/Users/hailong/Library/Application Support/typora-user-images/image-20190421231725188.png)



![image-20190421232845124](/Users/hailong/Library/Application Support/typora-user-images/image-20190421232845124.png)

#### __block内存管理

当block在栈上时，并不会对__block变量产生强引用

当block被copy到堆时
会调用block内部的copy函数,copy函数内部会调用`_Block_object_assign函数`,`_Block_object_assign`函数会对__block变量形成强引用（retain）

![image-20190421232529930](/Users/hailong/Library/Application Support/typora-user-images/image-20190421232529930.png)



#### 被__block修饰的对象类型

当__block变量在栈上时，不会对指向的对象产生强引用

当block变量被copy到堆时
会调用block变量内部的copy函数
copy函数内部会调用_Block_object_assign函数
_Block_object_assign函数会根据所指向对象的修饰符（__strong、__weak、__unsafe_unretained）做出相应的操作，形成强引用（retain）或者弱引用（注意：这里仅限于ARC时会retain，MRC时不会retain）

如果block变量从堆上移除
会调用block变量内部的dispose函数
dispose函数内部会调用_Block_object_dispose函数
_Block_object_dispose函数会自动释放指向的对象（release）

#### 对象类型的auto变量、__block变量

当block在栈上时，对它们都不会产生强引用

当block拷贝到堆上时，都会通过copy函数来处理它们
__block变量（假设变量名叫做a）_
`_Block_object_assign((void*)&dst->a, (void*)src->a, 8/*BLOCK_FIELD_IS_BYREF*/);`

对象类型的auto变量（假设变量名叫做p）
`_Block_object_assign((void*)&dst->p, (void*)src->p, 3/*BLOCK_FIELD_IS_OBJECT*/);`

当block从堆上移除时，都会通过dispose函数来释放它们
__block变量（假设变量名叫做a）_

`_Block_object_dispose((void*)src->a, 8/*BLOCK_FIELD_IS_BYREF*/);`

对象类型的auto变量（假设变量名叫做p）
`_Block_object_dispose((void*)src->p, 3/*BLOCK_FIELD_IS_OBJECT*/);`



#### 解决循环引用问题 - ARC

```
__weak typeof(self) weakSelf = self;//指向对象销毁时会自动让指针置为nil
self.block = ^{
  	NSLog("%p",weakSelf);
}

__unsafe_unretained id weakSelf = self;//指向对象销毁时指针指向的地址值不变
self.block = ^{
  	NSLog("%p",weakSelf);
}
```

__block解决(必须调用block，及weakSelf = nil;来解决循环引用)

```
__block id weakSelf = self;
self.block = ^{
  	NSLog("%p",weakSelf);
  	weakSelf = nil;
}
self.block();
```

![image-20190421235850911](/Users/hailong/Library/Application Support/typora-user-images/image-20190421235850911.png)

#### 解决循环引用问题 - MRC

MRC不支持`__weak`

```
__unsafe_unretained id weakSelf = self;
self.block = ^{
  	NSLog("%p",weakSelf);
}

//MRC下__bloc对象不会对self产生强引用
__block id weakSelf = self;
self.block = ^{
  	NSLog("%p",weakSelf);
}
```





#### 练习题

1. block的原理是怎样的？本质是什么？
2. __block的作用是什么？有什么使用注意点？
3. block的属性修饰词为什么是copy？使用block有哪些使用注意？
4. block在修改NSMutableArray，需不需要添加__block？
5. block内部__strong