package ibas.inchelin.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class IpLogFilter implements Filter {

    private static final String MDC_CLIENT_IP = "clientIp";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest req)) {
            chain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(req);
        MDC.put(MDC_CLIENT_IP, clientIp);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_CLIENT_IP);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String[] headerNames = {
                "X-Real-IP",
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };

        String ip = null;
        for (String header : headerNames) {
            ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                break;
            }
        }

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }
}
