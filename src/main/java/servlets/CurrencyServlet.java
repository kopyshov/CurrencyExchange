package servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repositories.CurrencyRepository;
import services.FindCurrencyService;
import services.ResponseGenerator;
import services.Service;

import java.io.IOException;
import java.net.URISyntaxException;

@WebServlet(urlPatterns = {"/currency/*"})
public class CurrencyServlet extends HttpServlet {

    Service exeService;
    ResponseGenerator responseGenerator;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        responseGenerator = new ResponseGenerator(req, resp);
        String requestURI = req.getRequestURI();
        String path = (requestURI).substring(requestURI.lastIndexOf('/') + 1);
        if (path.isEmpty()) {
            responseGenerator.codeIsMissing();
            return;
        }
        if(path.matches("([a-zA-Z]){3}") || path.matches("\\d")) {
            exeService = new FindCurrencyService();
        } else {
            responseGenerator.codeIsIncorrect();
        }
        responseGenerator.generateResponse(exeService);
    }
}
