<%@ page contentType = "text/html; charset=UTF-8" pageEncoding = "UTF-8" %>
<%@ page import = "bbs.Message" %>

<%!
private String escapeHTML(String src) {
    return src.replace("&", "&amp;").replace("<", "&lt;")
              .replace(">", "&gt;").replace("\"", "&quot;")
              .replace("'", "&#39;");
    }
%>

<html>
<head>
    <title>テスト掲示板</title>
</head>
<body>
    <h1>テスト掲示板</h1>
    <form action="/testbbs_jsp/PostBBS" method="POST">
    タイトル：<input type="text" name="title" size="60"/><br/>
    ハンドル名：<input type="text" name="handle"/><br/>
    <textarea name="message" cols="60" rows="4"></textarea><br/>
    <input type="submit"/>
</form>
<hr/>
<%
for (Message message : Message.messageList) {
%>
<p>「<%= escapeHtml(message.title) %>」&nbsp;&nbsp;
<%= escapeHTML(message.handle) %> さん&nbsp;&nbsp;
<%= escapeHTML(message.date.toString()) %>
</p>
<p>
<%= escapeHTML(message.message).replace("\r\n", "<br/>") %>
</p><hr/>
<%
}
%>
</form>
</body>
</html>