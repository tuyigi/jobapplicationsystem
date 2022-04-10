package com.bkexercise.jobapplication.service;


import com.bkexercise.jobapplication.model.JobApplicationDto;
import com.bkexercise.jobapplication.model.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

public interface JobApplicationService {
    ResponseEntity<ResponseDto> saveOne(JobApplicationDto jobApplicationDto);
    ResponseEntity<ResponseDto> dropApplication(long id);
    ResponseEntity<ResponseDto> passApplication(long id);
    ResponseEntity<ResponseDto> getJobApplication(long id);
    ResponseEntity<ResponseDto> uploadCv(MultipartFile file,long id);
    ResponseEntity<Resource> downloadCv(long id);
    ResponseEntity<ResponseDto> listApplicants();
}