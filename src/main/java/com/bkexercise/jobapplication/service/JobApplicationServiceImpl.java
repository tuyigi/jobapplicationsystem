package com.bkexercise.jobapplication.service;

import com.bkexercise.jobapplication.config.Config;
import com.bkexercise.jobapplication.dao.JobApplicationRepo;
import com.bkexercise.jobapplication.dao.UserRepo;
import com.bkexercise.jobapplication.domain.JobApplication;
import com.bkexercise.jobapplication.domain.User;
import com.bkexercise.jobapplication.model.JobApplicationDto;
import com.bkexercise.jobapplication.model.JobApplicationResponseDto;
import com.bkexercise.jobapplication.model.ResponseDto;
import com.bkexercise.jobapplication.util.EApplicationStatus;
import com.bkexercise.jobapplication.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class JobApplicationServiceImpl implements JobApplicationService {

    @Autowired
    Config config;
    @Autowired
    JobApplicationRepo jobApplicationRepo;
    @Autowired
    private JwtUtil tokenUtil;


    @Autowired
    UserRepo userRepo;
    @Autowired
    HttpServletRequest request;

    @Override
    public ResponseEntity<ResponseDto> saveOne(JobApplicationDto jobApplicationDto) {
        try{

            // validates if required values are available

            if(jobApplicationDto.getFirst_name().isEmpty()||jobApplicationDto.getLast_name().isEmpty()||jobApplicationDto.getEmail().isEmpty()){
                return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST,"Missing parameters"),HttpStatus.BAD_REQUEST);
            }

            // check first if we have valid phone number
            if(jobApplicationDto.getPhone_number().length()!=12){
                return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST,"Invalid phone number, please provide valid phone"), HttpStatus.BAD_REQUEST);
            }

            // check if we have valid email
            Pattern pattern = Pattern.compile(config.getRegexEmail());
            Matcher matcher = pattern.matcher(jobApplicationDto.getEmail());
            if(!matcher.matches()){
                return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST,"Invalid email, please provide valid email"), HttpStatus.BAD_REQUEST);
            }

            // check if phone number is not already used
            Optional<JobApplication> exists=jobApplicationRepo.findByPhoneNumber(jobApplicationDto.getPhone_number());
            if(exists.isPresent()){
                return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST,"Phone number already used, please try another one "), HttpStatus.BAD_REQUEST);
            }

            JobApplication jobApplication=new JobApplication();
            jobApplication.setBirthDate(jobApplicationDto.getBirth_date());
            jobApplication.setDegree(jobApplicationDto.getDegree());
            jobApplication.setExperience(jobApplicationDto.getExperience());
            jobApplication.setGender(jobApplicationDto.getGender());
            jobApplication.setFirstName(jobApplicationDto.getFirst_name());
            jobApplication.setLastName(jobApplicationDto.getLast_name());
            jobApplication.setEmail(jobApplicationDto.getEmail());
            jobApplication.setPhoneNumber(jobApplicationDto.getPhone_number());
            jobApplication.setCountry(jobApplicationDto.getCountry());
            jobApplication.setStatus(EApplicationStatus.PENDING);

            // save job application when every validation passed

            JobApplication obj=jobApplicationRepo.save(jobApplication);

            return new ResponseEntity<>(new ResponseDto(HttpStatus.CREATED,"Job application saved",obj),HttpStatus.CREATED);
        }catch (Exception e){
            System.out.println(e.getLocalizedMessage());
            return new ResponseEntity<>(new ResponseDto(e),HttpStatus.BAD_REQUEST);
        }
    }


    // drop application

    @Override
    public ResponseEntity<ResponseDto> dropApplication(long id) {

        try{
            // check if id is valie
            if(id<=0){
                return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST,"Invalid application ID"),HttpStatus.BAD_REQUEST);
            }

            // check first if we have the application
            Optional<JobApplication> exists=jobApplicationRepo.findById(id);
            if(!exists.isPresent()){
                return  new ResponseEntity<>(new ResponseDto(HttpStatus.NOT_FOUND,"Application not found by this id "+id),HttpStatus.NOT_FOUND);
            }

            // check if it is not already dropped
            JobApplication existObj=exists.get();
            if(existObj.getStatus().equals(EApplicationStatus.DROPPED)){
                return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST,"Already dropped"),HttpStatus.BAD_REQUEST);
            }

            // get the user who is dropping the application just for traceability

            Optional<User> exist=userRepo.findByUsername(getUsername());
            if(!exist.isPresent()){
               return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST,"Invalid user"),HttpStatus.BAD_REQUEST);
            }
            User user=exist.get();



            // then we drop the application
            existObj.setUpdatedBy(user);
            existObj.setStatus(EApplicationStatus.DROPPED);
            jobApplicationRepo.save(existObj);

            return new ResponseEntity<>(new ResponseDto(HttpStatus.OK,"Application droped successful",existObj),HttpStatus.OK);
        }catch (Exception e){
            return  new ResponseEntity<>(new ResponseDto(e),HttpStatus.BAD_REQUEST);
        }

    }

    // pass job application

    @Override
    public ResponseEntity<ResponseDto> passApplication(long id) {
        try {
            // check if id is valid
            if (id <= 0) {
                return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "INvalid application id"), HttpStatus.BAD_REQUEST);
            }

            // check if job application with the id is available
            Optional<JobApplication> exist = jobApplicationRepo.findById(id);
            if (!exist.isPresent()) {
                return new ResponseEntity<>(new ResponseDto(HttpStatus.NOT_FOUND, "Job application not found by this id " + id), HttpStatus.NOT_FOUND);
            }

            // check it not already passed
            JobApplication existObj = exist.get();

            if (existObj.getStatus().equals(EApplicationStatus.PASSED)) {
                return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST, "Job application is already passed!"), HttpStatus.BAD_REQUEST);
            }

            // get the user who is dropping the application just for traceability

            Optional<User> existUser=userRepo.findByUsername(getUsername());
            if(!existUser.isPresent()){
                return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST,"Invalid user"),HttpStatus.BAD_REQUEST);
            }
            User user=existUser.get();


            // then we drop the application
            existObj.setUpdatedBy(user);
            existObj.setUpdatedBy(user);

            // pass the job application

            existObj.setStatus(EApplicationStatus.PASSED);

            JobApplication obj = jobApplicationRepo.save(existObj);

            return new ResponseEntity<>(new ResponseDto(HttpStatus.OK, "Job Application passed successful",obj), HttpStatus.OK);
        }catch (Exception e){
            return  new ResponseEntity<>(new ResponseDto(e),HttpStatus.BAD_REQUEST);
        }
    }

    // get spcific job application

    @Override
    public ResponseEntity<ResponseDto> getJobApplication(long id) {
        // check if id is valid
        if(id<=0){
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST,"Invalid job application id"),HttpStatus.BAD_REQUEST);
        }

        // retrieve jobapplication with provided id
        Optional<JobApplication> exist=jobApplicationRepo.findById(id);
        // check if it exists
        if(!exist.isPresent()){
            return new ResponseEntity<>(new ResponseDto(HttpStatus.NOT_FOUND,"Job Application with this id "+id+" Doesn'' exist"),HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new ResponseDto(HttpStatus.OK,"Job application retrived successful",exist.get()),HttpStatus.OK);
    }

    // upload cv file to your job application

    @Override
    public ResponseEntity<ResponseDto> uploadCv(MultipartFile file, long id) {
        // check if job application is available with provided CV
        Optional<JobApplication> exists=jobApplicationRepo.findById(id);
        if(!exists.isPresent()){
            return new ResponseEntity<>(new ResponseDto(HttpStatus.NOT_FOUND,"Job Application id not found by "+id),HttpStatus.NOT_FOUND);
        }

        JobApplication existObj=exists.get();

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try{

            if(fileName.contains("..")) {
                return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST,"INvalid file path name"),HttpStatus.BAD_REQUEST);
            }

