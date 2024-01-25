import io.github.mcxinyu.kemail.email
import io.github.mcxinyu.kemail.emailHelper
import io.github.mcxinyu.kemail.user
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.test.Test

/**
 * @author <a href=mailto:mcxinyu@foxmail.com>yuefeng</a> in 2024/1/25.
 */
class EmailHelperTest {
    @Test
    fun tests() {
        val simple_yyyyMMddHHmmss = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE)

        val smtp = System.getenv("smtp")
        val from = System.getenv("from")
        val pwd = System.getenv("pwd")
        val to = System.getenv("to")

        emailHelper(smtp).user(from, pwd).email()
            .subject("事件通知 event notification")
            .from("服务端 service")
            .to(to)
            .attachURL(
                URL("https://album.biliimg.com/bfs/new_dyn/bc1d30b61d7cdbfd5162659715ff87941228892862.png@1048w_!web-dynamic.webp"),
                "广西消防.png"
            )
            .html(
                """
|<!DOCTYPE html>
|<html lang="zh">
|<head>
|    <meta charset="UTF-8">
|    <title>api-service</title>
|</head>
|<body>
|    <h1 style="color: Green;">你好哟 hello</h1>
|    发邮件，本该如此简单。<br>
|    一只敏捷的棕色狐狸跳过一只懒惰的狗<br>
|    The quick brown fox jumps over the lazy dog<br>
|    <img src='https://album.biliimg.com/bfs/new_dyn/bc1d30b61d7cdbfd5162659715ff87941228892862.png@1048w_!web-dynamic.webp' alt='广西消防'><br>
|    1月23日，消防员在云南镇雄县山体滑坡废墟下找到一对60余岁夫妻，两人紧拉着手，在场人员瞬间泪目...画师还原现场，愿再无灾难！@广西消防<br>
|    发送时间：${simple_yyyyMMddHHmmss.format(Date())}
|</body>
|</html>
                """.trimMargin()
            )
            .send()
        assert(true)
    }
}
