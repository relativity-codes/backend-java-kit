package com.standard.commerce_maven.auth;

@org.springframework.modulith.ApplicationModule(allowedDependencies = {
        "com.elwalkre.commerce_maven.user.UserModule",
        "com.elwalkre.commerce_maven.mailing.MailingModule"
})
public class AuthModule {
    // This class serves as a marker for the Auth module in the application.
    // It can be used to group related components and services under the Auth
    // module.
}