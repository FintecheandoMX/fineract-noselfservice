/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.core.exceptionmapper;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.infrastructure.core.data.ApiGlobalErrorResponse;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.exception.UnsupportedParameterException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * An {@link ExceptionMapper} to map {@link UnsupportedParameterException} thrown by platform into a HTTP API friendly
 * format.
 */
@Provider
@Component
@Scope("singleton")
@Slf4j
public class UnsupportedParameterExceptionMapper implements FineractExceptionMapper, ExceptionMapper<UnsupportedParameterException> {

    @Override
    public Response toResponse(final UnsupportedParameterException exception) {
        final List<ApiParameterError> errors = new ArrayList<>();

        for (final String parameterName : exception.getUnsupportedParameters()) {
            final StringBuilder validationErrorCode = new StringBuilder("error.msg.parameter.unsupported");
            final StringBuilder defaultEnglishMessage = new StringBuilder("The parameter ").append(parameterName)
                    .append(" is not supported.");
            final ApiParameterError error = ApiParameterError.parameterError(validationErrorCode.toString(),
                    defaultEnglishMessage.toString(), parameterName, parameterName);

            errors.add(error);
        }
        log.warn("Exception occurred", exception);

        final ApiGlobalErrorResponse invalidParameterError = ApiGlobalErrorResponse
                .badClientRequest("validation.msg.validation.errors.exist", "Validation errors exist.", errors);

        return Response.status(Status.BAD_REQUEST).entity(invalidParameterError).type(MediaType.APPLICATION_JSON).build();
    }

    @Override
    public int errorCode() {
        return 2001;
    }
}
