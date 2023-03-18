package services;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import repositories.CurrencyRepository;

import java.net.URISyntaxException;

import static java.lang.Integer.parseInt;

public class FindCurrencyService implements Service{
    @Override
    public String execute(HttpServletRequest request) {
        String path = (request.getRequestURI()).substring(27);
        CurrencyRepository currencyRepository = null;
        try {
            currencyRepository = new CurrencyRepository();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Gson gson = new Gson();
        String answer = "";
        if(path.matches("([a-zA-Z]){3}")) {
            answer = gson.toJson(currencyRepository.findCurrencyByCode(path.toUpperCase()));
        }
        if(path.matches("\\d")) {
            answer = gson.toJson(currencyRepository.findCurrencyById(parseInt(path)));
        }
        return answer;
    }
}
