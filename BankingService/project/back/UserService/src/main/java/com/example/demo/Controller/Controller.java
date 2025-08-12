package com.example.demo.Controller;


import com.example.demo.Service.UserService;
import com.example.demo.entity.User;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@OpenAPIDefinition(
        info = @Info(
                title = "users API",
                version = "1.0",
                description = "Handle operations ^cruds^ on users"
        )
)

@RestController
public class Controller {
    @Autowired
     private UserService userService;



    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void adduser( @RequestBody User user){
        userService.addUser(user);

    }

//    @DeleteMapping("/delete")
//
//    public void deleteItem(@RequestParam int id){
//
//     userService.deleteUser(id);
//    }


    @Operation(summary = "Update user via id ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" ,description = "OK, User updated  ")
            ,@ApiResponse(responseCode = "404",description = "NOT_FOUND, No user with this id")
            ,@ApiResponse(responseCode = "403",description = "FORBIDDEN, CHECK YOUR TOKEN")
            ,@ApiResponse(responseCode = "409",description = "CONFLICT,  Updated email is  already exist")

    })

    @PutMapping("/update")

    public void updateItem(@RequestBody User user){

       userService.updateUser(user);
    }


    @GetMapping("/search")

    public User searchUser(@RequestParam String email){
        return   userService.searchUser(email);
    }
    @Operation(summary = "Forget password send OTP ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" ,description = "OK,Look at your email ")
            ,@ApiResponse(responseCode = "404",description = "NOT_FOUND, No users found with this email in token")
            ,@ApiResponse(responseCode = "403",description = "FORBIDDEN, CHECK YOUR TOKEN")

    })


    @PostMapping("/forgetPassword")
    public String forgetPassword(@RequestHeader String Authorization)
    {
        return userService.forgetPassword(Authorization);

    }
    @Operation(summary = "Change password via OTP send ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" ,description = "OK,Password changed ")
            ,@ApiResponse(responseCode = "400",description = "BAD_REQUEST, CHECK YOUR OTP")
            ,@ApiResponse(responseCode = "403",description = "FORBIDDEN, CHECK YOUR TOKEN")
            ,@ApiResponse(responseCode = "404",description = "NOT_FOUND, No users found with this email in token")
    })

    @PutMapping("/changePassword")
    public String changePassword(@RequestHeader String Authorization,@RequestHeader String otp ,@RequestBody User user  )
    {
        return userService.changePassword(Authorization,otp,user);

    }




    @Operation(summary = "Regenerate OTP with email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" ,description = "OK,Look at your email ")
            ,@ApiResponse(responseCode = "404",description = "NOT_FOUND, No users found with this email in token")
            ,@ApiResponse(responseCode = "403",description = "FORBIDDEN, CHECK YOUR TOKEN")

    })
    @PostMapping("/regenerateOtp")
    public String regenerateOtp(@RequestHeader String email )
    {
        return userService.regenerateOtp(email);
    }


    @Operation(summary = "Delete user via id ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" ,description = "OK, User activated enabled = true ")
            ,@ApiResponse(responseCode = "403",description = "FORBIDDEN, CHECK YOUR TOKEN")
            ,@ApiResponse(responseCode = "404",description = "NOT_FOUND, No user with this id")
    })
    @PostMapping("/delete")

    public void deleteItem(@RequestBody User user){

        userService.deleteUser(user);
    }


    @Operation(summary = "Check validty of token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" ,description = "OK,Valid")
            ,@ApiResponse(responseCode = "403",description = "FORBIDDEN, CHECK YOUR TOKEN")
    })
    @PostMapping("/validateToken")
    public String validateToken(@RequestHeader String Authorization) {
        System.out.println("here in validate ");
        System.out.println("TOKEN RECEIVED: " + Authorization);
        return userService.validateToken(Authorization);
    }

    @PostMapping("/extractUserId")
    public int extractUserId(@RequestHeader String Authorization) {
        return userService.extractUserId(Authorization);
    }






    @GetMapping("/hello")
    @ResponseStatus(HttpStatus.CREATED)
    public String hello(){

        return "Hello user Service";
    }

}
