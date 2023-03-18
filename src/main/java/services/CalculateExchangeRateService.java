package services;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import model.ExchangeRate;
import repositories.ExchangeRateRepository;

import java.net.URISyntaxException;
import java.util.Map;

public class CalculateExchangeRateService implements Service{
    @Override
    public String execute(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        String[] froms = parameterMap.get("from");
        String from = froms[0];

        String[] tos = parameterMap.get("to");
        String to = tos[0];

        String[] amounts = parameterMap.get("amount");
        String amount = amounts[0];

        try {
            ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepository();
            ExchangeRate exchangeRate = exchangeRateRepository.findExchangeRate(from, to, Double.valueOf(amount));
            Gson gson = new Gson();
            return gson.toJson(exchangeRate);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
