package servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repositories.ExchangeRateRepository;
import services.AllExchangeRateService;
import services.CalculateExchangeRateService;
import services.FindExchangeRateService;
import services.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.Map;

@WebServlet(name = "ExchangeRateServlet", value = "/exchange/*")
public class ExchangeRateServlet extends HttpServlet {
    ExchangeRateRepository exRepository;
    Service exeService;

    @Override
    public void init() {
        try {
            exRepository = new ExchangeRateRepository();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.isEmpty()) {
            String path = (request.getRequestURI()).substring(27);
            if (path.matches("([a-zA-Z]){6}")) {
                exeService = new FindExchangeRateService();
            }
            if (path.equals("all")) {
                exeService = new AllExchangeRateService();
            }

        } else if (parameterMap.containsKey("from")
                && parameterMap.containsKey("to")
                && parameterMap.containsKey("amount"))
        {
                exeService = new CalculateExchangeRateService();
        }
        generateResponse(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String baseCode = request.getParameter("basecode");
        String targetCode = request.getParameter("targetcode");
        String rate = request.getParameter("rate");

        exRepository.insertExchangeRate(baseCode, targetCode, rate);
    }

    private void generateResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String answer = exeService.execute(request);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(answer);
        out.flush();
    }

    private void generateErrorMessage(HttpServletResponse response, String message) throws IOException {
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String answer = gson.toJson(message);
        PrintWriter out = response.getWriter();
        out.print(answer);
        out.flush();
    }
}
