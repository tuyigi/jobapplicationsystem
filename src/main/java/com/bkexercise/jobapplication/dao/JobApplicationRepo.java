package com.bkexercise.jobapplication.dao;

import com.bkexercise.jobapplication.domain.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepo extends JpaRepository<JobApplication,Long> {
    Optional<JobApplication> findByPhoneNumber(String phoneNUmber);
    Optional<JobApplication> findById(long id);
    @Query(value = "select j.id,j.first_name,j.last_name,email,j.phone_number,j.gender,j.degree,j.birth_date,j.experience,j.status from job_application j order by j.first_name,j.last_name asc limit 10",nativeQuery = true)
    List<Object[]> findFirstTenApplicants();
}
