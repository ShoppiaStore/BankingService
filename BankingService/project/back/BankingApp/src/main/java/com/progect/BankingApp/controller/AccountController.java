package com.progect.BankingApp.controller;

import com.progect.BankingApp.common.ApiResponse;
import com.progect.BankingApp.dto.AccountDto;
import com.progect.BankingApp.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final RestTemplate restTemplate;

    private static final String USER_SERVICE_URL = "http://localhost:8080/validateToken";
    private static final String USER_SERVICE_URL2 = "http://localhost:8080/extractUserId";

    @PostMapping
    public ResponseEntity<ApiResponse> createAccount(@RequestBody AccountDto accountDto,
                                                     @RequestHeader("Authorization") String token) {

        try {
            // Validate the provided authentication token
            if (!isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Invalid or expired token", null));
            }



            // Extract user ID from the token
            int userId = getUserId(token).getBody();

            System.out.println("User ID extracted from token: " + userId);

            AccountDto createdAccount = accountService.createAccount(accountDto, userId);


            // Return success response with created account data
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Account created successfully", createdAccount));

        }
        // Handle HTTP client errors (4xx status codes)
        catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse(false, e.getStatusText(), null));


            // Handle validation errors
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));

            // Handle unexpected errors
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while creating account", null));
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getAccountById(@PathVariable Long id,

                                                      @RequestHeader("Authorization") String token) {

        System.out.println(" in   getaccount");
        try {
            if (!isTokenValid(token)) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Invalid or expired token", null));
            }

            AccountDto accountDto = accountService.getAccountById(id);
            int userIdFromToken = getUserId(token).getBody();
            // Check if the requesting user owns the account
            if (userIdFromToken != accountDto.getUserId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse(false, "Forbidden: You do not own this account", null));
            }

            return ResponseEntity.ok()
                    .body(new ApiResponse(true, "Account retrieved successfully", accountDto));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while retrieving account", null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateAccount(@PathVariable Long id,
                                                     @RequestBody AccountDto accountDto,
                                                     @RequestHeader("Authorization") String token) {
        try {
            if (!isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Invalid or expired token", null));
            }

            AccountDto updatedAccount = accountService.updateAccount(id, accountDto);
            return ResponseEntity.ok()
                    .body(new ApiResponse(true, "Account updated successfully", updatedAccount));

            // Handle account not found errors
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage(), null));
            // Handle unexpected errors
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while updating account", null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteAccount(@PathVariable Long id,
                                                     @RequestHeader("Authorization") String token) {
        try {
            if (!isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Invalid or expired token", null));
            }

            accountService.deleteAccount(id);
            return ResponseEntity.ok()
                    .body(new ApiResponse(true, "Account deleted successfully", null));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while deleting account", null));
        }
    }

    // Endpoint to get all accounts for a specific user
    @GetMapping("/user")
    public ResponseEntity<ApiResponse> getAccountsForUser(@RequestHeader("Authorization") String token) {
        if (!isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid or expired token", null));
        }

        int userId = getUserId(token).getBody();
        System.out.println("USER ID FROM TOKEN = " + userId);

        List<AccountDto> accounts = accountService.getAccountsByUserId(userId);
        return ResponseEntity.ok(new ApiResponse(true, "User accounts retrieved", accounts));
    }


    // Private method to validate authentication token with user service
    private boolean isTokenValid(String token) {

      //  Create HTTP headers object to store request headers
        HttpHeaders headers = new HttpHeaders();

         // Set the "Authorization" header with the token value
        headers.set("Authorization", token);


        //Wrap the headers into an HttpEntity object for the request مفيش body
        HttpEntity<String> entity = new HttpEntity<>(headers);


        //Send POST request to USER_SERVICE_URL with headers and expect a String response.
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    USER_SERVICE_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            //Return true if the response body equals "valid token" (case-insensitive)
            return "valid token".equalsIgnoreCase(response.getBody());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return false;
        }
    }

    // Private method to extract user ID from authentication token
    private ResponseEntity<Integer> getUserId(String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Integer> response = restTemplate.exchange(
                    USER_SERVICE_URL2,
                    HttpMethod.POST,
                    entity,
                    Integer.class // return int
            );
            //Check if user ID is null or <= 0; if so, throw 401 Unauthorized exception
            if (response.getBody() == null || response.getBody() <= 0) {
                throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Invalid user ID from token");
            }
            // return the response containing the user ID
            return response;


          //  Catch HTTP errors (4xx or 5xx) and rethrow with the same status code but a custom error message.
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new HttpClientErrorException(e.getStatusCode(), "Failed to extract user ID: " + e.getMessage());
        }
    }


}