package services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class ResponseGenerator {
    HttpServletRequest req;
    HttpServletResponse resp;

    public ResponseGenerator(HttpServletRequest req, HttpServletResponse resp) {
        this.req = req;
        this.resp = resp;
    }

    public void generateResponse(Service exeService) throws IOException {
        String answer = exeService.execute(req);
        if (answer.isEmpty()) {
            resp.sendError(404, "Валюта не найдена");
        } else {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter out = resp.getWriter();
            out.print(answer);
            out.flush();
        }
    }

    public void DBisNotFound () throws IOException {
        resp.sendError(500, "База данных недоступна.");
    }

    public void currencyExists() throws IOException {
        resp.sendError(409, "Валюта с таким кодом уже уществует");
    }

    public void misField() throws IOException {
        resp.sendError(400, "Отсутствует нужное поле формы");
    }

    public void codeIsMissing() throws IOException {
        resp.sendError(400, "Код валюты отсутствует в адресе");
    }

    public void codeIsIncorrect() throws IOException {
        resp.sendError(400, "Некорректно указан код валюты");
    }
}
