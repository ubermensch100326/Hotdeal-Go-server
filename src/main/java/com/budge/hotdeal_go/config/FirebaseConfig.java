//
//package com.budge.hotdeal_go.config;
//
//import java.io.FileInputStream;
//import java.io.InputStream;
//
//import javax.annotation.PostConstruct;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.PropertySource;
//
//import com.google.api.client.util.Value;
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//
//@PropertySource("classpath:/application.properties")
//@Configuration
//public class FirebaseConfig {
//
//    @Value("${firebase.config}")
//    private String firebaseConfig;
//
//    @Value("${firebase.project.id}")
//    private String firebaseProjectId;
//
//    @PostConstruct
//    public void initialize() {
//        try {
//            System.out.println(firebaseConfig + " : " + firebaseProjectId);
//            InputStream serviceAccount = new FileInputStream(firebaseConfig);
//            FirebaseOptions options = new FirebaseOptions.Builder()
//                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                    .setProjectId("hotdeal-server")
//                    .build();
//
//            if (FirebaseApp.getApps().isEmpty()) {
//                FirebaseApp.initializeApp(options);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
