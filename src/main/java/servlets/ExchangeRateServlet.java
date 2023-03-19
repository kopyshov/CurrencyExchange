package servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repositories.ExchangeRateRepository;
import services.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.Map;

@WebServlet(name = "ExchangeRateServlet", value = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    Service exeService;
    ResponseGenerator responseGenerator;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        responseGenerator = new ResponseGenerator(req, resp);
        Map<String, String[]> parameterMap = req.getParameterMap();
        if (parameterMap.isEmpty()) {
            String requestURI = req.getRequestURI();
            String path = requestURI.substring(requestURI.lastIndexOf('/') + 1);
            if (path.matches("([a-zA-Z]){6}")) {
                exeService = new FindExchangeRateService();
            } else {
                responseGenerator.codeIsMissing();
            }
        } else if (parameterMap.containsKey("from")
                && parameterMap.containsKey("to")
                && parameterMap.containsKey("amount"))
            {
                    exeService = new CalculateExchangeRateService();
            } else {
                responseGenerator.codeIsIncorrect();
            }
        responseGenerator.generateResponse(exeService);
    }
}
