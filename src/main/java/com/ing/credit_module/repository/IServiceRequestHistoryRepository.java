package com.ing.credit_module.repository;

import com.ing.credit_module.model.ServiceRequestHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IServiceRequestHistoryRepository extends JpaRepository<ServiceRequestHistory, Long> {

}
