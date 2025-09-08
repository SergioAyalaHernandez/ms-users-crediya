package co.com.pragma.model.user.user.gateways;

import java.util.List;
import java.util.Map;

public interface JwtProvider {
  String generateToken(String userId, Map<String, Object> claims);
  boolean validateToken(String token);
  String getUserIdFromToken(String token);
  List<String> getRoleFromToken(String token);
}
