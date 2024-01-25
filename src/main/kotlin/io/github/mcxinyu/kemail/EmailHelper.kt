package io.github.mcxinyu.kemail

import java.io.File
import java.net.URL
import java.util.*
import jakarta.activation.DataHandler
import jakarta.activation.FileDataSource
import jakarta.mail.*
import jakarta.mail.internet.*

/**
 *
 * @param smtp [String] smtp 服务器地址
 * @param debug [Boolean]
 * @return [Properties]
 */
fun emailHelper(smtp: String, debug: Boolean = false): Properties = Properties().also {
    it["mail.smtp.ssl.enable"] = "true"
    it["mail.transport.protocol"] = "smtp"
    it["mail.debug"] = "$debug"
    it["mail.smtp.timeout"] = "10000"
    it["mail.smtp.port"] = "465"
    it["mail.smtp.host"] = smtp
}

/**
 *
 * @receiver [Properties]
 * @param username [String] 邮箱
 * @param password [String] 邮箱的密码
 * @return [Properties]
 */
fun Properties.user(username: String, password: String? = null): Properties = apply {
    this["mail.smtp.auth"] = "${!password.isNullOrEmpty()}"
    this["username"] = username
    this["password"] = password
}

fun Properties.session(): Session =
    Session.getInstance(this,
        if (this@session["mail.smtp.auth"] != "true") null
        else object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(
                    this@session["username"].toString(),
                    this@session["password"].toString()
                )
            }
        }
    )

fun Properties.email(): KEmail =
    KEmail(this.session(), this@email["username"].toString())

/**
 *
 * @property who [String] who will send the email
 * @constructor
 *
 * @author <a href=mailto:mcxinyu@foxmail.com>yuefeng</a> in 2024/1/24.
 */
class KEmail(session: Session, private val who: String) {
    private var message: MimeMessage = MimeMessage(session)
    private var text: String? = null
    private var html: String? = null
    private val attachments = mutableListOf<MimeBodyPart>()

    /**
     * email subject
     *
     * @param subject subject title
     */
    fun subject(subject: String?): KEmail = apply {
        message.setSubject(subject, "UTF-8")
    }

    /**
     * 昵称和或发件人
     *
     * @param nickName 支持自定义发件人昵称
     *
     * @param from     from email
     */
    fun from(nickName: String? = null, from: String = who): KEmail = apply {
        message.setFrom(InternetAddress("${MimeUtility.encodeText(nickName)} <$from>"))
    }

    /**
     * 回复邮件
     *
     * @param replyTo Array<out String?>
     * @return KEmail
     */
    fun replyTo(vararg replyTo: String): KEmail = apply {
        message.replyTo =
            InternetAddress.parse(listOf(*replyTo).joinToString(",") { it.replace("(^\\[|\\]$)".toRegex(), "") })
    }

    /**
     * 回复邮件
     *
     * @param replyTo String
     * @return KEmail
     */
    fun replyTo(replyTo: String): KEmail = apply {
        message.replyTo = InternetAddress.parse(replyTo.replace(";", ","))
    }

    /**
     * 发送到
     *
     * @param to Array<out String>
     * @return KEmail
     */
    fun to(vararg to: String): KEmail = apply {
        addRecipients(listOf(*to).toTypedArray(), Message.RecipientType.TO)
    }

    /**
     * 发送到
     * @param to String
     * @return KEmail
     */
    fun to(to: String): KEmail = apply {
        addRecipient(to, Message.RecipientType.TO)
    }

    /**
     * 抄送到
     *
     * @param cc Array<out String>
     * @return KEmail
     */
    fun cc(vararg cc: String): KEmail = apply {
        addRecipients(listOf(*cc).toTypedArray(), Message.RecipientType.CC)
    }

    /**
     * 抄送到
     *
     * @param cc String
     * @return KEmail
     */
    fun cc(cc: String): KEmail = apply {
        addRecipient(cc, Message.RecipientType.CC)
    }

