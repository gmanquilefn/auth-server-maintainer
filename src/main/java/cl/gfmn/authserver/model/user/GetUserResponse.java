package cl.gfmn.authserver.model.user;

import java.util.List;

public record GetUserResponse(String username, Boolean is_active, List<String> authorities) {
}
