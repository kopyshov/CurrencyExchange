package services;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import repositories.ExchangeRateRepository;

import java.net.URISyntaxException;

public class AllExchangeRateService implements Service{
    @Override
    public String execute(HttpServletRequest request) {
        Gson gson = new Gson();
        try {
            ExchangeRateRepository exCRR = new ExchangeRateRepository();
            String answer = gson.toJson(exCRR.findAllExchanges());
            return answer;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