    /**
     * 密送到
     *
     * @param bcc Array<out String>
     * @return KEmail
     */
    fun bcc(vararg bcc: String): KEmail = apply {
        addRecipients(listOf(*bcc).toTypedArray(), Message.RecipientType.BCC)
    }

    /**
     * 密送到
     *
     * @param bcc String
     * @return KEmail
     */
    fun bcc(bcc: String): KEmail = apply {
        addRecipient(bcc, Message.RecipientType.BCC)
    }

    /**
     * 增加收件人
     *
     * @param recipients Array<String>
     * @param type RecipientType
     * @return KEmail
     */
    private fun addRecipients(recipients: Array<String>, type: Message.RecipientType): KEmail = apply {
        val addresses = InternetAddress.parse(listOf(*recipients).joinToString(",") { it.replace("(^\\[|\\]$)", "") })
        message.setRecipients(type, addresses)
    }

    /**
     * 增加收件人
     *
     * @param recipient String
     * @param type RecipientType
     * @return KEmail
     */
    private fun addRecipient(recipient: String, type: Message.RecipientType): KEmail = apply {
        message.setRecipients(type, InternetAddress.parse(recipient.replace(";", ",")))
    }

    fun text(text: String): KEmail = apply { this.text = text }

    fun html(html: String): KEmail = apply { this.html = html }

    /**
     * 附件
     *
     * @param file File
     * @return KEmail
     */
    fun attach(file: File): KEmail = apply {
        attachments.add(createAttachment(file, null))
    }

    /**
     * 附件
     *
     * @param file File
     * @param fileName String?
     * @return KEmail
     */
    fun attach(file: File, fileName: String?): KEmail = apply {
        attachments.add(createAttachment(file, fileName))
    }

    /**
     * 附件
     *
     * @param url URL
     * @param fileName String?
     * @return KEmail
     */
    fun attachURL(url: URL, fileName: String?): KEmail = apply {
        attachments.add(createURLAttachment(url, fileName))
    }

    private fun createAttachment(file: File, fileName: String?) = MimeBodyPart().also {
        val fds = FileDataSource(file)
        it.dataHandler = DataHandler(fds)
        it.fileName = fileName?.let { MimeUtility.encodeText(it) } ?: MimeUtility.encodeText(fds.name)
    }

    private fun createURLAttachment(url: URL, fileName: String?) = MimeBodyPart().also {
        val dataHandler = DataHandler(url)
        it.dataHandler = dataHandler
        it.fileName = fileName?.let { MimeUtility.encodeText(it) } ?: MimeUtility.encodeText(fileName)
    }

    private fun MimeMultipart.toBodyPart() = MimeBodyPart().also { it.setContent(this) }

    private fun textPart() = MimeBodyPart().also { it.setText(text) }

    private fun htmlPart() = MimeBodyPart().also { it.setContent(html, "text/html; charset=utf-8") }

    fun send() {
        require(text != null || html != null) { "Text or HTML must have at least one." }

        val pending: MimeMultipart

        val alternative = when {
            text != null && html == null -> { // Text
                pending = MimeMultipart("mixed")
                pending.addBodyPart(textPart())
                false
            }

            text == null && html != null -> { // HTML
                pending = MimeMultipart("mixed")
                pending.addBodyPart(htmlPart())
                false
            }

            else -> { // HTML + Text
                pending = MimeMultipart("alternative")
                pending.addBodyPart(textPart())
                pending.addBodyPart(htmlPart())
                true
            }
        }

        var content = pending
        if (alternative && attachments.size > 0) {
            content = MimeMultipart("mixed")
            content.addBodyPart(pending.toBodyPart())
        }

        for (attachment in attachments) {
            content.addBodyPart(attachment)
        }

        message.setContent(content)
        message.sentDate = Date()
        Transport.send(message)
    }
}