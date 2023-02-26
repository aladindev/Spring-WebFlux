package aladin.web;

import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import aladin.accessingdatar2dbc.Customer;
import aladin.accessingdatar2dbc.CustomerRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class CustomerController {
	
	private final CustomerRepository customerRepository;
	
	public CustomerController(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@GetMapping("/customer")
	public Flux<Customer> findAll() {
		return customerRepository.findAll().log();
	}
	
	@GetMapping("/flux")
	public Flux<Integer> flux() {
		return Flux.just(1,2,3,4,5).delayElements(Duration.ofSeconds(1)).log();
	}

	@GetMapping(value="/fluxstream", produces=MediaType.APPLICATION_STREAM_JSON_VALUE)
	public Flux<Integer> fluxstream() {
		return Flux.just(1,2,3,4,5).delayElements(Duration.ofSeconds(1)).log();
	}
	
	@GetMapping("/customer/{id}") /* 반환 데이터가 1건이므로 Mono type(리스트면 flux)*/
	public Mono<Customer> findById(@PathVariable Long id) {
		return customerRepository.findById(id).log();
	}
	
	/* SSE EVENT로 응답을 준다. mediaType TEXT_EVENT_STREAM_VALUE */
	@GetMapping(value="/customer/sse", produces=MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Customer> findAllSSE() {
		return customerRepository.findAll();
		
	}
	
}
