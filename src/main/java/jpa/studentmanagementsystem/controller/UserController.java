package jpa.studentmanagementsystem.controller;

import jpa.studentmanagementsystem.dto.UserDto;
import jpa.studentmanagementsystem.entity.User;
import jpa.studentmanagementsystem.exception.ErrorResponse;
import jpa.studentmanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // this not use
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user){
       try {
           userService.createUser(user);
           Map<String, String> response = new HashMap<>();
           response.put("status", "Success");
           response.put("message", "Delete Successfully!!!");
           return ResponseEntity.status(HttpStatus.CREATED).body(response);
       } catch (Exception e){
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body(new ErrorResponse("Error", e.getMessage()));
       }
    }

    @PostMapping("/search")
    ResponseEntity<?> getUser(@RequestBody UserDto userDto){
        if(userDto.getUsername() == null && userDto.getLastname() == null && userDto.getEmail() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Error", "Filed is null"));
        }
        try {
            List<UserDto> user = userService.getUsersByCriteria(userDto);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUser(){
        try {
            List<UserDto> user = userService.getAllUser();
            return ResponseEntity.ok(user);
        } catch (Exception e){
            ErrorResponse errorResponse = new ErrorResponse("Error Unknown",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{username}")
    public ResponseEntity<?> updateUser(@PathVariable String username, @RequestBody User user){
        if(username.isEmpty()){
            ErrorResponse errorResponse = new ErrorResponse("Error validate","Username is required");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        try {
            userService.updateUser(username, user);
            return new ResponseEntity<>(new ErrorResponse("success", "updated user successfully"),HttpStatus.ACCEPTED);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error Unknown",e.getMessage()));
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUser(@PathVariable String username){
        if(username.isEmpty()){
            ErrorResponse errorResponse = new ErrorResponse("Error validate","Username is required");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        try {
            UserDto user = userService.findByUserName(username);
            return new ResponseEntity<>( user, HttpStatus.ACCEPTED);
        } catch (Exception e){
            ErrorResponse errorResponse = new ErrorResponse("Error Unknown",e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username){
        if(username.isEmpty()){
            ErrorResponse errorResponse = new ErrorResponse("Error validate","Username is required");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        try {
            userService.deleteByUserName(username);
            Map<String, String> response = new HashMap<>();
            response.put("status", "Success");
            response.put("message", "Delete Successfully!!!");
            return new ResponseEntity<>(response,HttpStatus.ACCEPTED);
        } catch(Exception e){
            ErrorResponse errorResponse = new ErrorResponse("Error Unknown",e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }
}