//            System.out.println(file.getContentType());
            // get file bytes
            byte[] fileBytes=file.getBytes();

            // convert bytes to string
            String fileB=new String(Base64.getEncoder().encode(fileBytes));

            existObj.setCv(fileB);

            JobApplication obj=jobApplicationRepo.save(existObj);
            return new ResponseEntity<>(new ResponseDto(HttpStatus.OK,"Cv uploaded successful",obj),HttpStatus.OK);

        }catch (IOException e){
            return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST,"could not upload cv try again!"),HttpStatus.BAD_REQUEST);
        }

    }


    // download cv or resume

    @Override
    public ResponseEntity<Resource> downloadCv(long id) {
        // check if job application is available with provided CV
        Optional<JobApplication> exists=jobApplicationRepo.findById(id);
        if(!exists.isPresent()){
            return new ResponseEntity(new ResponseDto(HttpStatus.NOT_FOUND,"Job Application id not found by "+id),HttpStatus.NOT_FOUND);
        }

        JobApplication existObj=exists.get();

        byte[] bytes = Base64.getDecoder().decode(existObj.getCv());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(new ByteArrayResource(bytes));
    }


    // list applicants first 10 and in alphabetic order

    @Override
    public ResponseEntity<ResponseDto> listApplicants() {

        try {

            List<Object[]> applicants=jobApplicationRepo.findFirstTenApplicants();
            // map the result to the dto
            List<JobApplicationResponseDto> objs=new ArrayList<>();
            JobApplicationResponseDto jobObj=null;
            for(Object[] obj:applicants){
                jobObj=new JobApplicationResponseDto(Integer.parseInt(obj[0].toString()),obj[1].toString(),obj[2].toString(),obj[3].toString(),obj[4].toString(),obj[5].toString(),obj[6].toString(),obj[7].toString(),Integer.parseInt(obj[8].toString()),obj[9].toString());
                objs.add(jobObj);
            }

            return new ResponseEntity<>(new ResponseDto(HttpStatus.OK,"Applicants retrieved successful",objs),HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getLocalizedMessage());
            return new ResponseEntity<>(new ResponseDto(e),HttpStatus.BAD_REQUEST);
        }
        // get first ten and in alphabetic order

    }

    // get username
    private String getUsername() {
        final String requestTokenHeader = request.getHeader("Authorization");
        String token = requestTokenHeader.substring(7);
        String username=null;
        try {
            username = tokenUtil.getUsernameFromToken(token);
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException
                | UnsupportedJwtException | IllegalArgumentException e) {
        }
        return username;
    }



}
