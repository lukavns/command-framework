package me.lukavns.commandframework.api.exception;

public class PermissionDeniedException extends CommandFrameworkException {

    private static final long serialVersionUID = 1L;

    private final String permission;

    public PermissionDeniedException(String permission) {
        super("Missing permission: " + permission);
        this.permission = permission;
    }

    public String permission() {
        return this.permission;
    }
}
