package com.synectiks.school.config;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
 
import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
 
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;




 
@Configuration
public class Firebaseconfig {
	  @PostConstruct
	    public void init() throws IOException {
	 
	    	 InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-school-service-account.json");
	 System.out.println("###"+serviceAccount);
	         // Check if the file is found in the classpath
	         if (serviceAccount == null) {
	             throw new IOException("firebase-school-service-account.json not found in classpath");
	         }
	 
	         // Set up FirebaseOptions
	         FirebaseOptions options = FirebaseOptions.builder()
	                 .setCredentials(GoogleCredentials.fromStream(serviceAccount))
	                 .setDatabaseUrl("https://school-7ff19.firebaseio.com")
	                 .build();
	 System.out.println("@@"+options);

	         
	         if (FirebaseApp.getApps().isEmpty()) {
	             FirebaseApp.initializeApp(options);
	         }
	  }
}
