package services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletRequest;
import repositories.ExchangeRateRepository;

import java.net.URISyntaxException;

public class FindExchangeRateService implements Service{
    @Override
    public String execute(HttpServletRequest req) {
        Gson gson = new Gson();
        /*new GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .create();*/
        String requestURI = req.getRequestURI();
        String path = requestURI.substring(requestURI.lastIndexOf('/') + 1);
        try {
            ExchangeRateRepository exCRR = new ExchangeRateRepository();
            String baseCode = path.substring(0, 3);
            String targetCode = path.substring(3);
            String answer = gson.toJson(exCRR.findExchangeRate(baseCode, targetCode, 1.0));
            return answer;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
