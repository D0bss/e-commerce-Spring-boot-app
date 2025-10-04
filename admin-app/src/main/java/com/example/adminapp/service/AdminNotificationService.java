package com.example.adminapp.service;

import com.example.adminapp.event.OrderCancelledEvent;
import com.example.adminapp.event.OrderCreatedEvent;
import com.example.adminapp.event.OrderPaidEvent;
import com.example.adminapp.model.Admin;
import com.example.adminapp.repository.AdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for handling admin notifications based on order events.
 */
@Service
public class AdminNotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminNotificationService.class);
    private final AdminRepository adminRepository;
    
    @Autowired
    public AdminNotificationService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }
    
    /**
     * Notifies admin users about a new order.
     */
    public void notifyAdminAboutNewOrder(OrderCreatedEvent event) {
        logger.info("Preparing admin notifications for new order: {}", event.getCheckoutId());
        
        // Get all admins
        List<Admin> admins = adminRepository.findAll();
        
        // Log notification for each admin
        for (Admin admin : admins) {
            try {
                logger.info("Notifying admin {} about new order {}", admin.getEmail(), event.getCheckoutId());
                
                // In a real implementation, you might send an email or push notification here
                // For now, we'll just log the notification
                
            } catch (Exception e) {
                logger.error("Failed to notify admin {} about new order {}: {}", 
                        admin.getEmail(), event.getCheckoutId(), e.getMessage(), e);
            }
        }
        
        // Update admin dashboard data (in a real implementation)
        logger.info("Updating admin dashboard with new order: {}", event.getCheckoutId());
    }
    
    /**
     * Notifies admin users about a paid order.
     */
    public void notifyAdminAboutPaidOrder(OrderPaidEvent event) {
        logger.info("Preparing admin notifications for paid order: {}", event.getCheckoutId());
        
        // Get all admins
        List<Admin> admins = adminRepository.findAll();
        
        // Log notification for each admin
        for (Admin admin : admins) {
            try {
                logger.info("Notifying admin {} about paid order {} with amount ${}", 
                        admin.getEmail(), event.getCheckoutId(), event.getTotalAmount());
                
                // In a real implementation, you might send an email or push notification here
                // For now, we'll just log the notification
                
            } catch (Exception e) {
                logger.error("Failed to notify admin {} about paid order {}: {}", 
                        admin.getEmail(), event.getCheckoutId(), e.getMessage(), e);
            }
        }
        
        // Update admin dashboard data (in a real implementation)
        logger.info("Updating admin dashboard with paid order: {}", event.getCheckoutId());
    }
    
    /**
     * Notifies admin users about a cancelled order.
     */
    public void notifyAdminAboutCancelledOrder(OrderCancelledEvent event) {
        logger.info("Preparing admin notifications for cancelled order: {}", event.getCheckoutId());
        
        // Get all admins
        List<Admin> admins = adminRepository.findAll();
        
        // Log notification for each admin
        for (Admin admin : admins) {
            try {
                logger.info("Notifying admin {} about cancelled order {}", 
                        admin.getEmail(), event.getCheckoutId());
                
                // In a real implementation, you might send an email or push notification here
                // For now, we'll just log the notification
                
            } catch (Exception e) {
                logger.error("Failed to notify admin {} about cancelled order {}: {}", 
                        admin.getEmail(), event.getCheckoutId(), e.getMessage(), e);
            }
        }
        
        // Update admin dashboard data (in a real implementation)
        logger.info("Updating admin dashboard with cancelled order: {}", event.getCheckoutId());
    }
}
