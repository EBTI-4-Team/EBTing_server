package com.github.ebtingserver.common.resolver;

import com.github.ebtingserver.common.annotation.CurrentUserId;
import com.github.ebtingserver.common.exception.CustomException;
import com.github.ebtingserver.common.util.JwtUtil;
import com.github.ebtingserver.domain.user.exception.UserExceptionCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class CurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtUtil jwtUtil;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUserId.class)
                && parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                   NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String authorization = request.getHeader("Authorization");

        // Authorization 헤더 체크
        if (authorization == null || authorization.isBlank()) {
            throw new CustomException(UserExceptionCode.MISSING_TOKEN);
        }

        // Bearer 토큰 형식 체크
        if (!authorization.startsWith("Bearer ")) {
            throw new CustomException(UserExceptionCode.MISSING_TOKEN);
        }

        // Bearer 토큰에서 JWT 추출
        String token = authorization.substring(7);

        // JWT에서 userId 추출 및 반환
        return jwtUtil.getUserIdFromToken(token);
    }
}
