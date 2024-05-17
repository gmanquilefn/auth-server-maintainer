package cl.gfmn.authserver.model.user;

public record ChangeUserPasswordRequest(String username, String old_password, String new_password) {
}
