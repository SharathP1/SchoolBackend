package com.synectiks.school.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.synectiks.school.service.AdminPageApiService;



@RestController
@CrossOrigin
public class AdminPageApisController {
	@Autowired
	private AdminPageApiService adminpageapiservice;
	
		@PostMapping("/add_fee_detais")
		public String feeDetails(@RequestBody Map<String , Object> transactiondetails) throws InterruptedException, ExecutionException
		{
			
			adminpageapiservice.addFeeDetails(transactiondetails);
			return null;
			
		}
		@GetMapping("/getFeeDetailsForCurrentMonth")
	 public List<Map<String, Object>> getfeedetails()
	 {
		return  adminpageapiservice.getFeeDetailsForCurrentMonth() ;
			
	 }
		@GetMapping("/getFeeDetailsForCurrentWeek")
	public List<Map<String, Object>> getfeedetailsofcurrentweek()
	{
		return  adminpageapiservice.getFeeDetailsForCurrentWeek() ;
			
	}
		@GetMapping("/totalpaymentsreceived")
	public double totalpaymentsreceived()
	{
		return  adminpageapiservice.getTotalPaymentsReceived() ;
			
	}
	@GetMapping("/TotalPaymentsReceivedToday")
public double totalPaymentsReceivedToday()
{
	return  adminpageapiservice.getTotalPaymentsReceivedToday() ;
}	
@GetMapping("/AllPaymentsWithPaidDate")
public List<Map<String, Object>> allPaymentsWithPaidDate()
{
return  adminpageapiservice.getAllPaymentsWithPaidDate() ;
}
}
