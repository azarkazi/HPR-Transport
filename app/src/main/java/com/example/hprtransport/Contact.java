package com.example.hprtransport;

import java.io.Serializable;

public class Contact implements Serializable {
    public String name;
    public String phoneNumber;

    public Contact() {
        // Default constructor required for calls to DataSnapshot.getValue(Contact.class)
    }

    public Contact(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
}
