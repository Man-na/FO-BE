package com.manna.fobe.config.aspect;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manna.fobe.common.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class UserQueryLoggingAspect {

    private final CommonUtils utils;
    private final ObjectMapper objectMapper;

    @Pointcut("within(com.manna.fobe.user.controller..*)")
    public void targetController() { }

    @Around("targetController()")
    public Object aroundUserController(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object methodResult = null;
        try {
            methodResult = joinPoint.proceed();
        } catch (Throwable e) {
            log.error("UserQueryLoggingAspect throwable error: ", e);
            throw e;
        }

        long elapsedTime = System.currentTimeMillis() - startTime;

        if (Objects.nonNull(methodResult)) {
            Map<String, Object> req = (joinPoint.getArgs().length > 0)
                    ? objectToMap(joinPoint.getArgs()[0])
                    : new HashMap<>();

            Map<String, Object> res = objectToMap(methodResult);

            logQuery(req, res, elapsedTime);
        }

        return methodResult;
    }

    private Map<String, Object> objectToMap(Object source) {
        return objectMapper.convertValue(source, new TypeReference<>() {});
    }

    private Map<String, Object> setDefaultQueryInfo(Map<String, Object> userQuery,
                                                    Map<String, Object> result,
                                                    long elapsedTime) {
        if (result.containsKey("data")) {
            Object data = result.get("data");
            if (data instanceof Map) {
                Map<?, ?> dataMap = (Map<?, ?>) data;
                if (dataMap.containsKey("totalSize")) {
                    userQuery.put("totalSize", dataMap.get("totalSize"));
                }
            }
        }

        OffsetDateTime now = OffsetDateTime.now(ZoneId.of("Asia/Seoul"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        userQuery.put("timestamp", now.format(formatter));
        userQuery.put("requestUrl", utils.getRequestURI());
        userQuery.put("responseTimeMs", elapsedTime);
        return userQuery;
    }

    private void logQuery(Map<String, Object> req, Map<String, Object> res, long elapsedTime) {
        try {
            Map<String, Object> userQuery = setDefaultQueryInfo(req, res, elapsedTime);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String userQueryString = objectMapper.writeValueAsString(userQuery);
            log.info("UserController Request/Response: {}", removeListFormat(userQueryString));
        } catch (Exception e) {
            log.error("UserQueryLoggingAspect error: ", e);
        }
    }

    private String removeListFormat(String listStyleString) {
        return listStyleString.replaceAll("^\\[|\\]$", "");
    }
}
