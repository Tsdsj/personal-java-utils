package top.tt.common.util;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import top.tt.common.config.security.userdetails.LoginUser;

/**
 * @author tt
 * @date 2025/3/21 14:28
 */
public class SecurityUtil {

    /**
     * 设置当前登录用户信息
     */
    public static void setLoginUser(LoginUser loginUser) {
        if (loginUser == null) {
            throw new IllegalArgumentException("LoginUser cannot be null");
        }
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
    }

    /**
     * 获取当前登录用户的用户名
     *
     * @return 用户名
     */
    public static String getCurrentUsername() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    /**
     * 获取当前登录用户的 id
     *
     * @return id
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof LoginUser) {
            return ((LoginUser) principal).getUserId();
        } else if (principal instanceof String) {
            // 如果是字符串但不是匿名用户，可能是用户ID
            if (!"anonymousUser".equals(principal.toString())) {
                return Long.valueOf(principal.toString());
            }
        }
        return null;
    }
}
