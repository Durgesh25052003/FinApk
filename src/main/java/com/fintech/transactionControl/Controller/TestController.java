package com.fintech.transactionControl.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/finApk/test")
public class TestController {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/testAdmin")
    public ResponseEntity<String> isAdmin(){
        return new ResponseEntity<>("Admin Accessed", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('VIEWER')")
    @GetMapping("/testViewer")
    public ResponseEntity<String> isViewer(){
        return new ResponseEntity<>("Viewer Accessed", HttpStatus.OK);
    }
}
