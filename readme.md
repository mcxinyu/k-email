# k-email

[![](https://jitpack.io/v/mcxinyu/k-email.svg)](https://jitpack.io/#mcxinyu/k-email)

用 kotlin 封装的 jvm 发邮件库，把发邮件中琐碎的、可能出错的地方封装起来。

## 一些事情

[Apache Commons Email](https://github.com/apache/commons-email) 无疑是个好库，但是发邮件一不小心容易乱码，发 HTML 会出现各种乱七八糟的换行和行间距问题。

[Jakarta Mail](https://github.com/jakartaee/mail-api) 也是一个好库，但是使用上没有那么便捷，**本库是对其的封装**。

## 特点

- 是封装，也是样例
- 支持附件、抄送、密抄、多收件人
- 支持 Text、HTML，两者混合
- 支持发件人昵称
- 更多...

## 引入

- 参考 [JitPack-k-email](https://jitpack.io/#mcxinyu/k-email)
- 或者复制文件 [EmailHelper.kt](src%2Fmain%2Fkotlin%2Fio%2Fgithub%2Fmcxinyu%2Fkemail%2FEmailHelper.kt) 到你的项目中（记得依赖 [Jakarta Mail](https://github.com/jakartaee/mail-api)）

## 使用

阅读下 [EmailHelperTest.kt](src%2Ftest%2Fkotlin%2FEmailHelperTest.kt) 吧。

```kotlin
emailHelper(smtp).user(from, pwd).email()
    .subject("事件通知")
    .from("服务端")
    .to(to)
    .html("html")
    .send()
```
