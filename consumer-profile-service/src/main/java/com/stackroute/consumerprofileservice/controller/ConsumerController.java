package com.stackroute.consumerprofileservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stackroute.consumerprofileservice.exception.UserNotFoundException;
import com.stackroute.consumerprofileservice.model.Consumer;
import com.stackroute.consumerprofileservice.model.ConsumerDTO;
import com.stackroute.consumerprofileservice.model.Land;
import com.stackroute.consumerprofileservice.repository.ConsumerRepository;
import com.stackroute.consumerprofileservice.service.ConsumerDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/consumer")
public class ConsumerController {

    @Autowired
    ConsumerRepository consumers;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static String TOPIC = "fr-kafka";

    @Autowired
    private  KafkaTemplate<String, String> kafkaTemplateConsumer1;

    private static String TOPIC11="fr-EmailRecommend";

    @Autowired
    private ConsumerDetailsService consumerService;

    @SuppressWarnings("rawtypes")
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody Consumer consumer) throws JsonProcessingException {
        Consumer consumerExists = consumerService.findConsumerByEmail(consumer.getEmail());
        if (consumerExists != null) {
            throw new BadCredentialsException("Consumer with username: " + consumer.getEmail() + " already exists");
        }
        consumerService.saveConsumer(consumer);
        ConsumerDTO consumerDTO=new ConsumerDTO();
        consumerDTO.setEmail(consumer.getEmail());
        consumerDTO.setPassword(consumer.getPassword());
        consumerDTO.setRole("consumer");
        System.out.println("Consumer DTO=" + consumerDTO);
        kafkaTemplate.send(TOPIC, new ObjectMapper().writeValueAsString(consumerDTO));
        consumerService.recommend(consumer);
        Map<Object, Object> model = new HashMap<>();
        model.put("message", "Consumer registered successfully");
        return ok(model);
    }

    @GetMapping("/register/{email}")
    public ResponseEntity<?> getConsumerById(@PathVariable String email) throws UserNotFoundException {
        System.out.println(email);
        ResponseEntity responseEntity;
        responseEntity = new ResponseEntity<>(consumerService.getConsumerByEmail(email), HttpStatus.CREATED);
        return responseEntity;
    }
    @GetMapping("/orders/{email}")
    public ResponseEntity<?> getConsumerOrders(@PathVariable String email) throws UserNotFoundException {
        System.out.println(email);
        ResponseEntity responseEntity;
        responseEntity = new ResponseEntity<>(consumerService.getConsumerOrders(email), HttpStatus.CREATED);
        return responseEntity;
    }

    @DeleteMapping("register")
    public ResponseEntity<?> deleteConsumer(@RequestParam("email") String email) {
        consumerService.deleteConsumer(email);
        return new ResponseEntity<String>("deleted", HttpStatus.FORBIDDEN);
    }

    @PutMapping("update")
    public ResponseEntity<?> updateConsumer(@RequestBody Consumer consumer) {
        consumerService.updateConsumer(consumer);
        return new ResponseEntity<Consumer>(consumer, HttpStatus.OK);
    }

    @PutMapping("/booking/{email}/{cropName}")
    public void bookLand(@PathVariable("email") String email, @PathVariable("cropName") String cropName, @RequestBody Land land) {
        consumerService.bookLand(email,cropName,land);
    }

    @GetMapping("recommend/{email}")
    public void getFarmersRecommend(@PathVariable String email){
        kafkaTemplateConsumer1.send(TOPIC11,email);
    }

}
