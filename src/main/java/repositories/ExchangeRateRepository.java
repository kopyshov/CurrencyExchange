package repositories;

import dbhandler.DataSourceFactory;
import model.Currency;
import model.ExchangeRate;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ExchangeRateRepository {
    DataSourceFactory dataSourceFactory;
    DataSource dataSource;

    public ExchangeRateRepository() throws URISyntaxException {
        dataSourceFactory = DataSourceFactory.getInstance();
        dataSource = dataSourceFactory.getDataSource();
    }

    public List<ExchangeRate> findAllExchanges() {
        List<ExchangeRate> exchanges = new ArrayList<>();
        String query =
                "SELECT ExchangeRates.id, " +
                        "C.id, C.code, C.fullname, C.sign, " +
                        "C2.id, C2.code, C2.fullname, C2.sign, " +
                        "ExchangeRates.rate\n" +
                        "FROM ExchangeRates\n" +
                        "JOIN Currency C on C.id = ExchangeRates.basecurrencyid\n" +
                        "JOIN Currency C2 on C2.id = ExchangeRates.targetcurrencyid";
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                ExchangeRate exRate = getExchangeRate(resultSet, null, null);
                exchanges.add(exRate);
            }
            return exchanges;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public ExchangeRate findExchangeRate(String baseCode, String targetCode, Double amount) {
        String forBaseQuery =
                "SELECT * FROM Currency\n" +
                "WHERE code = '" + baseCode + "';";
        String forTargetQuery =
                "SELECT ExchangeRates.targetcurrencyid, C.code, C.fullname, C.sign, ExchangeRates.rate FROM ExchangeRates\n" +
                "JOIN Currency C ON C.id = ExchangeRates.targetcurrencyid\n" +
                "WHERE C.code = '" + targetCode + "';";


        String baseRate = getCurrencyRate(forBaseQuery);
        BigDecimal bRate = new BigDecimal(baseRate);

        String targetRate = getCurrencyRate(forTargetQuery);
        BigDecimal tRate = new BigDecimal(targetRate);

        BigDecimal convertedAmount = calculate(bRate, tRate, amount);
        if (amount == 1.0) {
            amount = 0.0;
        }
        Currency baseCurrency = createCurrency(forBaseQuery);
        ExchangeRate exRate = new ExchangeRate(
            baseCurrency.id(),
            baseCurrency,
            createCurrency(forTargetQuery),
            tRate.divide(bRate, RoundingMode.UNNECESSARY),
            amount,
            convertedAmount);
        return exRate;
    }

    private Currency createCurrency(String forQuery) {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(forQuery);

            Currency currency = new Currency(
                    resultSet.getInt(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4)
            );
            return currency;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getCurrencyRate(String forQuery) {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(forQuery);
            String id = resultSet.getString(1);

            Statement aStatement = connection.createStatement();
            ResultSet aResultSet = aStatement.executeQuery(
                    "SELECT rate FROM ExchangeRates\n" +
                    "WHERE targetcurrencyid = " + id + ";");
            return aResultSet.getString(1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ExchangeRate getExchangeRate(ResultSet resultSet, Double amount, BigDecimal convertedAmount) throws SQLException {
        ExchangeRate exRate = new ExchangeRate(
                resultSet.getInt(1),
                new Currency(resultSet.getInt(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5)),
                new Currency(resultSet.getInt(6),
                        resultSet.getString(7),
                        resultSet.getString(8),
                        resultSet.getString(9)),
                resultSet.getBigDecimal(10),
                amount,
                convertedAmount);
        return exRate;
    }

    public BigDecimal calculate(BigDecimal baseRate, BigDecimal targetRate, Double amount) {
        BigDecimal convertedAmount =
                (targetRate.divide(baseRate, RoundingMode.UNNECESSARY))
                        .multiply(BigDecimal.valueOf(amount));
        return convertedAmount;
    }

    public void insertExchangeRate(String basecurrency, String targetcurrency, String rate) {
        String baseCurrencyId =
                "SELECT id FROM Currency\n" +
                "WHERE code = '" + basecurrency + "';";
        String targetCurrencyId =
                "SELECT id FROM Currency\n" +
                "WHERE code = '" + targetcurrency + "';";
        String insertExchange = "INSERT INTO ExchangeRates (basecurrencyid, targetcurrencyid, rate) " +
                "VALUES (?, ?, ?)";
        String baseCurId = getCurrencyId(baseCurrencyId);
        String targetCurId = getCurrencyId(targetCurrencyId);
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(insertExchange);
            ps.setString(1, baseCurId);
            ps.setString(2, targetCurId);
            ps.setString(3, rate);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getCurrencyId(String requestCurrencyId) {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(requestCurrencyId);
            String currencyId = resultSet.getString(1);
            statement.close();
            return currencyId;
        } catch (SQLException e) {
            return null;
        }
    }
}
