package com.tripsnap.api.auth.login;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tripsnap.api.exception.ServiceException;
import com.tripsnap.api.filter.MultiReadHttpServletRequest;
import com.tripsnap.api.utils.ParameterUtil;
import com.tripsnap.api.utils.ValidationType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class LoginFilter extends AbstractAuthenticationProcessingFilter {
    public LoginFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    private final Gson gson = new Gson();

    private final String FORM_USERNAME_ATTR_NAME = "email";
    private final String FORM_PASSWORD_ATTR_NAME = "password";
    private final String[] allowMethods = {HttpMethod.POST.name(), HttpMethod.OPTIONS.name()};

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        MultiReadHttpServletRequest multiReadRequest = new MultiReadHttpServletRequest(request);

        if (!checkMethod(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        try {

            Map<String, Object> map = gson.fromJson(multiReadRequest.getBodyJson(), new TypeToken<Map<String, Object>>(){}.getType());
            String email = ParameterUtil.validationAndConvert(map.get(FORM_USERNAME_ATTR_NAME), ValidationType.PrimitiveWrapper.Email);
            String password = ParameterUtil.validationAndConvert(map.get(FORM_PASSWORD_ATTR_NAME), ValidationType.PrimitiveWrapper.LoginPassword);

            UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(email,
                    password);
            // Allow subclasses to set the "details" property
//        setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        } catch (NullPointerException e) {
            throw ServiceException.BadRequestException();
        }
    }

    @Nullable
    protected String obtainPassword(HttpServletRequest request) {
        return request.getParameter(FORM_PASSWORD_ATTR_NAME);
    }

    @Nullable
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter(FORM_USERNAME_ATTR_NAME);
    }

    private boolean checkMethod(String method) {
        return Arrays.asList(allowMethods).contains(method);
    }

}
