package kz.rk1;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

// Tomcat 9 немесе одан ескі болса: jakarta.servlet -> javax.servlet деп өзгертіңіз
@WebServlet(name = "TextAnalyzerServlet", urlPatterns = {"/analyze"})
public class TextAnalyzerServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Қазақ әріптері дұрыс оқылуы үшін кодтауды параметр оқудан БҰРЫН орнатамыз
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String text = request.getParameter("text");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html lang=\"kk\"><head><meta charset=\"UTF-8\">");
            out.println("<title>Нәтиже</title></head><body>");
            out.println("<h2>Мәтін талдау нәтижесі</h2>");

            if (text == null || text.trim().isEmpty()) {
                out.println("<p style=\"color:red\">мәтін енгізілмеді</p>");
            } else {
                // Браузер жол ауыстыруды \r\n деп жібереді, оны бір таңба деп санаймыз
                String normalized = text.replace("\r\n", "\n");

                int charCount = normalized.length();
                int charCountNoSpaces = normalized.replaceAll("\\s", "").length();
                int wordCount = normalized.trim().split("\\s+").length;

                out.println("<p>Таңбалар саны (бос орындармен): <b>" + charCount + "</b></p>");
                out.println("<p>Таңбалар саны (бос орынсыз): <b>" + charCountNoSpaces + "</b></p>");
                out.println("<p>Сөздер саны: <b>" + wordCount + "</b></p>");

                // XSS-тен қорғау: пайдаланушы мәтінін экрандап шығарамыз
                out.println("<h3>Енгізілген мәтін:</h3>");
                out.println("<pre>" + escapeHtml(normalized) + "</pre>");
            }

            out.println("<p><a href=\"index.html\">Артқа</a></p>");
            out.println("</body></html>");
        }
    }

    // Мекенжай жолынан тікелей ашылса, формаға қайтарамыз
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("index.html");
    }

    // HTML арнайы таңбаларын экрандау (XSS қорғанысы)
    private String escapeHtml(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '&':  sb.append("&amp;");  break;
                case '<':  sb.append("&lt;");   break;
                case '>':  sb.append("&gt;");   break;
                case '"':  sb.append("&quot;"); break;
                case '\'': sb.append("&#39;");  break;
                default:   sb.append(c);
            }
        }
        return sb.toString();
    }
}
