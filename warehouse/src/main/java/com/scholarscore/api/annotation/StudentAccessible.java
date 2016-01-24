package com.scholarscore.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * EdPanel users who are students or families are not permitted to access API endpoints unless those
 * controller methods are annotated with @StudentAccessible.  In this manner, no sensitive endpoints
 * will be exposed without a developer proactively granting permission to that endpoint, limiting the
 * chance of error by omission.
 *
 * If an API controller method is annotated with @StudentAccessible, student requests will be
 * permitted to that endpoint. However, if the API has a userId path parameter, only requests
 * where the userId parameter equals the requesting user's userId will be permitted unless the
 * annotation explicitly declares otherwise as in:
 *
 * @StudentAccessible(userIdParamMustEqualRequestingUserId = false)
 *
 * By default, the userId API param will be assumed to be called 'userId'.  The name of the param can
 * he overridden on any given method in the following manner:
 *
 * @StudentAccessible(paramName = "studentId")
 *
 * Created by markroper on 1/23/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface StudentAccessible {
    public boolean userIdParamMustEqualRequestingUserId() default true;

    public String paramName() default "userId";
}
