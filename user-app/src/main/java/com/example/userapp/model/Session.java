package com.example.userapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter //  Needed for deserialization
@NoArgsConstructor
@AllArgsConstructor
public class Session implements Serializable {
    private String token;
    private Long userId;
    private LocalDateTime expiry;
    private String email;

}
