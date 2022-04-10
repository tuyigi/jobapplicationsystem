package com.bkexercise.jobapplication.controller;


import com.bkexercise.jobapplication.domain.JobApplication;
import com.bkexercise.jobapplication.model.JobApplicationDto;
import com.bkexercise.jobapplication.model.ResponseDto;
import com.bkexercise.jobapplication.service.JobApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
public class JobApplicationController {
    @Autowired
    JobApplicationService jobApplicationService;


    // save job application for step one
    @PostMapping("/application")
    private ResponseEntity<ResponseDto> saveApplicationStepOne(@Validated @RequestBody JobApplicationDto jobApplicationDto){
        try{
            return jobApplicationService.saveOne(jobApplicationDto);

        }catch (Exception e){
            return new ResponseEntity<>(new ResponseDto(e), HttpStatus.BAD_GATEWAY);
        }
    }

    // drop job application

    @PutMapping("/application/drop/{id}")
    private ResponseEntity<ResponseDto> dropJobApplication(@PathVariable(value = "id")long id){
        try{
            return jobApplicationService.dropApplication(id);
        }catch (Exception e){
            return new ResponseEntity<>(new ResponseDto(e),HttpStatus.BAD_REQUEST);
        }
    }

    // pass job application

    @PutMapping("/application/pass/{id}")
    private ResponseEntity<ResponseDto> passJobApplication(@PathVariable(value = "id")long id){
        try{
           return jobApplicationService.passApplication(id);
        }catch (Exception e){
            return new ResponseEntity<>(new ResponseDto(e),HttpStatus.BAD_REQUEST);
        }
    }

    // get specific job application by id

    @GetMapping("/application/{id}")
    private ResponseEntity<ResponseDto> getSpecificApplication(@PathVariable(value = "id")long id){
        try{
            return jobApplicationService.getJobApplication(id);
        }catch (Exception e){
            return new ResponseEntity<>(new ResponseDto(e),HttpStatus.BAD_REQUEST);
        }
    }

    // upload cv / resume file

    @PutMapping("/application/upload/{id}")
    private ResponseEntity<ResponseDto> uploadCV(@RequestParam(value = "file")MultipartFile file,@PathVariable(value="id")long id){
        try{
            return jobApplicationService.uploadCv(file,id);

        }catch (Exception e){
            return new ResponseEntity<>(new ResponseDto(e),HttpStatus.BAD_REQUEST);
        }
    }

    // download cv / resume file

    @GetMapping("/application/download/{id}")
    private ResponseEntity<Resource> uploadCV(@PathVariable(value="id")long id){
        try{
            return jobApplicationService.downloadCv(id);

        }catch (Exception e){
            return new ResponseEntity(new ResponseDto(e),HttpStatus.BAD_REQUEST);
        }
    }


    // get first 10 application and sort them alphabetic

    @GetMapping("/application")
    private ResponseEntity<ResponseDto> getApplicants(){
        try{
            return jobApplicationService.listApplicants();

        }catch (Exception e){
            return new ResponseEntity(new ResponseDto(e),HttpStatus.BAD_REQUEST);
        }
    }



}
