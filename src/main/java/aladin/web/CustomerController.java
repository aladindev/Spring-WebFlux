package aladin.web;

import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import aladin.accessingdatar2dbc.Customer;
import aladin.accessingdatar2dbc.CustomerRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@RestController
public class CustomerController {
	
	private final CustomerRepository customerRepository;
	private final Sinks.Many<Customer> sink;
	
	
	// blocking vs Non-blocking
	// RDBMS는 비동기 지원을 안해서 소스 코드가 리액티브해도 DB에서 event block을 당하기 때문에
	// RDBMS 환경에서는 reactive web 사용이 불가능하다.
	
	/**
	 * A요청 -> Flux -> Stream
	 * B요청 -> Flux -> Stream
	 *   -> Flux.merge -> Sink 
	 * */
	public CustomerController(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
		// 모든 클라이언트들이 이 싱크에 접근할 수 있음.
		sink = Sinks.many().multicast().onBackpressureBuffer(); // 새로 푸쉬된 데이터만 구독자에게 전달한다.
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
	
	//23.02.26 25:01
	/* SSE EVENT로 응답을 준다. mediaType TEXT_EVENT_STREAM_VALUE */
	@GetMapping(value="/customer/sse", produces=MediaType.TEXT_EVENT_STREAM_VALUE) // produces=MediaType.TEXT_EVENT_STREAM_VALUE 생략가
	public Flux<ServerSentEvent<Customer>> findAllSSE() {
		//return customerRepository.findAll();
		return sink.asFlux().map(c-> ServerSentEvent.builder(c).build()).doOnCancel(() -> {
			sink.asFlux().blockLast();
		});
	}
	
	@PostMapping("/customer")
	public Mono<Customer> save() {
		return customerRepository.save(new Customer("gildong", "Hong")).doOnNext(c-> {
			sink.tryEmitNext(c); // 퍼블리셔 데이터가 하나 추가가 됨.
		});
	}

	@GetMapping(value="/login")
	public String login() {
		return "login";
	}
	
}
