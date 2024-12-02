package com.ing.credit_module.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import static com.ing.credit_module.constants.TableConstants.SERVICE_REQUEST_HISTORY;

@Entity
@Table(name = SERVICE_REQUEST_HISTORY)
@NoArgsConstructor
@Getter
@Setter
public class ServiceRequestHistory extends AbstractEntity{

    @Column(name = "request_data", length = 5000)
    private String requestData;

    @Column(name = "response_data", length = 5000)
    private String responseData;

    private String methodName;

    private String className;

    private String clientIP;

}
