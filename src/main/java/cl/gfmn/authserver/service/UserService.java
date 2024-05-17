package cl.gfmn.authserver.service;

import cl.gfmn.authserver.exception.InvalidRequestDataException;
import cl.gfmn.authserver.model.Response;
import cl.gfmn.authserver.model.user.ChangeUserPasswordRequest;
import cl.gfmn.authserver.model.user.CreateUserRequest;
import cl.gfmn.authserver.model.user.GetUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    /**
     * Method that creates a user in database
     * @param request CreateUserRequest POJO
     * @return Response POJO
     */
    public Response createUser(CreateUserRequest request) {

        //validate if user exists
        if(userDetailsManager.userExists(request.username()))
            throw new InvalidRequestDataException("User already exists");


        List<SimpleGrantedAuthority> authoritiesList = new ArrayList<>();
        //validates if authorities list got the 'ROLE_' prefix, if is the case, transform string to SimpleGrantedAuthority class
        request.authorities().forEach(authority -> {
            if(!authority.startsWith("ROLE_")) {
                throw new InvalidRequestDataException("Authority =  " + authority + " must start with ROLE_ prefix");
            }
            authoritiesList.add(new SimpleGrantedAuthority(authority));
        });

        userDetailsManager.createUser(User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .authorities(authoritiesList)
                .build());

        return new Response(LocalDateTime.now().toString(), "User created");
    }

    /**
     * Method that gets user from database
     * @param username string that exists
     * @return GetUserResponse with user found data
     */
    public GetUserResponse getUser(String username) {

        if(!userDetailsManager.userExists(username))
            throw new InvalidRequestDataException("User not found");

        UserDetails user = userDetailsManager.loadUserByUsername(username);

        List<String> strAuthoritiesList = new ArrayList<>();

        user.getAuthorities().forEach(authority -> strAuthoritiesList.add(authority.getAuthority()));

        return new GetUserResponse(user.getUsername(), user.isEnabled(), strAuthoritiesList);
    }

    /**
     * Method that changes a user password
     * @param request ChangeUserPasswordRequest POJO with username, old password and new password
     * @return Generic Response POJO
     */
    public Response changeUserPassword(ChangeUserPasswordRequest request) {

        if(!userDetailsManager.userExists(request.username()))
            throw new InvalidRequestDataException("User not found");

        UserDetails user = userDetailsManager.loadUserByUsername(request.username());

        if(passwordEncoder.matches(request.old_password(), user.getPassword())) {
            UserDetails updatedUser = User.builder()
                    .username(user.getUsername())
                    .password(passwordEncoder.encode(request.new_password()))
                    .authorities(user.getAuthorities())
                    .disabled(user.isEnabled() ? false : true)
                    .build();
            userDetailsManager.updateUser(updatedUser);
        } else {
            throw new InvalidRequestDataException("old password doesn't match");
        }
        return new Response(LocalDateTime.now().toString(), "User password has been changed");
    }

}
