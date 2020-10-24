# ViewPagerIndicator
view pager 指示器
## 怎么使用？

在工程下build.gradle中添加 maven { url 'https://jitpack.io' }，如下
```groovy
allprojects {
   repositories {
	...
      maven { url 'https://jitpack.io' }
   }
}
```

在module的build.gradle中添加下边的依赖 
```groovy
dependencies {
   implementation 'com.github.WuKaiXia:ViewPagerIndicator:v0.1-alpha'
}
```
