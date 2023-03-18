package services;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import repositories.CurrencyRepository;

import java.net.URISyntaxException;

public class AllCurrenciesService implements Service {
    @Override
    public String execute(HttpServletRequest request) {
        CurrencyRepository currencyRepository;
        try {
            currencyRepository = new CurrencyRepository();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Gson gson = new Gson();
        String  answer = gson.toJson(currencyRepository.findAllCurrencies());

        return answer;
    }
}
