package jpa.studentmanagementsystem.controller;

import jpa.studentmanagementsystem.entity.User;
import jpa.studentmanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user){
       try {
           userService.createUser(user);
           return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully!!!");
       }catch (Exception e){
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
       }
    }

    @GetMapping
    public ResponseEntity<?> getAllUser(){
        try {
            return ResponseEntity.ok(userService.getAllUser());
        }catch (Exception e){
            return ResponseEntity.ok().build();
        }
    }

    @PutMapping("/{username}")
    public ResponseEntity<?> updateUser(@PathVariable String username, @RequestBody User user){
        if(username.isEmpty()){
            return ResponseEntity.badRequest().body("Username cannot be empty");
        }
        try {
            userService.updateUser(username, user);
            return ResponseEntity.ok("updated user successfully");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUser(@PathVariable String username){
        if(username.isEmpty()){
            return ResponseEntity.badRequest().body("Username cannot be empty");
        }
        try {
            return new ResponseEntity<>(userService.findByUserName(username), HttpStatus.ACCEPTED);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username){
        if(username.isEmpty()){
            return ResponseEntity.badRequest().body("Username cannot be empty");
        }
        try {
            userService.deleteByUserName(username);
            return new ResponseEntity<>("Delete User Successfully!!!",HttpStatus.ACCEPTED);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }
}
