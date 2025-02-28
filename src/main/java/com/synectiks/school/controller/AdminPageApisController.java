	package com.synectiks.school.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
	private AdminPageApiService adminpageapiservice;
	
	@Autowired
	private FeeDetails details;
	
		@PostMapping("/add_fee_detais/{schoolId}")
		public String feeDetails(@RequestBody StudentFeeDetails transactiondetails,@PathVariable String schoolId) throws InterruptedException, ExecutionException
		{
			
			adminpageapiservice.addFeeDetails(transactiondetails,schoolId);
			return null;
			
		}
		
		@PutMapping("/update_fee_details/{schoolId}")
		public void updateFeeDetailsBySid(@RequestParam String sid, String schoolId, @RequestBody List<FeeDetails> newFeeDetails) {
			details.updateFeeDetailsBySid(sid, schoolId, newFeeDetails);
		}
		
		
		@GetMapping("/getFeeDetailsForCurrentMonth/{schoolId}")
	 public List<Map<String, Object>> getfeedetails(@PathVariable String schoolId)
	 {
		return  adminpageapiservice.getFeeDetailsForCurrentMonth(schoolId) ;
			
	 }
		@GetMapping("/getFeeDetailsForCurrentWeek/{schoolId}")
	public List<Map<String, Object>> getfeedetailsofcurrentweek(@PathVariable String schoolId)
	{
		return  adminpageapiservice.getFeeDetailsForCurrentWeek(schoolId) ;
			
	}
		@GetMapping("/totalpaymentsreceived/{schoolId}")
	public double totalpaymentsreceived(@PathVariable String schoolId)
	{
		return  adminpageapiservice.getTotalPaymentsReceived(schoolId) ;
			
	}
	@GetMapping("/TotalPaymentsReceivedToday/{schoolId}")
public double totalPaymentsReceivedToday(@PathVariable String schoolId)
{
	return  adminpageapiservice.getTotalPaymentsReceivedToday(schoolId) ;
}	
@GetMapping("/AllPaymentsWithPaidDate/{schoolId}")
public List<Map<String, Object>> allPaymentsWithPaidDate(@PathVariable String schoolId)
{
return  adminpageapiservice.getAllPaymentsWithPaidDate(schoolId) ;
}


}



