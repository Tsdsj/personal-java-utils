package top.tt.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

import static top.tt.common.constants.SecurityConstant.TOKEN_EXPIRATION_TIME;

/**
 * @author tt
 * @date 2025/3/24 09:03
 */
@Slf4j
public class JwtUtil {

    private static final Algorithm hmac256 = Algorithm.HMAC256("YLWTSMTJFYHDCMGSCWHSSYBZSDKC");

    /**
     * 生成token
     *
     * @param pub 负载
     * @return token
     */
    public static String createToken(String pub, Long id) {
        // 当前时间 + 一天
        Date now = new Date();
        // 过期时间
        Date expirationDate = new Date(now.getTime() + TOKEN_EXPIRATION_TIME * 1000);
        // log.info("token将在 {} 过期", DateUtil.formatDate(expirationDate));
        return JWT.create() //生成令牌函数
                .withIssuer(pub) //自定义负载部分,其实就是添加Claim(jwt结构中的payload部分),可以通过源码查看
                .withClaim("id", String.valueOf(id)) //自定义负载部分,其实就是添加Claim(jwt结构中的payload部分),可以通过源码查看
                .withExpiresAt(expirationDate) //过期时间
                .sign(hmac256);
    }

    /**
     * 校验token
     */
    public static boolean checkToken(String token) {
        JWTVerifier verifier = JWT.require(hmac256).build();
        //如果正确,直接代码向下执行,如果错误,抛异常
        verifier.verify(token);
        return true;
    }

    /**
     * 从token中获取id
     *
     * @param token 令牌
     * @return 保存的负载
     */
    public static String getId(String token) {
        DecodedJWT jwt = JWT.decode(token);
        Claim tt = jwt.getClaim("id");
        return tt.asString();
    }

    /**
     * 从token中获取username
     *
     * @param token 令牌
     * @return 保存的负载
     */
    public static String getUsername(String token) {
        DecodedJWT jwt = JWT.decode(token);
        Claim tt = jwt.getClaim("iss");
        return tt.asString();
    }
}
