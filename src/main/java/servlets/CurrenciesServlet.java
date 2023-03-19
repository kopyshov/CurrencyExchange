package servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repositories.CurrencyRepository;
import services.AllCurrenciesService;
import services.ResponseGenerator;
import services.Service;

import java.io.IOException;
import java.net.URISyntaxException;

@WebServlet(urlPatterns = {"/currencies/*"})
public class CurrenciesServlet extends HttpServlet {
    CurrencyRepository currencyRepository;
    Service exeService;
    ResponseGenerator responseGenerator;

    @Override
    public void init() {
        try {
            currencyRepository = new CurrencyRepository();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            responseGenerator = new ResponseGenerator(req, resp);
            exeService = new AllCurrenciesService();
            responseGenerator.generateResponse(exeService);
        } catch (IOException ex) { //Почему необходимо прописать Exception в 2 местах?
            responseGenerator.DBisNotFound();
            ex.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        responseGenerator = new ResponseGenerator(req, resp);
        try {
            req.setCharacterEncoding("UTF-8");
            String code = req.getParameter("code");
            String fullName = req.getParameter("fullName");
            String sign = req.getParameter("sign");

            if (currencyRepository.checkCurrency(code)) {
                responseGenerator.currencyExists();
                return;
            }

            if (code.length() != 3 || fullName.length() < 1 || sign.length() == 0) {
                responseGenerator.misField();
            } else {
                currencyRepository.insertCurrency(code, fullName, sign);
            }
        } catch (IOException ex) {
            responseGenerator.DBisNotFound();
            ex.printStackTrace();
        }
    }
}
