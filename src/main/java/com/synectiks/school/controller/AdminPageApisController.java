package com.synectiks.school.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.synectiks.school.entity.StudentFeeDetails;
import com.synectiks.school.service.AdminPageApiService;
import com.synectiks.school.service.FeeDetails;

@RestController
@CrossOrigin
public class AdminPageApisController {

    @Autowired
    private AdminPageApiService adminPageApiService;

    @PostMapping("/add_fee_details/{schoolId}")
    public String addFeeDetails(@RequestBody StudentFeeDetails transactionDetails, @PathVariable String schoolId) throws InterruptedException, ExecutionException {
        String result = adminPageApiService.addFeeDetails(transactionDetails, schoolId);
        System.out.println("Fee Details Added: " + result); // Log the result
        return result;
    }

    @GetMapping("/getFeeDetailsForCurrentMonth/{schoolId}")
    public List<FeeDetails> getFeeDetailsForCurrentMonth(@PathVariable String schoolId, @RequestParam String sid) throws InterruptedException, ExecutionException {
        return adminPageApiService.getFeeDetailsForCurrentMonth(schoolId, sid);
    }

    @GetMapping("/getFeeDetailsForCurrentWeek/{schoolId}")
    public List<Map<String, Object>> getFeeDetailsForCurrentWeek(@PathVariable String schoolId) {
        List<Map<String, Object>> feeDetailsList = adminPageApiService.getFeeDetailsForCurrentWeek(schoolId);
        System.out.println("Fee Details for Current Week: " + feeDetailsList); // Log the data
        return feeDetailsList;
    }

    @GetMapping("/totalPaymentsReceived/{schoolId}")
    public double getTotalPaymentsReceived(@PathVariable String schoolId) {
        double totalAmountPaid = adminPageApiService.getTotalPaymentsReceived(schoolId);
        System.out.println("Total Payments Received: " + totalAmountPaid); // Log the data
        return totalAmountPaid;
    }

    @GetMapping("/totalPaymentsReceivedToday/{schoolId}")
    public double getTotalPaymentsReceivedToday(@PathVariable String schoolId) {
        double totalAmountPaidToday = adminPageApiService.getTotalPaymentsReceivedToday(schoolId);
        System.out.println("Total Payments Received Today: " + totalAmountPaidToday); // Log the data
        return totalAmountPaidToday;
    }

    @GetMapping("/allPaymentsWithPaidDate/{schoolId}")
    public List<Map<String, Object>> getAllPaymentsWithPaidDate(@PathVariable String schoolId) {
        List<Map<String, Object>> paymentList = adminPageApiService.getAllPaymentsWithPaidDate(schoolId);
        System.out.println("All Payments with Paid Date: " + paymentList); // Log the data
        return paymentList;
    }
}
