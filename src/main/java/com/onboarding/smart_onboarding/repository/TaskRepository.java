package com.onboarding.smart_onboarding.repository;

import com.onboarding.smart_onboarding.model.OnboardingTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<OnboardingTask, Long> {
    List<OnboardingTask> findByEmployeeId(Long employeeId);
}