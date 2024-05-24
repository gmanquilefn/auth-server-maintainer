package cl.gfmn.authserver.controller;

import cl.gfmn.authserver.model.Response;
import cl.gfmn.authserver.model.user.ChangeUserPasswordRequest;
import cl.gfmn.authserver.model.user.CreateUserRequest;
import cl.gfmn.authserver.model.user.GetUserResponse;
import cl.gfmn.authserver.service.UserService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@SecurityScheme(
        name = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)

@RestController
@RequestMapping(path = "/v1/api/user")
@RequiredArgsConstructor
@Tag(name = "User maintainer endpoints")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    private final Gson gson = new Gson();

    @PostMapping
    @Operation(summary = "Create an user",
            description = "Create an user in auth server database",
            security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("hasAuthority('SCOPE_api.consume')")
    ResponseEntity<Response> createUser(@RequestBody CreateUserRequest request) {

        logger.info("POST - Create user consumption BEGIN");

        Response response = userService.createUser(request);

        logger.info("POST - Create user consumption END, response = {}", gson.toJson(response));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}")
    @Operation(summary = "Obtain an user",
            description = "Searches for an user in auth server database",
            security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("hasAuthority('SCOPE_api.consume')")
    ResponseEntity<GetUserResponse> getUser(@PathVariable(value = "username") String username) {

        logger.info("GET - Get user consumption BEGIN");

        GetUserResponse response = userService.getUser(username);

        logger.info("GET - Get user consumption END, response = {}", gson.toJson(response));

        return ResponseEntity.ok(response);
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change password for user",
            description = "Change password for user in auth server database",
            security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("hasAuthority('SCOPE_api.consume')")
    ResponseEntity<Response> changeUserPassword(@RequestBody ChangeUserPasswordRequest request) {

        logger.info("PUT - Change user password consumption BEGIN");

        Response response = userService.changeUserPassword(request);

        logger.info("PUT - Change user password, response = {}", gson.toJson(response));

        return ResponseEntity.ok(response);
    }

}
