package com.caps.eteeapp.controller;

import com.caps.eteeapp.model.Evaluator;
import com.caps.eteeapp.service.EvaluatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/evaluators")
public class EvaluatorController {

    @Autowired
    private EvaluatorService evaluatorService;

    @PostMapping("/register")
    public ResponseEntity<Evaluator> registerEvaluator(@RequestBody Evaluator evaluator) {
        Evaluator registeredEvaluator = evaluatorService.registerEvaluator(evaluator);
        return ResponseEntity.ok(registeredEvaluator);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginEvaluator(@RequestParam String email, @RequestParam String password) {
        Optional<Evaluator> evaluator = evaluatorService.loginEvaluator(email, password);
        if (evaluator.isPresent()) {
            return ResponseEntity.ok("Login successful!");
        }
        return ResponseEntity.status(401).body("Invalid email or password.");
    }
}
