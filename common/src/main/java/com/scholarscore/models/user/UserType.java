package com.scholarscore.models.user;

public enum UserType {
    TEACHER,
    STUDENT,
    ADMINISTRATOR,
    GUARDIAN,
    SUPER_ADMIN;
    
    public static User clone(User input) {
        switch(input.getType()) {
            case TEACHER:
                return new Teacher((Teacher) input);
            case STUDENT:
                return new Student((Student) input);
            case ADMINISTRATOR:
                return new Administrator((Administrator) input);
            case GUARDIAN:
            case SUPER_ADMIN:
                throw new RuntimeException(
                        "Unable to construct a User instance for type: " + input.getType());
            default:
                throw new RuntimeException(
                        "Unable to construct a User instance for type: " + input.getType());
        }
    }
}
