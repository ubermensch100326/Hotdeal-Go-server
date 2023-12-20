package com.budge.hotdeal_go.config;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {

    @Value("classpath:${firebase.config}")
    private Resource firebaseConfigResource;

    @Value("${firebase.project.id}")
    private String firebaseProjectId;

    @PostConstruct
    public void initialize() {
        try {
            System.out.println(firebaseConfigResource.toString() + " : " + firebaseProjectId);
            InputStream serviceAccount = firebaseConfigResource.getInputStream();
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId("hotdeal-server")
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}