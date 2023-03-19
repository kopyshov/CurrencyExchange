package services;

import jakarta.servlet.http.HttpServletRequest;

public interface Service {
    String execute(HttpServletRequest req);
}
