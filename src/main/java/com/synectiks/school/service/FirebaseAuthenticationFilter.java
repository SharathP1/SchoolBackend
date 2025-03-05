//package com.synectiks.school.config;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseAuthException;
//import com.google.firebase.auth.FirebaseToken;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.filter.OncePerRequestFilter;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.ArrayList;
//
//public class FirebaseAuthenticationFilter extends OncePerRequestFilter {
//
////	protected void doFilterInternal1(
////	    jakarta.servlet.http.HttpServletRequest request,
////	    jakarta.servlet.http.HttpServletResponse response,
////	    jakarta.servlet.FilterChain filterChain
////	) throws jakarta.servlet.ServletException, IOException {
////        String token = request.getHeader("Authorization");
////        System.out.println("************");
////        if (token != null && token.startsWith("Bearer ")) {
////            token = token.substring(7);
////            try {
////                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
////                System.out.println("token"+token);
////                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
////                    decodedToken.getUid(),
////                    null,
////                    null
////                );
////                SecurityContextHolder.getContext().setAuthentication(auth);
////            } catch (FirebaseAuthException e) {
////                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
////                return;
////            }
////        }
////
////        filterChain.doFilter(request, response);
////    }
//
//	@Override
//	protected void doFilterInternal(
//	    jakarta.servlet.http.HttpServletRequest request,
//	    jakarta.servlet.http.HttpServletResponse response,
//	    jakarta.servlet.FilterChain filterChain
//	) throws jakarta.servlet.ServletException, IOException {
//	    String token = request.getHeader("Authorization");
//
//	    if (token != null && token.startsWith("Bearer ")) {
//	        token = token.substring(7); // Remove "Bearer " prefix
//	        try {
//	            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
//	            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
//	                decodedToken.getUid(),
//	                null,
//	                new ArrayList<>() // Empty authorities collection
//	            );
//	            SecurityContextHolder.getContext().setAuthentication(auth);
//	        } catch (FirebaseAuthException e) {
//	            // Log invalid token attempts
//	            logger.warn("Invalid token: {}");
//	        }
//	    }
//
//	    filterChain.doFilter(request, response); // Proceed to next filter in chain
//	}
//}








