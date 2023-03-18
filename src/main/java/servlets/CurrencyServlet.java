package servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repositories.CurrencyRepository;
import services.AllCurrenciesService;
import services.FindCurrencyService;
import services.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;

@WebServlet(urlPatterns = {"/currency/*"})
public class CurrencyServlet extends HttpServlet {
    CurrencyRepository currencyRepository;
    Service exeService;

    @Override
    public void init() {
        try {
            currencyRepository = new CurrencyRepository();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String path = (request.getRequestURI()).substring(27);
        if(path.matches("([a-zA-Z]){3}") || path.matches("\\d")) {
            exeService = new FindCurrencyService();
        }
        if(path.equals("all")) {
            exeService = new AllCurrenciesService();
        }
        generateResponse(request, response);
    }

    private void generateResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String answer = exeService.execute(request);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(answer);
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String code = request.getParameter("code");
        String fullname = request.getParameter("fullname");
        String sign = request.getParameter("sign");

        if(currencyRepository.checkCurrency(code)){
            String message = "Есть такой уже";
            generateErrorMessage(response, message);
            return;
        }

        if(code.length() != 3 || fullname.length() < 1 || sign.length() == 0) {
            String message = "Неправильно бля";
            generateErrorMessage(response, message);
        } else {
            currencyRepository.insertCurrency(code, fullname, sign);
        }
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
