package org.example.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.entity.RestBean;
import org.example.entity.dto.Client;
import org.example.service.ClientService;
import org.example.utils.Const;
import org.example.utils.JwtUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JWTAuthorizeFilter extends OncePerRequestFilter {

    @Resource
    JwtUtils utils;

    @Resource
    ClientService clientService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization"); // 获取请求头中的信息
        String uri = request.getRequestURI();
        if (uri.startsWith("/monitor")) {
            // 如果不是register, 直接通过token查询到client, 然后将client放入request中
            if (!uri.endsWith("/register")) {
                Client client = clientService.findClientByToken(authorization);
                if (client == null) {
                    response.setStatus(401);
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(RestBean.failure(401, "未注册").asJsonString());
                    return ;
                } else {
                    request.setAttribute(Const.ATTR_CLIENT, client);
                }
            }
        } else {
            DecodedJWT jwt = utils.resolveJwt(authorization); // 解析token
            if (jwt != null) {
                UserDetails user = utils.toUser(jwt);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication); // 将认证消息丢进去表示已经认证过了
                request.setAttribute("id", utils.toId(jwt));
                request.setAttribute(Const.ATTR_USER_ROLE, new ArrayList<>(user.getAuthorities()).get(0).getAuthority());
            }
        }
        filterChain.doFilter(request, response);
    }
}










