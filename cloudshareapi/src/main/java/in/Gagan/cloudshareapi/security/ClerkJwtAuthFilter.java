package in.Gagan.cloudshareapi.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClerkJwtAuthFilter extends OncePerRequestFilter {

    @Value("${clerk.issuer}")
    private String clerkIssuer;

    private final ClerkJwksProvider jwksProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        // ✅ Always skip OPTIONS preflight requests
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        boolean shouldSkip = path.startsWith("/webhooks")
                || path.startsWith("/health")
                || path.startsWith("/files/public");

        log.debug("Path: {}, Method: {}, SkipAuth: {}", path, method, shouldSkip);
        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();
        String authHeader = request.getHeader("Authorization");

        log.info("🔐 JWT Filter - Path: {} | Token: {}",
                path, authHeader != null ? "Present" : "MISSING");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("❌ Missing Bearer token for: {}", path);
            sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Missing Authorization header");
            return;
        }

        try {
            String token = authHeader.substring(7);
            String[] parts = token.split("\\.");

            if (parts.length != 3) {
                sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "Invalid JWT format");
                return;
            }

            String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
            JsonNode headerNode = new ObjectMapper().readTree(headerJson);
            String kid = headerNode.get("kid").asText();

            PublicKey publicKey = jwksProvider.getPublicKey(kid);

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .requireIssuer(clerkIssuer)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String clerkId = claims.getSubject();
            log.info("✅ Authenticated: {}", clerkId);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            clerkId, null, Collections.emptyList()
                    );

            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("❌ JWT validation failed: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid JWT: " + e.getMessage());
        }
    }

    private void sendJsonError(HttpServletResponse response, int status, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}