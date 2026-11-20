package com.rajveersingh.staysphere.dto;

public record AdminDashboardResponse(
        long totalOwners,
        long pendingOwners,
        long approvedOwners,
        long rejectedOwners,
        long suspendedOwners) {
}