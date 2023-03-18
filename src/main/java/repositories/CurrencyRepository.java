package repositories;

import dbhandler.DataSourceFactory;
import model.Currency;

import javax.sql.DataSource;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.*;

public class CurrencyRepository {
    DataSourceFactory dataSourceFactory;
    DataSource dataSource;
    public CurrencyRepository() throws URISyntaxException {
        dataSourceFactory = DataSourceFactory.getInstance();
        dataSource = dataSourceFactory.getDataSource();
    }

    public List<Currency> findAllCurrencies() {
        List<Currency> currencies = new ArrayList<>();
        String query = "SELECT * FROM Currency";
        try(Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Currency cur = getCurrency(resultSet);
                currencies.add(cur);
            }
            return currencies;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    public Optional<Currency> findCurrencyById(int id) {
        String query = "SELECT * FROM Currency WHERE id = " + id;
        return getConnectionToDatabase(query);
    }

    public Optional<Currency> findCurrencyByCode(String code) {
        String query = "SELECT * FROM Currency WHERE code = '" + code + "'";
        return getConnectionToDatabase(query);
    }

    public boolean insertCurrency(String code, String fullname, String sign) {
        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Currency (code, fullname, sign) VALUES (?, ?, ?)");
            statement.setString(1, code);
            statement.setString(2, fullname);
            statement.setString(3, sign);
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static Currency getCurrency(ResultSet resultSet) throws SQLException {
        Currency cur = new Currency(resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4));
        return cur;
    }
    private Optional<Currency> getConnectionToDatabase(String query) {
        try(Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            Currency cur = getCurrency(resultSet);
            return Optional.of(cur);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public boolean checkCurrency(String code) {
        try(Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT code FROM Currency WHERE code = " + "'" +  code + "'");
            return resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
