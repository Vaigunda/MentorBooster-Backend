//package com.example.flutter.controller;
//
//import com.example.flutter.entities.Certificate;
//import com.example.flutter.service.CertificateService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/certificates")
//public class CertificateController {
//
//    @Autowired
//    private CertificateService certificateService;
//
//    @PostMapping
//    public ResponseEntity<Certificate> addCertificate(@RequestBody Certificate certificate) {
//        Certificate createdCertificate = certificateService.addCertificate(certificate);
//        return new ResponseEntity<>(createdCertificate, HttpStatus.CREATED);
//    }
//
//
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Certificate> updateCertificate(@PathVariable Long id, @RequestBody Certificate certificate) {
//        Certificate updatedCertificate = certificateService.updateCertificate(id, certificate);
//        return ResponseEntity.ok(updatedCertificate);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteCertificate(@PathVariable Long id) {
//        certificateService.deleteCertificate(id);
//        return ResponseEntity.noContent().build();
//    }
//}
//
