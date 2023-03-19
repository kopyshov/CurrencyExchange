package servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import repositories.ExchangeRateRepository;
import services.AllExchangeRateService;
import services.ResponseGenerator;
import services.Service;

import java.io.IOException;
import java.net.URISyntaxException;

@WebServlet(urlPatterns = {"/exchangeRates/*"})
public class ExchangeRatesServlet extends HttpServlet {
    Service exeService;
    ResponseGenerator responseGenerator;
    ExchangeRateRepository exchangeRateRepository;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            responseGenerator = new ResponseGenerator(req, resp);
            exeService = new AllExchangeRateService();
            responseGenerator.generateResponse(exeService);
        } catch (IOException ex) {
            responseGenerator.DBisNotFound();
            ex.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            req.setCharacterEncoding("UTF-8");
            String baseCode = req.getParameter("basecode");
            String targetCode = req.getParameter("targetcode");
            String rate = req.getParameter("rate");

            if (baseCode.isEmpty() || targetCode.isEmpty() || rate.isEmpty()) {
                responseGenerator.misField();
                return;
            }
            if (baseCode.length() != 3 || targetCode.length() != 3 || !rate.matches("\\d")) {
                responseGenerator.codeIsIncorrect();
            } else {
                exchangeRateRepository.insertExchangeRate(baseCode, targetCode, rate);
            }
        } catch (IOException ex) {
            responseGenerator.DBisNotFound();
            ex.printStackTrace();
        }
    }
}
