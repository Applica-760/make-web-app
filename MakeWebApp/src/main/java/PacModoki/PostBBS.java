package PacModoki;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;

public class PostBBS extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding("UTF-8");
        Message newMessage = new Message(request.getParameter("title"),
                request.getParameter("handle"),
                request.getParameter("message"));
        Message.messageList.add(0, newMessage);
        response.sendRedirect("/testbbs/ShowBBS");
    }
}